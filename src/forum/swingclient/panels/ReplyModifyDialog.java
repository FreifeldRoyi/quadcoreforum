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

import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

/**
 * @author sepetnit
 *
 */
public class ReplyModifyDialog extends JDialog implements GUIHandler {

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
	private JLabel titleLabel;
	private JLabel contentLabel;
	private String topicType;
	private JButton ok;
	private JButton cancel;
	private boolean succeeded;

	// used in order to scroll to the created / updated subject / thread / message id
	private long createdOrUpdatedID;
	
	

	//	private JButton replyModifyButton;

	public ReplyModifyDialog(final long authorID, final long modifiedID, final String currentTitle, 
			final String currentContent, final JButton replyModifyButton) {
		super();
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
				try {
					if (title.getText().equals("")) {
						JOptionPane.showMessageDialog(ReplyModifyDialog.this, "message title cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (content.getText().equals("")) {
						JOptionPane.showMessageDialog(ReplyModifyDialog.this, "message content cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(ReplyModifyDialog.this),
							EventType.MESSAGES_UPDATED);
					ControllerHandlerFactory.getPipe().modifyMessage(authorID, modifiedID,
							title.getText().trim(), content.getText().trim(), replyModifyButton);
				}
				catch (IOException e) {
					// TODO: handle the exception
				}
			}

		});	

	}

	public ReplyModifyDialog(final long authorID, final long repliedID,
			final JButton replyModifyButton) {
		super();
		initializeGUIContent(authorID, repliedID, replyModifyButton);		
		arrangeLayout();
		this.ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("OK WAS KLICKEDDDDDDDDDDDDDDDDDD");
				try {
					if (title.getText().equals("")) {
						JOptionPane.showMessageDialog(ReplyModifyDialog.this, "message title cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (content.getText().equals("")) {
						JOptionPane.showMessageDialog(ReplyModifyDialog.this, "message content cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(ReplyModifyDialog.this),
							EventType.MESSAGES_UPDATED);
					ControllerHandlerFactory.getPipe().addReplyToMessage(authorID,
							repliedID, title.getText().trim(), content.getText().trim(), replyModifyButton);
				} catch (IOException e) {
					// TODO: handle the exception
				}
			}
		});	
	}

	public ReplyModifyDialog(final long authorID, final long fatherID, String topicType,
			final JButton replyModifyButton) {
		super();
		this.setTitle("Add new subject");
		initializeGUIContent(authorID, fatherID, replyModifyButton);
		this.topicType = topicType;
		arrangeLayout();
		if (topicType.equals("subject")) {
			titleLabel.setText("name");
			contentLabel.setText("description");
		}

		this.ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (topic != null && topic.isVisible() && topic.getText().equals("")){
						JOptionPane.showMessageDialog(ReplyModifyDialog.this, "thread topic cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (title.getText().equals("")) {
						JOptionPane.showMessageDialog(ReplyModifyDialog.this,
								ReplyModifyDialog.this.topicType + " title cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (content.getText().equals("")) {
						JOptionPane.showMessageDialog(ReplyModifyDialog.this, 
								ReplyModifyDialog.this.topicType + " content cannot be empty.", "error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (ReplyModifyDialog.this.topicType.equals("subject")) {
						ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(ReplyModifyDialog.this),
								EventType.SUBJECTS_UPDATED);
						ControllerHandlerFactory.getPipe().addNewSubject(authorID, fatherID,
								title.getText().trim(), content.getText().trim(), replyModifyButton);
						
						
						
					}
					else {
						ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(ReplyModifyDialog.this),
								EventType.THREADS_UPDATED);
						ControllerHandlerFactory.getPipe().addNewThread(authorID, fatherID, topic.getText(),
								title.getText().trim(), content.getText().trim(), replyModifyButton);
					}
				} catch (IOException e) {
					// TODO: handle the exception
				}
			}
		});	
	}


	private void initializeGUIContent(long authorID, long replyModifiedID, JButton replyModifyButton) {
		this.title = new JTextField();
		
		this.title.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		this.content = new JTextArea();
		//		this.content.setAutoscrolls(true);
		this.contentPane = new JScrollPane(content);
		this.ok = new JButton();
		this.cancel = new JButton();
		this.topicType = "message";
		
		this.content.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		this.setPreferredSize(new Dimension(400, 300));
		this.setMinimumSize(new Dimension(400, 300));


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
		JLabel topicLabel = null;
		if (topicType.equals("thread")) {
			topic = new JTextField();
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
																		.addComponent(this.cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addGap(10, 10, 10)
																		.addComponent(this.ok, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
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
		try {
			ControllerHandlerFactory.getPipe().deleteObserver(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(this, errorMessage, "error occured", JOptionPane.ERROR_MESSAGE);
	}

	public boolean shouldUpdateGUI() {
		return succeeded;
	}
	
	public long getChangedID() {
		return createdOrUpdatedID;
	}

	public void refreshForum(String encodedView) {
		try {
			ControllerHandlerFactory.getPipe().deleteObserver(this);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("subjects encodedview = \n"+ encodedView);
		
		String tLastMessageWord = null;
		if (encodedView.startsWith("replysuccess") || encodedView.startsWith("addsubjectsuccess") ||
				encodedView.startsWith("addthreadsuccess")) {
			tLastMessageWord = "added";
			this.createdOrUpdatedID = Long.parseLong(encodedView.split("\t")[1]); // the id of the added message
			System.out.println("Created idddddddddddddddddddddddddddddddddddd is " + this.createdOrUpdatedID);
		}
		else if (encodedView.startsWith("modifysuccess"))
			tLastMessageWord = "modified";
		else if (!encodedView.startsWith("searchresult")){
			JOptionPane.showMessageDialog(this, "error occurredd!!", "error", JOptionPane.ERROR_MESSAGE);
			System.out.println("\n\nencoded:" + encodedView + "\n\n---------------------");
		}
		else
			return;

		if (tLastMessageWord != null) {
			JOptionPane.showMessageDialog(this, "The " + topicType + " was " +
					tLastMessageWord + " successfully!", "success", JOptionPane.INFORMATION_MESSAGE);

			this.succeeded = true;
			setVisible(false);
		}
		else {
			System.out.println("erorrrrrrrrrrrrrrrrrrrrrrrr");
			// TODO: handle error cases
		}
	}
}