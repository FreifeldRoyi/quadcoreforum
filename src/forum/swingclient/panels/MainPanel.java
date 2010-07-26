package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.user.Permission;
import forum.swingclient.controllerlayer.ConnectedUserData;
import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.ForumTree;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

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
	private Component registerLoginGap;
	private Component profileShowMembersGap;


	private JButton btn_logout;
	private JButton btn_search;
	private JButton btn_changeProfile;

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

	private JLabel mainPanelSeparator;

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
				encodedView.startsWith("promoted\t") ||
				encodedView.startsWith("profiledetailsupdatesuccess\t") ||
				encodedView.startsWith("getpathsuccess") ||
				encodedView.startsWith("passwordupdate") ||
				encodedView.startsWith("memberdetails")) return;

		boolean shouldAskPasswordUpdate = false;


		if (!encodedView.startsWith("loggedout\t")) { // login


			String[] tSplitted = encodedView.split("\n");
			String[] tUserDetails = tSplitted[0].split("\t");

			long connectedUserID = Long.parseLong(tUserDetails[0]);

			Collection<Permission> tPermissions = new Vector<Permission>();
			for (int i = 1; i < tSplitted.length; i++)
				tPermissions.add(Permission.valueOf(tSplitted[i]));

			if (connectedUserID < 0) // guest
				this.connectedUser = new ConnectedUserData(connectedUserID, tPermissions);
			else {
				this.connectedUser = new ConnectedUserData(connectedUserID, tUserDetails[1], 
						tUserDetails[2], tUserDetails[3], tUserDetails[4], tUserDetails[5], tPermissions);
				if (tUserDetails[5].equals("ask_pass_update"))
					shouldAskPasswordUpdate = true;
			}


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

		this.controller.getActiveUsersNumber();


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
			this.registerLoginGap.setVisible(false);
			this.btn_logout.setVisible(true);			
			this.btn_register.setVisible(false);
			this.pnl_fastLogin.setVisible(false);
			btn_changeProfile.setVisible(true);
			this.profileShowMembersGap.setVisible(false);
			if (this.connectedUser.getID() == 0) {
				this.profileShowMembersGap.setVisible(true);
				btn_show_members.setVisible(true);
			}

		}
		else {
			this.profileShowMembersGap.setVisible(false);
			this.lbl_welcome.setText("Hello Guest!");
			this.btn_login.setVisible(true);
			this.registerLoginGap.setVisible(true);
			this.btn_logout.setVisible(false);			
			this.btn_register.setVisible(true);
			this.profileShowMembersGap.setVisible(false);
			this.pnl_fastLogin.setVisible(true);
			this.fastLoginButton.setEnabled(false);
			btn_show_members.setVisible(false);
			btn_changeProfile.setVisible(false);

		}

		this.fastLoginPasswordInput.setText("");
		this.fastLoginUsernameInput.setText("");

		this.stopWorkingAnimation();


		if (!this.threadsPanel.isVisible() && !this.threadsPanel.showsMessages())
			controller.getSubjects(-1, this.subjectsPanel);


		// Ask for password update if needed
		if (shouldAskPasswordUpdate) {
			ChangePasswordDialog tChangePasswordDlg =
				new ChangePasswordDialog(this.connectedUser.getID(), true);
			tChangePasswordDlg.setVisible(true);
		}
	}

	public void notifyError(String error) {
		if (!this.isActive())
			return;
		if (error.startsWith("profiledetailsupdateerror\t") ||
				error.startsWith("passwordupdate") ||
				error.startsWith("memberdetails\t")) return;

		this.setEnabled(true);

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



			tMainPanel.setSize(new Dimension(850, 560));
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
		this.pack();
	}

	private void initGUIComponents() {
		// prepares the navigate panel
		pnl_navigate = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9062536731426386680L;

			public void paint(Graphics g) {
				g.drawImage(new ImageIcon("./images/background2.jpg").getImage(), 
						0, 0, 1680, 1050, null);
				setOpaque(false);
				super.paint(g);
			}
		};

		btn_login = new JButton("login");

		registerLoginGap = Box.createRigidArea(new Dimension(18, 35));
		profileShowMembersGap = Box.createRigidArea(new Dimension(18, 35));

		this.btn_login.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					String tUsername = fastLoginUsernameInput.getText();
					String tPassword = new String(fastLoginPasswordInput.getPassword());
					fastLoginUsernameInput.setText("");
					fastLoginPasswordInput.setText("");
					fastLoginButton.setEnabled(false);

					LoginDialog tLogin = new LoginDialog(MainPanel.this, tUsername, tPassword, connectedUser.getID());
					tLogin.setVisible(true);
				} 
				catch (IOException e) {
					SystemLogger.warning("Error while connection to the server!");
					btn_login.setEnabled(false);
				}
			}
		});

		btn_register = new JButton("register");


		lbl_welcome = new JLabel();

		lbl_welcome.setForeground(Color.WHITE);

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
		lbl_welcome.setOpaque(false);



		this.btn_register.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					fastLoginUsernameInput.setText("");
					fastLoginPasswordInput.setText("");
					fastLoginButton.setEnabled(false);
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
						fastLoginUsernameInput.setText("");
						fastLoginPasswordInput.setText("");
						SearchDialog tNewSearchDialog = new SearchDialog();
						tNewSearchDialog.setVisible(true);
						try {
							// explanation: if the threads panel is visible then the number of views should
							// be incremented always, otherwise, only if the ids are different
							if (tNewSearchDialog.getSelectedID() != -1) {
								System.out.println(MainPanel.this.threadsPanel.isVisible() + " ooooooooooooooo");
								ControllerHandlerFactory.getPipe().getPath(MainPanel.this,
										MainPanel.this.threadsPanel.isVisible()? -1 :
											MainPanel.this.tree.getFatherMessageID(),
											tNewSearchDialog.getSelectedID());
							}
						} 
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				);
			}
		});


		btn_show_members = new JButton("show members");
		btn_show_members.setPreferredSize(new Dimension(100, 35));
		btn_show_members.setVisible(false);

		btn_changeProfile = new JButton("Change profile");
		btn_changeProfile.setPreferredSize(new Dimension(100, 35));
		btn_changeProfile.setVisible(false);

		btn_changeProfile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					ProfileDialog tUpdateDetailsDialog = 
						new ProfileDialog(connectedUser.getID(), connectedUser.getUsername(),
								connectedUser.getFirstName(), 
								connectedUser.getLastName(), 
								connectedUser.getEmail(), true);

					tUpdateDetailsDialog.setVisible(true);
					
					if (!tUpdateDetailsDialog.shouldUpdateData())
						return;

					startWorkingAnimation("retreiving updated details...");

					controller.addObserver(new GUIObserver(new GUIHandler() {

						@Override
						public void refreshForum(String encodedView) {
							if (!encodedView.startsWith("memberdetails"))
								return;
							controller.deleteObserver(this);

							String[] tSplitted = encodedView.split("\t");

							if (!tSplitted[1].equals(connectedUser.getUsername()))
								return;

							connectedUser.setFirstName(tSplitted[2]);
							connectedUser.setLastName(tSplitted[3]);
							connectedUser.setEmail(tSplitted[4]);

							lbl_welcome.setText("Hello " + connectedUser.getLastAndFirstName() + "!");

							stopWorkingAnimation();
						}

						@Override
						public void notifyError(String errorMessage) {
							if (!errorMessage.startsWith("memberdetailserror"))
								return;
							controller.deleteObserver(this);
							stopWorkingAnimation();

						}
					}), EventType.USER_CHANGED);

					controller.getMemberDetails(connectedUser.getID(), MainPanel.this);

				} 
				catch (IOException e) {
					return;
				}
			}
		});


		btn_show_members.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					new MembersDialog(connectedUser.getID() == 0).setVisible(true);
				}
				catch (IOException e) {
					return;
				}
			}
		});

		pnl_navigate.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		GroupLayout tNavigatePanelLayout = new GroupLayout(pnl_navigate);
		pnl_navigate.setLayout(tNavigatePanelLayout);
		JSeparator tNavigatePanelSeparator = new JSeparator();

		JLinkButton tShowRootSubjects = new JLinkButton("Show root subjects");

		tShowRootSubjects.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				subjectsPanel.setFatherID(-1);
				startWorkingAnimation("loading root subjects ...");
				subjectsPanel.updateFields(-1, "");
				controller.getSubjects(-1, mainPanel);
			}

		});


		pnl_links = new JNavigatePanel(tShowRootSubjects);

		pnl_links.setOpaque(false);
		//tMainLinkButton.setPreferredSize(new Dimension(20, 10));


		//		btn_register.setPreferredSize(new Dimension(85, 35));

		//	JPanel pnl_register = new JPanel();
		//pnl_register.setOpaque(false);
		//pnl_register.setLayout(new FlowLayout(FlowLayout.LEFT));
		//pnl_register.add(btn_register);
		//pnl_register.add();

		JSeparator tVerticalSeparator = new JSeparator(SwingConstants.VERTICAL);


		tNavigatePanelLayout.setHorizontalGroup(		
				tNavigatePanelLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(tNavigatePanelLayout.createParallelGroup()
						.addComponent(pnl_links, GroupLayout.PREFERRED_SIZE, 600, Short.MAX_VALUE)
						.addComponent(tNavigatePanelSeparator, GroupLayout.PREFERRED_SIZE, 500, Short.MAX_VALUE)
						.addGroup(tNavigatePanelLayout.createSequentialGroup()
								.addComponent(lbl_welcome, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 600, Short.MAX_VALUE)

								.addComponent(btn_changeProfile, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
								.addComponent(profileShowMembersGap, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
								.addComponent(btn_show_members, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(btn_search, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(tVerticalSeparator, 1, 1, 1)
								.addGap(18, 18, 18)
								.addComponent(btn_register, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
								.addComponent(registerLoginGap, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
								.addComponent(btn_login, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
								.addComponent(btn_logout, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)))
								.addContainerGap());



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
								.addComponent(btn_changeProfile, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
								.addComponent(profileShowMembersGap, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
								.addComponent(btn_show_members, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)							
								.addComponent(registerLoginGap, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)

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

		


		// prepares the fast login panel

		pnl_fastLogin = new JPanel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 8175327762515671628L;

			public void paint(Graphics g) {
				g.drawImage(new ImageIcon("./images/background2.jpg").getImage(), 
						0, 0, 1680, 1050, null);
				setOpaque(false);
				super.paint(g);
			}

		};
		fastLoginButton = new JButton();
		fastLoginUsernameLabel = new JLabel();
		fastLoginUsernameInput = new JRestrictedLengthTextField(20, 20, false, true);
		fastLoginPasswordLabel = new JLabel();
		fastLoginPasswordInput = new JRestrictedLengthPasswordField(20, 20);

		pnl_fastLogin.setBorder(BorderFactory.createEtchedBorder());
		pnl_fastLogin.setBorder(BorderFactory.createTitledBorder(pnl_fastLogin.getBorder(), "Fast Login", 0, 0,
				new Font("Tahoma", Font.BOLD, 12), Color.WHITE));

		fastLoginButton.setText("Login");

		fastLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.login(connectedUser.getID(), fastLoginUsernameInput.getText(), 
						new String(fastLoginPasswordInput.getPassword()), mainPanel);
			}
		});


		fastLoginUsernameLabel.setFont(lbl_welcome.getFont());
		fastLoginUsernameLabel.setForeground(Color.WHITE);

		fastLoginUsernameLabel.setText("Username");
		fastLoginUsernameInput.setText("");

		fastLoginPasswordLabel.setFont(lbl_welcome.getFont());
		fastLoginPasswordLabel.setForeground(Color.WHITE);
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

		this.mainPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 4663000630699083767L;

			public void paint(Graphics g) {
				g.drawImage(new ImageIcon("./images/background1.jpg").getImage(), 
						0, 0, 1920, 1200, null);
				setOpaque(false);
				super.paint(g);
			}
		};
		//		mainPanel.setPreferredSize(new Dimension(1159, 600));

		mainPanelSeparator = new JLabel();
		mainPanelSeparator.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));




		//		104, 413, 22



		System.out.println(pnl_navigate.getHeight());
		System.out.println(subjectsPanel.getHeight());
		System.out.println(statusPanel.getHeight());



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
								.addComponent(mainPanelSeparator, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.threadsPanel, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE)								

								.addComponent(tree.getForumTreeUI(), GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE))															
								//								.addComponent(tMainPanelSeparator, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE))
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
						.addComponent(mainPanelSeparator, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGap(16, 16, 16)
						//					.addGap(0, 0, Short.MAX_VALUE)
						//						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
						.addComponent(this.threadsPanel, 100, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// GroupLayout.PREFERRED_SIZE)

						//				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)

						//			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						//              .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						//                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
						//.addComponent(tMainPanelSeparator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)

						.addComponent(pnl_fastLogin, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
						.addGap(11, 11, 11)
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

		try {
			ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(subjectsPanel), EventType.SEARCH_UPDATED);
			ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(tree), EventType.SEARCH_UPDATED);

		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	public void switchToMessagesView() {
		this.subjectsPanel.setVisible(false);
		this.threadsPanel.setVisible(false);
		if (!this.tree.getForumTreeUI().isVisible()) {
			this.tree.getForumTreeUI().setVisible(true);
		}
	}

	public void switchToRootSubjectsView() {
		this.mainPanelSeparator.setVisible(false);
		this.subjectsPanel.setVisible(true);
		this.threadsPanel.setVisible(false);
		this.tree.getForumTreeUI().setVisible(false);
	}

	public void switchToSubjectsAndThreadsView() {
		this.mainPanelSeparator.setVisible(true);
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
