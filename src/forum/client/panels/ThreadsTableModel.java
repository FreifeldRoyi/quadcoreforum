package forum.client.panels;

import javax.swing.table.AbstractTableModel;

/**
 * @author sepetnit
 *
 */
public class ThreadsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2017616938246653051L;

	private long[] threadsIDs;
	private long[] rootsIDs;

	private String[][] rowData;

	private final String[] columnNames = {"Thread",  "Messages#", "Views#"};

	public void updateData(final long[] threadsIDs, final long[] rootsIDs, final String[][] rowData) {
		this.threadsIDs = threadsIDs;
		this.rootsIDs = rootsIDs;
		this.rowData = rowData;
	}

	public long getRootMesageIDofThreadInRow(int row) {
		return this.rootsIDs[row];
	}	
	
	public String getTitleOfThreadInRow(int row) {
		return this.rowData[row][0];
	}

	public long getIDofRootMessageInRow(int row) {
		return this.rootsIDs[row];
	}

	
	public void clearData() {
		this.threadsIDs = new long[0];
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
		return 3;
	}

	public int getRowCount() {
		if (this.rowData == null)
			return 0;
		return this.rowData.length;
	}

	public Object getValueAt(int row, int col) {
		if (row >= 0 && row < rowData.length)
			return rowData[row][col];
		return "";
	}
}
