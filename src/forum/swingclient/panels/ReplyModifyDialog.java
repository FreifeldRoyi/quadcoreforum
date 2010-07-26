/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.*;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

/**
 * @author sepetnit
 *
 */
public class ReplyModifyDialog extends JDialog implements GUIHandler, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5390910284724195205L;


	//	private long authorID;
	//	private long replyModifiedID;
	private JScrollPane contentPane;
	private JTextField title;
	private JTextArea content;
	private JTextField topic;
	private JLabel topicLabel;
	private JLabel titleLabel;
	private JLabel contentLabel;
	private String topicType;
	private JButton ok;
	private JButton cancel;
	private boolean succeeded;
	
	private boolean errorOccured;

	// used in order to scroll to the created / updated subject / thread / message id
	private long createdOrUpdatedID;

	private ControllerHandler controller;

	// KeyListener implementation
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (!content.isFocusOwner() && arg0.getKeyChar() == KeyEvent.VK_ENTER)
			ok.doClick();
		else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE)
			cancel.doClick();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {
		clearLabels();
	}
	// end of KeyListener implementation

	

	public ReplyModifyDialog(final long authorID, final long modifiedID, final String currentTitle, 
			final String currentContent, final JButton replyModifyButton) {
		super();
		errorOccured = false;
		try {
			controller = ControllerHandlerFactory.getPipe();
		}
		catch (IOException e) {
			// TODO: handle the exception
		}

		this.setTitle("Modify message");

		initializeGUIContent(authorID, modifiedID, replyModifyButton);
		arrangeLayout();
		this.succeeded = false;
		this.title.setText(currentTitle);	
		this.content.setText(currentContent);
		this.ok.setEnabled(false);

		this.title.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {
				ok.setEnabled(true);
			}			
		});

		this.content.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {
				ok.setEnabled(true);
			}
		});	

		this.ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (title.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(ReplyModifyDialog.this, "message title cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (content.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(ReplyModifyDialog.this, "message content cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				controller.addObserver(new GUIObserver(ReplyModifyDialog.this),
						EventType.MESSAGES_UPDATED);
				controller.modifyMessage(authorID, modifiedID,
						title.getText().trim(), content.getText().trim(), replyModifyButton);
			}
		});	

	}

	public ReplyModifyDialog(final long authorID, final long repliedID,
			final JButton replyModifyButton) {
		super();
		errorOccured = false;

		try {
			controller = ControllerHandlerFactory.getPipe();
		}
		catch (IOException e) {
			// TODO: handle the exception
		}

		this.setTitle("Reply to message");
		initializeGUIContent(authorID, repliedID, replyModifyButton);		
		arrangeLayout();
		this.ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (title.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(ReplyModifyDialog.this, "message title cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (content.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(ReplyModifyDialog.this, "message content cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				controller.addObserver(new GUIObserver(ReplyModifyDialog.this),
						EventType.MESSAGES_UPDATED);
				controller.addReplyToMessage(authorID,
						repliedID, title.getText().trim(), content.getText().trim(), replyModifyButton);
			}
		});	
	}

	public ReplyModifyDialog(final long authorID, final long fatherID, String existingName, String existingDescription, String topicType,
			final JButton replyModifyButton) {
		super();
		errorOccured = false;

		try {
			controller = ControllerHandlerFactory.getPipe();
		}
		catch (IOException e) {
			// TODO: handle the exception
		}

		if (topicType.equals("subject"))
			this.setTitle("Add new subject");
		else if (topicType.equals("thread"))
			this.setTitle("Open new thread");
		else
			this.setTitle("Modify subject");

		initializeGUIContent(authorID, fatherID, replyModifyButton);
		this.title.setText(existingName);
		this.content.setText(existingDescription);

		this.topicType = topicType;
		arrangeLayout();
		if (topicType.contains("subject")) {
			titleLabel.setText("name");
			contentLabel.setText("description");
		}

		this.ok.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String tPrefix = "";
				if (ReplyModifyDialog.this.topicType.contains("subject"))
					tPrefix = "subject";
				else if (ReplyModifyDialog.this.topicType.contains("message"))
					tPrefix = "message";
				else
					tPrefix = "thread";
				
				if (topic != null && topic.isVisible() && topic.getText().trim().length() == 0){
					JOptionPane.showMessageDialog(ReplyModifyDialog.this, "thread topic cannot be empty.",
							"error", JOptionPane.ERROR_MESSAGE);
					makeLabelNoticeble(topicLabel, topic);

					return;
				}
				if (title.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(ReplyModifyDialog.this,
							tPrefix + " " + titleLabel.getText() + 
							" cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
					makeLabelNoticeble(titleLabel, title);

					return;
				}
				if (content.getText().trim().length() == 0) {
					JOptionPane.showMessageDialog(ReplyModifyDialog.this, 
							tPrefix + " " + contentLabel.getText() + " cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
					makeLabelNoticeble(contentLabel, content);

					return;
				}
				else if (contentLabel.getText().toLowerCase().equals("description")) {
					if (content.getText().trim().split("\n").length > 3) {
						JOptionPane.showMessageDialog(ReplyModifyDialog.this, 
								"subject" + " " + contentLabel.getText() + " is restricted to 3 lines", "error", JOptionPane.ERROR_MESSAGE);
						makeLabelNoticeble(contentLabel, content);
						return;
					}
				}
				if (ReplyModifyDialog.this.topicType.equals("subject")) {
					controller.addObserver(new GUIObserver(ReplyModifyDialog.this),
							EventType.SUBJECTS_UPDATED);
					controller.addNewSubject(authorID, fatherID,
							title.getText().trim(), content.getText().trim(), replyModifyButton);						
				}
				else if (ReplyModifyDialog.this.topicType.equals("modifysubject")) {
					controller.addObserver(new GUIObserver(ReplyModifyDialog.this),
							EventType.SUBJECTS_UPDATED);
					controller.modifySubject(authorID, fatherID,
							title.getText().trim(), content.getText().trim(), replyModifyButton);					
				}
				else {
					controller.addObserver(new GUIObserver(ReplyModifyDialog.this),
							EventType.THREADS_UPDATED);
					controller.addNewThread(authorID, fatherID, topic.getText(),
							title.getText().trim(), content.getText().trim(), replyModifyButton);
				}
			}
		});	
	}

	private void makeLabelNoticeble(JLabel label, JTextField field) {
		label.setForeground(Color.RED);
		field.selectAll();
		field.grabFocus();
	}
	
	private void makeLabelNoticeble(JLabel label, JTextArea area) {
		label.setForeground(Color.RED);
		area.selectAll();
		area.grabFocus();
	}

	
	private void clearLabels() {
		this.contentLabel.setForeground(Color.BLACK);
		this.titleLabel.setForeground(Color.BLACK);
		if (topicLabel != null && topicLabel.isVisible())
			topicLabel.setForeground(Color.BLACK);
	}

	private void initializeGUIContent(long authorID, long replyModifiedID, JButton replyModifyButton) {
		this.title = new JRestrictedLengthTextField(40, 40, true, false);
		
		this.title.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		
		
		
		this.content = new JRestrictedLengthTextArea(60, false);
		
		//		this.content.setAutoscrolls(true);
		this.contentPane = new JScrollPane(content);
		
		
		this.ok = new JButton();
		this.cancel = new JButton();
		this.topicType = "message";

		this.content.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		
		
		this.setPreferredSize(new Dimension(500, 300));
		this.setMinimumSize(new Dimension(500, 300));


		this.title.setPreferredSize(new Dimension(200, 30));
		this.contentPane.setPreferredSize(new Dimension(200, 200));
		this.ok.setPreferredSize(new Dimension(100, 40));
		this.cancel.setPreferredSize(new Dimension(100, 40));


		this.ok.setText("ok");
		this.cancel.setText("cancel");

		titleLabel = new JLabel("title:");
		contentLabel = new JLabel("content:");

		this.setModal(true);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
	}


	private void arrangeLayout() {
		topicLabel = null;
		title.addKeyListener(this);
		content.addKeyListener(this);
		if (topicType.equals("thread")) {
			topic = new JRestrictedLengthTextField(40, 40, true, false);
			topic.addKeyListener(this);
			this.topic.setPreferredSize(new Dimension(200, 30));
			topicLabel = new JLabel("topic:");
		}
		GroupLayout tLayout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(tLayout);


		Box box = Box.createHorizontalBox();

		tLayout.setHorizontalGroup(tLayout.createParallelGroup(GroupLayout.Alignment.LEADING)


				.addGroup(tLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent((topicType.equals("thread")? topicLabel : box), 100, 100, 100)
						.addGap(10, 10, 10))
						.addGroup(tLayout.createSequentialGroup()
								.addGap(10, 10, 10)
								.addComponent((topicType.equals("thread")? this.topic : box), 
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
										.addGap(10, 10, 10))
										.addGroup(tLayout.createSequentialGroup()
												.addGap(10, 10, 10)
												.addComponent(titleLabel, 100, 100, 100)
												.addGap(10, 10, 10))
												.addGroup(tLayout.createSequentialGroup()
														.addGap(10, 10, 10)

														.addComponent(this.title, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
														.addGap(10, 10, 10))

														.addGroup(tLayout.createSequentialGroup()
																.addGap(10, 10, 10)

																.addComponent(contentLabel, 100, 100, 100)
																.addGap(10, 10, 10))

																.addGroup(tLayout.createSequentialGroup() 
																		.addGap(10, 10, 10)
																		.addComponent(this.contentPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
																		.addGap(10, 10, 10)

																)
																.addGroup(tLayout.createSequentialGroup()
																		.addGap(10, 10, Short.MAX_VALUE)
																		.addComponent(this.ok, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addGap(10, 10, 10)
																		.addComponent(this.cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addGap(10, 10, 10)

																));
		tLayout.setVerticalGroup(tLayout.createSequentialGroup()




				.addContainerGap(5, 5)

				.addComponent((topicType.equals("thread")? topicLabel : box), 10,10,10)

				.addGap(10, 10, 10)

				.addComponent((topicType.equals("thread")? topic : box), 
						GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)


						.addComponent(titleLabel, 10, 10, 10)
						.addGap(10, 10, 10)

						.addComponent(this.title, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10)
						.addComponent(contentLabel, 20, 20, 20)
						.addGap(10, 10, 10)

						.addComponent(this.contentPane, 50, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10)
						.addGroup(tLayout.createParallelGroup()
								.addComponent(this.cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.ok, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addContainerGap(10, 10));

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int Y = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, Y);
	}

	public void notifyError(String errorMessage) {
		this.controller.deleteObserver(this);
		JOptionPane.showMessageDialog(this, errorMessage, "error occured", JOptionPane.ERROR_MESSAGE);
		this.errorOccured = true;
		this.cancel.doClick();
	}

	public boolean shouldUpdateGUI() {
		return succeeded;
	}

	public boolean errorOcurred() {
		return errorOccured;
	}

	
	public long getChangedID() {
		return createdOrUpdatedID;
	}

	public void refreshForum(String encodedView) {

		String tLastMessageWord = null;
		if (encodedView.startsWith("replysuccess") || encodedView.startsWith("addsubjectsuccess") ||
				encodedView.startsWith("addthreadsuccess")) {
			controller.deleteObserver(this);

			tLastMessageWord = "added";
			this.createdOrUpdatedID = Long.parseLong(encodedView.split("\t")[1]); // the id of the added message
		}
		else if (encodedView.startsWith("modifysuccess") || (encodedView.startsWith("subjectupdatesuccess"))) {
			controller.deleteObserver(this);

			tLastMessageWord = "modified";
			this.createdOrUpdatedID = Long.parseLong(encodedView.split("\t")[1]); // the id of the added message
		}
		else if (!encodedView.startsWith("search") && !encodedView.startsWith("getpath")){
			controller.deleteObserver(this);
			JOptionPane.showMessageDialog(this, "error occurredd!!", "error", JOptionPane.ERROR_MESSAGE);
		}
		else
			return;

		if (tLastMessageWord != null) {
			if (topicType.contains("subject")) topicType = "subject";
			JOptionPane.showMessageDialog(this, "The " + topicType + " was " +
					tLastMessageWord + " successfully!", "success", JOptionPane.INFORMATION_MESSAGE);

			this.succeeded = true;
			setVisible(false);
		}
		else {
			// TODO: handle error cases
		}
	}
}
