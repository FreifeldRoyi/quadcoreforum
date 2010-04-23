package forum.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import forum.client.controllerlayer.ConnectedUserData;
import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.user.Permission;

/**
 * @author Tomer Heber
 *
 */
public class SelectedForumTreeCellPanel extends JPanel implements GUIHandler {

	private static final long serialVersionUID = 3195512056748314498L;

	private ForumTree m_forumTree;
	private JTextArea m_area;
	private JButton m_modifyButton;
	private JButton m_replyButton;
	private JButton m_deleteButton;

	private String currentCellUsername;
	private boolean allowModify;	

	private void keyTypedEventFunction() {
		if (!m_area.getText().isEmpty() && allowModify) {
			m_area.setEditable(true);
			m_modifyButton.setEnabled(true);
			m_deleteButton.setEnabled(true);
		}
		else {
			m_area.setEditable(false);
			m_modifyButton.setEnabled(false);
			m_deleteButton.setEnabled(false);
		}
	}
	
	public SelectedForumTreeCellPanel(ForumTree forumTree) {
		super();
		try {
			ControllerHandlerFactory.getPipe().addObserver(new GUIObserver(this), EventType.USER_CHANGED);
		} catch (IOException e1) {
			
		}
		this.allowModify = false;
		this.currentCellUsername = "";

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		m_forumTree = forumTree;

		m_area = new JTextArea();
		m_area.setFont(new Font("Tahoma", 0, 14));
		m_area.setText("");
		m_area.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				keyTypedEventFunction();
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}
		});

		JScrollPane scroll = new JScrollPane(m_area);		
		scroll.setPreferredSize(new Dimension(350,90));

		add(scroll);		

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		m_modifyButton = new JButton("Modify");
		m_modifyButton.setEnabled(false);
		m_modifyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				m_forumTree.modifyMessage(m_area.getText(),m_modifyButton);
			}
		});

		m_replyButton = new JButton("Reply");
		m_replyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				m_forumTree.replyToMessage(m_replyButton);
			}
		});

		m_deleteButton = new JButton("Delete");
		m_deleteButton.addActionListener(new ActionListener() {

			@Override
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
		currentCellUsername = cell.getAuthorUsername();;
		if (currentCellUsername.equals(connectedUser.getID() + ""))
			this.allowModify = true;
		else
			this.allowModify = false;
		if (connectedUser.isAllowed(Permission.REPLY_TO_MESSAGE))
			this.m_replyButton.setEnabled(true);
		else
			this.m_replyButton.setEnabled(false);
		m_area.setText(cell.toString());
		this.keyTypedEventFunction();
	}



	public void notifyError(String errorMessage) {}

	public void refreshForum(String encodedView) {
		String[] tSplitted = encodedView.split("\n");
		
		Collection<Permission> tPermissions = new Vector<Permission>();
		for (int i = 1; i < tSplitted.length; i++)
			tPermissions.add(Permission.valueOf(tSplitted[i]));
		
		
		long tNewUserID = Long.parseLong(encodedView.split("\t")[0]);
		if (currentCellUsername.equals(tNewUserID + ""))
			this.allowModify = true;
		else
			this.allowModify = false;

		if (tPermissions.contains(Permission.REPLY_TO_MESSAGE))
			this.m_replyButton.setEnabled(true);
		else
			this.m_replyButton.setEnabled(false);
		this.keyTypedEventFunction();
	}

}
