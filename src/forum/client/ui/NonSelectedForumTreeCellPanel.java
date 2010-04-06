package forum.client.ui;


import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Tomer Heber
 *
 */
public class NonSelectedForumTreeCellPanel extends JPanel {
	
	private static final long serialVersionUID = 6349491114710865385L;	
	private static final ImageIcon plusIcon = new ImageIcon("./images/arrow_in.png");
	
	private JTextArea m_area;
	
	public void updatePanel(ForumCell cell) {
		m_area.setText(cell.toString());
	}

	public NonSelectedForumTreeCellPanel() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel msgPanel = new JPanel();
		msgPanel.setBackground(Color.WHITE);

		JTextArea area = new JTextArea();
		m_area = area;
		area.setText("");
		area.setPreferredSize(new Dimension(350,20));

		msgPanel.add(area);		
		
		this.add(msgPanel);
		
		JLabel imgLabel = new JLabel(plusIcon);
		this.add(imgLabel);
		this.add(Box.createRigidArea(new Dimension(5,0)));		
		
		this.setPreferredSize(new Dimension(400,30));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setBackground(Color.WHITE);			
	}

}
