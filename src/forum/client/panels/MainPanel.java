package forum.client.panels;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.*;

import forum.client.controllerlayer.ConnectedUserData;
import forum.client.controllerlayer.ControllerHandler;
import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.ForumTree;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.user.Permission;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

/**
 * The application's main frame.
 */
public class MainPanel extends JFrame implements GUIHandler {

	private static final long serialVersionUID = -5251318786616475794L;	

	private JPanel mainPanel;
	private JMenuBar mainPanelMenu;
	private JButton loginButton;
	private JButton registerButton;
	private JButton logoutButton;

	private JLabel welcomeLabel;
	private JButton fastLoginButton;
	private JLabel fastLoginUsernameLabel;
	private JTextField fastLoginUsernameInput;
	private JLabel fastLoginPasswordLabel;
	private JPasswordField fastLoginPasswordInput;
	private JLabel connectedStatisticsLabel;
	private JPanel navigatePanel;

	private JNavigatePanel linksPanel;
	
	private JPanel fastLoginPanel;


	private Timer busyIconTimer;
	private static Icon[] busyIcons;
	private static Icon idleIcon;

	private JPanel statusPanel;
	private JLabel statusLabel;
	private JLabel statusAnimationLabel;

	private SubjectsPanel subjectsPanel;	
	private ThreadsPanel threadsPanel;	
	private ForumTree tree;

	private JButton homeButton;

	private int activeGuestsNumber;
	private long activeMembersNumber;

	public ControllerHandler controller;


	private ConnectedUserData connectedUser;


	public ConnectedUserData getConnectedUser() {		
		return this.connectedUser;
	}

	public void refreshForum(String encodedView) {
		// simulates a press on the home button

		System.out.println(encodedView + " kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
		String[] tSplitted = encodedView.split("\n");
		String[] tUserDetails = tSplitted[0].split("\t");

		long connectedUserID = Long.parseLong(tUserDetails[0]);

		Collection<Permission> tPermissions = new Vector<Permission>();
		for (int i = 1; i < tSplitted.length; i++)
			tPermissions.add(Permission.valueOf(tSplitted[i]));

		if (connectedUserID < 0) // guest
			this.connectedUser = new ConnectedUserData(connectedUserID, tPermissions);
		else
			this.connectedUser = new ConnectedUserData(connectedUserID, tUserDetails[1], 
					tUserDetails[2], tUserDetails[3], tPermissions);

		this.homeButtonPress();
		this.setEnabled(true);

		if (!this.connectedUser.isGuest()) {
			this.welcomeLabel.setText("Hello " + this.connectedUser.getLastAndFirstName() + "!");
			this.loginButton.setVisible(false);
			this.logoutButton.setVisible(true);			
			this.registerButton.setVisible(false);
			this.fastLoginUsernameInput.setEnabled(false);
			this.fastLoginPasswordInput.setEnabled(false);
			this.fastLoginButton.setEnabled(false);
		}
		else {
			this.welcomeLabel.setText("Hello Guest!");
			this.loginButton.setVisible(true);
			this.logoutButton.setVisible(false);			
			this.registerButton.setVisible(true);
			this.fastLoginUsernameInput.setEnabled(true);
			this.fastLoginPasswordInput.setEnabled(true);
			this.fastLoginButton.setEnabled(true);

		}

		this.fastLoginPasswordInput.setText("");
		this.fastLoginUsernameInput.setText("");

		this.stopWorkingAnimation();

		controller.getSubjects(-1, this.subjectsPanel);


	}

	public void notifyError(String error) {
		JOptionPane.showMessageDialog(this, error, 
				"User identification error", JOptionPane.ERROR_MESSAGE);
	}

	private void homeButtonPress() {

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
			MainPanel tMainPanel = new MainPanel();
			

			tMainPanel.pack();
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int X = (screen.width / 2) - (tMainPanel.getWidth() / 2); // Center horizontally.
			int Y = (screen.height / 2) - (tMainPanel.getHeight() / 2); // Center vertically.
			tMainPanel.setLocation(X, Y);

		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "initialization error", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}


	}

