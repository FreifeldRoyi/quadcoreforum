package forum.swingclient.ui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import forum.server.domainlayer.SystemLogger;
import forum.swingclient.controllerlayer.ConnectedUserData;
import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.panels.MainPanel;
import forum.swingclient.panels.ReplyModifyDialog;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.JExpansionStatusSavingTree;
import forum.swingclient.ui.events.GUIEvent.EventType;

/**
 * @author Tomer Heber
 *
 */

public class ForumTree implements GUIHandler {


	private boolean shouldAskExpansion;
	private long fatherSubjectID;

	private Map<Long, String> expansionStatus;


	/**
	 * The JTree GUI component.
	 */
	private JExpansionStatusSavingTree m_tree;

	/**
	 * The JPanel GUI component.
	 */
	private JPanel m_panel;

	/**
	 * A pipe interface to communicate with the controller layer.
	 */
	private ControllerHandler pipe;

	private MainPanel container;
	private long fatherMessageID;


	/**
	 * A thread pool that is used to initiate operations in the controller layer.
	 */
	private ExecutorService pool = Executors.newCachedThreadPool();
	private JScrollPane scrl_tree_pane;

	public ConnectedUserData getConnectedUser() {
		return this.container.getConnectedUser();
	}


	public void setFatherID(final long fatherSubjectID) {
		this.fatherSubjectID = fatherSubjectID;
	}



