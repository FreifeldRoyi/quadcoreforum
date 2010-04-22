package forum.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import forum.client.controllerlayer.ControllerHandler;
import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;

/**
 * @author Tomer Heber
 *
 */
public class ForumTree implements GUIHandler {

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


	private long fatherMessageID;



	/**
	 * A thread pool that is used to initiate operations in the controller layer.
	 */
	private ExecutorService m_pool = Executors.newCachedThreadPool();

	public ForumTree(long fatherMessageID) {				
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

		m_tree = new JTree();

		m_tree.putClientProperty("JTree.lineStyle", "None");		

		refreshForum(pipe.getForumView());
		SelectedForumTreeCellPanel selected = new SelectedForumTreeCellPanel(this);

		ForumTreeCellRenderer renderer = new ForumTreeCellRenderer(this, selected);
		m_tree.setCellRenderer(renderer);
		m_tree.setCellEditor(new ForumTreeCellEditor(renderer));
		m_tree.setEditable(false);

		m_tree.addTreeSelectionListener(new TreeSelectionListener() {			
			public void valueChanged(TreeSelectionEvent e) {
				BasicTreeUI ui = (BasicTreeUI) m_tree.getUI();
				ui.setLeftChildIndent(ui.getLeftChildIndent());
				ui.setRightChildIndent(ui.getRightChildIndent());
			}
		});

		m_panel = new JPanel();
		m_panel.setBackground(Color.WHITE);

		m_panel.setLayout(new BoxLayout(m_panel, BoxLayout.Y_AXIS));
		JScrollPane pane = new JScrollPane(m_tree);
		//pane.setPreferredSize(new Dimension(610,435));
		//m_panel.add(pane);


		// Adds the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		splitPane.setTopComponent(pane);


		splitPane.setBottomComponent(selected);


		splitPane.setDividerLocation(150); 
		splitPane.setPreferredSize(new Dimension(500, 300));

		m_panel.add(splitPane);


		this.setRootMessage(fatherMessageID);


		//		m_panel.setPreferredSize(new Dimension(620,460));
	}

	public void setRootMessage(final long fatherMessageID) {
		this.fatherMessageID = fatherMessageID;
		MessageTreeNode rootNode = new MessageTreeNode(null, this.fatherMessageID);

		if (this.fatherMessageID != -1) {
			System.out.println(fatherMessageID);
			this.pipe.addObserver(new GUIObserver(rootNode), EventType.MESSAGES_UPDATED);
			this.pipe.getNestedMessages(this.fatherMessageID, this.m_panel);
		}
		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		m_tree.setModel(model);	
	
		for (int i = 0; i < m_tree.getRowCount(); i++) {
			m_tree.expandRow(i);
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
			try {
				ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(this), EventType.MESSAGES_UPDATED);
			} 
			catch (IOException e)  {			}
		}

		public void notifyError(String errorMessage) {

		}

		public void refreshForum(String encodedView) {
			System.out.println(encodedView);
			ForumCell rootCell = decodeView(encodedView);
			if (rootCell.getId() != id) return;
			//			MessageTreeNode rootNode = new MessageTreeNode(rootCell); 

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
		}
	}	














	public void refreshForum(String encodedView) {	
		ForumCell rootCell = decodeView(encodedView);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootCell); 

		Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
		stack.add(rootNode);

		while (!stack.isEmpty()) {
			DefaultMutableTreeNode node = stack.pop();
			ForumCell cell = (ForumCell)(node.getUserObject());
			for (ForumCell sonCell : cell.getSons()) {
				DefaultMutableTreeNode sonNode = new DefaultMutableTreeNode(sonCell);
				node.add(sonNode);
				stack.add(sonNode);
			}		
		}

		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		m_tree.setModel(model);	
		for (int i = 0; i < m_tree.getRowCount(); i++) {
			m_tree.expandRow(i);
		}

	}


	public void notifyError(String errorMessage) {
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame,
				errorMessage,
				"Operation failed.",
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Receives an encoding describing the forum tree.<br>
	 * It decodes the description and returns the tree representation in a ForumCell instance.
	 * 
	 * @return The tree representing the forum.
	 */
	private ForumCell decodeView(String encodedView) {
		// TODO implement the decoder (based on the encoding your write.
		//return null;
		// Delete the below line codes.
		ForumCell fc1 = new ForumCell(2,"fsdf","d","fsdfsd");
		ForumCell fc2 = new ForumCell(1,"fsdf","dd","fsdfsd");
		ForumCell fc3 = new ForumCell(5,"fsfsdfsddf","dd","fsdvccxvfsd");
		ForumCell fc4 = new ForumCell(7,"fsfsdfsddf","dd","fsdvccxvfsd");
		ForumCell fc5= new ForumCell(0,"bcvfsddf","dd","fsdvccxvfsdcxvcx");

		fc3.add(fc4);
		fc2.add(fc3);
		fc1.add(fc2);
		//	fc1.add(fc5);

		return fc1;
	}

	/**
	 * Modifies a message, and updates the forum accordingly.
	 * 
	 * @param newContent The new content of the message.
	 */
	public void modifyMessage(final String newContent, final JButton button) {
		button.setEnabled(false);
		m_pool.execute(new Runnable() {

			@Override
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
		m_pool.execute(new Runnable() {

			@Override
			public void run() {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_tree.getSelectionPath().getLastPathComponent();
				ForumCell cell = (ForumCell) node.getUserObject();				
				pipe.addReplyToMessage(cell.getId(),"",button);					
			}
		});			
	}

	/**
	 * Deletes the selected message.
	 */
	public void deleteMessage(final JButton button) {
		button.setEnabled(false);
		m_pool.execute(new Runnable() {

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
		m_pool.execute(new Runnable() {

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
