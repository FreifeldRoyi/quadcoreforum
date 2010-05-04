/**
 * 
 */
package forum.client.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import forum.client.controllerlayer.ControllerHandlerFactory;
import forum.client.ui.events.GUIHandler;
import forum.server.domainlayer.SystemLogger;

/**
 * @author sepetnit
 *
 */
public class SubjectsPanel extends JPanel implements GUIHandler {

	/**
	 * A thread pool that is used to initiate operations in the controller layer.
	 */
	private ExecutorService pool = Executors.newCachedThreadPool();

	/**
	 * 
	 */
	private static final long serialVersionUID = -5210803978059150523L;

	private JTable subjectsTable;
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
						showingSubjectsOfName = subjectsTableModel.getNameOfSubjectInRow(rowSelected) ;
						container.startWorkingAnimation("retreiving subject " + 
								showingSubjectsOfName
								+ " content...");

						final long subjectToLoad = subjectsTableModel.getIDofSubjectInRow(rowSelected);
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

		Dimension tSubjectsButtonsDimension = new Dimension(85, 35);
		addNewSubjectButton.setPreferredSize(tSubjectsButtonsDimension);

		deleteSubjectButton.setPreferredSize(tSubjectsButtonsDimension);
		modifySubjectButton.setPreferredSize(tSubjectsButtonsDimension);		


		JScrollPane tSubjectsTablePane = new JScrollPane(subjectsTable);

		GroupLayout tLayout = new GroupLayout(this);
		this.setLayout(tLayout);
		tLayout.setHorizontalGroup(
				tLayout.createSequentialGroup()
				.addComponent(tSubjectsTablePane, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE)
				.addGap(16, 16, 16)
				.addGroup(tLayout.createParallelGroup()
						.addComponent(addNewSubjectButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(modifySubjectButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(deleteSubjectButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		);

		tLayout.setVerticalGroup(
				tLayout.createParallelGroup()
				.addComponent(tSubjectsTablePane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// GroupLayout.PREFERRED_SIZE)
				.addGroup(Alignment.CENTER, tLayout.createSequentialGroup()
						.addComponent(addNewSubjectButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(16, 16, 16)
								.addComponent(modifySubjectButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(16, 16, 16)
										.addComponent(deleteSubjectButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));


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
		this.subjectsTable.setVisible(false);
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
					this.showingSubjectsOfName = "";
					this.showingSubjectsOfID = -1;
					this.subjectsTableModel.clearData();
					break;
				}
			}
		}
		subjectsTableModel.fireTableDataChanged();
		this.subjectsTable.setVisible(true);
		if (showingSubjectsOfID > -1)
			container.switchToSubjectsAndThreadsView();
		else {
			container.switchToRootSubjectsView();
			container.stopWorkingAnimation();
		}
	}
	
	private ActionListener linkPressListener() {
		final String name = showingSubjectsOfName;
		final long id = showingSubjectsOfID;
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				subjectsTable.setVisible(false);
				threadsPanel.changeTableVisible();
				threadsPanel.setVisible(true);
				System.out.println("name = " + name);
				container.startWorkingAnimation("retreiving subject " + 
						name
						+ " content...");
				try {
					ControllerHandlerFactory.getPipe().getSubjects(id, container);
					ControllerHandlerFactory.getPipe().getThreads(id, container);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};	
	}
	

}
