package forum.client.panels;


import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import forum.client.controllerlayer.*;


import forum.client.ui.ForumCell;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

public class MessagesTree extends JPanel implements TreeSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1675210784225688329L;

	private JTextArea messageText;
	private JTree tree;
	private DefaultTreeModel treeModel;

	private long fatherMessageID;


	/**
	 * A pipe interface to communicate with the controller layer.
	 */
	private ControllerHandler pipe;

	/**
	 * A thread pool that is used to initiate operations in the controller layer.
	 */
	private ExecutorService m_pool = Executors.newCachedThreadPool();


	public MessagesTree(String threadTopic, long fatherMessageID) {
		super(new GridLayout(1,0));

		this.fatherMessageID = fatherMessageID;

		this.tree = new JTree();
		this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// adds new selection listener
		tree.addTreeSelectionListener(this);

		// Creates a scroll pane and adds the tree to it.
		JScrollPane treeScrollView = new JScrollPane(tree);

		// Creates a messages view area
		this.messageText = new JTextArea();
		this.messageText.setEditable(false);

		JScrollPane tMessageScrollView = new JScrollPane(this.messageText);

		tMessageScrollView.setPreferredSize(new Dimension(20, 100));

		JButton tReply = new JButton();
		tReply.setText("Reply");
		JButton tDelete = new JButton();
		tDelete.setText("Delete");
		JButton tModify = new JButton();
		tModify.setText("Modify");
		Dimension tButtonDimension = new Dimension(110, 40);


		//		this.tree.setBackground(tDelete.getBackground());


		tReply.setPreferredSize(tButtonDimension);
		tDelete.setPreferredSize(tButtonDimension);
		tModify.setPreferredSize(tButtonDimension);

		JPanel tMessageViewPanel = new JPanel();


		GroupLayout tMessageViewLayout = new GroupLayout(tMessageViewPanel);
		tMessageViewPanel.setLayout(tMessageViewLayout);

		tMessageViewLayout.setAutoCreateGaps(true);

		tMessageViewPanel.add(tMessageScrollView);
		tMessageViewPanel.add(tReply);
		tMessageViewPanel.add(tDelete);
		tMessageViewPanel.add(tModify);


		tMessageViewLayout.setHorizontalGroup(
				tMessageViewLayout.createParallelGroup()
				.addComponent(tMessageScrollView, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(tMessageViewLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(tReply, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(tDelete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(tModify, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())						
		);
		tMessageViewLayout.setVerticalGroup(
				tMessageViewLayout.createSequentialGroup()
				.addComponent(tMessageScrollView, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(tMessageViewLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(tReply, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(tDelete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(tModify, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		);




		// Adds the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeScrollView);

		splitPane.setBottomComponent(tMessageViewPanel);

		Dimension minimumSize = new Dimension(100, 100);
		treeScrollView.setMinimumSize(minimumSize);
		tMessageScrollView.setMinimumSize(new Dimension(100, 200));
		splitPane.setDividerLocation(150); 
		splitPane.setPreferredSize(new Dimension(500, 300));

		//Add the split pane to the messages view panel.
		this.add(splitPane);


		this.treeModel = null;
		this.tree.setModel(this.treeModel);

		MessageTreeNode root = new MessageTreeNode(null, this.fatherMessageID);


		try {
			pipe = ControllerHandlerFactory.getPipe();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		if (this.fatherMessageID != -1) {
			this.pipe.addObserver(new GUIObserver(root), EventType.MESSAGES_UPDATED);
			this.pipe.getNestedMessages(this.fatherMessageID, this);
		}
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

		@SuppressWarnings("unchecked")
		public void refreshForum(String encodedView) {
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


			/*			this.removeAllChildren();
			Enumeration<MessageTreeNode> tChildren = (Enumeration<MessageTreeNode>)rootNode.children();
			while (tChildren.hasMoreElements())
				this.add(tChildren.nextElement());
			 */		

		}
	}

	/**
	 * Implements the tree selection interface
	 * 
	 * @see
	 * 		TreeSelectionListener#valueChanged(TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = 
			(MessageTreeNode)this.tree.getLastSelectedPathComponent();
		if (node == null) return;

		if (!node.isLeaf()) {
			ForumCell nodeInfo = (ForumCell)node.getUserObject();
			this.pipe.getNestedMessages(nodeInfo.getId(), this);
		}
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	/*	private static void createAndShowGUI() {
		//if (!useSystemLookAndFeel) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}
		//}

		//Create and set up the window.
		JFrame frame = new JFrame("TreeDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add content to the window.
		frame.add(new MessagesTree("test message" ));

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	 */
	/**
	 * 
	 * @return The forum tree GUI component. 
	 */
	public Component getForumTreeUI() {
		return this;
	}

	/*
	public void refreshForum(String encodedView) {	
		ForumCell rootCell = decodeView(encodedView);
		if (rootCell == null) {
			this.notifyError("Can't refresh messages view");
			if (this.treeModel == null)
				this.treeModel = new DefaultTreeModel(null);
			return;
		}

		MessageTreeNode rootNode = new MessageTreeNode(rootCell); 

		Stack<MessageTreeNode> stack = new Stack<MessageTreeNode>();
		stack.add(rootNode);

		while (!stack.isEmpty()) {
			MessageTreeNode node = stack.pop();
			ForumCell cell = (ForumCell)(node.getUserObject());
			for (ForumCell sonCell : cell.getSons()) {
				MessageTreeNode sonNode = new MessageTreeNode(sonCell);
				node.add(sonNode);
				stack.add(sonNode);
			}		
		}

		if (this.treeModel == null) {
			this.treeModel = new DefaultTreeModel(rootNode);

			for (int i = 0; i < this.tree.getRowCount(); i++)
				this.tree.expandRow(i);
		}
	}

	public void notifyError(String errorMessage) {
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame,
				errorMessage,
				"Operation failed",
				JOptionPane.WARNING_MESSAGE);
	}
	 */
	/**
	 * Receives an encoding describing a message and its replies.<br>
	 * It decodes the description and returns the tree representation in a ForumCell instance.
	 * 
	 * encoded view is of the form <message-data>
	 * 							   <message-reply1>
	 * 									<message-reply11>
	 * 									<message-reply12>
	 * 									<message-reply13>
	 * 							   <message-reply2>
	 * 									<message-reply21>
	 * note that only two levels of nesting are currently supported
	 * 
	 * @return The tree representing the message and its replies.
	 */
	private ForumCell decodeView(String encodedView) {
		try {
			String[] tSplitted = encodedView.split("\n");
			String[] tRootAsStringArray = tSplitted[0].split("\t");
			ForumCell tRoot = new ForumCell(Long.parseLong(tRootAsStringArray[0]),
					tRootAsStringArray[1], tRootAsStringArray[2], tRootAsStringArray[3]);
			for (int i = 1; i < tSplitted.length; i++) {
				String[] tCurrentReplyAsStringArray = tSplitted[i].split("\t");
				ForumCell tCurrentReply = new ForumCell(Long.parseLong(tCurrentReplyAsStringArray[0]),
						tCurrentReplyAsStringArray[1], tCurrentReplyAsStringArray[2], tCurrentReplyAsStringArray[3]);
				for (int j = ++i; j < tSplitted.length && tSplitted[j].startsWith("\t"); j++, i++) {
					tCurrentReplyAsStringArray = tSplitted[i].split("\t");
					ForumCell tCurrentReplyToReply = new ForumCell(Long.parseLong(tCurrentReplyAsStringArray[0]),
							tCurrentReplyAsStringArray[1], tCurrentReplyAsStringArray[2], tCurrentReplyAsStringArray[3]);
					tCurrentReply.add(tCurrentReplyToReply);
				}
				tRoot.add(tCurrentReply);
			}
			return tRoot;
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
	public void modifyMessage(final String newContent, final JButton button) {
		button.setEnabled(false);
		m_pool.execute(new Runnable() {

			@Override
			public void run() {
				MessageTreeNode node = (MessageTreeNode)tree.getSelectionPath().getLastPathComponent();
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
				MessageTreeNode node = (MessageTreeNode)tree.getSelectionPath().getLastPathComponent();
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
				MessageTreeNode node = (MessageTreeNode)tree.getSelectionPath().getLastPathComponent();
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
}
