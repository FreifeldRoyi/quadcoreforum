/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

/**
 * @author sepetnit
 *
 */
public class SubjectsPanel extends TabularPanel implements GUIHandler {

	private static final long serialVersionUID = -5210803978059150523L;

	private ThreadsPanel threadsPanel;

	private String showingSubjectsOfName;
	private long showingSubjectsOfID;

	
	public SubjectsPanel(final MainPanel cont, final ThreadsPanel threads) {
		super(cont, TabularPanel.SUBJECTS_TABLE, 
				new String[] {"Select", "Subject",  "Description", "Subjects#", 
				"Messages#", "Last Message Info"});
		this.threadsPanel = threads;
		this.showingSubjectsOfName = "";
		this.showingSubjectsOfID = -1;

		this.table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// handle double click
				if (selectionState.shouldRespondToClick(e.getClickCount())) {
					int rowSelected = table.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1) {
						//subjectsTable.setVisible(false);
						threadsPanel.setVisible(true);
						showingSubjectsOfName = tableModel.getNameOfContentInRow(rowSelected) ;
						container.startWorkingAnimation("retreiving subject " + 
								showingSubjectsOfName
								+ " content...");

						final long subjectToLoad = tableModel.getIDofContentInRow(rowSelected);
						tableModel.clearData();
						tableModel.fireTableDataChanged();

						tableModel.setFatherID(subjectToLoad);
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

		addButton.setText("add new");

		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ReplyModifyDialog tNewSubjectDialog =
					new ReplyModifyDialog(container.getConnectedUser().getID(), 
							tableModel.getFatherID(), "", "", "subject", addButton);
				tNewSubjectDialog.setVisible(true);
				if (tNewSubjectDialog.shouldUpdateGUI()) {
					shouldScrollTo = tNewSubjectDialog.getChangedID();
					try {
						ControllerHandlerFactory.getPipe().getSubjects(tableModel.getFatherID(), table);
					} 
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				tNewSubjectDialog.dispose();
			}});


		this.modifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int tSelectedSubjectRowIndex = table.getSelectedRow();

				ReplyModifyDialog tModifySubjectDialog =
					new ReplyModifyDialog(container.getConnectedUser().getID(),
							tableModel.getIDofContentInRow(tSelectedSubjectRowIndex),
							(String)table.getValueAt(tSelectedSubjectRowIndex, 1),
							(String)table.getValueAt(tSelectedSubjectRowIndex, 2), 
							"modifysubject", modifyButton);

