package forum.swingclient.panels;

import javax.swing.table.AbstractTableModel;

/**
 * @author sepetnit
 *
 */
public class ThreadsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2017616938246653051L;

	private long[] rootsIDs;
	private long fatherID;
	private String[][] rowData;

	private final String[] columnNames = {"Thread",  "Messages#", "Views#"};

	public void updateData(final long[] rootsIDs, final String[][] rowData) {
		this.rootsIDs = rootsIDs;
		this.rowData = rowData;
	}

//	public long getRootMesageIDofThreadInRow(int row) {
//	return this.rootsIDs[row];
//}	
	public void setFatherID(long fatherID) {
		this.fatherID = fatherID;
	}

	public long getFatherID() {
		return this.fatherID;
	}
	
	public long getIDofContentInRow(int row) {
		return this.rootsIDs[row];
	}

	public String getNameOfContentInRow(int row) {
		return this.rowData[row][0];
	}
	
	public void clearData() {
		this.rootsIDs = new long[0];
		rowData = new String[0][0];
	}
	
	public ThreadsTableModel() {
		super();
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
