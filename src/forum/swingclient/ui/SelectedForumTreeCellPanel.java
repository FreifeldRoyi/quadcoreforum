package forum.swingclient.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import forum.server.domainlayer.user.Permission;
import forum.swingclient.controllerlayer.ConnectedUserData;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

/**
 * @author Tomer Heber
 *
 */
//occurredd
public class SelectedForumTreeCellPanel extends JPanel implements GUIHandler {

	private static final long serialVersionUID = 3195512056748314498L;

	private ForumTree m_forumTree;

	private JEditorPane m_area;
	private JButton m_modifyButton;
	private JButton m_replyButton;
	private JButton m_deleteButton;

	private String currentCellUsername;

/*	private void keyTypedEventFunction() {
		if (!m_area.getText().isEmpty() && allowModify) {
			m_area.setEditable(true);
			m_modifyButton.setEnabled(true);
		}
		else {
			m_area.setEditable(false);
			m_modifyButton.setEnabled(false);
		}
	}
*/
	public SelectedForumTreeCellPanel(ForumTree forumTree) {
		super();
		try {
			ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(this), EventType.USER_CHANGED);
		} catch (IOException e1) {

		}
		this.currentCellUsername = "";

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		m_forumTree = forumTree;

		m_area = new JEditorPane();
		m_area.setEditorKit(new HTMLEditorKit());
		m_area.setFont(new Font("Tahoma", 0, 14));
		m_area.setText("");
		m_area.setEditable(false);
		
		/*
		m_area.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				keyTypedEventFunction();
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}
		});*/

		JScrollPane scroll = new JScrollPane(m_area);		
		scroll.setPreferredSize(new Dimension(350,90));

		add(scroll);		

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		m_modifyButton = new JButton("Modify");
		m_modifyButton.setEnabled(false);
		m_modifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				m_forumTree.modifyMessage(m_modifyButton);
			}
		});

		m_replyButton = new JButton("Reply");
		
		m_replyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				m_forumTree.replyToMessage(m_replyButton);
			}
		});

		m_deleteButton = new JButton("Delete");
		m_deleteButton.setEnabled(false);
		m_deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_forumTree.deleteMessage(m_deleteButton);

			}
		});

		buttonPanel.add(m_modifyButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(m_replyButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5,0)));
		buttonPanel.add(m_deleteButton);

		this.add(buttonPanel);
		this.add(Box.createRigidArea(new Dimension(0,2)));	

		this.setPreferredSize(new Dimension(400,130));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.WHITE);
	}

	public void updatePanel(ForumCell cell, ConnectedUserData connectedUser) {
		if (cell == null) return;
		currentCellUsername = cell.getAuthorID();;
		if (connectedUser.getType() == ConnectedUserData.UserType.ADMIN || 
				connectedUser.getType() == ConnectedUserData.UserType.MODERATOR) {
				this.m_modifyButton.setEnabled(true);
				this.m_deleteButton.setEnabled(true);
		}
		else if (currentCellUsername.equals(connectedUser.getID() + ""))
			this.m_modifyButton.setEnabled(true);
		else
			this.m_modifyButton.setEnabled(false);
		
		if (connectedUser.isAllowed(Permission.REPLY_TO_MESSAGE))
			this.m_replyButton.setEnabled(true);
		else
			this.m_replyButton.setEnabled(false);
		m_area.setText(cell.getContent());
	}

	public void notifyError(String errorMessage) {}

	public void refreshForum(String encodedView) {

		if (encodedView.startsWith("register")  ||
				encodedView.startsWith("activenumbers\t") ||
				encodedView.startsWith("activeusernames\t") ||
				encodedView.startsWith("promoted\t") ||
				encodedView.startsWith("The")) return;



		if (encodedView.startsWith("loggedout")) {
			this.m_replyButton.setEnabled(false);			
			this.m_deleteButton.setEnabled(false);
			this.m_modifyButton.setEnabled(false);
		}
		else {
			String[] tSplitted = encodedView.split("\n");

			Collection<Permission> tPermissions = new Vector<Permission>();
			for (int i = 1; i < tSplitted.length; i++)
				tPermissions.add(Permission.valueOf(tSplitted[i]));

			if (tPermissions.contains(Permission.REPLY_TO_MESSAGE))
				this.m_replyButton.setEnabled(true);
			else
				this.m_replyButton.setEnabled(false);
		}
	}
}
