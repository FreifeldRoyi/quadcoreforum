package forum.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import forum.client.controllerlayer.ControllerHandler;
import forum.client.controllerlayer.ControllerHandlerFactory;

/**
 * @author Tomer Heber
 *
 */
public class ForumTree implements ForumTreeHandler {
	
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
	private ControllerHandler m_pipe;
	
	/**
	 * A thread pool that is used to initiate operations in the controller layer.
	 */
	private ExecutorService m_pool = Executors.newCachedThreadPool();
	
	public ForumTree() {				
		UIManager.put("Tree.collapsedIcon", new ImageIcon("./images/plus-8.png"));
		UIManager.put("Tree.expandedIcon", new ImageIcon("./images/minus-8.png"));
		
		m_pipe = ControllerHandlerFactory.getPipe();
		/* Add an observer to the controller (The observable). */
		m_pipe.addObserver(new ForumTreeObserver(this));
		
		m_tree = new JTree();
		m_tree.putClientProperty("JTree.lineStyle", "None");		

		refreshForum(m_pipe.getForumView());
		
		ForumTreeCellRenderer renderer = new ForumTreeCellRenderer(this);
		m_tree.setCellRenderer(renderer);
		m_tree.setCellEditor(new ForumTreeCellEditor(renderer));
		m_tree.setEditable(true);
		
		m_tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				BasicTreeUI ui = (BasicTreeUI) m_tree.getUI();
				ui.setLeftChildIndent(ui.getLeftChildIndent());
				ui.setRightChildIndent(ui.getRightChildIndent());
			}
		});
		
		m_panel = new JPanel();
		m_panel.setBackground(Color.WHITE);
		JScrollPane pane = new JScrollPane(m_tree);
		pane.setPreferredSize(new Dimension(610,435));
		m_panel.add(pane);
		
		m_panel.setPreferredSize(new Dimension(620,460));
	}
	
	/**
	 * 
	 * @return The forum tree GUI component. 
	 */
	public Component getForumTreeUI() {
		return m_panel;
	}
	
	@Override
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
	
	@Override
	public void NotifyError(String errorMessage) {
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
		ForumCell fc1 = new ForumCell(2,"fsdf","fsdfsd");
		ForumCell fc2 = new ForumCell(1,"fsdf","fsdfsd");
		ForumCell fc3 = new ForumCell(5,"fsfsdfsddf","fsdvccxvfsd");
		ForumCell fc4 = new ForumCell(7,"fsfsdfsddf","fsdvccxvfsd");
		ForumCell fc5= new ForumCell(0,"bcvfsddf","fsdvccxvfsdcxvcx");

		fc3.add(fc4);
		fc2.add(fc3);
		fc1.add(fc2);
		fc1.add(fc5);
		
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
				m_pipe.modifyMessage(cell.getId(),newContent,button);				
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
				m_pipe.addReplyToMessage(cell.getId(),"",button);					
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
				m_pipe.deleteMessage(cell.getId(),button);			
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
				m_pipe.addNewMessage(button);				
			}
		});
	}
	
	/**
	 * This is for testing purposes only! <br>
	 * Delete when done testing!
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ForumTree tree = new ForumTree();
		
		JFrame frame = new JFrame("test");
		frame.setSize(new Dimension(640,480));
		
		frame.getContentPane().add(tree.getForumTreeUI());	
		frame.setVisible(true);		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
