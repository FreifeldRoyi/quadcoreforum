/**
 * 
 */
package forum.swingclient.ui;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

/**
 * @author sepetnit
 *
 */
public class JScrollableTable extends JTable {

	private static final long serialVersionUID = 65621709684588442L;

	// Assumes table is contained in a JScrollPane. Scrolls the
	// cell (rowIndex, vColIndex) so that it is visible within the view-port.
	
	public void scrollToVisible(int rowIndex, int vColIndex) {
	    if (!(this.getParent() instanceof JViewport)) {
	        return;
	    }
	    JViewport tPane = (JViewport)this.getParent();

	    
	    int pRow = this.getRowHeight();
	    tPane.setViewPosition(new Point(0, rowIndex * pRow + pRow));
	    /*
	    // This rectangle is relative to the table where the
	    // northwest corner of cell (0, 0) is always (0, 0).
	    Rectangle tRequiredCellRect = this.getCellRect(rowIndex, vColIndex, true);

	    // The location of the view-port relative to the table
	    Point tViewPortStartPoint = tViewport.getViewPosition();

	    // Translates the cell location so that it is relative
	    // to the view, assuming the northwest corner of the
	    // view is (0, 0)
	    System.out.println(tRequiredCellRect.height + "gggggggggggggggggggggggggggggggggggggg");
	    tRequiredCellRect.setLocation(tRequiredCellRect.x - tViewPortStartPoint.x, tRequiredCellRect.y 
	    		- tViewPortStartPoint.y);

	    // Scroll the area into view
	    tViewport.scrollRectToVisible(tRequiredCellRect);*/
	}
}