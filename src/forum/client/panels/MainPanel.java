package forum.client.panels;

import javax.swing.*;
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
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Vector;

/**
 * The application's main frame.
 */
public class MainPanel extends JFrame implements GUIHandler {

	private static final long serialVersionUID = -5251318786616475794L;	

	private JButton btn_show_members;

	private JPanel mainPanel;
	private JMenuBar mainPanelMenu;
	private JButton btn_login;
	private JButton btn_register;
	private JButton btn_logout;
	private JButton btn_search;

	private JLabel lbl_welcome;
	private JButton fastLoginButton;
	private JLabel fastLoginUsernameLabel;
	private JTextField fastLoginUsernameInput;
	private JLabel fastLoginPasswordLabel;
	private JPasswordField fastLoginPasswordInput;
	private JPanel pnl_navigate;

	private JNavigatePanel pnl_links;

	private JPanel pnl_fastLogin;


	private Timer busyIconTimer;
	private static Icon[] busyIcons;
	private static Icon idleIcon;

	private JPanel statusPanel;
	private JLabel statusLabel;
	private JLabel statusAnimationLabel;

	private SubjectsPanel subjectsPanel;	
	private ThreadsPanel threadsPanel;	
	private ForumTree tree;

	public ControllerHandler controller;

	private ConnectedUserData connectedUser;


	public ConnectedUserData getConnectedUser() {		
		return this.connectedUser;
	}

	public void refreshForum(String encodedView) {
		// simulates a press on the home button

		if (encodedView.startsWith("register") || 
				encodedView.startsWith("activenumbers\t") ||
				encodedView.startsWith("activeusernames\t") ||
				encodedView.startsWith("promoted\t")) return;


		this.pnl_navigate.setVisible(false);

		if (!encodedView.startsWith("loggedout\t")) {


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
						tUserDetails[2], tUserDetails[3], tUserDetails[4], tPermissions);

		}
		else {

			String[] tSplitted = encodedView.split("\n");
			String[] tUserDetails = tSplitted[0].split("\t");

			long connectedUserID = Long.parseLong(tUserDetails[1]);

			Collection<Permission> tPermissions = new Vector<Permission>();
			for (int i = 1; i < tSplitted.length; i++)
				tPermissions.add(Permission.valueOf(tSplitted[i]));

			this.connectedUser = new ConnectedUserData(connectedUserID, tPermissions);

		}		

		if (connectedUser.getType() == ConnectedUserData.UserType.ADMIN || 
				connectedUser.getType() == ConnectedUserData.UserType.MODERATOR) {
				subjectsPanel.setModeratorOrAdminView();
		}
		else if (connectedUser.getType() == ConnectedUserData.UserType.MEMBER)
			subjectsPanel.setMemberView();
		else
			subjectsPanel.setGuestView();
		
		
		this.setEnabled(true);

		if (!this.connectedUser.isGuest()) {
			this.lbl_welcome.setText("Hello " + this.connectedUser.getLastAndFirstName() + "!");
			this.btn_login.setVisible(false);
			this.btn_logout.setVisible(true);			
			this.btn_register.setVisible(false);
			this.pnl_fastLogin.setVisible(false);
		}
		else {
			this.lbl_welcome.setText("Hello Guest!");
			this.btn_login.setVisible(true);
			this.btn_logout.setVisible(false);			
			this.btn_register.setVisible(true);

			this.pnl_fastLogin.setVisible(true);
			this.fastLoginButton.setEnabled(false);
		}

		this.fastLoginPasswordInput.setText("");
		this.fastLoginUsernameInput.setText("");

		this.stopWorkingAnimation();


		if (!this.threadsPanel.isVisible() && !this.threadsPanel.showsMessages())
			controller.getSubjects(-1, this.subjectsPanel);

