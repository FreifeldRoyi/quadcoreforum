package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

public class LoginDialog extends JDialog implements GUIHandler, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//	private GUIHandler forumMainPanel;

	private JPanel mainPanel;
	private JButton loginButton;
	private JLinkButton forgotPasswordButton;
	private JButton cancelButton;
	private JLabel usernameLabel;
	private JTextField usernameInput;
	private JLabel passwordLabel;
	private JPasswordField passwordInput;
	private JPanel loginPanel;

	private JPanel informationPanel;
	private JLabel informationLabel;

	private long guestID;

	public ControllerHandler controller;

	@Override
	public void notifyError(String errorMessage) {
		if (errorMessage.startsWith("passwordupdate")) return;
		this.controller.deleteObserver(this);
		informationLabel.setText("Wrong username or password");
		this.usernameLabel.setForeground(Color.RED);
		this.passwordLabel.setForeground(Color.RED);
		this.usernameInput.selectAll();
		this.setEnabled(true);
		this.usernameInput.grabFocus();
	}

	@Override
	public void refreshForum(String encodedView) {
		if (encodedView.startsWith("activenumbers\t") ||
				encodedView.startsWith("passwordupdate")) return;
		this.controller.deleteObserver(this);
		this.cancelButton.doClick();
	}

	// KeyListener implementation
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			loginButton.doClick();
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			cancelButton.doClick();
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		this.informationLabel.setText("");
		this.passwordLabel.setForeground(Color.BLACK);
		this.usernameLabel.setForeground(Color.BLACK);
	}


	public LoginDialog(GUIHandler forumMainPanel, String username, String password, long guestID) throws IOException {
		super();
		this.guestID = guestID;
		this.setTitle("Login Page");
		controller = ControllerHandlerFactory.getPipe();


		initGUIComponents(username, password);
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.getContentPane().add(mainPanel);
		this.setMinimumSize(new Dimension(450, 300));

		this.setEnabled(true);
		this.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int Y = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, Y);
		this.setModal(true);
	}

	private void initGUIComponents(String username, String password) {

		this.informationPanel  = new JPanel();
		this.informationLabel = new JLabel("", JLabel.TRAILING);
		this.informationLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

		this.informationLabel.setForeground(new Color(255, 0, 0));
		informationPanel.add(informationLabel);


		this.loginPanel = new JPanel();
		Border tBlueBorder = BorderFactory.createLineBorder(Color.BLUE, 3);
		this.loginPanel.setBorder(BorderFactory.createTitledBorder(tBlueBorder, "login"));


		SpringLayout jLoginPanel = new SpringLayout();
		loginPanel.setLayout(jLoginPanel);

		this.usernameLabel = new JLabel("Username", JLabel.TRAILING);;
		this.usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		loginPanel.add(this.usernameLabel);
		this.usernameInput =  new JRestrictedLengthTextField(20, 20, false, true);  
		
		this.usernameInput.setText(username);
		usernameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));

		this.usernameInput.addKeyListener(this);


		loginPanel.add(this.usernameInput);
		this.usernameLabel.setLabelFor(this.usernameInput);

		this.passwordLabel = new JLabel("Password", JLabel.TRAILING);
		this.passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		loginPanel.add(this.passwordLabel);
		this.passwordInput = new JRestrictedLengthPasswordField(20, 20);
		this.passwordInput.setText(password);
		passwordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));


		this.passwordInput.addKeyListener(this);

		loginPanel.add(this.passwordInput);
		this.passwordLabel.setLabelFor(this.passwordInput);

		SpringUtilities.makeCompactGrid(loginPanel,
				2, 2, //rows, cols
				4, 4,        //initX, initY
				30, 30);       //xPad, yPad


		loginButton = new JButton();

		loginButton.setText("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				informationLabel.setText("");
				if(usernameInput.getText().length() > 0 && passwordInput.getPassword().length > 0) {
					controller.addObserver(new GUIObserver(LoginDialog.this), EventType.USER_CHANGED);
					LoginDialog.this.setEnabled(false);
					controller.login(LoginDialog.this.guestID, usernameInput.getText(),
							new String(passwordInput.getPassword()), LoginDialog.this);
				}
				else {
					informationLabel.setText("you must insert not empty user name and password");
					if (usernameInput.getText().length() == 0) {
						usernameLabel.setForeground(Color.red);
						usernameInput.selectAll();
						usernameInput.grabFocus();
						if (passwordInput.getPassword().length == 0)
							passwordLabel.setForeground(Color.red);
					}
					else {
						passwordLabel.setForeground(Color.red);
						passwordInput.selectAll();
						passwordInput.grabFocus();
					}
				}
			}
		}
		);

		forgotPasswordButton = new JLinkButton("forgot password?", Color.black);
		forgotPasswordButton.setForeground(Color.black);

		forgotPasswordButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					informationLabel.setText("");
					LoginDialog.this.setVisible(false);
					ForgotPasswordDialog tForgotPassDialog = new ForgotPasswordDialog();
					tForgotPassDialog.setVisible(true);
					LoginDialog.this.setVisible(true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}				
			}
		});


		cancelButton = new JButton("Cancel");

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LoginDialog.this.dispose();
			}
		});

		loginButton.setPreferredSize(new Dimension(100, 40));
		cancelButton.setPreferredSize(new Dimension(100, 40));

		this.mainPanel = new JPanel();

		GroupLayout tMainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(tMainPanelLayout);
		tMainPanelLayout.setHorizontalGroup(
				tMainPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(tMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(informationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(loginPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGroup(tMainPanelLayout.createSequentialGroup()
								.addComponent(forgotPasswordButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE)
								.addComponent(loginButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
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
						.addComponent(loginPanel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(0, 30, 30)
						.addGroup(tMainPanelLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(forgotPasswordButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addComponent(loginButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE)
								.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE))
								.addContainerGap()
				)
		);
	}	
}

