/**
 * 
 */
package forum.swingclient.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.StyleConstants;


import forum.swingclient.panels.ForumTableCellEditor;
import forum.swingclient.panels.ForumTableCellRenderer;
import forum.swingclient.panels.TabularPanel;

/**
 * @author sepetnit
 *
 */
public class JScrollableTable extends JForumTable implements MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 65621709684588442L;

	private static final Color SELECTION_BACKGROUND_COLOR = new Color(231, 239, 214);

	private ButtonGroup selectionRowsGroup;
	private int type;

	private TableColumn firstColumn;
	private boolean firstColumnShown;

	public JScrollableTable(int type) {
		super();
		this.type = type;
		this.firstColumnShown = false;
		this.setSelectionBackground(JScrollableTable.SELECTION_BACKGROUND_COLOR);
	}

	// MouseListener implementation
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {
		this.clearSelection();
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	// MouseMotionListener implementation
	public void mouseMoved(MouseEvent e) {  
		int row = this.rowAtPoint(e.getPoint());  
		if (row > -1) {
			this.clearSelection();  
			this.setRowSelectionInterval(row, row);  
			//this.scrollToVisible(row, 0);
		}
	}
	public void mouseDragged(MouseEvent e) {/* do nothing */}

	private void setFirstColumnVisible(boolean value) {
		if (value && !firstColumnShown) {
			this.getColumnModel().addColumn(firstColumn);
			this.moveColumn(this.getColumnCount() - 1, 0);			
			firstColumnShown = true;
		}
		else {
			this.getColumnModel().removeColumn(firstColumn);
			firstColumnShown = false;
		}
	}

	public void setAdministrativeView(boolean value) {
		this.clearSelection();

		if (value) {
			if (this.getRowCount() > 0)
				this.getSelectionModel().setSelectionInterval(0, 0);
		}


		this.setFirstColumnVisible(value);
		this.setSelectionOnMouseMotion(!value);
	}

	public ButtonGroup getFirstColumnRadiosGroup() {
		return selectionRowsGroup;
	}

	public void setFirstColumnRadiosGroup(ButtonGroup toSet) {
		this.selectionRowsGroup = toSet;
	}

	public void setModel(TableModel model) {
		super.setModel(model);
		if (model.getColumnCount() > 0) { // the the model was initialized
			// sets the first column to radio buttons column
			this.getColumnModel().getColumn(0).setCellRenderer(new RadioButtonRenderer());
			this.getColumnModel().getColumn(0).setResizable(false);
			this.getColumnModel().getColumn(0).setCellEditor(
					new ForumTableCellEditor(new JCheckBox()));
			firstColumn = this.getColumnModel().getColumn(0);		
			// assigns renderers and sets widths
			if (type == TabularPanel.SUBJECTS_TABLE)
				this.constructSubjectsTable(this.getColumnCount());
			else
				this.constructThreadsTable(this.getColumnCount());
		}
	}

	private void constructSubjectsTable(int columnsNumber) {
		// sets widths
		this.setColumnWidth(0, 10);
		this.setColumnWidth(1, 340);
		this.setColumnWidth(2, 340);
		this.setColumnWidth(3, 90);
		this.setColumnWidth(4, 90);
		//this.setColumnWidth(5, 250);

		TableCellRenderer tLeftAlignmentRenderer = new ForumTableCellRenderer(StyleConstants.ALIGN_LEFT);
		TableCellRenderer tCenterAlignmentRenderer = new ForumTableCellRenderer(StyleConstants.ALIGN_CENTER);

		this.setColumnRenderer(1, tLeftAlignmentRenderer);
		this.setColumnRenderer(2, tLeftAlignmentRenderer);
		this.setColumnRenderer(3, tCenterAlignmentRenderer);
		this.setColumnRenderer(4, tCenterAlignmentRenderer);
		//this.setColumnRenderer(5, tLeftAlignmentRenderer);
	}


	private void constructThreadsTable(int columnsNumber) {
		this.setColumnWidth(0, 50);
		this.setColumnWidth(1, 1001);
		this.setColumnWidth(2, 100);
		this.setColumnWidth(3, 100);

		this.setColumnRenderer(1,  new ForumTableCellRenderer(StyleConstants.ALIGN_LEFT));

		TableCellRenderer tCenterAlignmentRenderer = new ForumTableCellRenderer(StyleConstants.ALIGN_CENTER);
		this.setColumnRenderer(2, tCenterAlignmentRenderer);
		this.setColumnRenderer(3, tCenterAlignmentRenderer);

	}

	// Assumes table is contained in a JScrollPane. Scrolls the
	// cell (rowIndex, vColIndex) so that it is visible within the view-port.

	public void scrollToVisible(int rowIndex, int vColIndex) {
		if (!(this.getParent() instanceof JViewport)) {
			return;
		}
		JViewport tViewport = (JViewport)this.getParent();

		// This rectangle is relative to the table where the
		// northwest corner of cell (0, 0) is always (0, 0).
		Rectangle tRequiredCellRect = this.getCellRect(rowIndex, vColIndex, true);

		// The location of the view-port relative to the table
		Point tViewPortStartPoint = tViewport.getViewPosition();

		// Translates the cell location so that it is relative
		// to the view, assuming the northwest corner of the
		// view is (0, 0)	    
		tRequiredCellRect.setLocation(tRequiredCellRect.x - tViewPortStartPoint.x, tRequiredCellRect.y 
				- tViewPortStartPoint.y);

		// Scroll the area into view
		tViewport.scrollRectToVisible(tRequiredCellRect);
	}

	private class RadioButtonRenderer implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5702840760849759826L;


		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (value == null)
				return null;
			selectionRowsGroup.clearSelection();
			if (isSelected)
				((JRadioButton)value).setSelected(true);
			else
				((JRadioButton)value).setSelected(false);
			return (Component) value;
		}
	}
}