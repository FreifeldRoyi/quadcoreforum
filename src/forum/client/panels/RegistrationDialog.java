/**
 * 
 */
package forum.client.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import forum.client.controllerlayer.ControllerHandler;
import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

/**
 * @author dahany
 *
 */
public class RegistrationDialog extends JDialog implements GUIHandler {

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

	private JPanel mainPanel;
	private JButton registerButton;
	private JLabel welcomeLabel;
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
	private JPanel navigatePanel;
	private JPanel registrationPanel;
	private JPanel informationPanel;
	private JLabel informationLabel;

	public ControllerHandler controller;

	
	
	public void refreshForum(String encodedView) {
		System.out.println("Encoded = " + encodedView);
		if (encodedView.startsWith("registersuccess\t")) {
			this.controller.deleteObserver(this);
			JOptionPane.showMessageDialog(this, "The registration process was completed successfully!",
					"success", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		}
		else if (encodedView.startsWith("registererror\t"))
			this.notifyError(encodedView);
	}

	public void notifyError(String error) {
		System.out.println("register  --- error");
		if (error.startsWith("registererror\t")) {
			String[] tSplittedMessage = error.split("\t");
			String tErrorMessage = tSplittedMessage[1];
			informationLabel.setText(tErrorMessage);
		}
		
	}

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			SystemLogger.warning("Can't use default system look and feel, will use java default" +
			" look and feel style.");
		}

