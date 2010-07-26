/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

/**
 * @author sepetnit
 *
 */
public class StatisticsPanel extends JPanel implements GUIHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7022646380012022665L;

	private JLabel connectedStatisticsLabel;
	private JButton btn_showConnected;
	private String connectedData;

	private ControllerHandler controller;
	private boolean viewActiveRequested;


	public void notifyError(String errorMessage) {
		// TODO: handle
	}

	public void refreshForum(String encodedView) {
		if (!encodedView.startsWith("activenumbers\t"))
			return;
		String[] tSplitted = encodedView.split("\t");
		long tGuestsNumber = Long.parseLong(tSplitted[1]);
		long tMembersNumber = Long.parseLong(tSplitted[2]);
		connectedStatisticsLabel.setText("Total: " +
				(tGuestsNumber + tMembersNumber) + " are connected, " + 
				tMembersNumber + (tMembersNumber == 1? " is registered user" : " are registered users") + " and " + 
				tGuestsNumber + (tGuestsNumber == 1? " is guest" : " are guests"));


		if (tMembersNumber == 0)
			btn_showConnected.setEnabled(false);
		else {
			btn_showConnected.setEnabled(true);
			connectedData = "";
			for (int i = 3; i < tSplitted.length; i++)
				connectedData += (i - 2) + ") " + tSplitted[i] + "\n";
		}
		if (this.viewActiveRequested) {
			JOptionPane.showMessageDialog(StatisticsPanel.this, 
					connectedData, "connected usernames", JOptionPane.INFORMATION_MESSAGE);
			this.viewActiveRequested = false;
		}
	}

	public StatisticsPanel() throws IOException {

		viewActiveRequested = false;
		btn_showConnected = new JButton("show connected");
		btn_showConnected.setPreferredSize(new Dimension(200, 20));

		btn_showConnected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				viewActiveRequested = true;
				controller.getActiveUsersNumber();
			}			
		});

		connectedData = "";

		controller = ControllerHandlerFactory.getPipe();

		controller.addObserver(new GUIObserver(this), EventType.USER_CHANGED);
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setBorder(BorderFactory.createTitledBorder(this.getBorder(), "Currently Connected", 0, 0, new Font("Tahoma", Font.BOLD, 12), Color.WHITE));
		connectedStatisticsLabel = new JLabel();


		connectedStatisticsLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

		GroupLayout tGroupLayout = new GroupLayout(this);
		this.setLayout(tGroupLayout);
		tGroupLayout.setHorizontalGroup(
				tGroupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(tGroupLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(connectedStatisticsLabel, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, Short.MAX_VALUE)
						.addComponent(btn_showConnected, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 
								GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
				)
		);
		tGroupLayout.setVerticalGroup(
				tGroupLayout.createSequentialGroup()
				.addGroup(tGroupLayout.createParallelGroup()
						.addComponent(connectedStatisticsLabel, 20, 20, 20)
						.addComponent(btn_showConnected, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 
								GroupLayout.PREFERRED_SIZE))
								.addGap(10, 10, 10)
		);


		this.connectedStatisticsLabel.setForeground(Color.WHITE);
		this.connectedStatisticsLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

	}


	public void paint(Graphics g) {
		g.drawImage(new ImageIcon("./images/background2.jpg").getImage(), 
				0, 0, 1680, 1050, null);
		setOpaque(false);
		super.paint(g);
	}


}
