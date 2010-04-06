package forum.client.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author Tomer Heber
 *
 */
public class ForumTreeRootPanel extends JPanel {
	

	private static final long serialVersionUID = -6952266542697445089L;
	private ForumTree m_forumTree;
	private JButton m_addMessageButton;
	
	public ForumTreeRootPanel(ForumTree forumTree) {
		super();
		
		//this.setPreferredSize(new Dimension(400,30));	
		this.setBackground(Color.WHITE);
		
		m_addMessageButton = new JButton("Add New Message");
		m_addMessageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				m_forumTree.addNewMessage(m_addMessageButton);
			}
		});
		
		this.add(m_addMessageButton);
		
		m_forumTree = forumTree;
	}

}
