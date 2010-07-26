package forum.swingclient.ui;

import java.awt.Color;
import java.awt.Dimension;
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
	private ForumTree forumTree;
	private JButton addMessageButton;
	
	public ForumTreeRootPanel(ForumTree tree) {
		super();
		
		this.setBackground(Color.WHITE);
		
		addMessageButton = new JButton("Add New Message");
		addMessageButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {				
				forumTree.addNewMessage(addMessageButton);
			}
		});
		
		this.add(addMessageButton);
		
		this.forumTree = tree;
	}

}