	public ForumTree(final MainPanel container) {
		//		shouldRestoreExpanded = true;
		expansionStatus = new HashMap<Long, String>();

		shouldAskExpansion = true;

		UIManager.put("Tree.collapsedIcon", new ImageIcon("./images/plus-8.png"));
		UIManager.put("Tree.expandedIcon", new ImageIcon("./images/minus-8.png"));

		try {
			pipe = ControllerHandlerFactory.getPipe();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		}

		this.container = container;
		m_tree = new JExpansionStatusSavingTree();


		m_tree.putClientProperty("JTree.lineStyle", "Angled");

		m_tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		SelectedForumTreeCellPanel selected = new SelectedForumTreeCellPanel(this);

		ForumTreeCellRenderer renderer = new ForumTreeCellRenderer(this, selected);
		m_tree.setCellRenderer(renderer);
		m_tree.setCellEditor(new ForumTreeCellEditor(renderer));
		m_tree.setEditable(false);

		m_tree.setRowHeight(40);

		m_tree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		// one click before toggle
		m_tree.setToggleClickCount(1);

		m_tree.addTreeSelectionListener(new TreeSelectionListener() {			
			public void valueChanged(TreeSelectionEvent e) {
				BasicTreeUI ui = (BasicTreeUI)m_tree.getUI();
				ui.setLeftChildIndent(ui.getLeftChildIndent());
				ui.setRightChildIndent(ui.getRightChildIndent());

			}
		});


		m_tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)m_tree.getSelectionPath().getLastPathComponent();
				if (!tNode.isLeaf() && tNode != m_tree.getModel().getRoot())
					return;
				else
					try {
						m_tree.fireTreeWillExpand(new TreePath(tNode.getPath()));
					}
				catch (ExpandVetoException e1) {
					return;
				}
			}
		});


		m_tree.addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent arg0) {
				//				shouldRestoreExpanded = false;
				long tID = ((ForumCell)((DefaultMutableTreeNode)arg0.getPath().getLastPathComponent()).getUserObject()).getId();

				String tE = (String)expansionStatus.get(tID);
				//				shouldRestoreExpanded = false;
				m_tree.setToggleClickCount(2);
				if (tE != null) {
					m_tree.restoreExpanstionState(m_tree.getRowForPath(arg0.getPath()), tE);
					m_tree.getSelectionModel().setSelectionPath(arg0.getPath());
				}
				m_tree.setToggleClickCount(1);
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		m_tree.addTreeWillExpandListener(new TreeWillExpandListener() {

			public void treeWillCollapse(TreeExpansionEvent event)	throws ExpandVetoException {
				if (event.getPath().getLastPathComponent() == m_tree.getModel().getRoot()) {
					throw new ExpandVetoException(event);
				}



				long tID = ((ForumCell)((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).getUserObject()).getId();
				try {
					expansionStatus.put(tID, m_tree.getExpansionState(m_tree.getRowForPath(event.getPath())));

				}
				catch (Exception e) {
					expansionStatus.put(tID, null);
				}

			}


			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				if (shouldAskExpansion) {


					new Thread(new Runnable() {
						public void run() {
							container.startWorkingAnimation("Retrieving replies ...");
						}
					}).start();


					TreePath path = (TreePath)event.getPath();
					MessageTreeNode node = (MessageTreeNode)path.getPathComponent(path.getPathCount() - 1);

					synchronized (node) {
						try {
							ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(node),
									EventType.MESSAGES_UPDATED);

						} 
						catch (IOException e)  {
						}

						pipe.getNestedMessages(((ForumCell)node.getUserObject()).getId(), false, m_panel);


						try {
							node.wait();

							((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(node);

							//					if (shouldRestoreExpanded) {

							TreePath tPathToSelect = new TreePath(
									((DefaultTreeModel)m_tree.getModel()).getPathToRoot(node));
							m_tree.getSelectionModel().setSelectionPath(tPathToSelect);
							//}

							new Thread(new Runnable() {
								public void run() {
									container.stopWorkingAnimation();							
								}
							}).start();
						}

						catch (InterruptedException e1) {
							// TODO Auto-generated catch block
						}

					}
				}
			}
		});

		m_panel = new JPanel();
		m_panel.setBackground(Color.WHITE);

		m_panel.setLayout(new BoxLayout(m_panel, BoxLayout.PAGE_AXIS));


		JPanel tCurrentPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8175327762515671628L;

			public void paint(Graphics g) {
				g.drawImage(new ImageIcon("./images/quadcore5.png").getImage(), 
						725, 0, 596, 333, null);
				setOpaque(false);
				super.paint(g);
			}
		};



		GroupLayout tLayout = new GroupLayout(tCurrentPanel);

		tLayout.setHorizontalGroup(tLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addComponent(m_tree, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addGap(20, 20, 20)
				.addComponent(m_tree, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

		//		tCurrentPanel.setBackground(Color.yellow);
		//		m_tree.setBackground(Color.yellow);

		m_tree.setOpaque(false);
		tCurrentPanel.setLayout(tLayout);

		scrl_tree_pane = new JScrollPane(tCurrentPanel);



		scrl_tree_pane.setPreferredSize(new Dimension(610,635));

		// Adds the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);


		splitPane.setTopComponent(scrl_tree_pane);

		splitPane.setBottomComponent(selected);

		splitPane.setDividerLocation(260); 



		m_panel.add(splitPane);

		m_tree.setModel(new DefaultTreeModel(null));


	}

	public long getFatherMessageID() {
		return this.fatherMessageID;
	}

	public void setRootMessage(final long fatherMessageID) {
		this.fatherMessageID = fatherMessageID;

		if (this.fatherMessageID != -1) {

			MessageTreeNode rootNode = new MessageTreeNode(null, this.fatherMessageID);

			DefaultTreeModel model = new DefaultTreeModel(rootNode);
			m_tree.setModel(model);

			try {
				ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(rootNode),
						EventType.MESSAGES_UPDATED);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}

			synchronized (rootNode) {
				try {
					this.pipe.getNestedMessages(this.fatherMessageID, true, this.m_panel);			
					rootNode.wait();
					((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(rootNode);
					this.m_tree.setSelectionRow(0);
					new Thread(new Runnable() {
						public void run() {
							container.stopWorkingAnimation();							
						}
					}).start();
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		else {
			m_tree.setModel(new DefaultTreeModel(null));
		}		
	}

	public void notifyError(String errorMessage) {
		JOptionPane.showMessageDialog(this.container, "The message couldn't be found, maybe" +
				" it was deleted by another user.\nYou can try run the search again.", "error while searching", JOptionPane.ERROR_MESSAGE);
	}

	private ForumCell getRootCell(String[] tSplitted, String tRepliesDelimiter) {
		try {
			String[] tRootAsStringArray = tSplitted[0].split("\t");
			if (tRootAsStringArray.length > 5) {
				for (int i = 5; i < tRootAsStringArray.length; i++) {
					tRootAsStringArray[4] += ("\t" + tRootAsStringArray[i]);  
				}
			}

			ForumCell tRoot = new ForumCell(Long.parseLong(tRootAsStringArray[1]),
					tRootAsStringArray[2], tRootAsStringArray[0], tRootAsStringArray[3], tRootAsStringArray[4]);

			for (int i = 1; i < tSplitted.length; i++) {
				String[] tCurrentReplies = tSplitted[i].split("\n" + tRepliesDelimiter);
				tRoot.add(getRootCell(tCurrentReplies, "\t" + tRepliesDelimiter));
			}
			return tRoot;

		}
		catch (NumberFormatException e) {
			SystemLogger.warning("Error while parsing messages view response");
			return null;
		}
		catch (Exception e) { // any parsing exception
			SystemLogger.warning("Error while parsing messages view response");
			return null;
		}	
	}

	public void refreshForum (final String encodedView) {

		if (encodedView.startsWith("getpathsuccess")) {
			m_panel.setVisible(false);
			final long tMessageID = Long.parseLong(encodedView.substring(encodedView.indexOf("\n") + 1, encodedView.indexOf("\nSUBJECTS")));
			final String tMessages = encodedView.substring(encodedView.indexOf("MESSAGES") + 9);


			new SwingWorker<TreePath, Void>() {
				public TreePath doInBackground() {
					new Thread(new Runnable() {
						public void run() {
							container.startWorkingAnimation("Retrieving found message ...");
						}
					}).start();


					String tRepliesDelimiter = "\t\tAREPLYMESSAGE: ";
					String[] tSplitted = tMessages.split("\n\tAREPLYMESSAGE: ");

					MessageTreeNode tNodeToExpand = null;
					ForumCell tRoot = getRootCell (tSplitted, tRepliesDelimiter);

					MessageTreeNode rootNode = new MessageTreeNode(null, tRoot.getId());

					if (tRoot.getId() == tMessageID)
						tNodeToExpand = rootNode;

					DefaultTreeModel model = new DefaultTreeModel(rootNode);
					m_tree.setModel(model);


					rootNode.setUserObject(tRoot);
					Stack<MessageTreeNode> stack = new Stack<MessageTreeNode>();
					stack.add(rootNode);

					while (!stack.isEmpty()) {
						MessageTreeNode node = stack.pop();
						ForumCell cell = (ForumCell)(node.getUserObject());
						if ((tNodeToExpand == null) && (cell.getId() == tMessageID))
							tNodeToExpand = node;

						for (ForumCell sonCell : cell.getSons()) {
							MessageTreeNode sonNode = new MessageTreeNode(sonCell, sonCell.getId());

							if ((tNodeToExpand == null) && (sonCell.getId() == tMessageID)) {
								tNodeToExpand = sonNode;
							}

							node.add(sonNode);
							stack.add(sonNode);

						}
						((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(node);
					}

					shouldAskExpansion = false;

					TreePath tPathToRequiredNode = null;
					if (tNodeToExpand != null) {

						tPathToRequiredNode = new TreePath(
								((DefaultTreeModel)m_tree.getModel()).getPathToRoot(tNodeToExpand)); 

						m_tree.expandPath(tPathToRequiredNode);
						m_tree.fireTreeExpanded(tPathToRequiredNode);

						synchronized (tNodeToExpand) {
							try {
								ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(tNodeToExpand),
										EventType.MESSAGES_UPDATED);

							} 
							catch (IOException e)  {
							}

							pipe.getNestedMessages(((ForumCell)tNodeToExpand.getUserObject()).getId(), false, m_panel);


							try {
								tNodeToExpand.wait();
								((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(tNodeToExpand);

								new Thread(new Runnable() {
									public void run() {
										container.stopWorkingAnimation();							
									}
								}).start();

							}

							catch (InterruptedException e1) {
								// TODO Auto-generated catch block
							}
						}
						shouldAskExpansion = true;
					}
					container.switchToMessagesView();
					return tPathToRequiredNode;
				}


				public void done() {
					try {
						TreePath tPathToRequiredNode = get();
						shouldAskExpansion = false;
						if (tPathToRequiredNode != null)
							m_tree.getSelectionModel().setSelectionPath(tPathToRequiredNode);
						else
							m_tree.setSelectionRow(0);
						shouldAskExpansion = true;
					} 
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
					catch (ExecutionException e) {
						// TODO Auto-generated catch block
					}
					m_tree.scrollPathToVisible(m_tree.getSelectionModel().getSelectionPath());
				}
			}.execute();
		}
	}

	/**
	 * 
	 * @return The forum tree GUI component. 
	 */
	public Component getForumTreeUI() {
		return m_panel;
	}

	private class MessageTreeNode extends DefaultMutableTreeNode implements GUIHandler {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7408164679702226870L;

		private long id;

		public MessageTreeNode(ForumCell info, long id) {
			super(info);
			this.id = id;
		}

		public void notifyError(String errorMessage) {
			synchronized (this) {
				pipe.deleteObserver(this);
				this.notifyAll();
			}

		}

		public void refreshForum(String encodedView) {
			ForumCell rootCell = decodeView(encodedView);

			if (rootCell == null || rootCell.getId() != id) return;

			this.removeAllChildren();


			this.setUserObject(rootCell);
			Stack<MessageTreeNode> stack = new Stack<MessageTreeNode>();
			stack.add(this);

			while (!stack.isEmpty()) {
				MessageTreeNode node = stack.pop();
				ForumCell cell = (ForumCell)(node.getUserObject());
				for (ForumCell sonCell : cell.getSons()) {
					MessageTreeNode sonNode = new MessageTreeNode(sonCell, sonCell.getId());
					node.add(sonNode);
					stack.add(sonNode);
				}
			}



			for (int i = 0; i < this.getChildCount(); i++) { 
				DefaultMutableTreeNode n = (DefaultMutableTreeNode)this.getChildAt(i); 
				((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(n);
			}


			container.switchToMessagesView();


			synchronized (this) {

				try {
					ControllerHandlerFactory.getPipe().deleteObserver(this);



				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
				}

				this.notifyAll();
			}
		}
	}

	/**
	 * Receives an encoding describing the forum tree.<br>
	 * It decodes the description and returns the tree representation in a ForumCell instance.
	 * 
	 * @return The tree representing the forum.
	 */
	private ForumCell decodeView(String encodedView) {
		if (encodedView.startsWith("replysuccess") || 
				encodedView.startsWith("modifysuccess") || 
				encodedView.startsWith("deletesuccess") ||
				encodedView.startsWith("searchresult") ||
				encodedView.startsWith("searchnotmessages") ||
				encodedView.startsWith("getpathsuccess"))
			return null;
		try {
			String[] tSplitted = encodedView.split("\n\tAREPLYMESSAGE: ");

			String[] tRootAsStringArray = tSplitted[0].split("\t");
			if (tRootAsStringArray.length > 5) {
				for (int i = 5; i < tRootAsStringArray.length; i++) {
					tRootAsStringArray[4] += ("\t" + tRootAsStringArray[i]);  
				}
			}

			ForumCell tRoot = new ForumCell(Long.parseLong(tRootAsStringArray[1]),
					tRootAsStringArray[2], tRootAsStringArray[0], tRootAsStringArray[3], tRootAsStringArray[4]);

			for (int i = 1; i < tSplitted.length; i++) {
				String[] tCurrentReplies = tSplitted[i].split("\n\t\tASUBREPLYMESSAGE: ");

				String[] tCurrReply = tCurrentReplies[0].split("\t");
				if (tCurrReply.length > 5) {
					for (int j = 5; j < tCurrReply.length; j++) {
						tCurrReply[4] += ("\t" + tCurrReply[j]);  
					}
				}
				ForumCell tReply = new ForumCell(Long.parseLong(tCurrReply[1]),
						tCurrReply[2], tCurrReply[0], tCurrReply[3], tCurrReply[4]);
				for (int j = 1; j < tCurrentReplies.length; j++) {
					String[] tCurrentReplyAsStringArray = tCurrentReplies[j].split("\t");
					if (tCurrentReplyAsStringArray.length > 5) {
						for (int k = 5; k < tCurrentReplyAsStringArray.length; k++) {
							tCurrentReplyAsStringArray[4] += ("\t" + tCurrentReplyAsStringArray[k]);  
						}
					}
					ForumCell tCurrentReply = new ForumCell(Long.parseLong(tCurrentReplyAsStringArray[1]),
							tCurrentReplyAsStringArray[2], tCurrentReplyAsStringArray[0], 
							tCurrentReplyAsStringArray[3], tCurrentReplyAsStringArray[4]);
					tReply.add(tCurrentReply);
				}
				tRoot.add(tReply);
			}
			return tRoot;
		}
		catch (NumberFormatException e) {
			SystemLogger.warning("Error while parsing messages view response");
			return null;
		}
		catch (Exception e) { // any parsing exception
			SystemLogger.warning("Error while parsing messages view response");
			return null;
		}
	}

	/**
	 * Modifies a message, and updates the forum accordingly.
	 * 
	 * @param newContent The new content of the message.
	 */
	public void modifyMessage(final JButton button) {
		//		button.setEnabled(false);
		MessageTreeNode node = (MessageTreeNode)m_tree.getSelectionPath().getLastPathComponent();
		ForumCell cell = (ForumCell) node.getUserObject();
		ReplyModifyDialog tModifyDialog = 
			new ReplyModifyDialog(container.getConnectedUser().getID(), cell.getId(), cell.getTitle(),
					cell.getContent(), button);
		try {
			tModifyDialog.setVisible(true);

			if (tModifyDialog.shouldUpdateGUI()) {
				synchronized (node) {
					ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(node),
							EventType.MESSAGES_UPDATED);
					ControllerHandlerFactory.getPipe().getNestedMessages(cell.getId(), false, m_tree);
					try {
						node.wait();
						((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(node);
					} 
					catch (InterruptedException e) {
					}

					TreePath tPathToSelect = new TreePath(node.getPath());
					m_tree.setSelectionPath(tPathToSelect);
				}
			}
			else if (tModifyDialog.errorOcurred()) {
				this.handleViewMessageError(node);
			}

			tModifyDialog.dispose();

		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	private void handleViewMessageError(final TreeNode node) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {

				final long tRootID = ((ForumCell)(
						(DefaultMutableTreeNode)m_tree.getSelectionPath().getPathComponent(0)).getUserObject()).getId();
				final boolean[] rootFound = new boolean[1];
				rootFound[0] = false;


				pipe.addObserver(new GUIObserver(new GUIHandler() {
					@Override
					public void refreshForum(String encodedView) {
						pipe.deleteObserver(this);

						String[] tSplitted = encodedView.split("\n");

						for (int i = 0; i < tSplitted.length; i++) {
							String[] tCurrentThreadInfo = tSplitted[i].split("\t");
							// this is the subject's id
							try {
								if (Long.parseLong(tCurrentThreadInfo[0]) == tRootID) {
									rootFound[0] = true;
									break;
								}
							}
							catch (NumberFormatException e) {
								SystemLogger.warning("The server response related to subject's update is invalid");
								break;
							}
						}
						node.notifyAll();
					}
					@Override
					public void notifyError(String errorMessage) {
						pipe.deleteObserver(this);
					}
				}), EventType.THREADS_UPDATED);
				pipe.getThreads(ForumTree.this.fatherSubjectID, container);
				try {
					synchronized (node) {
						node.wait();
					}
					if (rootFound[0])
						handleShowExistingMessages(node);
					else
						handleReturnToThreadsSubjectsView(node);						
				}
				catch (InterruptedException e) { /* do nothing */ }
			}

		});
	}

	@SuppressWarnings("unchecked")
	private void handleReturnToThreadsSubjectsView(TreeNode node) {
		TreePath tPath = m_tree.getSelectionPath();

		MessageTreeNode tNode = (MessageTreeNode)tPath.getPathComponent(0);
		for (int i = 1; i < tPath.getPathCount() - 1; i++) {
			synchronized (tNode) {
				try {
					ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(tNode),
							EventType.MESSAGES_UPDATED);
				} 
				catch (IOException e)  {

				}

				pipe.getNestedMessages(((ForumCell)tNode.getUserObject()).getId(), false, m_panel);


				try {
					tNode.wait();
					((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(tNode);
				}

				catch (InterruptedException e1) {
					// TODO Auto-generated catch block
				}
			}

			Enumeration<MessageTreeNode> tChilds = tNode.children();
			boolean tFound = false;
			MessageTreeNode tCurrentChild = null;
			while (tChilds.hasMoreElements()) {
				tCurrentChild = tChilds.nextElement();
				if (((ForumCell)tCurrentChild.getUserObject()).getId() == 
					((ForumCell)((MessageTreeNode)tPath.getPathComponent(i + 1)).getUserObject()).getId()) {
					tFound = true;
					break;
				}
			}
			if (tFound) {
				tNode = tCurrentChild;
				continue;
			}
			else
				break;
		}

		new Thread(new Runnable() {
			public void run() {
				container.stopWorkingAnimation();							
			}
		}).start();

	}

	private void handleShowExistingMessages(TreeNode node) {


	}


	/**
	 * Replies to the selected message.
	 */
	public void replyToMessage(final JButton button) {
		//		button.setEnabled(false);
		MessageTreeNode node = (MessageTreeNode)m_tree.getSelectionPath().getLastPathComponent();
		ForumCell cell = (ForumCell) node.getUserObject();				
		ReplyModifyDialog tReplyDialog = 
			new ReplyModifyDialog(container.getConnectedUser().getID(), cell.getId(), button);
		tReplyDialog.setVisible(true);
		if (tReplyDialog.shouldUpdateGUI()) {
			synchronized (node) {
				pipe.addObserver(new GUIObserver(node),
						EventType.MESSAGES_UPDATED);
				pipe.getNestedMessages(cell.getId(), false, m_tree);
				try {
					node.wait();
					((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(node);
					m_tree.expandPath(new TreePath(((DefaultTreeModel)m_tree.getModel()).getPathToRoot(node)));

				} catch (InterruptedException e) {
				}
			}
			m_tree.setSelectionRow(0);
		}
		tReplyDialog.dispose();
	}

	/**
	 * Deletes the selected message.
	 */
	public void deleteMessage(final JButton button) {
		//		button.setEnabled(false);
		final MessageTreeNode node = (MessageTreeNode)m_tree.getSelectionPath().getLastPathComponent();

		if (m_tree.getModel().getRoot() == node) {
			if (JOptionPane.showConfirmDialog(this.m_panel, "Deleting this message will delete the entire thread,\n" +
					"are you sure you want to continue?", "delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
						JOptionPane.NO_OPTION) {
				button.setEnabled(true);
				return;
			}
		}
		else if (JOptionPane.showConfirmDialog(this.m_panel, "Are you sure you want to delete the message?", "delete",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
					JOptionPane.NO_OPTION) {
			button.setEnabled(true);
			return;
		}


		final MessageTreeNode parent = (MessageTreeNode) node.getParent();

		final ForumCell cell = (ForumCell) node.getUserObject();
		final ForumCell parentCell = parent != null? (ForumCell) ((MessageTreeNode)parent).getUserObject() : null; 

		pipe.addObserver(new GUIObserver(new GUIHandler() {
			public void notifyError(String errorMessage) {
				if (errorMessage.startsWith("deleteerror\n")) {
					pipe.deleteObserver(this);
					JOptionPane.showMessageDialog(ForumTree.this.m_panel,
							"cannot delete the message!", "error", JOptionPane.ERROR_MESSAGE);
				}
			}

			public void refreshForum(final String encodedView) {

				if (encodedView.startsWith("deletesuccess")) {
					pipe.deleteObserver(this);
					new Thread(new Runnable() {
						public void run() {
							if (parentCell != null) {
								synchronized (parent) {
									pipe.addObserver(new GUIObserver(parent), EventType.MESSAGES_UPDATED);

									pipe.getNestedMessages(parentCell.getId(), false, m_tree);
									try {
										parent.wait();
										((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(parent);

										JOptionPane.showMessageDialog(ForumTree.this.m_panel, 
												"the message was deleted successfully!", "success",
												JOptionPane.INFORMATION_MESSAGE);
									} 
									catch (InterruptedException e) {
										/// TODO Auto-generated catch block
									}
								}
							}
							else { // delete a thread
								JOptionPane.showMessageDialog(ForumTree.this.container, 
										"the thread was deleted successfully!", "success",
										JOptionPane.INFORMATION_MESSAGE);

								pipe.getSubjects(fatherSubjectID, container);
								pipe.getThreads(fatherSubjectID, container);
								container.switchToSubjectsAndThreadsView();
							}
						}}).start();
				}
			}
		}), EventType.MESSAGES_UPDATED);
		pipe.deleteMessage(container.getConnectedUser().getID(), parentCell == null? -1 : parentCell.getId(), cell.getId(), button);

		m_tree.setSelectionRow(0);
	}

	/**
	 * Adds a new message.
	 */
	public void addNewMessage(final JButton button) {
		button.setEnabled(false);
		pool.execute(new Runnable() {

			@Override
			public void run() {				
				pipe.addNewMessage(button);				
			}
		});
	}





	/**
	 * This is for testing purposes only! <br>
	 * Delete when done testing!
	 * 
	 * @param args
	 *//*
	public static void main(String[] args) {
		ForumTree tree = new ForumTree(-1);

		JFrame frame = new JFrame("test");
		frame.setSize(new Dimension(640,480));

		frame.getContentPane().add(tree.getForumTreeUI());	
		frame.setVisible(true);		

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}*/
}
