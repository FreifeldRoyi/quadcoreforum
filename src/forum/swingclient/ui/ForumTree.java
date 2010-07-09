package forum.swingclient.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
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
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
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


import forum.swingclient.controllerlayer.*;
import forum.swingclient.panels.ReplyModifyDialog;
import forum.swingclient.panels.MainPanel;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

/**
 * @author Tomer Heber
 *
 */

public class ForumTree implements GUIHandler {

	private boolean shouldAskExpansion;
	private long fatherSubjectID;

	/**
	 * The JTree GUI component.
	 */
	private JTree m_tree;

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

	public void selectFirstRow() {
		//try {
		//if (m_tree.getRowCount() > 0)
		//	m_tree.setSelectionRow(0);
		//}
		//catch (Exception e) {}
	}

	public void setFatherID(final long fatherSubjectID) {
		this.fatherSubjectID = fatherSubjectID;
	}

	public ForumTree(final MainPanel container) {

		shouldAskExpansion = true;

		UIManager.put("Tree.collapsedIcon", new ImageIcon("./images/plus-8.png"));
		UIManager.put("Tree.expandedIcon", new ImageIcon("./images/minus-8.png"));

		try {
			pipe = ControllerHandlerFactory.getPipe();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.container = container;
		m_tree = new JTree();
		m_tree.putClientProperty("JTree.lineStyle", "Angled");

		m_tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		SelectedForumTreeCellPanel selected = new SelectedForumTreeCellPanel(this);

		ForumTreeCellRenderer renderer = new ForumTreeCellRenderer(this, selected);
		m_tree.setCellRenderer(renderer);
		m_tree.setCellEditor(new ForumTreeCellEditor(renderer));
		m_tree.setEditable(false);

		m_tree.setRowHeight(40);

		m_tree.addTreeSelectionListener(new TreeSelectionListener() {			
			public void valueChanged(TreeSelectionEvent e) {
				BasicTreeUI ui = (BasicTreeUI)m_tree.getUI();
				ui.setLeftChildIndent(ui.getLeftChildIndent());
				ui.setRightChildIndent(ui.getRightChildIndent());

			}
		});

		m_tree.addTreeWillExpandListener(new TreeWillExpandListener() {

			public void treeWillCollapse(TreeExpansionEvent event){}

			public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
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

						pipe.getNestedMessages(((ForumCell)node.getUserObject()).getId(), m_panel);


						try {
							node.wait();
							((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(node);
							m_tree.getSelectionModel().setSelectionPath(new TreePath(
									((DefaultTreeModel)m_tree.getModel()).getPathToRoot(node)));

							new Thread(new Runnable() {
								public void run() {
									container.stopWorkingAnimation();							
								}
							}).start();

						}

						catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				}
			}
		});

		m_panel = new JPanel();
		m_panel.setBackground(Color.WHITE);

		m_panel.setLayout(new BoxLayout(m_panel, BoxLayout.PAGE_AXIS));


		JPanel tCurrentPanel = new JPanel();

		GroupLayout tLayout = new GroupLayout(tCurrentPanel);

		tLayout.setHorizontalGroup(tLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addComponent(m_tree, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addGap(20, 20, 20)
				.addComponent(m_tree, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

		tCurrentPanel.setBackground(Color.white);

		tCurrentPanel.setLayout(tLayout);

		scrl_tree_pane = new JScrollPane(tCurrentPanel);

		
		scrl_tree_pane.setPreferredSize(new Dimension(610,635));

		// Adds the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		splitPane.setTopComponent(scrl_tree_pane);

		splitPane.setBottomComponent(selected);

		splitPane.setDividerLocation(260); 
		splitPane.setPreferredSize(new Dimension(500, 600));

		m_panel.add(splitPane);

		m_tree.setModel(new DefaultTreeModel(null));



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
				e.printStackTrace();
			}

			synchronized (rootNode) {
				try {
					this.pipe.getNestedMessages(this.fatherMessageID, this.m_panel);			
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
					e.printStackTrace();
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
			System.out.println(Arrays.toString(tSplitted));
			String[] tRootAsStringArray = tSplitted[0].split("\t");
			if (tRootAsStringArray.length > 4) {
				for (int i = 4; i < tRootAsStringArray.length; i++) {
					tRootAsStringArray[3] += ("\t" + tRootAsStringArray[i]);  
				}
			}

			ForumCell tRoot = new ForumCell(Long.parseLong(tRootAsStringArray[0]),
					tRootAsStringArray[1], tRootAsStringArray[2], tRootAsStringArray[3]);

			for (int i = 1; i < tSplitted.length; i++) {
				String[] tCurrentReplies = tSplitted[i].split("\n" + tRepliesDelimiter);
				tRoot.add(getRootCell(tCurrentReplies, "\t" + tRepliesDelimiter));
			}
			return tRoot;

		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			SystemLogger.warning("Error while parsing messages view response");
			return null;
		}
		catch (Exception e) { // any parsing exception
			e.printStackTrace();
			SystemLogger.warning("Error while parsing messages view response");
			return null;
		}	
	}

	public void refreshForum (final String encodedView) {

		if (encodedView.startsWith("getpathsuccess")) {
			m_panel.setVisible(false);
			System.out.println(encodedView);
			final long tMessageID = Long.parseLong(encodedView.substring(encodedView.indexOf("\n") + 1, encodedView.indexOf("\nSUBJECTS")));
			final String tMessages = encodedView.substring(encodedView.indexOf("MESSAGES") + 9);
			new Thread(new Runnable() {
				public void run() {
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

							if ((tNodeToExpand == null) && (sonCell.getId() == tMessageID))
								tNodeToExpand = sonNode;

							node.add(sonNode);
							stack.add(sonNode);
						}
						((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(node);
					}

					shouldAskExpansion = false;
					if (tNodeToExpand != null) {


						TreePath tPathToRequiredNode = new TreePath(
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

							pipe.getNestedMessages(((ForumCell)tNodeToExpand.getUserObject()).getId(), m_panel);


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
								e1.printStackTrace();
							}
						}


						

						m_tree.getSelectionModel().setSelectionPath(tPathToRequiredNode);
						shouldAskExpansion = true;
					}
					container.switchToMessagesView();
				}
			}).start();
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

			System.out.println(errorMessage);
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
					e.printStackTrace();
				}

				System.out.println("noyif" + id);
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
		System.out.println(encodedView);
		if (encodedView.startsWith("replysuccess") || 
				encodedView.startsWith("modifysuccess") || 
				encodedView.startsWith("deletesuccess") ||
				encodedView.startsWith("searchresult") ||
				encodedView.startsWith("searchnotmessages") ||
				encodedView.startsWith("getpathsuccess"))
			return null;
		try {
			String[] tSplitted = encodedView.split("\n\tAREPLYMESSAGE: ");

			System.out.println("***********splitted: ");

			for (int i = 0; i <tSplitted.length;i++)
				System.out.println("splitted [" + i + "] = " + tSplitted[i]);

			String[] tRootAsStringArray = tSplitted[0].split("\t");
			if (tRootAsStringArray.length > 4) {
				for (int i = 4; i < tRootAsStringArray.length; i++) {
					tRootAsStringArray[3] += ("\t" + tRootAsStringArray[i]);  
				}
			}

			ForumCell tRoot = new ForumCell(Long.parseLong(tRootAsStringArray[0]),
					tRootAsStringArray[1], tRootAsStringArray[2], tRootAsStringArray[3]);

			for (int i = 1; i < tSplitted.length; i++) {
				String[] tCurrentReplies = tSplitted[i].split("\n\t\tASUBREPLYMESSAGE: ");

				String[] tCurrReply = tCurrentReplies[0].split("\t");
				if (tCurrReply.length > 4) {
					for (int j = 4; j < tCurrReply.length; j++) {
						tCurrReply[3] += ("\t" + tCurrReply[j]);  
					}
				}
				ForumCell tReply = new ForumCell(Long.parseLong(tCurrReply[0]),
						tCurrReply[1], tCurrReply[2], tCurrReply[3]);
				for (int j = 1; j < tCurrentReplies.length; j++) {
					String[] tCurrentReplyAsStringArray = tCurrentReplies[j].split("\t");
					if (tCurrentReplyAsStringArray.length > 4) {
						for (int k = 4; k < tCurrentReplyAsStringArray.length; k++) {
							tCurrentReplyAsStringArray[3] += ("\t" + tCurrentReplyAsStringArray[k]);  
						}
					}
					ForumCell tCurrentReply = new ForumCell(Long.parseLong(tCurrentReplyAsStringArray[0]),
							tCurrentReplyAsStringArray[1], tCurrentReplyAsStringArray[2], tCurrentReplyAsStringArray[3]);
					tReply.add(tCurrentReply);
				}
				tRoot.add(tReply);
			}
			return tRoot;
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			SystemLogger.warning("Error while parsing messages view response");
			return null;
		}
		catch (Exception e) { // any parsing exception
			e.printStackTrace();
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
					ControllerHandlerFactory.getPipe().getNestedMessages(cell.getId(), m_tree);
					try {
						node.wait();
						((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(node);
					} 
					catch (InterruptedException e) {
					}
					m_tree.setSelectionRow(0);
				}
			}
			tModifyDialog.dispose();



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				pipe.getNestedMessages(cell.getId(), m_tree);
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


		System.out.println("PARENTCELL = " + parentCell);

		//	System.out.println(cell.getTitle());
		//	System.out.println(parentCell.getTitle());

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

									System.out.println("id " + parentCell.getId());
									System.out.println(parent.id);
									pipe.getNestedMessages(parentCell.getId(), m_tree);
									try {
										System.out.println("waiting");
										parent.wait();
										System.out.println("wait end");
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