				tModifySubjectDialog.setVisible(true);
				if (tModifySubjectDialog.shouldUpdateGUI()) {
					shouldScrollTo = tModifySubjectDialog.getChangedID();
					try {
						ControllerHandlerFactory.getPipe().getSubjects(tableModel.getFatherID(), table);
					}
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				tModifySubjectDialog.dispose();
			}});
		
		
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {

					if (JOptionPane.showConfirmDialog(SubjectsPanel.this,
							"Are you sure you want to delete the entire subject?", "delete",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
								JOptionPane.NO_OPTION) {
						return;
					}

					final long tSubjectToDeleteID = tableModel.getIDofContentInRow(table.getSelectedRow());

					final ControllerHandler controller = ControllerHandlerFactory.getPipe();

					controller.addObserver(
							new GUIObserver(new GUIHandler() {
								public void notifyError(String errorMessage) {
									controller.deleteObserver(this);
									JOptionPane.showMessageDialog(SubjectsPanel.this, 
											"cannot delete the subject!", "error", JOptionPane.ERROR_MESSAGE);
								}

								public void refreshForum(final String encodedView) {

									if (encodedView.startsWith("deletesubjectsuccess")) {
										JOptionPane.showMessageDialog(SubjectsPanel.this, 
												"The subject with id " + tSubjectToDeleteID + " was deleted " +
												" successfully.", "delete success", JOptionPane.INFORMATION_MESSAGE);
										controller.deleteObserver(this);
										shouldScrollTo = -1;
										new Thread(new Runnable() {
											public void run() {
												System.out
														.println("iiiiiiiiiiiiiiiiiiiiiiii " + tableModel.getFatherID());
												controller.getSubjects(tableModel.getFatherID(), container);
												if (tableModel.getFatherID() > -1)
													controller.getThreads(tableModel.getFatherID(), container);
											}}).start();
									}
								}
							}), EventType.SUBJECTS_UPDATED);

					controller.deleteSubject(container.getConnectedUser().getID(), 
							tableModel.getFatherID(), tSubjectToDeleteID, deleteButton);


				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

	public void setFatherID(int fatherID) {
		this.tableModel.setFatherID(fatherID);
	}

	public void setGuestView(){
		super.setGuestView();
		this.threadsPanel.setGuestView();
	}

	public void setMemberView(){
		super.setMemberView();
		this.threadsPanel.setMemberView();
	}

	public void setModeratorOrAdminView(){
		super.setModeratorOrAdminView();
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
				encodedView.startsWith("addsubjectsuccess") ||
				encodedView.startsWith("subjectupdatesuccess") ||
				encodedView.startsWith("deletesubject")) return;

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
			this.tableModel.clearData();
			this.table.removeAll();
			if (!encodedView.startsWith("There")) {

				// each line should represent one subject
				String[] tSplitted = encodedView.split("\n\t\r");
				// this is the data which will be presented in the subjects table
				Object[][] tData = new Object[tSplitted.length][6];

				JRadioButton[] tSelectionButtons = new JRadioButton[tSplitted.length];

				this.table.setFirstColumnRadiosGroup(new ButtonGroup());
				
				boolean tScrollToFound = 
					(this.shouldScrollTo == -1)? true : false; // after a thread was updated we want to scroll to its row


				// this is the IDs array which should contain the presented subjects' IDs
				long[] tIDs = new long[tSplitted.length];
				for (int i = 0; i < tSplitted.length; i++) {
					String[] tCurrentSubjectInfo = tSplitted[i].split("\t");
					// this is the subject's id
					try {
						tIDs[i] = Long.parseLong(tCurrentSubjectInfo[0]);

						if (!tScrollToFound && (this.shouldScrollTo == tIDs[i])) {
							this.shouldScrollTo = i;
							tScrollToFound = true;
						}						

						tSelectionButtons[i] = new JRadioButton();
						this.table.getFirstColumnRadiosGroup().add(tSelectionButtons[i]);
						tSelectionButtons[i].setHorizontalAlignment(SwingConstants.CENTER);
						tData[i][0] = tSelectionButtons[i];
						for (int j = 1; j < tCurrentSubjectInfo.length; j++)
							tData[i][j] = tCurrentSubjectInfo[j];
						this.tableModel.updateData(tIDs, tData);


					}
					catch (NumberFormatException e) {
						SystemLogger.warning("The server response related to subject's update is invalid");
						this.showingSubjectsOfName = "";
						this.showingSubjectsOfID = -1;
						this.tableModel.clearData();
						break;
					}
				}
			}

			//this.table.setVisible(false);


			tableModel.fireTableDataChanged();

			super.selectAndScrollToRow();

			this.table.setVisible(true);
			this.setVisible(true);
			if (showingSubjectsOfID > -1)
				container.switchToSubjectsAndThreadsView();
			else {
				container.switchToRootSubjectsView();
				container.stopWorkingAnimation();
			}
		}
	}

	private ActionListener linkPressListener() {
		final String name = showingSubjectsOfName;
		final long id = showingSubjectsOfID;
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.setFatherID(id);
				//				threadsPanel.setVisible(true);
				System.out.println("name = " + name);
				container.startWorkingAnimation("retreiving subject " + 
						name
						+ " content...");
				try {

					ControllerHandlerFactory.getPipe().getSubjects(id, (Component)e.getSource());
					ControllerHandlerFactory.getPipe().getThreads(id, (Component)e.getSource());
				} 
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};	
	}
}
