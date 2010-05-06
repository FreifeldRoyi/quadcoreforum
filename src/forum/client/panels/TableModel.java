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
	private long fatherID;
	private long[] subjectsIDs;
	private String[][] rowData;
	private String[] columnNames;

	public void updateData(final long[] subjectsIDs, final String[][] rowData) {
		this.subjectsIDs = subjectsIDs;
		this.rowData = rowData;
	}

	public void setFatherID(long fatherID) {
		this.fatherID = fatherID;
	}

	public long getFatherID() {
		return this.fatherID;
	}

	public long getIDofContentInRow(int row) {
		return this.subjectsIDs[row];
	}

	public String getNameOfContentInRow(int row) {
		return this.rowData[row][0];
	}

	public void clearData() {
		this.subjectsIDs = new long[0]; 
		rowData = new String[0][0];		
	}
	
	public TableModel(String[] columns) {
		super();
		fatherID = -1;
		columnNames = columns;
		this.clearData();
	}
	
	public String getColumnName(int col) {
		return this.columnNames[col];
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		if (this.rowData == null)
			return 0;
		return this.rowData.length;
	}

	public Object getValueAt(int row, int col) {
		if (row >= 0 && row < rowData.length && col >= 0 && col < columnNames.length)
			return rowData[row][col];
		return "";
	}
}
