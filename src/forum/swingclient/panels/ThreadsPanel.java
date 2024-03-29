/**
 * 
 */
package forum.swingclient.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.*;

import forum.swingclient.controllerlayer.ControllerHandler;
import forum.swingclient.controllerlayer.ControllerHandlerFactory;
import forum.swingclient.controllerlayer.GUIObserver;
import forum.swingclient.ui.ForumTree;
import forum.swingclient.ui.events.GUIHandler;
import forum.swingclient.ui.events.GUIEvent.EventType;
import forum.server.domainlayer.SystemLogger;

/**
 * @author sepetnit
 *
 */
public class ThreadsPanel extends TabularPanel implements GUIHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5210803978059150523L;

	private ForumTree messages;

	
	public ThreadsPanel(final MainPanel cont, final ForumTree messages) {
		super(cont, TabularPanel.THREADS_TABLE, new String[]{"Select", "Thread",  "Responses#", "Views#"});
		this.messages = messages;
		
		this.table.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// handle double click
				if (selectionState.shouldRespondToClick(e.getClickCount())) {
					int rowSelected = table.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1) {

						final long tMessageIDToLoad = tableModel.getIDofContentInRow(rowSelected);

						container.startWorkingAnimation("retreiving thread " + 
								tableModel.getNameOfContentInRow(rowSelected) 
								+ " content...");

						messages.setFatherID(tableModel.getFatherID());
						tableModel.clearData();
						tableModel.fireTableDataChanged();

						
						messages.setRootMessage(tMessageIDToLoad);
					}
				}
			}
		});
		
		this.addButton.setText("open new");

		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ReplyModifyDialog tNewThreadDialog =
					new ReplyModifyDialog(container.getConnectedUser().getID(), tableModel.getFatherID(), 
							"", "", "thread", addButton);
				tNewThreadDialog.setVisible(true);
				if (tNewThreadDialog.shouldUpdateGUI()) {
					shouldScrollTo = tNewThreadDialog.getChangedID();
					try {
						ControllerHandlerFactory.getPipe().getThreads(tableModel.getFatherID(), table);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
					}
				}
				tNewThreadDialog.dispose();
			}});


		modifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String tCurrentThreadTopic = (String)tableModel.getValueAt(table.getSelectedRow(), 1);

				String tResponse = (String)
				JOptionPane.showInputDialog(ThreadsPanel.this, "Please enter a new topic: ", "Modify " +
						"thread topic", JOptionPane.PLAIN_MESSAGE, null, null, tCurrentThreadTopic);
				if (tResponse == null) 
					return;
				else 
					if (tResponse.trim().length() == 0) {
						JOptionPane.showMessageDialog(ThreadsPanel.this, "The thread topic can't be empty!", "Modify " +
								"thread topic", JOptionPane.ERROR_MESSAGE);
						modifyButton.doClick();
					}
					else {
						try {
							final long tThreadToModifyID = tableModel.getIDofContentInRow(table.getSelectedRow());
							shouldScrollTo = tThreadToModifyID;
							final ControllerHandler controller = ControllerHandlerFactory.getPipe();

							controller.addObserver(
									new GUIObserver(new GUIHandler() {
										public void notifyError(String errorMessage) {
											controller.deleteObserver(this);
											JOptionPane.showMessageDialog(ThreadsPanel.this, 
													"cannot modify the thread!", "error", JOptionPane.ERROR_MESSAGE);
										}

										public void refreshForum(final String encodedView) {

											if (encodedView.startsWith("threadupdatesuccess")) {
												JOptionPane.showMessageDialog(ThreadsPanel.this, 
														"The thread was modified " +
														" successfully.", "modify success", JOptionPane.INFORMATION_MESSAGE);

												controller.deleteObserver(this);
												new Thread(new Runnable() {
													public void run() {
														controller.getSubjects(tableModel.getFatherID(), container);
														controller.getThreads(tableModel.getFatherID(), container);
													}}).start();
											}
										}
									}), EventType.THREADS_UPDATED);

							controller.modifyThread(container.getConnectedUser().getID(),
									tThreadToModifyID, tResponse, modifyButton);
						}


						catch (IOException e) {
							// TODO Auto-generated catch block
						}
					}
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {

					if (JOptionPane.showConfirmDialog(ThreadsPanel.this,
							"Are you sure you want to delete the entire thread?", "delete",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
								JOptionPane.NO_OPTION) {
						return;
					}

					final long tThreadToDeleteID = tableModel.getIDofContentInRow(table.getSelectedRow());

					final ControllerHandler controller = ControllerHandlerFactory.getPipe();


					controller.addObserver(
							new GUIObserver(new GUIHandler() {
								public void notifyError(String errorMessage) {
									controller.deleteObserver(this);
									JOptionPane.showMessageDialog(ThreadsPanel.this, 
											"cannot delete the thread!", "error", JOptionPane.ERROR_MESSAGE);
								}

								public void refreshForum(final String encodedView) {

									if (encodedView.startsWith("deletesuccess")) {
										JOptionPane.showMessageDialog(ThreadsPanel.this, 
												"The thread was deleted " +
												" successfully.", "delete success", JOptionPane.INFORMATION_MESSAGE);
										controller.deleteObserver(this);
										shouldScrollTo = -1;
										new Thread(new Runnable() {
											public void run() {
												controller.getSubjects(tableModel.getFatherID(), container);
												controller.getThreads(tableModel.getFatherID(), container);
											}}).start();
									}
								}
							}), EventType.MESSAGES_UPDATED);

					controller.deleteMessage(container.getConnectedUser().getID(), -1, 
							tThreadToDeleteID, deleteButton);


				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		});

	}	

	public void updateFather(long fatherID) {
		tableModel.setFatherID(fatherID);
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
		if (encodedView.startsWith("addthreadsuccess") ||
				encodedView.startsWith("threadupdatesuccess")) return;
		this.tableModel.clearData();
		if (!encodedView.startsWith("There")) {
			// each line should represent one thread
			String[] tSplitted = encodedView.split("\n");
			// this is the data which will be presented in the threads table
			Object[][] tData = new Object[tSplitted.length][4];

			JRadioButton[] tSelectionButtons = new JRadioButton[tSplitted.length];
			this.table.setFirstColumnRadiosGroup(new ButtonGroup());

			
			boolean tScrollToFound = 
				(this.shouldScrollTo == -1)? true : false; // after a thread was updated we want to scroll to its row

			// this is the IDs array which should contain the presented subjects' IDs
			long[] tIDs = new long[tSplitted.length];
			long[] tRoots = new long[tSplitted.length];

			for (int i = 0; i < tSplitted.length; i++) {
				String[] tCurrentThreadInfo = tSplitted[i].split("\t");
				// this is the subject's id
				try {
					tIDs[i] = Long.parseLong(tCurrentThreadInfo[0]);

					if (!tScrollToFound && (this.shouldScrollTo == tIDs[i])) {
						this.shouldScrollTo = i;
						tScrollToFound = true;
					}						

					tRoots[i] = Long.parseLong(tCurrentThreadInfo[1]);
					
					tSelectionButtons[i] = new JRadioButton();
					this.table.getFirstColumnRadiosGroup().add(tSelectionButtons[i]);
					tSelectionButtons[i].setHorizontalAlignment(SwingConstants.CENTER);
					tData[i][0] = tSelectionButtons[i];

					for (int j = 2; j < tCurrentThreadInfo.length; j++)
						tData[i][j - 1] = tCurrentThreadInfo[j];
					this.tableModel.updateData(tRoots, tData);
				}
				catch (NumberFormatException e) {
					SystemLogger.warning("The server response related to subject's update is invalid");
					break;
				}
			}
		}
		
		this.tableModel.fireTableDataChanged();

		super.selectAndScrollToRow();
		
		this.table.setVisible(true);
		this.setVisible(true);
		container.stopWorkingAnimation();
	}
	
	protected void setMemberView(){
		super.setMemberView();
		this.addButton.setVisible(true);
	}
}
