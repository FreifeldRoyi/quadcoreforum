/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.Border;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

/**
 * @author dahany
 *
 */
public class ProfileDialog extends JDialog implements GUIHandler, KeyListener {

	private long memberID;
	private boolean shouldUpdateData;

	private JButton updateButton;
	private JButton cancelButton = new JButton();

	private JLabel usernameLabel;
	private JTextField usernameInput;
	private JLabel emailLabel;
	private JTextField emailInput;
	private JLabel firstNameLabel;
	private JTextField firstNameInput;
	private JLabel lastNameLabel;
	private JTextField lastNameInput;
	private JPanel profilePanel;
	private JPanel informationPanel;
	private JLabel informationLabel;

	private JLinkButton updatePasswordButton;


	public ControllerHandler controller;

	// KeyListener implementation
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ENTER)
			updateButton.doClick();
		else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE)
			cancelButton.doClick();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() != KeyEvent.VK_ENTER)
			clearLabels();
	}
	// end of KeyListener implementation

	public void refreshForum(String encodedView) {
		if (encodedView.startsWith("profiledetailsupdatesuccess\t")) {
			this.controller.deleteObserver(this);
			JOptionPane.showMessageDialog(this, "Your details were updated successfully!",
					"success", JOptionPane.INFORMATION_MESSAGE);
			shouldUpdateData = true;
			this.dispose();
		}
	}

	public void notifyError(String error) {
		if (error.startsWith("profiledetailsupdateerror\texistingemail\t")) {
			informationLabel.setText("The following data already exists: " + emailInput.getText());
			makeLabelNoticeble(emailLabel, emailInput);
		}
		else if (error.startsWith("profiledetailsupdateerror\t")) {
			this.controller.deleteObserver(this);
			String[] tSplitted = error.split("\t");
			JOptionPane.showMessageDialog(this, tSplitted[2],
					"error", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void setTextFieldNoEditableAppearance(JTextField field) {
		final Font tFont = new Font("Tahoma", Font.BOLD, 13);
		field.setEditable(false);
		field.setFont(tFont);
		field.setForeground(Color.RED);
	}

	public ProfileDialog(long memberID, String username, String firstName, String lastName, String email,
			boolean enableInput) throws IOException {
		this();

		updatePasswordButton.setVisible(true);

		this.setTextFieldNoEditableAppearance(usernameInput);

		if (!enableInput) {
			updatePasswordButton.setVisible(false);
			this.setTitle("Profile display");
			this.setTextFieldNoEditableAppearance(firstNameInput);
			this.setTextFieldNoEditableAppearance(lastNameInput);
			this.setTextFieldNoEditableAppearance(emailInput);

			this.updateButton.setVisible(false);
		}
		else {
			this.memberID = memberID;

			this.setTitle("User Profile");

			this.updateButton.setText("Update");
			for (ActionListener tAl : this.updateButton.getActionListeners())
				this.updateButton.removeActionListener(tAl);

			this.updateButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String tCheck = checkDataValidity();
					if (tCheck.equals("O.K.")) {
						controller.addObserver(new GUIObserver(ProfileDialog.this), EventType.USER_CHANGED);
						informationLabel.setText("");
						controller.updateMemberDetails(ProfileDialog.this, 
								ProfileDialog.this.memberID, usernameInput.getText(),
								firstNameInput.getText(), lastNameInput.getText(), emailInput.getText());
					}
					else {
						informationLabel.setText(tCheck);
					}
				}
			}
			);

		}		
		this.usernameInput.setText(username);
		this.firstNameInput.setText(firstName);
		this.lastNameInput.setText(lastName);
		this.emailInput.setText(email);
	}

	public boolean shouldUpdateData() {
		return shouldUpdateData;
	}

	public static ProfileDialog getProfileDialog(Component container) throws IOException {
		try {
			return new ProfileDialog();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(container, "Can't connect to the forum database",
					"error", JOptionPane.ERROR_MESSAGE);
			throw e;
		}
	}

	public ProfileDialog() throws IOException {
		super();
		shouldUpdateData = false;

		updatePasswordButton = new JLinkButton("change password", Color.black);
		updatePasswordButton.setForeground(Color.black);

		updatePasswordButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				informationLabel.setText("");
				new ChangePasswordDialog(ProfileDialog.this.memberID, false).setVisible(true);
			}
		});

		updatePasswordButton.setVisible(false);

		this.setTitle("User Profile");
		controller = ControllerHandlerFactory.getPipe();


		initGUIComponents();
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


		this.addWindowListener(new WindowListener() {

			public void windowDeactivated(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}


			public void windowClosing(WindowEvent e) {
				controller.deleteObserver(ProfileDialog.this);
			}
		});

		this.setEnabled(true);
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
		this.informationLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

		this.informationLabel.setForeground(new Color(255, 0, 0));
		informationPanel.add(informationLabel);

		this.profilePanel = new JPanel();
		Border tBlueBorder = BorderFactory.createLineBorder(Color.BLUE, 3);
		this.profilePanel.setBorder(BorderFactory.createTitledBorder(tBlueBorder, "Profile"));

		SpringLayout jProfilePanel = new SpringLayout();
		profilePanel.setLayout(jProfilePanel);

		Font tFont = new Font("Tahoma", Font.BOLD, 13);

		this.usernameLabel = new JLabel("Username", JLabel.TRAILING);;
		this.usernameLabel.setFont(tFont);
		profilePanel.add(this.usernameLabel);
		this.usernameInput = new JRestrictedLengthTextField(20, 20, false, true);
		this.usernameInput.setText("");
		usernameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		profilePanel.add(this.usernameInput);
		this.usernameLabel.setLabelFor(this.usernameInput);

		this.emailLabel = new JLabel("Email", JLabel.TRAILING);
		this.emailLabel.setFont(tFont);
		profilePanel.add(this.emailLabel);
		this.emailInput = new JRestrictedLengthTextField(40, 40, false, true);
		this.emailInput.setText("");
		emailInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		profilePanel.add(this.emailInput);
		this.emailLabel.setLabelFor(this.emailInput);

		this.firstNameLabel = new JLabel("First Name", JLabel.TRAILING);
		this.firstNameLabel.setFont(tFont);
		profilePanel.add(this.firstNameLabel);
		this.firstNameInput = new JRestrictedLengthTextField(20, 20, false, false);
		this.firstNameInput.setText("");
		firstNameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		profilePanel.add(this.firstNameInput);
		this.firstNameLabel.setLabelFor(this.firstNameInput);

		this.lastNameLabel = new JLabel("Last Name", JLabel.TRAILING);
		this.lastNameLabel.setFont(tFont);
		profilePanel.add(this.lastNameLabel);
		this.lastNameInput = new JRestrictedLengthTextField(20, 20, false, false);
		this.lastNameInput.setText("");
		lastNameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		profilePanel.add(this.lastNameInput);
		this.lastNameLabel.setLabelFor(this.lastNameInput);


		this.usernameInput.addKeyListener(this);
		this.emailInput.addKeyListener(this);
		this.firstNameInput.addKeyListener(this);
		this.lastNameInput.addKeyListener(this);

		//		registrationPanel.add(this.registerButton);
		//	jRegistrationPanel.putConstraint(SpringLayout.NORTH, this.registerButton,
		//				5,
		//			SpringLayout.SOUTH, this.lastNameInput);
		//jRegistrationPanel.putConstraint(SpringLayout.WEST, this.registerButton,
		//	0,
		//SpringLayout.WEST, this.lastNameInput);

		//Lay out the panel.
		SpringUtilities.makeCompactGrid(profilePanel,
				4, 2, //rows, cols
				6, 6,        //initX, initY
				15, 15);       //xPad, yPad



		updateButton = new JButton();

		updateButton.setText("Update");

		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		cancelButton.setPreferredSize(new Dimension(100, 40));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ProfileDialog.this.dispose();				
			}

		});

		updateButton.setPreferredSize(new Dimension(100, 40));



		GroupLayout tMainPanelLayout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(tMainPanelLayout);
		tMainPanelLayout.setHorizontalGroup(
				tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(informationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(tMainPanelLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(profilePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10))
						.addGroup(tMainPanelLayout.createSequentialGroup()
								.addGap(10, 10, 10)
								.addComponent(updatePasswordButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE)
								.addComponent(updateButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10))
		);

		tMainPanelLayout.setVerticalGroup(
				tMainPanelLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addComponent(informationPanel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
				.addGap(10, 10, 10)
				.addComponent(profilePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGap(30, 30, 30)
				.addGroup(tMainPanelLayout.createParallelGroup()
						.addGroup(tMainPanelLayout.createSequentialGroup()
								.addGap(20, 20, 20)
								.addComponent(updatePasswordButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE))
								.addComponent(updateButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE))
								.addGap(10, 10, 10));

		this.setMinimumSize(new Dimension(700, 520));
		this.setPreferredSize(new Dimension(700, 520));


		/*
		this.usernameInput.setText("yakirda");
		identification this.passwordInput.setText("123456");
		this.confirmPasswordInput.setText("123456");
		this.firstNameInput.setText("Yakir");
		this.lastNameInput.setText("Dahan");
		this.emailInput.setText("a@b.c");
		this.confirmEmailInput.setText("a@b.c");
		 */
	}

	private void clearLabels() {
		this.usernameLabel.setForeground(Color.BLACK);
		this.lastNameLabel.setForeground(Color.BLACK);
		this.firstNameLabel.setForeground(Color.BLACK);
		this.emailLabel.setForeground(Color.BLACK);
		this.informationLabel.setText("");
	}

	private void makeLabelNoticeble(JLabel label, JTextField field) {
		label.setForeground(Color.RED);
		field.selectAll();
		field.grabFocus();
	}

	public String checkDataValidity() {
		String toReturn = "O.K.";
		if (this.usernameInput.getText().length() < 4) {
			makeLabelNoticeble(usernameLabel, usernameInput);

			toReturn = "The username must be at least 4 letters long";
		}
		else{
			String tEmail = this.emailInput.getText();
			//int tIndex = tEmail.indexOf('@');
			String tPattern = ".+@.+[.].+";
			if (!tEmail.matches(tPattern)) {
				makeLabelNoticeble(emailLabel, emailInput);
				//if ((tIndex == -1) || (tEmail.substring(tIndex + 2, tEmail.length() - 1).indexOf('.') == -1))
				toReturn = "The email address must be in the form: username@domain.extension";
			}
			else if (this.firstNameInput.getText().length() == 0) {
				makeLabelNoticeble(firstNameLabel, firstNameInput);
				toReturn = "The field \"First Name\" is essential";
			}
			else if (this.lastNameInput.getText().length() == 0) {
				makeLabelNoticeble(lastNameLabel, lastNameInput);
				toReturn = "The field \"Last Name\" is essential";
			}
		}

		return toReturn;
	}

}
