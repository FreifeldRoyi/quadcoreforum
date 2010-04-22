/**
 * 
 */
package forum.client.panels;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import forum.client.ui.events.GUIHandler;
import forum.server.domainlayer.SystemLogger;

/**
 * @author sepetnit
 *
 */
public class SubjectsPanel extends JPanel implements GUIHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5210803978059150523L;

	private JTable subjectsTable;
	private SubjectsTableModel subjectsTableModel;
	private MainPanel container;	
	private ThreadsPanel threadsPanel;
	
	public SubjectsPanel(final MainPanel cont, final ThreadsPanel threads) {
		this.container = cont;
		this.threadsPanel = threads;
		this.subjectsTable = new JTable();
		this.subjectsTable.setSelectionModel(new DefaultListSelectionModel());
		this.subjectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.subjectsTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// handle double click
				if (e.getClickCount() == 2) {
					int rowSelected = subjectsTable.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1) {
						subjectsTable.setVisible(false);
						threadsPanel.changeTableVisible();
						threadsPanel.setVisible(true);
						container.startWorkingAnimation("retreiving subject " + 
								subjectsTableModel.getNameOfSubjectInRow(rowSelected) 
								+ " content...");
						final long subjectToLoad = subjectsTableModel.getIDofSubjectInRow(rowSelected);
						MainPanel.controller.getSubjects(subjectToLoad, container);
						MainPanel.controller.getThreads(subjectToLoad, container);
					}
				}
			}
		});
		subjectsTableModel = new SubjectsTableModel();
		this.subjectsTable.setModel(subjectsTableModel);
		this.add(new JScrollPane(subjectsTable));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}	

	/* (non-Javadoc)
	 * @see forum.client.ui.events.GUIHandler#notifyError(java.lang.String)
	 */
	public void notifyError(String errorMessage) {
		// TODO Auto-generated method stub

	}

	public void refreshForum(String encodedView) {
		this.subjectsTableModel.clearData();
		if (!encodedView.startsWith("There")) {
		/*	JOptionPane.showMessageDialog(container, "There are no subjects under the subject " + 
					this.subjectsTableModel.getNameOfSubject(subjectsTable.getSelectionModel().getMinSelectionIndex()), 
					"no subjects", JOptionPane.INFORMATION_MESSAGE);
		 */


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
					break;
				}
			}
		}
		subjectsTableModel.fireTableDataChanged();
		this.subjectsTable.setVisible(true);
		container.stopWorkingAnimation();
	}
}