	public MainPanel() throws IOException {
		super("QuadCoreForum Client Application");
		controller = ControllerHandlerFactory.getPipe();			

		initGUIComponents();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setJMenuBar(mainPanelMenu);

		this.getContentPane().add(mainPanel);
		this.setMinimumSize(new Dimension(700, 560));

		this.addWindowListener(new WindowListener() {

			public void windowDeactivated(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}


			public void windowClosing(WindowEvent e) {
				SystemLogger.info("The client requested to stop the application.");
				SystemLogger.info("Closing connections");
				controller.closeConnection();
				SystemLogger.info("Done.");
				System.exit(0);
			}
		});

		this.setEnabled(false);
		this.startWorkingAnimation("Connecting as a guest ...");		
		controller.addObserver(new GUIObserver(this), EventType.USER_CHANGED);
		controller.registerAsNewGuest(this);
		this.setVisible(true);

	}

	private void fastLoginTextChanged() {
		System.out.println("fastlogin");
		if (!fastLoginUsernameInput.getText().isEmpty() &&
				fastLoginPasswordInput.getPassword().length > 0) {
			fastLoginButton.setEnabled(true);
System.out.println(fastLoginUsernameInput.getText());
System.out.println(fastLoginPasswordInput.getPassword().length);
		}
		else {
			System.out.println("ddd");
			fastLoginButton.setEnabled(false);
		}
	}


