/**
 * 
 */
package forum.client.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;

import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;

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

	private JButton ok;
	private JButton cancel;
	private boolean succeeded;
	
	
//	private JButton replyModifyButton;

	public ReplyModifyDialog(final long authorID, final long modifiedID, final String currentTitle, 
			final String currentContent, final JButton replyModifyButton) {
		super();
		initializeGUIContent(authorID, modifiedID, replyModifyButton);
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
					ControllerHandlerFactory.getPipe().addReplyToMessage(authorID,
							repliedID, title.getText().trim(), content.getText().trim(), replyModifyButton);
				} catch (IOException e) {
					// TODO: handle the exception
				}
			}
		});	
	}
	
	private void initializeGUIContent(long authorID, long replyModifiedID, JButton replyModifyButton) {
		this.title = new JTextField();
		this.content = new JTextArea();
//		this.content.setAutoscrolls(true);
		this.contentPane = new JScrollPane(content);
		this.ok = new JButton();
		this.cancel = new JButton();
		
		this.content.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setPreferredSize(new Dimension(400, 300));
		this.setMinimumSize(new Dimension(400, 300));


		this.title.setPreferredSize(new Dimension(200, 30));
		this.contentPane.setPreferredSize(new Dimension(200, 200));
		this.ok.setPreferredSize(new Dimension(100, 40));
		this.cancel.setPreferredSize(new Dimension(100, 40));


		this.ok.setText("ok");
		this.cancel.setText("cancel");

		GroupLayout tLayout = new GroupLayout(this.getContentPane());

		JLabel tTitle = new JLabel("title:");
		JLabel tContent = new JLabel("content:");
		
		this.getContentPane().setLayout(tLayout);
		tLayout.setHorizontalGroup(tLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tLayout.createSequentialGroup()
						.addGap(10, 10, 10)

						.addComponent(tTitle, 100, 100, 100)
						.addGap(10, 10, 10))
						.addGroup(tLayout.createSequentialGroup()
								.addGap(10, 10, 10)

								.addComponent(this.title, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addGap(10, 10, 10))

								.addGroup(tLayout.createSequentialGroup()
										.addGap(10, 10, 10)

										.addComponent(tContent, 100, 100, 100)
										.addGap(10, 10, 10))

										.addGroup(tLayout.createSequentialGroup() 
												.addGap(10, 10, 10)
												.addComponent(this.contentPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
												.addGap(10, 10, 10)

										)
										.addGroup(tLayout.createSequentialGroup()
												.addGap(10, 10, 10)
												.addComponent(this.cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addGap(5, 10, Short.MAX_VALUE)
												.addComponent(this.ok, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addGap(10, 10, 10)

										));
		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addContainerGap(5, 5)
				.addComponent(tTitle, 10, 10, 10)
				.addGap(10, 10, 10)

				.addComponent(this.title, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(10, 10, 10)
				.addComponent(tContent, 20, 20, 20)
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

		this.setModal(true);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});

	}

	public void notifyError(String errorMessage) {
		try {
			ControllerHandlerFactory.getPipe().deleteObserver(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(this, "error occurred!", "error", JOptionPane.ERROR_MESSAGE);
	}

	public boolean shouldUpdateGUI() {
		return succeeded;
	}
	
	public void refreshForum(String encodedView) {
		String tLastMessageWord = null;
		if (encodedView.equals("replysuccess"))
			tLastMessageWord = "added";
		else if (encodedView.equals("modifysuccess"))
			tLastMessageWord = "modified";
		else {
			JOptionPane.showMessageDialog(this, "error occurredrrr!", "error", JOptionPane.ERROR_MESSAGE);
			System.out.println(encodedView);
		}

		if (tLastMessageWord != null) {
			JOptionPane.showMessageDialog(this, "Your reply has been " +
					tLastMessageWord + " successfully!", "success", JOptionPane.INFORMATION_MESSAGE);
			try {
				ControllerHandlerFactory.getPipe().deleteObserver(this);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.succeeded = true;
			setVisible(false);
		}
		else {
			try {
				ControllerHandlerFactory.getPipe().deleteObserver(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("erorrrrrrrrrrrrrrrrrrrrrrrr");
			// TODO: handle error cases
		}
	}
}
