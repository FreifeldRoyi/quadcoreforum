/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.StyleConstants;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.JForumTable;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;


/**
 * @author sepetnit
 *
 */
public class MembersDialog extends JDialog implements GUIHandler, KeyListener {

	private static final long serialVersionUID = -2093075059694378491L;

	private JForumTable resultsTable;
	private MembersTableModel tableModel;

	private ControllerHandler controller;

	private JButton btn_promote_demote;
	private JButton btn_show_profile;
	private JButton btn_cancel;
	private JButton btn_updateStatistics;

	private ActionListener promoteDemoteListener;
	private ActionListener showProfileListener;
	private ActionListener cancelListener;

	private JRadioButton connectedStatistics;
	private JRadioButton existingStatistics;

	private boolean promotionAllowed;

	private JPanel pnl_usersStatisticsPie;

	private Timer connectedTimer;
	private Timer existingTimer;
	
	// KeyListener implementation
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
			btn_cancel.doClick();
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}


	public static JDialog getMembersDialog(Component mainpanel, 
			boolean promotionAllowed) throws IOException{
		try {
			return new MembersDialog(promotionAllowed);
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(mainpanel, "Can't connect to the forum database",
					"error", JOptionPane.ERROR_MESSAGE);
			throw e;
		}
	}

	public MembersDialog(boolean promotionAllowed) throws IOException {
		super();
		
		
		this.btn_updateStatistics = new JButton("Update Statistics");

		pnl_usersStatisticsPie = new ForumUsersPieChart();

		
		this.connectedStatistics = new JRadioButton("connected");
		this.existingStatistics = new JRadioButton("existing");

		this.connectedStatistics.addKeyListener(this);
		this.existingStatistics.addKeyListener(this);
		
		this.connectedStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UIManager.put("Selected", "Connected");
				((ForumUsersPieChart)pnl_usersStatisticsPie).updateConnectedStatistics();
			}
		});

		this.existingStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UIManager.put("Selected", "Existing");
				((ForumUsersPieChart)pnl_usersStatisticsPie).updateExistingStatistics();
			}
		});
		
		
		this.btn_updateStatistics.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (connectedStatistics.isSelected()) {
					System.out.println("ddddddddddddddddddddddddd " + (String)UIManager.get("Selected"));

					connectedStatistics.doClick();
				}
				else {
					System.out.println("ddddddddddddddddddddddddd " + (String)UIManager.get("Selected"));

					existingStatistics.doClick();
				}
			}
		});
		

		
		ButtonGroup tButtonGroup = new ButtonGroup();
		tButtonGroup.add(connectedStatistics);
		tButtonGroup.add(existingStatistics);

		JPanel tStatisticsSelectionPanel = new JPanel();

		tStatisticsSelectionPanel.setBorder(BorderFactory.createTitledBorder(""));
		tStatisticsSelectionPanel.setPreferredSize(new Dimension(170, 40));
		
		tStatisticsSelectionPanel.setLayout(
				new BoxLayout(tStatisticsSelectionPanel, BoxLayout.X_AXIS));

		tStatisticsSelectionPanel.add(this.connectedStatistics);
		tStatisticsSelectionPanel.add(this.existingStatistics);



		JPanel tStatisticsContainer = new JPanel();

		GroupLayout tStatisticsContainerLayout = new GroupLayout(tStatisticsContainer);

		tStatisticsContainerLayout.setHorizontalGroup(
				tStatisticsContainerLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addComponent(pnl_usersStatisticsPie, 
						GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10));
		tStatisticsContainerLayout.setVerticalGroup(
				tStatisticsContainerLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addComponent(pnl_usersStatisticsPie, 
						GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10));


		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.addWindowListener(new WindowListener() {

			public void windowOpened(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {
				btn_cancel.doClick();
			}
			public void windowClosed(WindowEvent arg0) {}
			public void windowActivated(WindowEvent arg0) {}
		});

		this.setTitle("Forum members status");
		controller = ControllerHandlerFactory.getPipe();

		this.promotionAllowed = promotionAllowed;

		Dimension tButtonSize = new Dimension(100, 40);

		this.btn_cancel = new JButton("Cancel");
		this.btn_cancel.addKeyListener(this);
		
		this.btn_promote_demote = new JButton("Promote");
		this.btn_promote_demote.addKeyListener(this);
		this.btn_promote_demote.setEnabled(false);

		this.btn_show_profile = new JButton("Profile");
		this.btn_show_profile.addKeyListener(this);
		this.btn_show_profile.setEnabled(false);

		this.btn_promote_demote.setPreferredSize(tButtonSize);

		promoteDemoteListener = new ActionListener() {
			private static final String MODERATOR_POSTFIX = "to a forum moderator?";
			private static final String MEMBER_POSTFIX = "to a regular forum member?";

			public void actionPerformed(ActionEvent arg0) {
				int tSelectedRow = resultsTable.getSelectionModel().getMinSelectionIndex();
				if (tSelectedRow == -1) { // defensive code
					btn_promote_demote.setEnabled(false);
					btn_show_profile.setEnabled(false);
					return;
				}

				String tUsername = (String)tableModel.getMemberUsername(tSelectedRow);
				String tUsernameAndNames = tUsername + " (" + tableModel.getMemberName(tSelectedRow) + ") "; 

				if (JOptionPane.showConfirmDialog(MembersDialog.this,
						btn_promote_demote.getText() + " " + tUsernameAndNames + 
						(btn_promote_demote.getText().equals("Promote")? MODERATOR_POSTFIX : MEMBER_POSTFIX),
						btn_promote_demote.getText() + " user",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
							JOptionPane.NO_OPTION)
					return;

				btn_promote_demote.removeActionListener(promoteDemoteListener);
				btn_show_profile.removeActionListener(showProfileListener);
				btn_cancel.removeActionListener(cancelListener);

				controller.addObserver(new GUIObserver(MembersDialog.this), EventType.USER_CHANGED);
				if (btn_promote_demote.getText().equals("Promote"))
					controller.promoteToModerator(MembersDialog.this, tUsername);
				else
					controller.demoteToMember(MembersDialog.this, tUsername);
			}
		};

		this.btn_promote_demote.addActionListener(promoteDemoteListener);

		this.btn_updateStatistics.setPreferredSize(new Dimension(150, 40));

		this.btn_show_profile.setPreferredSize(tButtonSize);

		showProfileListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int tSelectedRow = resultsTable.getSelectionModel().getMinSelectionIndex();
				if (tSelectedRow == -1) { // defensive code
					btn_promote_demote.setEnabled(false);
					btn_show_profile.setEnabled(false);
					return;
				}

				long tMemberID = tableModel.getIDofContentInRow(tSelectedRow);

				controller.addObserver(new GUIObserver(new GUIHandler() {

					@Override
					public void refreshForum(String encodedView) {
						if (!encodedView.startsWith("memberdetails\t"))
							return;
						System.out.println("oooooooooooooooooooooooooooooooooooooooooooo");

						controller.deleteObserver(this);

						String[] tSplitted = encodedView.split("\t");
						try {
							new RegistrationDialog(-1, tSplitted[1], tSplitted[2], 
									tSplitted[3], tSplitted[4], false).setVisible(true);
						}
						catch (IOException e) {
							JOptionPane.showMessageDialog(MembersDialog.this, "Can't connect to the forum database",
									"Profile retrieval error", JOptionPane.ERROR_MESSAGE);							
						}
						btn_promote_demote.addActionListener(promoteDemoteListener);
						btn_show_profile.addActionListener(showProfileListener);
						btn_cancel.addActionListener(cancelListener);


					}

					@Override
					public void notifyError(String errorMessage) {
						String tErrorMessage = null;

						if (errorMessage.startsWith("memberdetailserror\tregistration"))
							tErrorMessage = "The requested forum member isn't registered to the forum";
						else if (errorMessage.startsWith("memberdetailserror\tdatabase"))
							tErrorMessage = "The connection to database failed";
						else
							return;
						System.out.println("ppppppppppppppppppppppppppppppppppppppppppppppp");

						controller.deleteObserver(this);

						JOptionPane.showMessageDialog(MembersDialog.this, tErrorMessage, "Profile retrieval error",
								JOptionPane.ERROR_MESSAGE);

						btn_promote_demote.addActionListener(promoteDemoteListener);
						btn_show_profile.addActionListener(showProfileListener);
						btn_cancel.addActionListener(cancelListener);


					}
				}), EventType.USER_CHANGED);

				btn_promote_demote.removeActionListener(promoteDemoteListener);
				btn_show_profile.removeActionListener(showProfileListener);
				btn_cancel.removeActionListener(cancelListener);

				System.out.println(tMemberID + " dssdsdsdsdsdsdsd");
				controller.getMemberDetails(tMemberID, MembersDialog.this);
			}
		};

		this.btn_show_profile.addActionListener(showProfileListener);

		final JPanel tMembersTableCont = new InternalConnectedUsersPanel();

		this.btn_cancel.setPreferredSize(tButtonSize);


		cancelListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.deleteObserver((GUIHandler)tMembersTableCont);
				((ForumUsersPieChart)pnl_usersStatisticsPie).dispose();
				if (connectedTimer != null)
					connectedTimer.stop();
				if (existingTimer != null)
					existingTimer.stop();
				MembersDialog.this.dispose();
			}
		};

		this.btn_cancel.addActionListener(cancelListener);

		this.addKeyListener(this);


		tStatisticsContainer.setLayout(tStatisticsContainerLayout);


		tStatisticsContainer.setBorder(BorderFactory.createTitledBorder("Statistics"));

		GroupLayout tLayout = new GroupLayout(this.getContentPane());

		tLayout.setHorizontalGroup(tLayout.createParallelGroup()
				.addGroup(tLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(tMembersTableCont, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGap(10, 10, 10)
						.addComponent(tStatisticsContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10))
						.addGroup(tLayout.createSequentialGroup()
								.addGap(10, 10, Short.MAX_VALUE)
								.addComponent(this.btn_show_profile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(this.btn_promote_demote, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(this.btn_cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(tStatisticsSelectionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(this.btn_updateStatistics, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)));


		tLayout.setVerticalGroup(tLayout.createSequentialGroup()
				.addGap(10, 10, 10)
				.addGroup(tLayout.createParallelGroup()
						.addComponent(tMembersTableCont, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(tStatisticsContainer, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
						.addGap(10, 10, 10)
						.addGroup(tLayout.createParallelGroup()
								.addComponent(this.btn_show_profile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.btn_promote_demote, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.btn_cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(tStatisticsSelectionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)

								.addComponent(this.btn_updateStatistics, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(10, 10, 10));

		this.getContentPane().setLayout(tLayout);

		this.setMinimumSize(new Dimension(800, 440));


		this.setModal(true);

		this.setPreferredSize(new Dimension(1010, 440));

		controller.getAllMembers(this);
		
		String tStoredSelection = null;
		if ((tStoredSelection = (String)UIManager.get("Selected")) == null) {
			UIManager.put("Selected", "Existing");
			this.existingStatistics.setSelected(true);
		}
		else if (tStoredSelection.equals("Existing") ) {
			this.existingStatistics.setSelected(true);
		}
		else {
			this.connectedStatistics.setSelected(true);
			((ForumUsersPieChart)this.pnl_usersStatisticsPie).updateConnectedStatistics();			
		}

		
		this.pack();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (this.getWidth() / 2); // Center horizontally.
		int YY = (screen.height / 2) - (this.getHeight() / 2); // Center vertically.
		this.setLocation(X, YY);
		
		connectedTimer = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (connectedStatistics.isSelected())
					connectedStatistics.doClick();
			}
		});
		
		connectedTimer.start();
		
		existingTimer = new Timer(5000, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (existingStatistics.isSelected())
					existingStatistics.doClick();
			}
		});
		
		existingTimer.start();
		
		

	}

	private class MembersTableModel extends ForumTableModel {


		private static final long serialVersionUID = -7027006082362962895L;

		private static final int USERNAME_COLUMN = 0;
		private static final int USER_FIRST_NAME_COLUMN = 1;
		private static final int USER_LAST_NAME_COLUMN = 2;
		private static final int USER_TYPE_COLUMN = 3;

		public MembersTableModel(String[] columns) {
			super(columns);
		}

		public String getMemberType(int row) {
			if (row > 0 && row < this.getRowCount()) 
				return (String)this.getValueAt(row, USER_TYPE_COLUMN);
			return null;
		}

		public String getMemberUsername(int row) {
			if (row > 0 && row < this.getRowCount()) 
				return (String)this.getValueAt(row, USERNAME_COLUMN);
			return null;
		}

		public String getMemberName(int row) {
			if (row > 0 && row < this.getRowCount()) 
				return (String)this.getValueAt(row, USER_FIRST_NAME_COLUMN) +
				" " + (String)this.getValueAt(row, USER_LAST_NAME_COLUMN);
			return null;
		}
	}

	private class InternalConnectedUsersPanel extends JPanel implements GUIHandler {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1299302652127708772L;

		private void createMembersTable() {
			resultsTable = new JForumTable();

			resultsTable.addKeyListener(MembersDialog.this);
			resultsTable.setFocusable(false);
			resultsTable.setRowHeight(30);

			resultsTable.setFont(new Font("Tahoma", Font.BOLD, 13));

			String[] columns = {"Username", "Last name", "First name", "type"};
			tableModel = new MembersTableModel(columns);
			resultsTable.setModel(tableModel);

			resultsTable.setBorder(BorderFactory.createLineBorder(Color.black));

			TableCellRenderer tLeftAlignmentRenderer = new ForumTableCellRenderer(StyleConstants.ALIGN_LEFT);
			TableCellRenderer tCenterAlignmentRenderer = new ForumTableCellRenderer(StyleConstants.ALIGN_CENTER);

			resultsTable.setColumnRenderer(0, tLeftAlignmentRenderer);
			resultsTable.setColumnRenderer(1, tLeftAlignmentRenderer);
			resultsTable.setColumnRenderer(2, tLeftAlignmentRenderer);
			resultsTable.setColumnRenderer(3, tCenterAlignmentRenderer);

			resultsTable.setSelectionOnMouseMotion(false);
		}


		public InternalConnectedUsersPanel() {

			controller.addObserver(new GUIObserver(this), EventType.USER_CHANGED);

			this.addKeyListener(MembersDialog.this);

			this.setBorder(BorderFactory.createTitledBorder("Existing forum members"));

			this.createMembersTable();

			JScrollPane tScroll = new JScrollPane(resultsTable);

			tScroll.setPreferredSize(new Dimension(224, 100));

			GroupLayout tLayout = new GroupLayout(this);

			tLayout.setHorizontalGroup(tLayout.createSequentialGroup()
					.addGap(10, 10, 10)
					.addComponent(tScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addGap(10, 10, 10));

			tLayout.setVerticalGroup(tLayout.createSequentialGroup()
					.addGap(10, 10, 10)
					.addComponent(tScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addGap(10, 10, 10));

			this.setLayout(tLayout);


			this.add(tScroll);


			resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			resultsTable.getSelectionModel().setValueIsAdjusting(false);


			resultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {

					if (arg0.getFirstIndex() != -1)
						btn_show_profile.setEnabled(true);
					else
						btn_show_profile.setEnabled(false);

					if (!promotionAllowed)
						return;

					if (resultsTable.getSelectionModel().getMinSelectionIndex() == -1) {
						btn_promote_demote.setEnabled(false);
					}
					else {
						String userType = tableModel.getMemberType(resultsTable.getSelectionModel().getMinSelectionIndex());
						if (userType == null)
							btn_promote_demote.setEnabled(false);
						else if (userType.equals("ADMIN")) {
							btn_promote_demote.setEnabled(false);
						}
						else if (userType.equals("MODERATOR")) {
							btn_promote_demote.setText("Demote");
							btn_promote_demote.setEnabled(true);
						}							
						else if (userType.equals("MEMBER")) {
							btn_promote_demote.setText("Promote");
							btn_promote_demote.setEnabled(true);							
						}
						else {
							btn_promote_demote.setEnabled(false);
						}
					}
				}
			});
		}

		public void notifyError(String errorMessage) {
			// do nothing
		}

		@Override
		public void refreshForum(String encodedView) {
			if (!encodedView.startsWith("activeusernames"))
				return;
			long tSelectedID = (resultsTable.getSelectedRow() != -1)? tableModel.getIDofContentInRow(resultsTable.getSelectedRow()) : -1;

			String[] tConnectedMembers = encodedView.split("\n");

			long[] tConnectedMembersIDs = new long[tConnectedMembers.length - 1];
			String[][] tConnectedMembersData = new String[tConnectedMembers.length - 1][4];

			for (int i = 1; i < tConnectedMembers.length; i++) {
				String[] tSplittedDetails = tConnectedMembers[i].split("\t");
				tConnectedMembersIDs[i - 1] = Long.parseLong(tSplittedDetails[0]);
				for (int j = 0; j < 4; j++)
					tConnectedMembersData[i - 1][j] = tSplittedDetails[j + 1];
			}

			tableModel.updateData(tConnectedMembersIDs, tConnectedMembersData);
			tableModel.fireTableDataChanged();

			if (tableModel.getRowCount() > 0) {
				if (tSelectedID != -1) {
					boolean tFound = false;
					for (int i = 0; i < tableModel.getRowCount(); i++)
						if (tableModel.getIDofContentInRow(i) == tSelectedID) {
							resultsTable.getSelectionModel().setSelectionInterval(i, i);
							tFound = true;
							break;
						}
					if (!tFound)
						resultsTable.getSelectionModel().setSelectionInterval(0, 0);
				}
				else
					resultsTable.getSelectionModel().setSelectionInterval(0, 0);
			}
		}
	}

	@Override
	public void notifyError(String errorMessage) {
		String tErrorMessage = null;
		String tTitle = "";
		if (errorMessage.startsWith("promotionerror\tpermissions"))
			tErrorMessage = "You don't have permissions to promote members";
		else if (errorMessage.startsWith("promotionerror\tregistration") ||
				errorMessage.startsWith("demotion\tregistration"))
			tErrorMessage = "The requested forum member isn't registered to the forum";
		else if (errorMessage.startsWith("promotionerror\tdatabase") ||
				errorMessage.startsWith("promotionerror\tdatabase"))
			tErrorMessage = "The connection to database failed";
		if (errorMessage.startsWith("demotionerror\tpermissions"))
			tErrorMessage = "You don't have permissions to demote members";
		else
			return;

		if (errorMessage.startsWith("promotion\t"))
			tTitle = "Member promotion failure";
		else if (errorMessage.startsWith("demotion\t"))
			tTitle = "Member demotion failure";
		else
			return;

		controller.deleteObserver(this);
		JOptionPane.showMessageDialog(this, tErrorMessage, tTitle, JOptionPane.ERROR_MESSAGE);
		btn_promote_demote.addActionListener(promoteDemoteListener);
		btn_show_profile.addActionListener(showProfileListener);
		btn_cancel.addActionListener(cancelListener);
	}

	@Override
	public void refreshForum(String encodedView) {
		if (encodedView.startsWith("promoted") || encodedView.startsWith("demoted")) {
			controller.deleteObserver(this);
			JOptionPane.showMessageDialog(this, encodedView.substring(encodedView.indexOf("\t") + 1),
					Character.toUpperCase(encodedView.charAt(0)) +
					encodedView.substring(1, 4) + "tion success", JOptionPane.INFORMATION_MESSAGE);
			btn_promote_demote.addActionListener(promoteDemoteListener);
			btn_show_profile.addActionListener(showProfileListener);
			btn_cancel.addActionListener(cancelListener);
			controller.getAllMembers(this);
		}
	}
}