		try {
			RegistrationDialog tRegistrationPanel = new RegistrationDialog();


			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int X = (screen.width / 2) - (tRegistrationPanel.getWidth() / 2); // Center horizontally.
			int Y = (screen.height / 2) - (tRegistrationPanel.getHeight() / 2); // Center vertically.
			tRegistrationPanel.setLocation(X, Y);

		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "initialization error", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}


	}

	public RegistrationDialog() throws IOException {
		super();
		this.setTitle("Registration Page");
		controller = ControllerHandlerFactory.getPipe();
		
		
		initGUIComponents();
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.getContentPane().add(mainPanel);
		this.setMinimumSize(new Dimension(615, 640));

		this.addWindowListener(new WindowListener() {

			public void windowDeactivated(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}


			public void windowClosing(WindowEvent e) {
				SystemLogger.info("The client requested to finish the registration process.");
				controller.deleteObserver(RegistrationDialog.this);
			}
		});

		this.setEnabled(true);
		controller.addObserver(new GUIObserver(this), EventType.USER_CHANGED);
		this.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int Y = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, Y);
		this.setModal(true);
	}

	private void initGUIComponents() {


		navigatePanel = new JPanel();
		welcomeLabel = new JLabel();




		welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		welcomeLabel.setText("Hello guest!");

		navigatePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		GroupLayout tNavigatePanelLayout = new GroupLayout(navigatePanel);
		navigatePanel.setLayout(tNavigatePanelLayout);
		JSeparator tNavigatePanelSeparator = new JSeparator();


		tNavigatePanelLayout.setHorizontalGroup(
				tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(GroupLayout.Alignment.TRAILING, tNavigatePanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)	
								.addComponent(tNavigatePanelSeparator, GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE)
								.addGroup(tNavigatePanelLayout.createSequentialGroup()
										.addComponent(welcomeLabel, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 797, Short.MAX_VALUE)
										.addGap(180, 180, 180)
								))
								.addContainerGap())
		);
		tNavigatePanelLayout.setVerticalGroup(
				tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tNavigatePanelLayout.createSequentialGroup()
						.addGap(16, 16, 16)
						.addGap(16, 16, 16)
						.addComponent(tNavigatePanelSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(welcomeLabel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
						)
						.addContainerGap())
		);



		JPanel tStatisticsPanel = new JPanel();

		JMenu tFileMenu= new JMenu();
		tFileMenu.setText("File");

		JMenuItem tExitMenuItem = new JMenuItem();
		JMenu tHelpMenu = new JMenu();
		tFileMenu.add(tExitMenuItem);


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

		this.usernameLabel = new JLabel("Username", JLabel.TRAILING);;
		this.usernameLabel.setFont(welcomeLabel.getFont());
		registrationPanel.add(this.usernameLabel);
		this.usernameInput = new JTextField(10);
		this.usernameInput.setText("");
		usernameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registrationPanel.add(this.usernameInput);
		this.usernameLabel.setLabelFor(this.usernameInput);

		this.passwordLabel = new JLabel("Password", JLabel.TRAILING);
		this.passwordLabel.setFont(welcomeLabel.getFont());
		registrationPanel.add(this.passwordLabel);
		this.passwordInput = new JPasswordField(10);
		this.passwordInput.setText("");
		passwordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registrationPanel.add(this.passwordInput);
		this.passwordLabel.setLabelFor(this.passwordInput);

		this.confirmPasswordLabel = new JLabel("Confirm Password", JLabel.TRAILING);
		this.confirmPasswordLabel.setFont(welcomeLabel.getFont());
		registrationPanel.add(this.confirmPasswordLabel);
		this.confirmPasswordInput = new JPasswordField(10);
		this.confirmPasswordInput.setText("");
		confirmPasswordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registrationPanel.add(this.confirmPasswordInput);
		this.confirmPasswordLabel.setLabelFor(this.confirmPasswordInput);

		this.emailLabel = new JLabel("Email", JLabel.TRAILING);
		this.emailLabel.setFont(welcomeLabel.getFont());
		registrationPanel.add(this.emailLabel);
		this.emailInput = new JTextField(10);
		this.emailInput.setText("");
		emailInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registrationPanel.add(this.emailInput);
		this.emailLabel.setLabelFor(this.emailInput);

		this.confirmEmailLabel = new JLabel("Confirm Email", JLabel.TRAILING);
		this.confirmEmailLabel.setFont(welcomeLabel.getFont());
		registrationPanel.add(this.confirmEmailLabel);
		this.confirmEmailInput = new JTextField(10);
		this.confirmEmailInput.setText("");
		confirmEmailInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registrationPanel.add(this.confirmEmailInput);
		this.confirmEmailLabel.setLabelFor(this.confirmEmailInput);

		this.firstNameLabel = new JLabel("First Name", JLabel.TRAILING);
		this.firstNameLabel.setFont(welcomeLabel.getFont());
		registrationPanel.add(this.firstNameLabel);
		this.firstNameInput = new JTextField(10);
		this.firstNameInput.setText("");
		firstNameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registrationPanel.add(this.firstNameInput);
		this.firstNameLabel.setLabelFor(this.firstNameInput);

		this.lastNameLabel = new JLabel("Last Name", JLabel.TRAILING);
		this.lastNameLabel.setFont(welcomeLabel.getFont());
		registrationPanel.add(this.lastNameLabel);
		this.lastNameInput = new JTextField(10);
		this.lastNameInput.setText("");
		lastNameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registrationPanel.add(this.lastNameInput);
		this.lastNameLabel.setLabelFor(this.lastNameInput);



		//		registrationPanel.add(this.registerButton);
		//	jRegistrationPanel.putConstraint(SpringLayout.NORTH, this.registerButton,
		//				5,
		//			SpringLayout.SOUTH, this.lastNameInput);
		//jRegistrationPanel.putConstraint(SpringLayout.WEST, this.registerButton,
		//	0,
		//SpringLayout.WEST, this.lastNameInput);

		//Lay out the panel.
		SpringUtilities.makeCompactGrid(registrationPanel,
				7, 2, //rows, cols
				6, 6,        //initX, initY
				25, 25);       //xPad, yPad


		tStatisticsPanel.setBorder(BorderFactory.createTitledBorder("Currently Connected")); // NOI18N

		GroupLayout tStatisticsPanelLayout = new GroupLayout(tStatisticsPanel);
		tStatisticsPanel.setLayout(tStatisticsPanelLayout);
		tStatisticsPanelLayout.setHorizontalGroup(
				tStatisticsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tStatisticsPanelLayout.createSequentialGroup()
						.addContainerGap(621, Short.MAX_VALUE))
		);
		tStatisticsPanelLayout.setVerticalGroup(
				tStatisticsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tStatisticsPanelLayout.createSequentialGroup()
						.addGap(29, 29, 29))
		);



		registerButton = new JButton();

		registerButton.setText("register");
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//				startWorkingAnimation("loading root subjects ...");
				String tCheck = checkDataValidity();
				if (tCheck.equals("O.K.")) {
					informationLabel.setText("");
					System.out.println("controller register");
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


		JButton tCancelButton = new JButton();
		tCancelButton.setText("cancel");
		tCancelButton.setPreferredSize(new Dimension(100, 50));
		tCancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				dispose();				
			}
			
		});
		
		registerButton.setPreferredSize(new Dimension(100, 50));

		this.mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(600, 620));

		JSeparator tMainPanelSeparator = new JSeparator();

		GroupLayout tMainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(tMainPanelLayout);
		tMainPanelLayout.setHorizontalGroup(
				tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tMainPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(navigatePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(informationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addComponent(registrationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addComponent(tMainPanelSeparator, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE)
								.addGroup(tMainPanelLayout.createSequentialGroup()
										.addComponent(registerButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(tCancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE))
						)
						.addContainerGap())
		);

		tMainPanelLayout.setVerticalGroup(
				tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tMainPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(navigatePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)
						.addComponent(informationPanel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addComponent(tMainPanelSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
						.addComponent(registrationPanel, 0, 400, Short.MAX_VALUE)
						.addGap(11, 11, 11)
						.addGroup(tMainPanelLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(registerButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE)
								.addComponent(tCancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
		);
		
		/*
		this.usernameInput.setText("yakirda");
		this.passwordInput.setText("123456");
		this.confirmPasswordInput.setText("123456");
		this.firstNameInput.setText("Yakir");
		this.lastNameInput.setText("Dahan");
		this.emailInput.setText("a@b.c");
		this.confirmEmailInput.setText("a@b.c");
		*/
	}

	public String checkDataValidity() {
		String toReturn = "O.K.";
		if (this.usernameInput.getText().length() < 4)
			toReturn = "The username must be at least 4 letters long";
		else if(this.passwordInput.getPassword().length < 6)
			toReturn = "The password must be at least 6 letters long";
		else if (!Arrays.equals(this.passwordInput.getPassword(), this.confirmPasswordInput.getPassword()))
			toReturn = "The password fields must be identical";
		else{
			String tEmail = this.emailInput.getText();
			//int tIndex = tEmail.indexOf('@');
			String tPattern = ".+@.+[.].+";
			if (!tEmail.matches(tPattern))
			//if ((tIndex == -1) || (tEmail.substring(tIndex + 2, tEmail.length() - 1).indexOf('.') == -1))
				toReturn = "Invaild address. The email address must be in the form: username@domain.extension";
			else if (!this.emailInput.getText().equals(this.confirmEmailInput.getText()))
				toReturn = "The email fields must be identical";
			else if (this.firstNameInput.getText().length() == 0)
				toReturn = "The field \"First Name\" is essential";
			else if (this.lastNameInput.getText().length() == 0)
				toReturn = "The field \"Last Name\" is essential";
		}
		return toReturn;
	}



	/*	public void startWorkingAnimation(String message) {
		//		statusLabel.setText(message);
	}

	public void stopWorkingAnimation() {
		try {
			Thread.sleep(100);
		} 
		catch (InterruptedException e) { 
			// Do nothing - continue to stop the animation
		}
		//		statusLabel.setText("");
	}
	 */












}
