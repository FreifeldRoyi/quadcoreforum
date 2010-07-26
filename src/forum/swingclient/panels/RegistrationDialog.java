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
public class RegistrationDialog extends JDialog implements GUIHandler, KeyListener {

	/*	@Override
	public void notifyError(String errorMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshForum(String encodedView) {
		// TODO Auto-generated method stub

	}
	 */	



	private static final long serialVersionUID = -5251318786616475794L;	

	private long memberID;
	private boolean shouldUpdateData;

	private JButton registerButton;
	private JButton cancelButton = new JButton();

	private JLabel usernameLabel;
	private JTextField usernameInput;
	private JLabel passwordLabel;
	private JPasswordField passwordInput;
	private JLabel confirmPasswordLabel;
	private JPasswordField confirmPasswordInput;
	private JLabel emailLabel;
	private JTextField emailInput;
	private JLabel confirmEmailLabel;
	private JTextField confirmEmailInput;
	private JLabel firstNameLabel;
	private JTextField firstNameInput;
	private JLabel lastNameLabel;
	private JTextField lastNameInput;
	private JPanel registrationPanel;
	private JPanel informationPanel;
	private JLabel informationLabel;

	private JLinkButton updatePasswordButton;


	public ControllerHandler controller;

	// KeyListener implementation
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ENTER)
			registerButton.doClick();
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
		if (encodedView.startsWith("registersuccess\t")) {
			this.controller.deleteObserver(this);
			JOptionPane.showMessageDialog(this, "The registration process was completed successfully!",
					"success", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		}
		else if (encodedView.startsWith("registererror\t"))
			this.notifyError(encodedView);
		else if (encodedView.startsWith("profiledetailsupdatesuccess\t")) {
			this.controller.deleteObserver(this);
			JOptionPane.showMessageDialog(this, "Your details were updated successfully!",
					"success", JOptionPane.INFORMATION_MESSAGE);
			shouldUpdateData = true;
			this.dispose();
		}
	}

	public void notifyError(String error) {
		if (error.startsWith("registererror\t")) {
			this.controller.deleteObserver(this);
			String[] tSplittedMessage = error.split("\t");
			String tErrorMessage = tSplittedMessage[1];
			informationLabel.setText(tErrorMessage);
		}
		else if (error.startsWith("profiledetailsupdateerror\texistingemail\t")) {
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

/*	public RegistrationDialog(long memberID, String username, String firstName, String lastName, String email,
			boolean enableInput) throws IOException {
		this();

		updatePasswordButton.setVisible(true);

		this.passwordInput.setEnabled(false);
		this.passwordInput.setBackground(Color.GRAY);

		this.confirmPasswordInput.setEnabled(false);
		this.confirmPasswordInput.setBackground(Color.GRAY);

		this.setTextFieldNoEditableAppearance(usernameInput);

		if (!enableInput) {
			this.setTitle("Profile display");
			this.confirmEmailInput.setEnabled(false);
			this.confirmEmailInput.setBackground(Color.GRAY);

			this.setTextFieldNoEditableAppearance(firstNameInput);
			this.setTextFieldNoEditableAppearance(lastNameInput);
			this.setTextFieldNoEditableAppearance(emailInput);

			this.registerButton.setEnabled(false);
		}
		else {
			this.memberID = memberID;

			this.confirmEmailInput.setText(email);
			this.setTitle("Profile changing");

			this.registerButton.setText("Update");
			for (ActionListener tAl : this.registerButton.getActionListeners())
				this.registerButton.removeActionListener(tAl);

			this.registerButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String tCheck = checkDataValidity();
					if (tCheck.equals("O.K.")) {
						controller.addObserver(new GUIObserver(RegistrationDialog.this), EventType.USER_CHANGED);
						informationLabel.setText("");
						controller.updateMemberDetails(RegistrationDialog.this, 
								RegistrationDialog.this.memberID, usernameInput.getText(),
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
*/
	public boolean shouldUpdateData() {
		return shouldUpdateData;
	}

	public static RegistrationDialog getRegistrationDialog(Component container) throws IOException {
		try {
			return new RegistrationDialog();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(container, "Can't connect to the forum database",
					"error", JOptionPane.ERROR_MESSAGE);
			throw e;
		}
	}

	public RegistrationDialog() throws IOException {
		super();
		shouldUpdateData = false;

		updatePasswordButton = new JLinkButton("change password", Color.black);
		updatePasswordButton.setForeground(Color.black);

		updatePasswordButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				informationLabel.setText("");
				new ChangePasswordDialog(RegistrationDialog.this.memberID, false).setVisible(true);
			}
		});

		updatePasswordButton.setVisible(false);

		this.setTitle("Register to the forum");
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
				controller.deleteObserver(RegistrationDialog.this);
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

		this.registrationPanel = new JPanel();
		Border tBlueBorder = BorderFactory.createLineBorder(Color.BLUE, 3);
		this.registrationPanel.setBorder(BorderFactory.createTitledBorder(tBlueBorder, "Rgistration"));

		SpringLayout jRegistrationPanel = new SpringLayout();
		registrationPanel.setLayout(jRegistrationPanel);

		Font tFont = new Font("Tahoma", Font.BOLD, 13);		
		initUsernameComponents(tFont);
		initPasswordComponents(tFont);
		initEmailComponents(tFont);
		initFirstNameComponents(tFont);
		initLastNameComponents(tFont);

		this.usernameInput.addKeyListener(this);
		this.passwordInput.addKeyListener(this);
		this.confirmPasswordInput.addKeyListener(this);
		this.emailInput.addKeyListener(this);
		this.confirmEmailInput.addKeyListener(this);
		this.firstNameInput.addKeyListener(this);
		this.lastNameInput.addKeyListener(this);

		SpringUtilities.makeCompactGrid(registrationPanel,
				7, 2, //rows, cols
				6, 6,        //initX, initY
				15, 15);       //xPad, yPad


		this.registerButton = new JButton();
		this.cancelButton = new JButton();		
		initButtons();

		GroupLayout tMainPanelLayout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(tMainPanelLayout);
		tMainPanelLayout.setHorizontalGroup(
				tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(informationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(tMainPanelLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(registrationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10))
						.addGroup(tMainPanelLayout.createSequentialGroup()
								.addGap(10, 10, 10)
								.addComponent(updatePasswordButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE)
								.addComponent(registerButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10))
		);

		tMainPanelLayout.setVerticalGroup(
				tMainPanelLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addComponent(informationPanel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
				.addGap(10, 10, 10)
				.addComponent(registrationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGap(30, 30, 30)
				.addGroup(tMainPanelLayout.createParallelGroup()
						.addGroup(tMainPanelLayout.createSequentialGroup()
								.addGap(20, 20, 20)
								.addComponent(updatePasswordButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE))
								.addComponent(registerButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE))
								.addGap(10, 10, 10));
		this.setMinimumSize(new Dimension(700, 520));
		this.setPreferredSize(new Dimension(700, 520));
	}

	private void initButtons() {
		this.registerButton.setText("Register");
		this.registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tCheck = checkDataValidity();
				if (tCheck.equals("O.K.")) {
					controller.addObserver(new GUIObserver(RegistrationDialog.this), EventType.USER_CHANGED);
					informationLabel.setText("");
					controller.registerToForum(registerButton, usernameInput.getText(),
							new String(passwordInput.getPassword()), 
							emailInput.getText(),
							firstNameInput.getText(),
							lastNameInput.getText());
				}
				else {
					informationLabel.setText(tCheck);
				}
			}
		});
		this.registerButton.setPreferredSize(new Dimension(100, 40));

		this.cancelButton.setText("Cancel");
		this.cancelButton.setPreferredSize(new Dimension(100, 40));
		this.cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RegistrationDialog.this.dispose();				
			}
		});		
	}
	
	private void initUsernameComponents(Font tFont) {
		this.usernameLabel = new JLabel("Username", JLabel.TRAILING);;
		this.usernameLabel.setFont(tFont);
		this.registrationPanel.add(this.usernameLabel);
		this.usernameInput = new JRestrictedLengthTextField(20, 20, false, true);
		this.usernameInput.setText("");
		this.usernameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.usernameLabel.setLabelFor(this.usernameInput);
		this.registrationPanel.add(this.usernameInput);
	}

	private void initPasswordComponents(Font tFont) {
		this.passwordLabel = new JLabel("Password", JLabel.TRAILING);
		this.passwordLabel.setFont(tFont);
		this.registrationPanel.add(this.passwordLabel);
		this.passwordInput = new JRestrictedLengthPasswordField(20, 20);
		this.passwordInput.setText("");
		this.passwordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.registrationPanel.add(this.passwordInput);
		this.passwordLabel.setLabelFor(this.passwordInput);

		this.confirmPasswordLabel = new JLabel("Confirm Password", JLabel.TRAILING);
		this.confirmPasswordLabel.setFont(tFont);
		this.registrationPanel.add(this.confirmPasswordLabel);
		this.confirmPasswordInput = new JRestrictedLengthPasswordField(20, 20);
		this.confirmPasswordInput.setText("");
		this.confirmPasswordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.registrationPanel.add(this.confirmPasswordInput);
		this.confirmPasswordLabel.setLabelFor(this.confirmPasswordInput);			
	}
	
	private void initEmailComponents(Font tFont) {
		this.emailLabel = new JLabel("Email", JLabel.TRAILING);
		this.emailLabel.setFont(tFont);
		this.registrationPanel.add(this.emailLabel);
		this.emailInput = new JRestrictedLengthTextField(40, 40, false, true);
		this.emailInput.setText("");
		this.emailInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.registrationPanel.add(this.emailInput);
		this.emailLabel.setLabelFor(this.emailInput);

		this.confirmEmailLabel = new JLabel("Confirm Email", JLabel.TRAILING);
		this.confirmEmailLabel.setFont(tFont);
		this.registrationPanel.add(this.confirmEmailLabel);
		this.confirmEmailInput = new JRestrictedLengthTextField(40, 40, false, true);
		this.confirmEmailInput.setText("");
		this.confirmEmailInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.registrationPanel.add(this.confirmEmailInput);
		this.confirmEmailLabel.setLabelFor(this.confirmEmailInput);
	}
	
	private void initFirstNameComponents(Font tFont) {
		this.firstNameLabel = new JLabel("First Name", JLabel.TRAILING);
		this.firstNameLabel.setFont(tFont);
		this.registrationPanel.add(this.firstNameLabel);
		this.firstNameInput = new JRestrictedLengthTextField(20, 20, false, false);
		this.firstNameInput.setText("");
		this.firstNameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.registrationPanel.add(this.firstNameInput);
		this.firstNameLabel.setLabelFor(this.firstNameInput);
	}
	
	private void initLastNameComponents(Font tFont) {
		this.lastNameLabel = new JLabel("Last Name", JLabel.TRAILING);
		this.lastNameLabel.setFont(tFont);
		this.registrationPanel.add(this.lastNameLabel);
		this.lastNameInput = new JRestrictedLengthTextField(20, 20, false, false);
		this.lastNameInput.setText("");
		this.lastNameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		this.registrationPanel.add(this.lastNameInput);
		this.lastNameLabel.setLabelFor(this.lastNameInput);
	}
	
	
	private void clearLabels() {
		this.usernameLabel.setForeground(Color.BLACK);
		this.passwordLabel.setForeground(Color.BLACK);
		this.confirmPasswordLabel.setForeground(Color.BLACK);
		this.lastNameLabel.setForeground(Color.BLACK);
		this.firstNameLabel.setForeground(Color.BLACK);
		this.emailLabel.setForeground(Color.BLACK);
		this.confirmEmailLabel.setForeground(Color.BLACK);
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
		else if (this.passwordInput.isEnabled() && this.passwordInput.getPassword().length < 6) {
			makeLabelNoticeble(passwordLabel, passwordInput);
			toReturn = "The password must be at least 6 letters long";
		}
		else if (this.passwordInput.isEnabled() && !Arrays.equals(this.passwordInput.getPassword(), this.confirmPasswordInput.getPassword())) {
			makeLabelNoticeble(passwordLabel, passwordInput);
			makeLabelNoticeble(confirmPasswordLabel, confirmPasswordInput);
			toReturn = "The password fields must be identical";
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
			else if (!this.emailInput.getText().equals(this.confirmEmailInput.getText())) {
				makeLabelNoticeble(emailLabel, emailInput);
				makeLabelNoticeble(confirmEmailLabel, confirmEmailInput);
				toReturn = "The email fields must be identical";
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
