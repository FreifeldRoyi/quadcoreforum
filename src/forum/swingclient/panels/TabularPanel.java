/**
 * 
 */
package forum.swingclient.panels;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import forum.swingclient.ui.JScrollableTable;

/**
 * @author sepetnit
 *
 */
public class TabularPanel extends JPanel {

	private static final TabularPanelState ADMINISTRATIVE_STATE = new TabularAdministrativeState();
	private static final TabularPanelState GUEST_MEMBER_STATE = new TabularGuestMemberState();
	
	private static final long serialVersionUID = -4598142672001301125L;

	public static final int SUBJECTS_TABLE = 1;
	public static final int THREADS_TABLE = 2;
	
	private static final int TABLE_LINES_NUMBER = 3;
	private static final int TABLE_ROW_HEIGHT = 17;


	protected JButton addButton;
	protected JButton deleteButton;
	protected JButton modifyButton;

	protected JScrollableTable table;
	protected ForumTableModel tableModel;

	protected MainPanel container;
	protected long shouldScrollTo; // points to the subject id whose row should be selected after GUI refresh

	protected TabularPanelState selectionState;
	
	public TabularPanel(final MainPanel cont, final int type, final String[] columnsNames) {
		super();
		this.setOpaque(false);
		
		this.selectionState = TabularPanel.GUEST_MEMBER_STATE;
		
		//this.clicksToWork = 1;
		this.container = cont;
		this.table = new JScrollableTable(type);
		table.setOpaque(false);
		this.table.setFocusable(false);
		table.setFont(new Font("Tahoma", Font.BOLD, 13));
		table.setRowHeight(TABLE_ROW_HEIGHT * TABLE_LINES_NUMBER);

		tableModel = new ForumTableModel(columnsNames);
		this.table.setModel(tableModel);
		this.table.setSelectionModel(new DefaultListSelectionModel());
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


		Dimension tButtonsDimension = new Dimension(90, 35);

		addButton = new JButton();
		deleteButton = new JButton();
		modifyButton = new JButton();

		addButton.setPreferredSize(tButtonsDimension);
		modifyButton.setPreferredSize(tButtonsDimension);		
		deleteButton.setPreferredSize(tButtonsDimension);

		deleteButton.setText("delete");
		modifyButton.setText("modify");

		JScrollPane tTablePane = new JScrollPane(table);

		tTablePane.setOpaque(false);
		tTablePane.getViewport().setOpaque(false);
		tTablePane.setBorder(BorderFactory.createEmptyBorder());
		tTablePane.getViewport().add(table);

		GroupLayout tLayout = new GroupLayout(this);
		this.setLayout(tLayout);

		tLayout.setHorizontalGroup(
				tLayout.createParallelGroup(Alignment.CENTER)
				.addGroup(tLayout.createSequentialGroup()
						.addComponent(addButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(16, 16, 16)
								.addComponent(modifyButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(16, 16, 16)
										.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(16, 16, 16)
												.addComponent(tTablePane, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE));

		tLayout.setVerticalGroup(
				tLayout.createSequentialGroup()
				.addGroup(tLayout.createParallelGroup()
						.addComponent(addButton, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(modifyButton, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGap(16, 16, 16)
												.addComponent(tTablePane, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE/* 1139*/, Short.MAX_VALUE));

		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getFirstIndex() == -1) {
					deleteButton.setEnabled(false);
					modifyButton.setEnabled(false);
				}
				else {
					deleteButton.setEnabled(true);
					modifyButton.setEnabled(true);
				}
			}
		});



		// Selection on creation

		((DefaultListSelectionModel)this.table.
				getSelectionModel()).getListSelectionListeners()[0].
				valueChanged(new ListSelectionEvent(table, -1, -1, true));

	}

	protected void setGuestView() {		
		this.selectionState = GUEST_MEMBER_STATE;
		this.table.setAdministrativeView(false);
		this.addButton.setVisible(false);
		this.modifyButton.setVisible(false);
		this.deleteButton.setVisible(false);
	}

	protected void setMemberView(){
		this.selectionState = GUEST_MEMBER_STATE;
		this.table.setAdministrativeView(false);
	}

	protected void setModeratorOrAdminView(){
		this.selectionState = ADMINISTRATIVE_STATE;
		this.table.setAdministrativeView(true);
		this.addButton.setVisible(true);
		this.modifyButton.setVisible(true);
		this.deleteButton.setVisible(true);
	}
}
