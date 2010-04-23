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

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
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
public class RegistrationPanel extends JFrame implements GUIHandler {

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
	private JTextField passwordInput;
	private JLabel confirmPasswordLabel;
	private JTextField confirmPasswordInput;
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
		this.setEnabled(true);
		//		this.stopWorkingAnimation();
	}

	public void notifyError(String error) {



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
			RegistrationPanel tRegistrationPanel = new RegistrationPanel();


			tRegistrationPanel.pack();
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

	public RegistrationPanel() throws IOException {
		super("Registration Page");
		controller = ControllerHandlerFactory.getPipe();

		initGUIComponents();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

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
				controller.closeConnection();
				System.exit(0);
			}
		});

		this.setEnabled(true);
		controller.addObserver(new GUIObserver(this), EventType.USER_CHANGED);
		this.setVisible(true);

	}

	private void initGUIComponents() {


		navigatePanel = new JPanel();
		registerButton = new JButton();
		welcomeLabel = new JLabel();

		registerButton.setText("register");
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//				startWorkingAnimation("loading root subjects ...");
				String tCheck = checkDataValidity();
				if (tCheck.equals("O.K.")) {
					informationLabel.setText("");
					//					controller.
				}
				else {
					informationLabel.setText(tCheck);
				}
			}

		});


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
		this.passwordInput = new JTextField(10);
		this.passwordInput.setText("");
		passwordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		registrationPanel.add(this.passwordInput);
		this.passwordLabel.setLabelFor(this.passwordInput);

		this.confirmPasswordLabel = new JLabel("Confirm Password", JLabel.TRAILING);
		this.confirmPasswordLabel.setFont(welcomeLabel.getFont());
		registrationPanel.add(this.confirmPasswordLabel);
		this.confirmPasswordInput = new JTextField(10);
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



		registrationPanel.add(this.registerButton);
		jRegistrationPanel.putConstraint(SpringLayout.NORTH, this.registerButton,
				5,
				SpringLayout.SOUTH, this.lastNameInput);
		jRegistrationPanel.putConstraint(SpringLayout.WEST, this.registerButton,
				0,
				SpringLayout.WEST, this.lastNameInput);

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
								.addComponent(tStatisticsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(tMainPanelSeparator, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE)
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
						.addComponent(registrationPanel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
						.addComponent(tStatisticsPanel, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)
						.addContainerGap())
		);
	}

	public String checkDataValidity() {
		String toReturn = "O.K.";
		if (this.usernameInput.getText().length() < 4)
			toReturn = "The username must be at least 4 letters long";
		else if(this.passwordInput.getText().length() < 6)
			toReturn = "The password must be at least 6 letters long";
		else if (!this.passwordInput.getText().equals(this.confirmPasswordInput.getText()))
			toReturn = "The password fields must be identical";
		else{
			String tEmail = this.emailInput.getText();
			int tIndex = tEmail.indexOf('@');
			if ((tIndex == -1) || (tEmail.substring(tIndex + 1).indexOf('.') == -1))
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
