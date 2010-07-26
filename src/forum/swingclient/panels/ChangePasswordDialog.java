/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Arrays;

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
public class ChangePasswordDialog extends JDialog implements GUIHandler, KeyListener {

	private static final long serialVersionUID = 5841384666369000328L;
	private long memberID;

	private ControllerHandler controller;

	private JLabel lbl_information;

	private JLabel lbl_prevPassword;
	private JLabel lbl_newPassword;
	private JLabel lbl_confirmPassword;

	private JPasswordField pass_field_prevPassword;
	private JPasswordField pass_field_newPassword;
	private JPasswordField pass_field_confirmPassword;

	private JButton btn_ok;
	private JButton btn_cancel;

	private JCheckBox chk_dont_show_again;

	private boolean succeeded;

	private ActionListener btn_ok_listener;
	private ActionListener btn_cancel_listener;



	// KeyListener implementation
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		lbl_information.setText("");
		lbl_prevPassword.setForeground(Color.BLACK);
		lbl_newPassword.setForeground(Color.BLACK);
		lbl_confirmPassword.setForeground(Color.BLACK);
		if (e.getKeyChar() == KeyEvent.VK_ENTER)
			btn_ok.doClick();
	}			
	// End of KeyListener implementation

	public ChangePasswordDialog(final long memberID, final boolean showCheckBox) {
		super();
		try {
			controller = ControllerHandlerFactory.getPipe();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(ChangePasswordDialog.this, "Can't communicate with the server!", "error", JOptionPane.ERROR_MESSAGE);

			// TODO Auto-generated catch block
		}


		this.succeeded = false;
		this.memberID = memberID;
		this.setTitle("Change password");

		this.lbl_information = new JLabel();
		this.lbl_information.setHorizontalAlignment(SwingConstants.CENTER);
		this.lbl_information.setForeground(Color.RED);

		this.lbl_prevPassword = new JLabel("Previous password:");
		this.lbl_newPassword = new JLabel("New password:");
		this.lbl_confirmPassword = new JLabel("Confirm password:");

		this.pass_field_prevPassword = new JRestrictedLengthPasswordField(20, 20);
		this.pass_field_newPassword = new JRestrictedLengthPasswordField(20, 20);
		this.pass_field_confirmPassword = new JRestrictedLengthPasswordField(20, 20);

		this.pass_field_prevPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		this.pass_field_newPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		this.pass_field_confirmPassword.setFont(new Font("Tahoma", Font.BOLD, 12));

		this.chk_dont_show_again = new JCheckBox("Don't show this dialog again");

		this.btn_ok = new JButton("Ok");
		this.btn_cancel = new JButton("Cancel");

		this.btn_ok.setPreferredSize(new Dimension(100, 40));
		this.btn_cancel.setPreferredSize(new Dimension(100, 40));

		this.pass_field_prevPassword.addKeyListener(this);
		this.pass_field_newPassword.addKeyListener(this);
		this.pass_field_confirmPassword.addKeyListener(this);

		this.arrangeLayout();

		btn_ok_listener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lbl_prevPassword.setForeground(Color.BLACK);
				lbl_newPassword.setForeground(Color.BLACK);
				lbl_confirmPassword.setForeground(Color.BLACK);

				if (pass_field_prevPassword.getPassword().length == 0) {
					lbl_information.setText("previous password field cannot be empty");
					lbl_prevPassword.setForeground(Color.RED);
					pass_field_prevPassword.grabFocus();
					return;
				}
				else if (pass_field_newPassword.getPassword().length == 0) {
					lbl_information.setText("new password field cannot be empty");
					lbl_newPassword.setForeground(Color.RED);
					pass_field_newPassword.grabFocus();
					return;
				}
				else if (pass_field_confirmPassword.getPassword().length == 0) {
					lbl_information.setText("confirm password field cannot be empty");
					lbl_confirmPassword.setForeground(Color.RED);
					pass_field_confirmPassword.grabFocus();
					return;
				}
				else if (pass_field_newPassword.getPassword().length < 6) {
					lbl_information.setText("The password must be at least 6 letters long");
					lbl_newPassword.setForeground(Color.RED);
					pass_field_newPassword.selectAll();
					pass_field_newPassword.grabFocus();
					return;
				}
				else if (!Arrays.equals(pass_field_newPassword.getPassword(), 
						pass_field_confirmPassword.getPassword())) {
					lbl_information.setText("The password and confirmation fields must be identical");
					lbl_newPassword.setForeground(Color.RED);
					lbl_confirmPassword.setForeground(Color.RED);
					pass_field_confirmPassword.selectAll();
					pass_field_confirmPassword.grabFocus();
					return;
				}
				else if (Arrays.equals(pass_field_prevPassword.getPassword(), 
						pass_field_newPassword.getPassword())) {
					lbl_information.setText("The new password must be different than the previous one");
					lbl_newPassword.setForeground(Color.RED);
					pass_field_newPassword.selectAll();
					pass_field_newPassword.grabFocus();
					return;
				}

				else {
					// discard multiple choices
					btn_ok.removeActionListener(btn_ok_listener);
					btn_cancel.removeActionListener(btn_cancel_listener);
					ChangePasswordDialog.this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
					
					controller.addObserver(new GUIObserver(ChangePasswordDialog.this), EventType.USER_CHANGED);
					controller.changePassword(ChangePasswordDialog.this.memberID, new String(pass_field_prevPassword.getPassword()), 
							new String(pass_field_newPassword.getPassword()),
							!chk_dont_show_again.isSelected(), ChangePasswordDialog.this);
				}					
			}

		};	

		if (!showCheckBox)
			chk_dont_show_again.setVisible(false);
		
		btn_cancel_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (chk_dont_show_again.isSelected()) {
					// discard multiple choices
					btn_ok.removeActionListener(btn_ok_listener);
					btn_cancel.removeActionListener(btn_cancel_listener);
					controller.changePassword(memberID, null, null, false, ChangePasswordDialog.this);
				}
				ChangePasswordDialog.this.dispose();
			}

		};
		
		this.btn_ok.addActionListener(btn_ok_listener);
		this.btn_cancel.addActionListener(btn_cancel_listener);
		
		
	}

	private void arrangeLayout() {

		this.setResizable(false);

		this.setMinimumSize(new Dimension(400, 269));

		JPanel mainPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9062536731426386680L;

			public void paint(Graphics g) {
				g.drawImage(new ImageIcon("./images/background1.jpg").getImage(), 
						0, 0, 1920, 1200, null);
				setOpaque(false);
				super.paint(g);
			}
		};
		
		GroupLayout tLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(tLayout);
		this.getContentPane().add(mainPanel);

		tLayout.setHorizontalGroup(tLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.lbl_information, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)

				.addGroup(tLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(this.lbl_prevPassword, 120, 120, 120)
						.addGap(10, 10, 10)
						.addComponent(this.pass_field_prevPassword, 100, 100, Short.MAX_VALUE)
						.addGap(10, 10, 10))
						.addGroup(tLayout.createSequentialGroup()
								.addGap(10, 10, 10)
								.addComponent(this.lbl_newPassword, 120, 120, 120)
								.addGap(10, 10, 10)
								.addComponent(this.pass_field_newPassword, 100, 100, Short.MAX_VALUE)
								.addGap(10, 10, 10))
								.addGroup(tLayout.createSequentialGroup()
										.addGap(10, 10, 10)
										.addComponent(this.lbl_confirmPassword, 120, 120, 120)
										.addGap(10, 10, 10)
										.addComponent(this.pass_field_confirmPassword, 100, 100, Short.MAX_VALUE)
										.addGap(10, 10, 10))
										.addGroup(tLayout.createSequentialGroup()
												.addGap(6, 6, 6)
												.addComponent(this.chk_dont_show_again, 100, 100, Short.MAX_VALUE)
												.addGap(10, 10, 10))
												.addGroup(tLayout.createSequentialGroup()
														.addGap(10, 10, Short.MAX_VALUE)
														.addComponent(this.btn_ok, 85, 85, 85)
														.addGap(10, 10, 10)
														.addComponent(this.btn_cancel, 85, 85, 85)
														.addGap(10, 10, 10)));


		final int DEFAULT_HEIGHT = 30;

		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addComponent(this.lbl_information, DEFAULT_HEIGHT, DEFAULT_HEIGHT, Short.MAX_VALUE)
				.addGap(10, 10, 10)
				.addGroup(tLayout.createParallelGroup()
						.addGap(10, 10, 10)
						.addComponent(this.lbl_prevPassword, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
						.addGap(10, 10, 10)
						.addComponent(this.pass_field_prevPassword, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT))
						.addGap(10, 10, 10)
						.addGroup(tLayout.createParallelGroup()
								.addGap(10, 10, 10)
								.addComponent(this.lbl_newPassword, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
								.addGap(10, 10, 10)
								.addComponent(this.pass_field_newPassword, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT))
								.addGap(10, 10, 10)
								.addGroup(tLayout.createParallelGroup()
										.addGap(10, 10, 10)
										.addComponent(this.lbl_confirmPassword, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
										.addGap(10, 10, 10)
										.addComponent(this.pass_field_confirmPassword, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT))
										.addGap(10, 10, 10)
										.addComponent(this.chk_dont_show_again, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
										.addGap(10, 10, 10)
										.addGap(10, 10, 10)
										.addGroup(tLayout.createParallelGroup()
												.addGap(10, 10, Short.MAX_VALUE)
												.addComponent(this.btn_ok, 35, 35, 35)
												.addGap(10, 10, 10)
												.addComponent(this.btn_cancel, 35, 35, 35)
										)
										.addGap(10, 10, 10));

		this.setModal(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int Y = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, Y);
	}

	private void setListeners() {
		btn_ok.addActionListener(btn_ok_listener);
		btn_cancel.addActionListener(btn_cancel_listener);
		ChangePasswordDialog.this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	@Override
	public void notifyError(String errorMessage) {

		if (errorMessage.startsWith("passwordupdateerror\tregistration\t")) {
			controller.deleteObserver(this);
			JOptionPane.showMessageDialog(this, errorMessage, 
					"error occured, you aren't registered to the forum", JOptionPane.ERROR_MESSAGE);
			this.setListeners();
			this.dispose();
		}
		else if (errorMessage.startsWith("passwordupdateerror\tpassword\t")) {
			controller.deleteObserver(this);
			this.lbl_information.setText("Incorrect previous password");
			this.setListeners();
			lbl_prevPassword.setForeground(Color.RED);
			pass_field_prevPassword.selectAll();
			pass_field_prevPassword.grabFocus();
			return;
		}
		else if (errorMessage.startsWith("passwordupdateerror\tdatabase\t")) {
			controller.deleteObserver(this);

			JOptionPane.showMessageDialog(this, errorMessage, 
					"Can't update the password due to a communication error", JOptionPane.ERROR_MESSAGE);
			this.setListeners();

			this.dispose();
		}
	}

	public boolean shouldUpdateGUI() {
		return succeeded;
	}

	@Override
	public void refreshForum(String encodedView) {
		if (encodedView.startsWith("passwordupdatesuccess")) {
			controller.deleteObserver(this);
			JOptionPane.showMessageDialog(ChangePasswordDialog.this, "The password was successfully updated",
					"Password update succeeded", JOptionPane.INFORMATION_MESSAGE);
			this.setListeners();
			this.dispose();
		}
	}

}
