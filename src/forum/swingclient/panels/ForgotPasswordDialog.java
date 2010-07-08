package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;


import javax.mail.MessagingException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import javax.swing.SpringLayout;
import javax.swing.GroupLayout.Alignment;

import javax.swing.border.Border;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

public class ForgotPasswordDialog extends JDialog implements GUIHandler {

	/**
	 * 
	 */
	
	// A timer for the sending button

	private static final String SENDING_STRING = "Sending, please wait";
	private static final String UPDATING_PASSWORD_STRING = "Updating, please wait";

	private static final String[] SENDING_STRINGS = {SENDING_STRING, SENDING_STRING + ".",  SENDING_STRING + "..", 
			SENDING_STRING + "...", SENDING_STRING + "....", SENDING_STRING + "....."};

	private static final String[] UPDATING_STRINGS = {UPDATING_PASSWORD_STRING, UPDATING_PASSWORD_STRING + ".",  UPDATING_PASSWORD_STRING + "..", 
			UPDATING_PASSWORD_STRING + "...", UPDATING_PASSWORD_STRING + "....", UPDATING_PASSWORD_STRING + "....."};
	
	private static final long serialVersionUID = 1L;

	private static final String PASSWORD_CHARSET = "!@0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int RANDOM_PASSWORD_LENGTH = 8;

	private JPanel mainPanel;
	private JButton sendPasswordButton;
	private JButton cancelButton;

	private JLabel sendingLabel;
	private JLabel updatingLabel;
	
	private JLabel usernameLabel;
	private JTextField usernameInput;

	private JLabel emailLabel;
	private JTextField emailInput;
	private JPanel forgotPasswordPanel;

	private JPanel informationPanel;
	private JLabel informationLabel;

	public ControllerHandler controller;
	
	private String generatedPassword;
	
	private void sendNewPassword(final String[] dataToSend) {
		sendingLabel.setVisible(true);				

		SwingWorker<Boolean, Void> tSender = new SwingWorker<Boolean, Void>() {
			public Boolean doInBackground() {
				final SendMail tSender = new SendMail();
				
				String toSend = "<h3>Hello " + dataToSend[3] + " " + dataToSend[4] + "!</h3>" +
				"This is a message from QuadCoreForum Administrator. <br /> Someone, maybe it was you, asked us to change " +
				" your forum password. <br /> The new password is: <b><u>" + generatedPassword + "</u></b>.\n\n You can change this " +
				" password after the first login to the forum.<br />" + "<p>" +
				"Please don't reply to this message.<br />" + "Best Regards<br />" +
				"       QuadCoreForum Administrator</p>";
				
				
				System.out.println(toSend + "  " +  generatedPassword + " " + "ppppppppppppppppppppppppppppppppp");
				
				try {
					tSender.postMail(dataToSend[5], "QuadCoreForum admin message", toSend, "QuadCoreForumAdmin");
					return true;
				}
				catch (MessagingException e) {
					return false;
				}
			}

			public void done() {
				try {
					boolean tSendingRetVal = get();
					sendingLabel.setVisible(false);
					
					if (tSendingRetVal)
						JOptionPane.showMessageDialog(ForgotPasswordDialog.this, "A new password was successfully sent to your " +
								"email,\nyou can change it after the first login.", "Message recovery succeeded",
								JOptionPane.INFORMATION_MESSAGE);
					else 
						JOptionPane.showMessageDialog(ForgotPasswordDialog.this, "A new password couldn't be sent due to a communication "
								+ "error\n. Please try again later.", "Error while message sending", JOptionPane.ERROR_MESSAGE);
					cancelButton.doClick();

				} catch (InterruptedException ex) {
					ex.printStackTrace();
				} catch (ExecutionException ex) {
					ex.printStackTrace();
				}
			}

		};

		tSender.execute();
	}
	
	
	public void refreshForum(String encodedView) {
		informationLabel.setText("");
		updatingLabel.setVisible(false);

		System.out.println("Encoded = " + encodedView);
		if (encodedView.startsWith("profiledetailsupdatesuccess\t")) {
			this.controller.deleteObserver(this);
			String[] tUpdatedDetails = encodedView.split("\t");
			this.sendNewPassword(tUpdatedDetails);
		}
		else if (encodedView.startsWith("profiledetailsupdateerror\t"))
			this.notifyError(encodedView);
	}

	public void notifyError(String error) {
		if (error.startsWith("profiledetailsupdateerror\t")) {
			this.controller.deleteObserver(this);
			String[] tSplittedMessage = error.split("\t");
			String tErrorMessage = (tSplittedMessage[1].equals("registration"))? 
					"Wrong username or email" : tSplittedMessage[2];
			informationLabel.setText(tErrorMessage);
		}
	}
	

