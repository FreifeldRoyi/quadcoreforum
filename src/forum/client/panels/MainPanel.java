package forum.client.panels;

import javax.swing.*;
import javax.swing.border.*;

import forum.client.controllerlayer.ControllerHandler;
import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.ForumTree;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

/**
 * The application's main frame.
 */
public class MainPanel extends JFrame implements GUIHandler {

	private static final long serialVersionUID = -5251318786616475794L;	

	private JPanel mainPanel;
	private JMenuBar mainPanelMenu;
	private JButton loginButton;
	private JButton registerButton;
	private JLabel welcomeLabel;
	private JButton fastLoginButton;
	private JLabel fastLoginUsernameLabel;
	private JTextField fastLoginUsernameInput;
	private JLabel fastLoginPasswordLabel;
	private JPasswordField fastLoginPasswordInput;
	private JLabel connectedStatisticsLabel;
	private JPanel navigatePanel;
	private JPanel fastLoginPanel;
	

	private Timer busyIconTimer;
	private static Icon[] busyIcons;
	private static Icon idleIcon;

	private JPanel statusPanel;
	private JLabel statusLabel;
	private JLabel statusAnimationLabel;

	private SubjectsPanel subjectsPanel;	
	private ThreadsPanel threadsPanel;	
	
	private JButton homeButton;
	
	private int activeGuestsNumber;
	private long activeMembersNumber;

	public static ControllerHandler controller;

	public void refreshForum(String encodedView) {
		// simulates a press on the home button
		this.homeButtonPress();
		this.setEnabled(true);
		this.stopWorkingAnimation();
		this.statusLabel.setText(encodedView);
		
		
		MainPanel.controller.getSubjects(-1, this.subjectsPanel);
		
		
	}

	public void notifyError(String error) {
		
		
		
	}
	
	private void homeButtonPress() {
		
	}
	
	public static void main(String[] args) {


		try {
			MainPanel.controller = ControllerHandlerFactory.getPipe();	
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "initialization error", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			SystemLogger.warning("Can't use default system look and feel, will use java default" +
			" look and feel style.");
		}

		MainPanel tMainPanel = new MainPanel();
		
		
		tMainPanel.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (tMainPanel.getWidth() / 2); // Center horizontally.
		int Y = (screen.height / 2) - (tMainPanel.getHeight() / 2); // Center vertically.
		tMainPanel.setLocation(X, Y);

		MainPanel.controller.addObserver(new GUIObserver(tMainPanel), EventType.USER_CHANGED);
		MainPanel.controller.registerAsNewGuest(tMainPanel);
		tMainPanel.setVisible(true);

	}

	public MainPanel() {
		super("QuadCoreForum Client Application");
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
	}

	private void initGUIComponents() {


		// prepares the navigate panel
		navigatePanel = new JPanel();
		loginButton = new JButton();
		registerButton = new JButton();
		welcomeLabel = new JLabel();

		loginButton.setText("login");
		registerButton.setText("register");
		welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 14)); // NOI18N
		welcomeLabel.setText("Hello guest!"); // NOI18N

		navigatePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		GroupLayout tNavigatePanelLayout = new GroupLayout(navigatePanel);
		navigatePanel.setLayout(tNavigatePanelLayout);
		JSeparator tNavigatePanelSeparator = new JSeparator();

		JButton tShowRootSubjects = new JButton();
		tShowRootSubjects.setText("Show root subjects");	
		
		tShowRootSubjects.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				startWorkingAnimation("loading root subjects ...");
				threadsPanel.setVisible(false);
				controller.getSubjects(-1, mainPanel);
			}
			
		});
		
		tNavigatePanelLayout.setHorizontalGroup(
				tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(GroupLayout.Alignment.TRAILING, tNavigatePanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)	
								.addComponent(tNavigatePanelSeparator, GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE)
								.addComponent(tShowRootSubjects, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
								.addGroup(tNavigatePanelLayout.createSequentialGroup()
										.addComponent(welcomeLabel, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 797, Short.MAX_VALUE)
										.addComponent(registerButton)
										.addGap(18, 18, 18)
										.addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
										.addContainerGap())
		);
		tNavigatePanelLayout.setVerticalGroup(
				tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tNavigatePanelLayout.createSequentialGroup()
						.addGap(16, 16, 16)
						.addComponent(tShowRootSubjects, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addGap(16, 16, 16)
						.addComponent(tNavigatePanelSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(welcomeLabel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(loginButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(registerButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
								.addContainerGap())
		);



		JPanel tStatisticsPanel = new JPanel();
		connectedStatisticsLabel = new JLabel();

		//MessagesTree tMessagesViewPanel = new MessagesTree("thread1", -1);
		
		ForumTree tree = new ForumTree(-1);

		
		
		this.threadsPanel = new ThreadsPanel(this, tree);
		this.threadsPanel.setVisible(false);
		this.subjectsPanel = new SubjectsPanel(this, this.threadsPanel);
		
		MainPanel.controller.addObserver(new GUIObserver(this.subjectsPanel) , EventType.SUBJECTS_UPDATED);
		MainPanel.controller.addObserver(new GUIObserver(this.threadsPanel) , EventType.THREADS_UPDATED);

		
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

		fastLoginUsernameLabel.setFont(welcomeLabel.getFont());
		fastLoginUsernameLabel.setText("Username");
		fastLoginUsernameInput.setText("");

		fastLoginPasswordLabel.setFont(welcomeLabel.getFont());
		fastLoginPasswordLabel.setText("Password");
		fastLoginPasswordInput.setText("");

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


		//jTable2.setModel(null


		/*new AbstractTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        rootMessagesTable.setRowHeight(40);
        jScrollPane2.setViewportView(rootMessagesTable);
		 */


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


	}

	public void startWorkingAnimation(String message) {
		statusLabel.setText(message);

		busyIconTimer.start();

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