	private void initGUIComponents() {


		// prepares the navigate panel
		navigatePanel = new JPanel();
		loginButton = new JButton();
		registerButton = new JButton();
		welcomeLabel = new JLabel();

		logoutButton = new JButton();
		logoutButton.setText("logout");
		logoutButton.setVisible(false);


		loginButton.setText("login");
		registerButton.setText("register");
		welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 14)); // NOI18N
		welcomeLabel.setText("Hello guest!"); // NOI18N

		navigatePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		GroupLayout tNavigatePanelLayout = new GroupLayout(navigatePanel);
		navigatePanel.setLayout(tNavigatePanelLayout);
		JSeparator tNavigatePanelSeparator = new JSeparator();

		JLinkButton tShowRootSubjects = new JLinkButton("Show root subjects");

		tShowRootSubjects.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				startWorkingAnimation("loading root subjects ...");
				subjectsPanel.updateFields(-1, "");
				controller.getSubjects(-1, mainPanel);
			}

		});

		
		linksPanel = new JNavigatePanel(tShowRootSubjects);
				
		//tMainLinkButton.setPreferredSize(new Dimension(20, 10));
		
		tNavigatePanelLayout.setHorizontalGroup(
				tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(GroupLayout.Alignment.TRAILING, tNavigatePanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)	
								.addComponent(tNavigatePanelSeparator, GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE)
								.addComponent(linksPanel, GroupLayout.PREFERRED_SIZE, 600, Short.MAX_VALUE)
								.addGroup(tNavigatePanelLayout.createSequentialGroup()
										.addComponent(welcomeLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 600, Short.MAX_VALUE)
										.addComponent(registerButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)										
										.addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
										.addGap(18, 18, 18)
										.addComponent(logoutButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
										.addContainerGap())
		);
		tNavigatePanelLayout.setVerticalGroup(
				tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tNavigatePanelLayout.createSequentialGroup()
						.addGap(5, 5, 5)
						.addComponent(linksPanel, GroupLayout.PREFERRED_SIZE, 35, 35)
						.addGap(5, 5, 5)
						.addComponent(tNavigatePanelSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(welcomeLabel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(logoutButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(registerButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)							
								.addComponent(loginButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
								.addContainerGap())
		);



		JPanel tStatisticsPanel = new JPanel();
		connectedStatisticsLabel = new JLabel();

		//MessagesTree tMessagesViewPanel = new MessagesTree("thread1", -1);

		tree = new ForumTree(this);

		this.threadsPanel = new ThreadsPanel(this, tree);
		this.threadsPanel.setVisible(false);
		this.subjectsPanel = new SubjectsPanel(this, this.threadsPanel);

		controller.addObserver(new GUIObserver(this.subjectsPanel) , EventType.SUBJECTS_UPDATED);
		controller.addObserver(new GUIObserver(this.threadsPanel) , EventType.THREADS_UPDATED);


		//		this.threadsPanel.setVisible(false);
		this.subjectsPanel.setVisible(true);
		tree.getForumTreeUI().setVisible(false);


		mainPanelMenu = new JMenuBar();

		JMenu tFileMenu= new JMenu();
		tFileMenu.setText("File");

		JMenuItem tExitMenuItem = new JMenuItem();
		JMenu tHelpMenu = new JMenu();
		tFileMenu.add(tExitMenuItem);

		mainPanelMenu.add(tFileMenu);

		mainPanelMenu.add(tHelpMenu);


		// prepares the fast login panel

		fastLoginPanel = new JPanel();
		fastLoginButton = new JButton();
		fastLoginUsernameLabel = new JLabel();
		fastLoginUsernameInput = new JTextField();
		fastLoginPasswordLabel = new JLabel();
		fastLoginPasswordInput = new JPasswordField();

		Border tBlueBorder = BorderFactory.createLineBorder(Color.BLUE, 3);
		fastLoginPanel.setBorder(BorderFactory.createTitledBorder(tBlueBorder, "Fast Login"));
		fastLoginButton.setText("Login");

		fastLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.login(fastLoginUsernameInput.getText(), 
						new String(fastLoginPasswordInput.getPassword()), mainPanel);
			}
		});


		fastLoginUsernameLabel.setFont(welcomeLabel.getFont());
		fastLoginUsernameLabel.setText("Username");
		fastLoginUsernameInput.setText("");

		fastLoginPasswordLabel.setFont(welcomeLabel.getFont());
		fastLoginPasswordLabel.setText("Password");
		fastLoginPasswordInput.setText("");


		this.fastLoginUsernameInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}

			public void keyTyped(KeyEvent e) {
				fastLoginTextChanged();
			}
		});

		this.fastLoginPasswordInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}

			public void keyTyped(KeyEvent e) {
				fastLoginTextChanged();
			}			
		});






		GroupLayout jFastLoginPanelLayout = new GroupLayout(fastLoginPanel);
		fastLoginPanel.setLayout(jFastLoginPanelLayout);
		jFastLoginPanelLayout.setHorizontalGroup(
				jFastLoginPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jFastLoginPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(fastLoginUsernameLabel, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(fastLoginUsernameInput, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(fastLoginPasswordLabel, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(fastLoginPasswordInput, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
						.addGap(27, 27, 27)
						.addComponent(fastLoginButton, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(468, Short.MAX_VALUE))
		);
		jFastLoginPanelLayout.setVerticalGroup(
				jFastLoginPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(jFastLoginPanelLayout.createSequentialGroup()
						.addGroup(jFastLoginPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(fastLoginUsernameLabel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(fastLoginUsernameInput, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
								.addComponent(fastLoginPasswordLabel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(fastLoginPasswordInput, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
								.addComponent(fastLoginButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
		);

		
		tStatisticsPanel.setBorder(BorderFactory.createTitledBorder("Currently Connected")); // NOI18N

		connectedStatisticsLabel.setFont(welcomeLabel.getFont()); // NOI18N
		connectedStatisticsLabel.setText("Total: x are connected, y are registered users and z are guests"); // NOI18N

		GroupLayout tStatisticsPanelLayout = new GroupLayout(tStatisticsPanel);
		tStatisticsPanel.setLayout(tStatisticsPanelLayout);
		tStatisticsPanelLayout.setHorizontalGroup(
				tStatisticsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tStatisticsPanelLayout.createSequentialGroup()
						.addComponent(connectedStatisticsLabel, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(621, Short.MAX_VALUE))
		);
		tStatisticsPanelLayout.setVerticalGroup(
				tStatisticsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tStatisticsPanelLayout.createSequentialGroup()
						.addComponent(connectedStatisticsLabel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
						.addGap(29, 29, 29))
		);



		// prepares the layout of the status panel 

		this.statusPanel = new JPanel();
		JSeparator tStatusPanelSeparator = new JSeparator();
		this.statusLabel = new JLabel();
		this.statusLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		this.statusAnimationLabel = new JLabel();
		this.statusAnimationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
		statusPanel.setLayout(statusPanelLayout);
		statusPanelLayout.setHorizontalGroup(
				statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(tStatusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1159, Short.MAX_VALUE)
				.addGroup(statusPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(statusLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1139, Short.MAX_VALUE)
						.addComponent(statusAnimationLabel)
						.addContainerGap())
		);
		statusPanelLayout.setVerticalGroup(
				statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(statusPanelLayout.createSequentialGroup()
						.addComponent(tStatusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(statusLabel)
								.addComponent(statusAnimationLabel)))
		);


		// prepares the layout of the main panel




		this.mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(1159, 600));

		JSeparator tMainPanelSeparator = new JSeparator();

		GroupLayout tMainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(tMainPanelLayout);
		tMainPanelLayout.setHorizontalGroup(
				tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tMainPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)

								.addComponent(navigatePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(statusPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(tStatisticsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(fastLoginPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.subjectsPanel, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE)


								.addComponent(this.threadsPanel, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE)								



								.addComponent(tree.getForumTreeUI(), GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE)																
								.addComponent(tMainPanelSeparator, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE))

								.addContainerGap())
		);

		tMainPanelLayout.setVerticalGroup(
				tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tMainPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(navigatePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)
						.addComponent(tree.getForumTreeUI(), 100, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.subjectsPanel, 100, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// GroupLayout.PREFERRED_SIZE)

						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
						.addComponent(this.threadsPanel, 100, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// GroupLayout.PREFERRED_SIZE)


						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)

						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						//              .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						//                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
						.addComponent(tMainPanelSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
						.addComponent(fastLoginPanel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
						.addComponent(tStatisticsPanel, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)

						.addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
		);

		//		this.threadsPanel.setVisible(true);
		//		this.subjectsPanel.setVisible(true);


		busyIcons = new ImageIcon[15];
		for (int i = 0; i < busyIcons.length; i++)
			busyIcons[i] = new ImageIcon("images/busyicons/busy-icon" + i + ".png");
		MainPanel.idleIcon = new ImageIcon("images/busyicons/idle-icon.png");

		this.busyIconTimer = new Timer(30, new ActionListener() {
			int busyIconIndex = 0;
			public void actionPerformed(ActionEvent e) {
				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
			}
		});

		
		this.busyIconTimer.setRepeats(true);
		//this.switchToMessagesView();
	}

	public void switchToMessagesView() {
		this.subjectsPanel.setVisible(false);
		this.threadsPanel.setVisible(false);
		this.tree.getForumTreeUI().setVisible(true);
	}

	public void switchToRootSubjectsView() {
		this.subjectsPanel.setVisible(true);
		this.threadsPanel.setVisible(false);
		this.tree.getForumTreeUI().setVisible(false);
	}

	public void switchToSubjectsAndThreadsView() {
		this.subjectsPanel.setVisible(true);
		this.threadsPanel.setVisible(true);
		this.tree.getForumTreeUI().setVisible(false);
	}


	public void startWorkingAnimation(String message) {
		statusLabel.setText(message);

		busyIconTimer.start();

	}

	public void addToNavigate(String text, ActionListener action) {
		this.linksPanel.insertLink(text, action);
		this.linksPanel.setVisible(false);
		this.linksPanel.setVisible(true);
	}
	
	public void removeFromNavigateUntil(String name) {
		this.linksPanel.removeAllBeforeAction(name);
	}
	
	public void stopWorkingAnimation() {
		try {
			Thread.sleep(100);
		} 
		catch (InterruptedException e) { 
			// Do nothing - continue to stop the animation
		}
		busyIconTimer.stop();
		statusAnimationLabel.setIcon(MainPanel.idleIcon);
		statusLabel.setText("");
	}
}
