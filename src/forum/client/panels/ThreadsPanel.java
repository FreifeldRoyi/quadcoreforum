/**
 * 
 */
package forum.client.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeModel;

import forum.client.controllerlayer.ControllerHandler;
import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.controllerlayer.GUIObserver;
import forum.client.ui.ForumTree;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

/**
 * @author sepetnit
 *
 */
public class ThreadsPanel extends JPanel implements GUIHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5210803978059150523L;

	private JTable threadsTable;
	private TableModel threadsTableModel;
	private MainPanel container;	
	private ForumTree messages;

	private JButton addNewThreadButton;
	private JButton deleteThreadButton;
	private JButton modifyThreadButton;	

	public ThreadsPanel(final MainPanel cont, final ForumTree messages) {
		this.container = cont;
		this.messages = messages;
		//		this.messages.getForumTreeUI().setVisible(false);
		this.threadsTable = new JTable();

		this.threadsTable.setSelectionModel(new DefaultListSelectionModel());



		this.threadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


		this.threadsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getFirstIndex() == -1) {
					deleteThreadButton.setEnabled(false);
					modifyThreadButton.setEnabled(false);
				}
				else {
					deleteThreadButton.setEnabled(true);
					modifyThreadButton.setEnabled(true);
				}
			}
		});
		
		
		

		this.threadsTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// handle double click
				if (e.getClickCount() == 2) {
					int rowSelected = threadsTable.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1) {

						final long tMessageIDToLoad = threadsTableModel.getIDofContentInRow(rowSelected);
						container.startWorkingAnimation("retreiving thread " + 
								threadsTableModel.getNameOfContentInRow(rowSelected) 
								+ " content...");

						messages.setFatherID(threadsTableModel.getFatherID());
						messages.setRootMessage(tMessageIDToLoad);
					}
				}
			}
		});

		String[] columns = {"Thread",  "Messages#", "Views#"};
		this.threadsTableModel = new TableModel(columns);

		this.threadsTable.setModel(this.threadsTableModel);


		addNewThreadButton = new JButton();
		deleteThreadButton = new JButton();
		modifyThreadButton = new JButton();

		addNewThreadButton.setText("open new");
		deleteThreadButton.setText("delete");
		modifyThreadButton.setText("modify");

		Dimension tSubjectsButtonsDimension = new Dimension(85, 35);
		addNewThreadButton.setPreferredSize(tSubjectsButtonsDimension);

		addNewThreadButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ReplyModifyDialog tNewThreadDialog =
					new ReplyModifyDialog(container.getConnectedUser().getID(), threadsTableModel.getFatherID(), "thread", addNewThreadButton);
				tNewThreadDialog.setVisible(true);
				if (tNewThreadDialog.shouldUpdateGUI()) {
					try {
						ControllerHandlerFactory.getPipe().getThreads(threadsTableModel.getFatherID(), threadsTable);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				tNewThreadDialog.dispose();
			}});

		
		modifyThreadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String tResponse = (String)
				JOptionPane.showInputDialog(ThreadsPanel.this, "Please enter a new topic: ", "Modify " +
						"thread topic", JOptionPane.PLAIN_MESSAGE);
				if (tResponse.equals(""))
					JOptionPane.showMessageDialog(container, "The thread topic can't be empty!", "Modify " +
							"thread topic", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		deleteThreadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					final ControllerHandler controller = ControllerHandlerFactory.getPipe();

					controller.addObserver(new GUIObserver(new GUIHandler() {
						public void notifyError(String errorMessage) {
							controller.deleteObserver(this);
							JOptionPane.showMessageDialog(container, 
									"cannot delete the thread!", "error", JOptionPane.ERROR_MESSAGE);
						}

						public void refreshForum(final String encodedView) {

							if (encodedView.startsWith("deletesuccess")) {
								controller.deleteObserver(this);
								new Thread(new Runnable() {
									public void run() {
										controller.getSubjects(threadsTableModel.getFatherID(), container);
										controller.getThreads(threadsTableModel.getFatherID(), container);
									}}).start();
							}
						}
					}), EventType.MESSAGES_UPDATED);
					controller.deleteMessage(container.getConnectedUser().getID(), -1, 
							threadsTableModel.getIDofContentInRow(threadsTable.getSelectedRow()), 
							deleteThreadButton);

				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		deleteThreadButton.setPreferredSize(tSubjectsButtonsDimension);
		modifyThreadButton.setPreferredSize(tSubjectsButtonsDimension);		


		JScrollPane tSubjectsTablePane = new JScrollPane(threadsTable);

		tSubjectsTablePane.setOpaque(false);
		threadsTable.setOpaque(false);
		this.setOpaque(false);
		tSubjectsTablePane.setBorder(BorderFactory.createEmptyBorder());
		tSubjectsTablePane.getViewport().setOpaque(false);

		threadsTable.setFont(new Font("Tahoma", Font.BOLD, 13));
		threadsTable.setRowHeight(30);




		GroupLayout tLayout = new GroupLayout(this);
		this.setLayout(tLayout);



		/*		tLayout.setHorizontalGroup(
				tLayout.createSequentialGroup()
				.addComponent(tSubjectsTablePane, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGap(16, 16, 16)
				.addGroup(tLayout.createParallelGroup()
						.addComponent(addNewThreadButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(modifyThreadButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(deleteThreadButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		 */



		tLayout.setHorizontalGroup(
				tLayout.createParallelGroup(Alignment.CENTER)
				.addGroup(tLayout.createSequentialGroup()
						.addComponent(addNewThreadButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(16, 16, 16)
								.addComponent(modifyThreadButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(16, 16, 16)
										.addComponent(deleteThreadButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(16, 16, 16)
												.addComponent(tSubjectsTablePane, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE));


		/*		
		tLayout.setVerticalGroup(
				tLayout.createParallelGroup()
				.addComponent(tSubjectsTablePane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// GroupLayout.PREFERRED_SIZE)
				.addGroup(Alignment.CENTER, tLayout.createSequentialGroup()
						.addComponent(addNewThreadButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(16, 16, 16)
								.addComponent(modifyThreadButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(16, 16, 16)
										.addComponent(deleteThreadButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
		 */
		tLayout.setVerticalGroup(
				tLayout.createSequentialGroup()
				.addGroup(tLayout.createParallelGroup()
						.addComponent(addNewThreadButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(modifyThreadButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(deleteThreadButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(16, 16, 16)
												.addComponent(tSubjectsTablePane, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE));




		((DefaultListSelectionModel)this.threadsTable.
				getSelectionModel()).getListSelectionListeners()[0].
				valueChanged(new ListSelectionEvent(threadsTable, -1, -1, true));
		
	}	

	public void updateFather(long fatherID) {
		threadsTableModel.setFatherID(fatherID);
	}

	public void setGuestView(){
		this.addNewThreadButton.setVisible(false);
		this.modifyThreadButton.setVisible(false);
		this.deleteThreadButton.setVisible(false);
	}

	public void setMemberView(){
		this.addNewThreadButton.setVisible(true);
		this.modifyThreadButton.setVisible(false);
		this.deleteThreadButton.setVisible(false);
	}

	public void setAuthorView(){
		this.addNewThreadButton.setVisible(true);
		this.modifyThreadButton.setVisible(true);
		this.deleteThreadButton.setVisible(false);
	}

	public void setModeratorOrAdminView(){
		this.addNewThreadButton.setVisible(true);
		this.modifyThreadButton.setVisible(true);
		this.deleteThreadButton.setVisible(true);
	}


	public boolean showsMessages() {
		return this.messages.getForumTreeUI().isVisible();
	}

	/* (non-Javadoc)
	 * @see forum.client.ui.events.GUIHandler#notifyError(java.lang.String)
	 */
	public void notifyError(String errorMessage) {
		// TODO Auto-generated method stub

	}

	public void refreshForum(String encodedView) {
		this.threadsTableModel.clearData();
		if (!encodedView.startsWith("There")) {

			// each line should represent one thread
			String[] tSplitted = encodedView.split("\n");
			// this is the data which will be presented in the threads table
			String[][] tData = new String[tSplitted.length][3];
			// this is the IDs array which should contain the presented subjects' IDs
			long[] tIDs = new long[tSplitted.length];
			long[] tRoots = new long[tSplitted.length];

			for (int i = 0; i < tSplitted.length; i++) {
				String[] tCurrentThreadInfo = tSplitted[i].split("\t");
				// this is the subject's id
				try {
					tIDs[i] = Long.parseLong(tCurrentThreadInfo[0]);
					tRoots[i] = Long.parseLong(tCurrentThreadInfo[1]);
					for (int j = 2; j < tCurrentThreadInfo.length; j++)
						tData[i][j - 2] = tCurrentThreadInfo[j];
					this.threadsTableModel.updateData(tRoots, tData);
				}
				catch (NumberFormatException e) {
					SystemLogger.warning("The server response related to subject's update is invalid");
					break;
				}
			}
		}	
		this.threadsTableModel.fireTableDataChanged();
		this.threadsTable.setVisible(true);
		this.setVisible(true);
		container.stopWorkingAnimation();
	}
}
