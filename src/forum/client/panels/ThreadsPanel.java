/**
 * 
 */
package forum.client.panels;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import forum.client.ui.ForumTree;
import forum.client.ui.events.GUIHandler;
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
	private ThreadsTableModel threadsTableModel;
	private MainPanel container;	
	private ForumTree messages;

	public ThreadsPanel(final MainPanel container, final ForumTree messages) {
		this.container = container;
		this.messages = messages;
		//		this.messages.getForumTreeUI().setVisible(false);
		this.threadsTable = new JTable();

		this.threadsTable.setSelectionModel(new DefaultListSelectionModel());
		this.threadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.threadsTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				// handle double click
				if (e.getClickCount() == 2) {
					int rowSelected = threadsTable.getSelectionModel().getMinSelectionIndex();
					if (rowSelected != -1) {
						container.startWorkingAnimation("retreiving thread " + 
								threadsTableModel.getTitleOfThreadInRow(rowSelected) 
								+ " content...");
						final long tMessageIDToLoad = threadsTableModel.getIDofRootMessageInRow(rowSelected);
						messages.setRootMessage(tMessageIDToLoad);
					}
				}
			}
		});

		this.threadsTableModel = new ThreadsTableModel();

		this.threadsTable.setModel(this.threadsTableModel);
		this.add(new JScrollPane(threadsTable));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}	

	public void changeTableVisible() {
		this.threadsTable.setVisible(!this.threadsTable.isVisible());
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
					this.threadsTableModel.updateData(tIDs, tRoots, tData);
				}
				catch (NumberFormatException e) {
					SystemLogger.warning("The server response related to subject's update is invalid");
					break;
				}
			}
		}	
		this.threadsTableModel.fireTableDataChanged();
		this.threadsTable.setVisible(true);

		container.stopWorkingAnimation();
	}
}
