package forum.client.ui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;

/**
 * @author Tomer Heber
 *
 */
public class NonSelectedForumTreeCellPanel extends JPanel {

	private static final long serialVersionUID = 6349491114710865385L;
	private static final ImageIcon plusIcon = new ImageIcon("./images/arrow_in.png");

	private JTextArea m_area;

	public void updatePanel(ForumCell cell) {
		if (cell == null)
			m_area.setText("");
		else
			m_area.setText(cell.toString());
	}


	public NonSelectedForumTreeCellPanel() {
		super();

		GroupLayout tLayout = new GroupLayout(this);
		this.setLayout(tLayout);



		JPanel msgPanel = new JPanel();

		msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.X_AXIS));

		msgPanel.setBackground(Color.WHITE);

		JTextArea area = new JTextArea();
		area.setFont(new Font("Tahoma", Font.BOLD, 14));
		m_area = area;
	//	m_area.setAlignmentX(LEFT_ALIGNMENT);

		area.setText("");

//		area.setPreferredSize(new Dimension(800,200));

	//	this.setPreferredSize(new Dimension(800,200));

		msgPanel.setBackground(Color.white);
		this.setBackground(Color.white);
		
		msgPanel.add(area);		


		msgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JLabel imgLabel = new JLabel(plusIcon);
	
		Component rigid = Box.createRigidArea(new Dimension(5,0));
		msgPanel.add(rigid);		

		msgPanel.add(imgLabel);

		
		tLayout.setHorizontalGroup(
				tLayout.createSequentialGroup()
						.addComponent(msgPanel, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
//						.addComponent(rigid, GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE)
	//					.addComponent(imgLabel, GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE)
						);


		tLayout.setVerticalGroup(
				tLayout.createSequentialGroup()
				.addGroup(tLayout.createSequentialGroup()
						.addGap(5, 5, 5)
						.addComponent(msgPanel, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
		//						.addComponent(rigid, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
			//					.addComponent(imgLabel, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
						.addGap(5, 5, 5))
				);


//		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	public void select(Color color) {
		this.m_area.setBackground(color);
		Component[] tComponents = this.getComponents();
		for (int i = 0; i < tComponents.length; i++)
			tComponents[i].setBackground(color);
	}

	public void unselect(Color color) {
		this.m_area.setBackground(Color.white);
		Component[] tComponents = this.getComponents();
		for (int i = 0; i < tComponents.length; i++)
			tComponents[i].setBackground(Color.white);
	}

}
