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
import javax.swing.GroupLayout.Alignment;

import javax.swing.border.Border;

import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

public class LoginDialog extends JDialog implements GUIHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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


	public ControllerHandler controller;


	@Override
	public void notifyError(String errorMessage) {
		System.out.println("login  --- error");
		if (errorMessage.startsWith("loginerror\t")) {
			String[] tSplittedMessage = errorMessage.split("\t");
			String tErrorMessage = tSplittedMessage[1];
			informationLabel.setText(tErrorMessage);
		}

	}

	@Override
	public void refreshForum(String encodedView) {
		System.out.println("Encoded = " + encodedView);
		if (encodedView.startsWith("loginsuccess\t")) {
			this.controller.deleteObserver(this);
			JOptionPane.showMessageDialog(this, "The login process was completed successfully!",
					"success", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		}
		else if (encodedView.startsWith("loginerror\t"))
			this.notifyError(encodedView);

	}
	/*
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			SystemLogger.warning("Can't use default system look and feel, will use java default" +
			" look and feel style.");
		}

		try {
			LoginDialog tLoginPanel = new LoginDialog();


			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int X = (screen.width / 2) - (tLoginPanel.getWidth() / 2); // Center horizontally.
			int Y = (screen.height / 2) - (tLoginPanel.getHeight() / 2); // Center vertically.
			tLoginPanel.setLocation(X, Y);

		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "initialization error", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}


	}*/

	public LoginDialog() throws IOException {
		super();
		this.setTitle("Login Page");
		controller = ControllerHandlerFactory.getPipe();


		initGUIComponents();
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.getContentPane().add(mainPanel);
		this.setMinimumSize(new Dimension(450, 300));

		this.addWindowListener(new WindowListener() {

			public void windowDeactivated(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}


			public void windowClosing(WindowEvent e) {
				SystemLogger.info("The client requested to finish the login without finish the operation.");
				controller.deleteObserver(LoginDialog.this);
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
		this.usernameInput = new JTextField(20);
		this.usernameInput.setText("");
		usernameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		loginPanel.add(this.usernameInput);
		this.usernameLabel.setLabelFor(this.usernameInput);

		this.passwordLabel = new JLabel("Password", JLabel.TRAILING);
		this.passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		loginPanel.add(this.passwordLabel);
		this.passwordInput = new JPasswordField(20);
		this.passwordInput.setText("");
		passwordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
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
				if(usernameInput.getText().length()>0 && passwordInput.getPassword().length>0)
				{
					System.out.println("controller login");
					controller.login(1,usernameInput.getText(),
							new String(passwordInput.getPassword()),loginButton);
				}
				else {
					informationLabel.setText("you must insert not empty user name and password");
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
					e1.printStackTrace();
				}				
			}
		});
		
		
		cancelButton = new JButton("Cancel");

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


	public static void main(String[] args) {
		try {
			new LoginDialog().setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}

