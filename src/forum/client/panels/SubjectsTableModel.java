/**
 * 
 */
package forum.client.panels;

import javax.swing.table.AbstractTableModel;

/**
 * @author sepetnit
 *
 */
public class SubjectsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2017616938246653051L;

	private long[] subjectsIDs;
	private String[][] rowData;

	private final String[] columnNames = {"Subject",  "Description", "Sub-Subjects#", 
			"Messages#", "Last Message Info" };

	public void updateData(final long[] subjectsIDs, final String[][] rowData) {
		this.subjectsIDs = subjectsIDs;
		this.rowData = rowData;
	}
	
	public void clearData() {
		this.subjectsIDs = new long[0]; 
		rowData = new String[0][0];		
	}

	public SubjectsTableModel() {
		super();
		this.clearData();
	}

	public String getColumnName(int col) {
		return this.columnNames[col];
	}

	public int getColumnCount() {
		return 5;
	}

	public String getNameOfSubjectInRow(int row) {
		return this.rowData[row][0];
	}
	public long getIDofSubjectInRow(int row) {
		return this.subjectsIDs[row];
	}
	
	public int getRowCount() {
		if (this.rowData == null)
			return 0;
		return this.rowData.length;
	}

	public Object getValueAt(int row, int col) {
		if (row >= 0 && row < rowData.length && col < 2)
				return rowData[row][col];
		return "";

	}
}
