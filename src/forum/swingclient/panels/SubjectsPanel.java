/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.ui.JScrollableTable;
import forum.swingclient.ui.events.GUIHandler;
import forum.server.domainlayer.SystemLogger;

/**
 * @author sepetnit
 *
 */
public class SubjectsPanel extends JPanel implements GUIHandler {

	private static final long serialVersionUID = -5210803978059150523L;

	private JScrollableTable subjectsTable;
	private TableModel subjectsTableModel;
	private MainPanel container;	
	private ThreadsPanel threadsPanel;

	private JButton addNewSubjectButton;
	private JButton deleteSubjectButton;
	private JButton modifySubjectButton;


	private String showingSubjectsOfName;
	private long showingSubjectsOfID;


	public SubjectsPanel(final MainPanel cont, final ThreadsPanel threads) {
		this.container = cont;
		this.threadsPanel = threads;
		this.showingSubjectsOfName = "";
		this.showingSubjectsOfID = -1;
		this.subjectsTable = new JScrollableTable();
		this.subjectsTable.setFocusable(false);
		this.subjectsTable.setSelectionModel(new DefaultListSelectionModel());
		this.subjectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.subjectsTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// handle double click
				if (e.getClickCount() == 2) {
					int rowSelected = subjectsTable.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1) {
						//subjectsTable.setVisible(false);
						threadsPanel.setVisible(true);
						showingSubjectsOfName = subjectsTableModel.getNameOfContentInRow(rowSelected) ;
						container.startWorkingAnimation("retreiving subject " + 
								showingSubjectsOfName
								+ " content...");

						final long subjectToLoad = subjectsTableModel.getIDofContentInRow(rowSelected);
						subjectsTableModel.setFatherID(subjectToLoad);
						threadsPanel.updateFather(subjectToLoad);
						showingSubjectsOfID = subjectToLoad;
						try {
							ControllerHandlerFactory.getPipe().getSubjects(subjectToLoad, container);

							ControllerHandlerFactory.getPipe().getThreads(subjectToLoad, container);

							container.addToNavigate(showingSubjectsOfName, linkPressListener());

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		this.subjectsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getFirstIndex() == -1) {
					deleteSubjectButton.setEnabled(false);
					modifySubjectButton.setEnabled(false);
				}
				else {
					deleteSubjectButton.setEnabled(true);
					modifySubjectButton.setEnabled(true);
				}
			}
		});

		
		String[] columns = {"Subject",  "Description", "Sub-Subjects#", 
				"Messages#", "Last Message Info" };
		subjectsTableModel = new TableModel(columns);
		this.subjectsTable.setModel(subjectsTableModel);


		addNewSubjectButton = new JButton();
		deleteSubjectButton = new JButton();
		modifySubjectButton = new JButton();

		addNewSubjectButton.setText("add new");
		deleteSubjectButton.setText("delete");
		modifySubjectButton.setText("modify");

		Dimension tSubjectsButtonsDimension = new Dimension(90, 35);
		addNewSubjectButton.setPreferredSize(tSubjectsButtonsDimension);

		addNewSubjectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ReplyModifyDialog tNewSubjectDialog =
					new ReplyModifyDialog(container.getConnectedUser().getID(), 
							subjectsTableModel.getFatherID(), "subject", addNewSubjectButton);
				tNewSubjectDialog.setVisible(true);
				if (tNewSubjectDialog.shouldUpdateGUI()) {
					try {
						ControllerHandlerFactory.getPipe().getSubjects(subjectsTableModel.getFatherID(), subjectsTable);
					} 
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				tNewSubjectDialog.dispose();
			}});




		deleteSubjectButton.setPreferredSize(tSubjectsButtonsDimension);
		modifySubjectButton.setPreferredSize(tSubjectsButtonsDimension);		


		JScrollPane tSubjectsTablePane = new JScrollPane(subjectsTable);

		tSubjectsTablePane.setViewport(new JViewport() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2313357428427772725L;

			public void paint(Graphics g) {
				/*				g.drawImage(new ImageIcon("./images/background1.jpg").getImage(), 
						0, 0, 1920, 1200, null);
				setOpaque(false);
				 */
				super.paint(g);
			}
		});



		tSubjectsTablePane.setOpaque(false);
		subjectsTable.setOpaque(false);
		this.setOpaque(false);
		tSubjectsTablePane.setBorder(BorderFactory.createEmptyBorder());
		tSubjectsTablePane.getViewport().setOpaque(false);

		subjectsTable.setFont(new Font("Tahoma", Font.BOLD, 13));
		subjectsTable.setRowHeight(30);



		tSubjectsTablePane.getViewport().add(subjectsTable);


		GroupLayout tLayout = new GroupLayout(this);
		this.setLayout(tLayout);

		tLayout.setHorizontalGroup(
				tLayout.createParallelGroup(Alignment.CENTER)
				.addGroup(tLayout.createSequentialGroup()
						.addComponent(addNewSubjectButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(16, 16, 16)
								.addComponent(modifySubjectButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(16, 16, 16)
										.addComponent(deleteSubjectButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(16, 16, 16)
												.addComponent(tSubjectsTablePane, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE));

		tLayout.setVerticalGroup(
				tLayout.createSequentialGroup()
				.addGroup(tLayout.createParallelGroup()
						.addComponent(addNewSubjectButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(modifySubjectButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(deleteSubjectButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(16, 16, 16)
												.addComponent(tSubjectsTablePane, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE));



		((DefaultListSelectionModel)this.subjectsTable.
				getSelectionModel()).getListSelectionListeners()[0].
				valueChanged(new ListSelectionEvent(subjectsTable, -1, -1, true));

	}

	public void setFatherID(int fatherID) {
		this.subjectsTableModel.setFatherID(fatherID);
	}

	public void setGuestView(){
		this.addNewSubjectButton.setVisible(false);
		this.modifySubjectButton.setVisible(false);
		this.deleteSubjectButton.setVisible(false);
		this.threadsPanel.setGuestView();
	}

	public void setMemberView(){
		this.threadsPanel.setMemberView();
	}

	public void setModeratorOrAdminView(){
		this.addNewSubjectButton.setVisible(true);
		this.modifySubjectButton.setVisible(true);
		this.deleteSubjectButton.setVisible(true);
		this.threadsPanel.setModeratorOrAdminView();
	}


	public void updateFields(long id, String name) {
		this.showingSubjectsOfID = id;
		this.showingSubjectsOfName = name;
	}

	/* (non-Javadoc)
	 * @see forum.client.ui.events.GUIHandler#notifyError(java.lang.String)
	 */
	public void notifyError(String errorMessage) {
		// TODO Auto-generated method stub

	}

	public void refreshForum(String encodedView) {

		if (encodedView.startsWith("searchresult") ||
				encodedView.startsWith("searchnotmessages") ||
				encodedView.startsWith("addsubjectsuccess")) return;

		if (encodedView.startsWith("getpathsuccess")) {
			container.removeFromNavigateUntil("Show root subjects");
			String[] tSplitted = encodedView.split("\n");
			for (int i = 3; i < tSplitted.length; i++) {
				if (tSplitted[i].startsWith("THREAD")) break;
				final String[] tCurrentSubject = tSplitted[i].split("\t");
				final long id = Integer.parseInt(tCurrentSubject[0]);
				container.addToNavigate(tCurrentSubject[1], new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						SubjectsPanel.this.setFatherID(Integer.parseInt(tCurrentSubject[0]));
						container.switchToRootSubjectsView();
						container.startWorkingAnimation("retreiving subject " + 
								tCurrentSubject[1] + " content...");
						try {

							ControllerHandlerFactory.getPipe().getSubjects(id, container);
							ControllerHandlerFactory.getPipe().getThreads(id, container);
						} 
						catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});	
			}
		}
		else {

			this.subjectsTableModel.clearData();
			if (!encodedView.startsWith("There") && !encodedView.startsWith("addsubjectsuccess")) {

				// each line should represent one subject
				String[] tSplitted = encodedView.split("\n");
				// this is the data which will be presented in the subjects table
				String[][] tData = new String[tSplitted.length][5];
				// this is the IDs array which should contain the presented subjects' IDs
				long[] tIDs = new long[tSplitted.length];
				for (int i = 0; i < tSplitted.length; i++) {
					String[] tCurrentSubjectInfo = tSplitted[i].split("\t");
					// this is the subject's id
					try {
						tIDs[i] = Long.parseLong(tCurrentSubjectInfo[0]);
						for (int j = 1; j < tCurrentSubjectInfo.length; j++)
							tData[i][j - 1] = tCurrentSubjectInfo[j];
						this.subjectsTableModel.updateData(tIDs, tData);


					}
					catch (NumberFormatException e) {
						SystemLogger.warning("The server response related to subject's update is invalid");
						this.showingSubjectsOfName = "";
						this.showingSubjectsOfID = -1;
						this.subjectsTableModel.clearData();
						break;
					}
				}
			}

			//this.subjectsTable.setVisible(false);
			subjectsTableModel.fireTableDataChanged();
			this.subjectsTable.setVisible(true);
			if (showingSubjectsOfID > -1)
				container.switchToSubjectsAndThreadsView();
			else {
				container.switchToRootSubjectsView();
				container.stopWorkingAnimation();
			}
		}
	}

	public void showActionButtons() {

	}

	private ActionListener linkPressListener() {
		final String name = showingSubjectsOfName;
		final long id = showingSubjectsOfID;
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("idddddd" + id);
				subjectsTableModel.setFatherID(id);
				//subjectsTable.setVisible(false);
				threadsPanel.setVisible(true);
				System.out.println("name = " + name);
				container.startWorkingAnimation("retreiving subject " + 
						name
						+ " content...");
				try {

					ControllerHandlerFactory.getPipe().getSubjects(id, container);
					ControllerHandlerFactory.getPipe().getThreads(id, container);
				} 
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};	
	}


}
