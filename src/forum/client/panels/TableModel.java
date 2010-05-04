/**
 * 
 */
package forum.client.panels;

import javax.swing.table.AbstractTableModel;

/**
 * @author sepetnit
 *
 */
public class TableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2017616938246653051L;

	private long[] subjectsIDs;
	private String[][] rowData;

	private String[] columnNames;

	public void updateData(final long[] subjectsIDs, final String[][] rowData) {
		this.subjectsIDs = subjectsIDs;
		this.rowData = rowData;
	}
	
	public void clearData() {
		this.subjectsIDs = new long[0]; 
		rowData = new String[0][0];		
	}

	public TableModel(String[] columns) {
		super();
		columnNames = columns;
		this.clearData();
	}

	public String getColumnName(int col) {
		return this.columnNames[col];
	}

	public int getColumnCount() {
		return columnNames.length;
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
