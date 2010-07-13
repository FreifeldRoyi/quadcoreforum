/**
 * 
 */
package forum.swingclient.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author sepetnit
 *
 */
public class JForumTable extends JTable implements MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 65621709684588442L;

	private static final Color SELECTION_BACKGROUND_COLOR = new Color(231, 239, 214);

	public JForumTable() {
		super();
		this.setSelectionBackground(JForumTable.SELECTION_BACKGROUND_COLOR);
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


	public void setSelectionOnMouseMotion(boolean value) {
		this.removeMouseListener(this);		
		for (MouseMotionListener m : this.getMouseMotionListeners())
			this.removeMouseMotionListener(m);
		if (value) {
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		else {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void setColumnRenderer(int column, TableCellRenderer renderer) {
		this.getColumnModel().getColumn(column).setCellRenderer(renderer);
	}

	public void setColumnWidth(int column, int width) {
		this.getColumnModel().getColumn(column).setPreferredWidth(width);
	}
}