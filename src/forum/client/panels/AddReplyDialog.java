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
import java.io.IOException;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;

import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.events.GUIHandler;

/**
 * @author sepetnit
 *
 */
public class AddReplyDialog extends JDialog implements GUIHandler {

	private long authorID;
	private long replyToID;
	private JTextField title;
	private JTextArea content;

	private JButton ok;
	private JButton cancel;

	private JButton replyButton;

	public AddReplyDialog(long authorID, long replyTo, JButton replyButton) {
		super();
		this.authorID = authorID;
		this.replyToID = replyTo;
		this.replyButton = replyButton;
		this.title = new JTextField();
		this.content = new JTextArea();
		this.content.setAutoscrolls(true);
		this.ok = new JButton();
		this.cancel = new JButton();

		this.content.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setPreferredSize(new Dimension(400, 300));
		this.setMinimumSize(new Dimension(400, 300));


		this.title.setPreferredSize(new Dimension(200, 30));
		this.content.setPreferredSize(new Dimension(200, 200));
		this.ok.setPreferredSize(new Dimension(100, 40));
		this.cancel.setPreferredSize(new Dimension(100, 40));


		this.ok.setText("ok");
		this.cancel.setText("cancel");

		GroupLayout tLayout = new GroupLayout(this.getContentPane());

		JLabel tTitle = new JLabel("title:");
		JLabel tContent = new JLabel("content:");


		this.cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}

		});

		this.ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					
					ControllerHandlerFactory.getPipe().addReplyToMessage(AddReplyDialog.this.authorID,
							replyToID, title.getText(), content.getText(), AddReplyDialog.this.replyButton);
				} catch (IOException e) {
					// TODO: handle the exception
				}
			}

		});


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
												.addComponent(this.content, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
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

				.addComponent(this.content, 50, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
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

	}


	public void notifyError(String errorMessage) {
		this.dispose();
	}

	public void refreshForum(String encodedView) {
		if (encodedView.equals("replysuccess")) {
			JOptionPane.showMessageDialog(this, "Your reply has been successfully added!", "success", JOptionPane.INFORMATION_MESSAGE);
			try {
				ControllerHandlerFactory.getPipe().deleteObserver(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.dispose();
		}
	}
}
