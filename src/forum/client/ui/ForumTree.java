package forum.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
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

import forum.client.controllerlayer.ConnectedUserData;
import forum.client.controllerlayer.ControllerHandler;
import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.panels.AddReplyDialog;
import forum.client.panels.MainPanel;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

/**
 * @author Tomer Heber
 *
 */
public class ForumTree {

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


	public ForumTree(final MainPanel container) {



		UIManager.put("Tree.collapsedIcon", new ImageIcon("./images/plus-8.png"));
		UIManager.put("Tree.expandedIcon", new ImageIcon("./images/minus-8.png"));

		try {
			pipe = ControllerHandlerFactory.getPipe();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/* Add an observer to the controller (The observable). */
		//m_pipe.addObserver(new ForumTreeObserver(this));

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
		/*
		for (TreeWillExpandListener a : m_tree.getTreeWillExpandListeners()) {
			m_tree.removeTreeWillExpandListener(a);
		}

		for (TreeExpansionListener a : m_tree.getTreeExpansionListeners()) {
			m_tree.removeTreeExpansionListener(a);
		}
		 */

		m_tree.addTreeWillExpandListener(new TreeWillExpandListener() {

			public void treeWillCollapse(TreeExpansionEvent event){}

			public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
				

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

		JScrollPane pane = new JScrollPane(tCurrentPanel);

		pane.setPreferredSize(new Dimension(610,635));

		// Adds the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		splitPane.setTopComponent(pane);

		splitPane.setBottomComponent(selected);

		splitPane.setDividerLocation(310); 
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
			/*			try {
				ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(this),
						EventType.MESSAGES_UPDATED);

			} 
			catch (IOException e)  {
			}
			 */			
		}

		public void notifyError(String errorMessage) {
			System.out.println("error");
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




			//			System.out.println("ffffffffffffffffffff");
			//		m_tree.setVisible(true);
			//				((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(this);



			container.switchToMessagesView();


			synchronized (this) {
				this.notifyAll();

				try {
					ControllerHandlerFactory.getPipe().deleteObserver(this);

					
					
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}



			//				m_tree.expandPath();


			//	if (this.getChildCount() != tPrevChildrenNum)
			//					((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(this);


		}
	}

	/**
	 * Receives an encoding describing the forum tree.<br>
	 * It decodes the description and returns the tree representation in a ForumCell instance.
	 * 
	 * @return The tree representing the forum.
	 */
	private ForumCell decodeView(String encodedView) {
		if (encodedView.startsWith("replysuccess"))
			return null;
		try {
			String[] tSplitted = encodedView.split("\n");
			String[] tRootAsStringArray = tSplitted[0].split("\t");
			ForumCell tRoot = new ForumCell(Long.parseLong(tRootAsStringArray[0]),
					tRootAsStringArray[1], tRootAsStringArray[2], tRootAsStringArray[3]);
			for (int i = 1; i < tSplitted.length; i++) {

				String[] tCurrentReplyAsStringArray = tSplitted[i].split("\t");
				ForumCell tCurrentReply = new ForumCell(Long.parseLong(tCurrentReplyAsStringArray[0]),
						tCurrentReplyAsStringArray[1], tCurrentReplyAsStringArray[2], tCurrentReplyAsStringArray[3]);
				int j = ++i;
				while (j < tSplitted.length && tSplitted[j].startsWith("\t")) {
					tCurrentReplyAsStringArray = tSplitted[j].split("\t");
					ForumCell tCurrentReplyToReply = new ForumCell(Long.parseLong(tCurrentReplyAsStringArray[1]),
							tCurrentReplyAsStringArray[2], tCurrentReplyAsStringArray[3], tCurrentReplyAsStringArray[4]);
					tCurrentReply.add(tCurrentReplyToReply);
					j++;
				}
				i = j - 1;
				tRoot.add(tCurrentReply);
			}
			return tRoot;
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
	public void modifyMessage(final String newContent, final JButton button) {
		button.setEnabled(false);
		pool.execute(new Runnable() {

			public void run() {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_tree.getSelectionPath().getLastPathComponent();
				ForumCell cell = (ForumCell) node.getUserObject();				
				pipe.modifyMessage(cell.getId(),newContent,button);				
			}
		});
	}

	/**
	 * Replies to the selected message.
	 */
	public void replyToMessage(final JButton button) {
		button.setEnabled(false);
		pool.execute(new Runnable() {

			public void run() {
				MessageTreeNode node = (MessageTreeNode)m_tree.getSelectionPath().getLastPathComponent();
				ForumCell cell = (ForumCell) node.getUserObject();				
				AddReplyDialog tReplyDialog = 
					new AddReplyDialog(container.getConnectedUser().getID(), cell.getId(), button);
				try {
					ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(tReplyDialog), EventType.MESSAGES_UPDATED);
					tReplyDialog.setVisible(true);

					synchronized (node) {
						ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(node),
								EventType.MESSAGES_UPDATED);
						ControllerHandlerFactory.getPipe().getNestedMessages(cell.getId(), m_tree);
						try {
							node.wait();
							((DefaultTreeModel)m_tree.getModel()).nodeStructureChanged(node);
						} catch (InterruptedException e) {
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				//				pipe.addReplyToMessage(container.getConnectedUser().getID(), cell.getId(),"",button);					
			}
		});			
	}

	/**
	 * Deletes the selected message.
	 */
	public void deleteMessage(final JButton button) {
		button.setEnabled(false);
		pool.execute(new Runnable() {

			@Override
			public void run() {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_tree.getSelectionPath().getLastPathComponent();
				ForumCell cell = (ForumCell) node.getUserObject();
				pipe.deleteMessage(cell.getId(),button);			
			}
		});		
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