		if (this.connectedUser.getID() == 0)
			btn_show_members.setVisible(true);
		else
			btn_show_members.setVisible(false);
		this.pnl_navigate.setVisible(true);

	}

	public void notifyError(String error) {
		JOptionPane.showMessageDialog(this, error, 
				"User identification error", JOptionPane.ERROR_MESSAGE);
	}


	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception e) {
			SystemLogger.warning("Can't use default system look and feel, will use java default" +
			" look and feel style.");
		}

		try {
			MainPanel tMainPanel = new MainPanel();



			tMainPanel.pack();
			tMainPanel.setSize(new Dimension(450, 500));
			//			tMainPanel.setPreferredSize(new Dimension(450, 500));

			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int X = (screen.width / 2) - (tMainPanel.getWidth() / 2); // Center horizontally.
			int Y = (screen.height / 2) - (tMainPanel.getHeight() / 2); // Center vertically.
			tMainPanel.setLocation(X, Y);
			tMainPanel.setExtendedState(MAXIMIZED_BOTH);
			tMainPanel.setVisible(true);


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
		this.setMinimumSize(new Dimension(850, 560));

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
	}

	private void initGUIComponents() {
		// prepares the navigate panel
		pnl_navigate = new JPanel();
		btn_login = new JButton("login");
		btn_register = new JButton("register");
		lbl_welcome = new JLabel();

		btn_logout = new JButton("logout");
		btn_logout.setVisible(false);


		this.btn_logout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				MainPanel.this.setEnabled(false);
				controller.logout(MainPanel.this, connectedUser.getUsername());
			}
		});


		lbl_welcome.setFont(new Font("Tahoma", Font.BOLD, 14)); // NOI18N
		lbl_welcome.setText("Hello guest!"); // NOI18N

		this.btn_register.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					RegistrationDialog tRegister = new RegistrationDialog();
					tRegister.setVisible(true);
				} 
				catch (IOException e) {
					SystemLogger.warning("Error while connection to the server!");
					btn_register.setEnabled(false);
				}

			}

		});
		
		btn_search = new JButton("search");

		btn_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SearchDialog tNewSearchDialog = new SearchDialog();
						tNewSearchDialog.setVisible(true);
					}
				}
				);
			}
		});


		btn_show_members = new JButton("show members");
		btn_show_members.setPreferredSize(new Dimension(100, 35));
		btn_show_members.setVisible(false);

		btn_show_members.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new MembersDialog(connectedUser.getID() == 0).setVisible(true);
			}
		});

		pnl_navigate.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		GroupLayout tNavigatePanelLayout = new GroupLayout(pnl_navigate);
		pnl_navigate.setLayout(tNavigatePanelLayout);
		JSeparator tNavigatePanelSeparator = new JSeparator();

		JLinkButton tShowRootSubjects = new JLinkButton("Show root subjects");

		tShowRootSubjects.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				startWorkingAnimation("loading root subjects ...");
				subjectsPanel.updateFields(-1, "");
				controller.getSubjects(-1, mainPanel);
			}

		});


		pnl_links = new JNavigatePanel(tShowRootSubjects);

		//tMainLinkButton.setPreferredSize(new Dimension(20, 10));

		JSeparator tVerticalSeparator = new JSeparator(SwingConstants.VERTICAL);

		tNavigatePanelLayout.setHorizontalGroup(
				tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(GroupLayout.Alignment.TRAILING, tNavigatePanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)	
								.addComponent(tNavigatePanelSeparator, GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE)
								.addComponent(pnl_links, GroupLayout.PREFERRED_SIZE, 600, Short.MAX_VALUE)
								.addGroup(tNavigatePanelLayout.createSequentialGroup()
										.addComponent(lbl_welcome, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 600, Short.MAX_VALUE)
										.addComponent(btn_show_members, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(btn_search, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(tVerticalSeparator, 1, 1, 1)
										.addGap(18, 18, 18)
										.addComponent(btn_register, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(btn_login, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)))
										.addGap(18, 18, 18)
										.addComponent(btn_logout, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
										.addContainerGap())
		);
		tNavigatePanelLayout.setVerticalGroup(
				tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tNavigatePanelLayout.createSequentialGroup()
						.addGap(5, 5, 5)
						.addComponent(pnl_links, GroupLayout.PREFERRED_SIZE, 35, 35)
						.addGap(5, 5, 5)
						.addComponent(tNavigatePanelSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(tNavigatePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(lbl_welcome, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(btn_logout, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(btn_search, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)							
								.addComponent(btn_show_members, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)							

								.addComponent(btn_register, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)							
								.addComponent(tVerticalSeparator, 35, 35, 35)
								.addComponent(btn_login, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
								.addContainerGap())
		);




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

		pnl_fastLogin = new JPanel();
		fastLoginButton = new JButton();
		fastLoginUsernameLabel = new JLabel();
		fastLoginUsernameInput = new JTextField();
		fastLoginPasswordLabel = new JLabel();
		fastLoginPasswordInput = new JPasswordField();

		Border tBlueBorder = BorderFactory.createLineBorder(Color.BLUE, 3);
		pnl_fastLogin.setBorder(BorderFactory.createTitledBorder(tBlueBorder, "Fast Login"));
		fastLoginButton.setText("Login");

		fastLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.login(connectedUser.getID(), fastLoginUsernameInput.getText(), 
						new String(fastLoginPasswordInput.getPassword()), mainPanel);
			}
		});


		fastLoginUsernameLabel.setFont(lbl_welcome.getFont());
		fastLoginUsernameLabel.setText("Username");
		fastLoginUsernameInput.setText("");

		fastLoginPasswordLabel.setFont(lbl_welcome.getFont());
		fastLoginPasswordLabel.setText("Password");
		fastLoginPasswordInput.setText("");


		this.fastLoginUsernameInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				fastLoginTextChanged();

			}

			public void keyTyped(KeyEvent e) {
			}
		});

		this.fastLoginPasswordInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				fastLoginTextChanged();
			}

			public void keyTyped(KeyEvent e) {
			}			
		});

		GroupLayout tFastLoginPanelLayout = new GroupLayout(pnl_fastLogin);
		pnl_fastLogin.setLayout(tFastLoginPanelLayout);
		tFastLoginPanelLayout.setHorizontalGroup(
				tFastLoginPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tFastLoginPanelLayout.createSequentialGroup()
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
		tFastLoginPanelLayout.setVerticalGroup(
				tFastLoginPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tFastLoginPanelLayout.createSequentialGroup()
						.addGroup(tFastLoginPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(fastLoginUsernameLabel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(fastLoginUsernameInput, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
								.addComponent(fastLoginPasswordLabel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(fastLoginPasswordInput, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
								.addComponent(fastLoginButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
		);










		// prepares the layout of the status panel 


		this.statusPanel = new JPanel();
		JSeparator tStatusPanelSeparator = new JSeparator();
		this.statusLabel = new JLabel();
		this.statusLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		this.statusLabel.setPreferredSize(new Dimension(700, 35));
		this.statusAnimationLabel = new JLabel();
		this.statusAnimationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
		statusPanel.setLayout(statusPanelLayout);
		statusPanelLayout.setHorizontalGroup(
				statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(tStatusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1159, Short.MAX_VALUE)
				.addGroup(statusPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(statusLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
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


		StatisticsPanel tStatisticsPanel = null;
		try {
			tStatisticsPanel = new StatisticsPanel();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.mainPanel = new JPanel();
		//		mainPanel.setPreferredSize(new Dimension(1159, 600));

		JSeparator tMainPanelSeparator = new JSeparator();

		GroupLayout tMainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(tMainPanelLayout);
		tMainPanelLayout.setHorizontalGroup(
				tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tMainPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)

								.addComponent(pnl_navigate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(statusPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(tStatisticsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(pnl_fastLogin, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
						.addComponent(pnl_navigate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)
						.addComponent(tree.getForumTreeUI(), 100, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.subjectsPanel, 100, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// GroupLayout.PREFERRED_SIZE)
	//					.addGap(0, 0, Short.MAX_VALUE)
						//						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
						.addComponent(this.threadsPanel, 100, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// GroupLayout.PREFERRED_SIZE)

		//				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)

			//			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						//              .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						//                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
						.addComponent(tMainPanelSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnl_fastLogin, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
						.addComponent(tStatisticsPanel, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)

						.addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
		);

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

		this.fastLoginButton.setEnabled(false);


		this.fastLoginPasswordInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					fastLoginButton.doClick();
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});

		this.fastLoginUsernameInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					fastLoginButton.doClick();
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		
	}

	public void switchToMessagesView() {
		this.subjectsPanel.setVisible(false);
		this.threadsPanel.setVisible(false);
		if (!this.tree.getForumTreeUI().isVisible()) {
			this.tree.selectFirstRow();
			this.tree.getForumTreeUI().setVisible(true);
		}
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


	public void addToNavigate(String text, ActionListener action) {
		this.pnl_links.insertLink(text, action);
		this.pnl_links.setVisible(false);
		this.pnl_links.setVisible(true);
	}

	public void removeFromNavigateUntil(String name) {
		this.pnl_links.removeAllBeforeAction(name);
	}

	public void startWorkingAnimation(final String message) {
		statusLabel.setText(message);
		busyIconTimer.start();
	}

	public void stopWorkingAnimation() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		busyIconTimer.stop();
		statusAnimationLabel.setIcon(MainPanel.idleIcon);
		statusLabel.setText("");
	}

	private void fastLoginTextChanged() {
		if (!fastLoginUsernameInput.getText().isEmpty() &&
				fastLoginPasswordInput.getPassword().length > 0) {
			fastLoginButton.setEnabled(true);
		}
		else
			fastLoginButton.setEnabled(false);
	}

}