	public ForgotPasswordDialog() throws IOException {
		super();
		controller = ControllerHandlerFactory.getPipe();

		this.setTitle("Password Recovery Page");

		initGUIComponents();
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.getContentPane().add(mainPanel);
		this.setMinimumSize(new Dimension(450, 300));


		this.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int Y = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, Y);
		this.setModal(true);
	}

	private void initGUIComponents() {

		this.informationPanel  = new JPanel();
		this.informationLabel = new JLabel("", JLabel.TRAILING);
		this.informationLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

		this.informationLabel.setForeground(new Color(255, 0, 0));
		informationPanel.add(informationLabel);


		this.forgotPasswordPanel = new JPanel();
		Border tBlueBorder = BorderFactory.createLineBorder(Color.BLUE, 3);
		this.forgotPasswordPanel.setBorder(BorderFactory.createTitledBorder(tBlueBorder, "Please enter your username and e-mail"));


		SpringLayout jLoginPanel = new SpringLayout();
		forgotPasswordPanel.setLayout(jLoginPanel);

		this.usernameLabel = new JLabel("Username", JLabel.TRAILING);;
		this.usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		forgotPasswordPanel.add(this.usernameLabel);
		this.usernameInput = new JTextField(20);
		this.usernameInput.setText("");
		usernameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		forgotPasswordPanel.add(this.usernameInput);
		this.usernameLabel.setLabelFor(this.usernameInput);

		this.emailLabel = new JLabel("E-mail", JLabel.TRAILING);
		this.emailLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		forgotPasswordPanel.add(this.emailLabel);
		this.emailInput = new JTextField(20);
		this.emailInput.setText("");
		emailInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		forgotPasswordPanel.add(this.emailInput);
		this.emailLabel.setLabelFor(this.emailInput);

		updatingLabel = new JLabel("Updating, please wait");
		updatingLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

		sendingLabel = new JLabel("Sending, please wait");
		sendingLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

		sendingLabel.setVisible(false);
		updatingLabel.setVisible(false);

		// Timer initialization
		
		Timer tSendingTimer = new Timer(500, new ActionListener() {
			int tIndex = 0;
			public void actionPerformed(ActionEvent e) {
				tIndex = (tIndex + 1) % SENDING_STRINGS.length;
				sendingLabel.setText(SENDING_STRINGS[tIndex]);
				updatingLabel.setText(UPDATING_STRINGS[tIndex]);
			}
		});

		tSendingTimer.start();

		// End of timer initialization

		SpringUtilities.makeCompactGrid(forgotPasswordPanel,
				2, 2, //rows, cols
				4, 4,        //initX, initY
				30, 30);       //xPad, yPad


		sendPasswordButton = new JButton();
		sendPasswordButton.setText("Send new");
		sendPasswordButton.addActionListener(new SendPasswordActionListener());

		cancelButton = new JButton("Cancel");

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ForgotPasswordDialog.this.dispose();

			}
		});

		sendPasswordButton.setPreferredSize(new Dimension(100, 40));
		cancelButton.setPreferredSize(new Dimension(100, 40));

		this.mainPanel = new JPanel();
		//		mainPanel.setPreferredSize(new Dimension(2, 4));

		//JSeparator tMainPanelSeparator = new JSeparator();

		GroupLayout tMainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(tMainPanelLayout);
		tMainPanelLayout.setHorizontalGroup(
				tMainPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(informationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(forgotPasswordPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGroup(tMainPanelLayout.createSequentialGroup()
								.addComponent(sendingLabel, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addComponent(updatingLabel, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE)
								.addComponent(sendPasswordButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, 20)
								.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
						))
						.addContainerGap()
		);


		tMainPanelLayout.setVerticalGroup(
				tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tMainPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(informationPanel, 30, 30, 30)
						.addComponent(forgotPasswordPanel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(0, 30, 30)
						.addGroup(tMainPanelLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(sendingLabel, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addComponent(updatingLabel, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addComponent(sendPasswordButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE))
								.addContainerGap()
				)
		);


	}	

	/**
	 * 
	 * @return
	 * 		A new generate random password which is set as the new user's password
	 */
	private String getRandomPassword() {
		Random rand = new Random(System.currentTimeMillis());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < RANDOM_PASSWORD_LENGTH; i++) {
			int pos = rand.nextInt(PASSWORD_CHARSET.length());
			sb.append(PASSWORD_CHARSET.charAt(pos));
		}
		return sb.toString();
	}

	private class SendPasswordActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			controller.addObserver(new GUIObserver(ForgotPasswordDialog.this), EventType.USER_CHANGED);
			if(usernameInput.getText().length() > 0 && emailInput.getText().length() > 0) {
				ForgotPasswordDialog.this.generatedPassword = getRandomPassword();
				controller.updatePassword(usernameInput.getText(),
						emailInput.getText(), generatedPassword, ForgotPasswordDialog.this);
			}
			else
				informationLabel.setText("you must insert not empty user name and email");
		}
	}

	public static void main(String[] args) {
		try {
			ForgotPasswordDialog d = new ForgotPasswordDialog();
			for (int i = 0 ;i< 19; i++)  {
				//				System.out.println(d.getRandomPassword());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			d.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}

