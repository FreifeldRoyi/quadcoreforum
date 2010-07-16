/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;

/**
 * @author sepetnit
 *
 */
public class MembersDialog extends JDialog implements GUIHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5027704541176379476L;

	private class ConnectedUsersListModel extends DefaultListModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4071457853590148926L;
		private String[] data;
		private String[] usernames;
		
		public ConnectedUsersListModel() {
			this.data = new String[0];
			this.usernames = new String[0];
		}
		
		public String getUserNameAt(int i) {
			if (i >= 0 && i < this.usernames.length)
				return this.usernames[i];
			return "";
		}
		
		public void updateData(String[] newData, String[] usernames) {
			if (newData!=null)		System.out.println(newData.length);
			if (data!=null)		System.out.println(data.length);

			this.data = newData;
			this.usernames = usernames;
			//			this.fireContentsChanged(null, 0, data.length - 1);
		}

		public Object getElementAt(int arg0) {
			if (arg0 >= 0 && arg0 < data.length)
				return data[arg0];
			return null;
		}

		public int getSize() {
			return data == null? 0 : data.length;
		}
	}

	private class InternalConnectedUsersPanel extends JPanel implements GUIHandler {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1299302652127708772L;

		private JList lst_connected;		
		private ListModel lst_connected_model;
		private ControllerHandler controller;

		public String getSelectedUsername() {
			return ((ConnectedUsersListModel)lst_connected.getModel()).getUserNameAt(lst_connected.getSelectedIndex());
		}

		public InternalConnectedUsersPanel() {
			this.lst_connected_model = new ConnectedUsersListModel();
			this.lst_connected = new JList();
			this.lst_connected.setModel(this.lst_connected_model);
			
	
			this.lst_connected.setPreferredSize(new Dimension(233, 333));

			this.lst_connected.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent arg0) {
					if (arg0.getFirstIndex() == -1)
						btn_set_moderator.setEnabled(false);
					else {
						if (!((ConnectedUsersListModel)lst_connected_model).
								getUserNameAt(lst_connected.getSelectedIndex()).equals("admin") 
								&& promotionAllowed)
							btn_set_moderator.setEnabled(true);
						else	
							btn_set_moderator.setEnabled(false);
					}

				}

			});

			this.lst_connected.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);



			//			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


			GroupLayout tLayout = new GroupLayout(this);
			tLayout.setHorizontalGroup(tLayout.createSequentialGroup()
					.addComponent(this.lst_connected, 0, 100, Short.MAX_VALUE)
			);
			tLayout.setVerticalGroup(tLayout.createSequentialGroup()
					.addComponent(this.lst_connected, 0, 100, Short.MAX_VALUE)
			);

			this.setLayout(tLayout);	

			try { 
				controller = ControllerHandlerFactory.getPipe();
				controller.addObserver(new GUIObserver(this), EventType.USER_CHANGED);
				this.setEnabled(false);
				controller.getAllMembers(this);
			}
			catch (IOException e) {

			}

		}

		public void notifyError(String errorMessage) {
			// TODO Auto-generated method stub

		}

		@Override
		public void refreshForum(String encodedView) {
			
			System.out.println("updated ==============================");
			System.out.println(encodedView);
			System.out.println(encodedView);
			if (!encodedView.startsWith("activeusernames\t"))
				return;
			this.lst_connected.setVisible(false);

			if (!encodedView.equals("activeusernames\t")) {
				String[] tConnectedUsers = encodedView.substring(encodedView.indexOf("\t") + 1).split("\n");
				System.out.println("arraaaaaaaaaaaaaaaaaaaa");
				System.out.println(Arrays.toString(tConnectedUsers));
				String[] tUsernames = new String[tConnectedUsers.length];
				for (int i = 0; i < tConnectedUsers.length; i++) {
					System.out.println("tcurent member = " + tConnectedUsers[i] + " "  + Arrays.toString(tConnectedUsers[i].split("\t")));
					String[] tCurrentMember = tConnectedUsers[i].split("\t");
					tUsernames[i] = tCurrentMember[0];
					System.out.println("tCurrentMember[0] " + tCurrentMember[0] );
					System.out.println("tCurrentMember[1] " + tCurrentMember[1] );
					System.out.println("tCurrentMember[2] " + tCurrentMember[2] );

					tConnectedUsers[i] = "Username: " + tCurrentMember[0] + ", LastName: " + tCurrentMember[1] +
					", First Name: " + tCurrentMember[2];
				}
					
				((ConnectedUsersListModel)this.lst_connected_model).updateData(tConnectedUsers, tUsernames);	
			}
			else
				((ConnectedUsersListModel)this.lst_connected_model).updateData(new String[0], new String[0]);
			this.lst_connected.repaint();
			this.lst_connected.setVisible(true);
			if (this.lst_connected_model.getSize() > 0)
				this.lst_connected.setSelectedIndex(0);
		}
	}

	private JButton btn_set_moderator;
	private ControllerHandler controller;
	private boolean promotionAllowed;
	
	public MembersDialog(boolean promotionAllowed) {
		this.promotionAllowed = promotionAllowed;
		this.btn_set_moderator = new JButton("promote to moderator");
				
		
		this.btn_set_moderator.setPreferredSize(new Dimension(200, 35));

		final InternalConnectedUsersPanel tUsersPanel = new InternalConnectedUsersPanel();

		try {
			controller = ControllerHandlerFactory.getPipe();
		} catch (IOException e) {
		}



		this.addWindowListener(new WindowListener() {

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowClosed(WindowEvent arg0) {
			}

			public void windowClosing(WindowEvent arg0) {
				controller.deleteObserver(tUsersPanel);
				controller.deleteObserver(MembersDialog.this);
				dispose();
			}

			public void windowDeactivated(WindowEvent arg0) {
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {				
			}

			public void windowOpened(WindowEvent arg0) {
			}

		});

		controller.addObserver(new GUIObserver(this), EventType.USER_CHANGED);

		tUsersPanel.setPreferredSize(new Dimension(100, 200));

		this.btn_set_moderator.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				btn_set_moderator.setEnabled(false);
				controller.promoteToModerator(btn_set_moderator, tUsersPanel.getSelectedUsername());				
			}			
		});

		
		this.setMinimumSize(new Dimension(400, 300));

		this.setPreferredSize(new Dimension(400, 300));

		GroupLayout tLayout = new GroupLayout(this.getContentPane());
		tLayout.setHorizontalGroup(tLayout.createParallelGroup()
				.addGroup(tLayout.createSequentialGroup()
						.addContainerGap()

						.addComponent(tUsersPanel, GroupLayout.PREFERRED_SIZE, 
								GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addContainerGap())
								.addGroup(tLayout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(this.btn_set_moderator, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addContainerGap()));

		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(tUsersPanel, GroupLayout.PREFERRED_SIZE, 
						GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10)

						.addComponent(this.btn_set_moderator, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addContainerGap()
		);
		this.getContentPane().setLayout(tLayout);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int Y = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, Y);



		this.setModal(true);
		this.pack();

	}

	@Override
	public void notifyError(String errorMessage) {
		// TODO Auto-generated method stub

	}

	public void refreshForum(String encodedView) {
		if (encodedView.startsWith("promoted"))
			JOptionPane.showMessageDialog(this, encodedView.substring(encodedView.indexOf("\t") + 1)
					, "success", JOptionPane.INFORMATION_MESSAGE);
	}


	public static void main(String[] args) {
		new MembersDialog(false).setVisible(true);
	}

}
