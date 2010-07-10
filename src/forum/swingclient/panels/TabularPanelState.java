/**
 * 
 */
package forum.swingclient.panels;

import javax.swing.JTable;

/**
 * @author sepetnit
 *
 */
public abstract class TabularPanelState {
	protected int clicksToWork; // the number of clicks in order to open the clicked issue

	public abstract void selectFirstRow(JTable table);
	
	public boolean shouldRespondToClick(int clickCount) {
		return this.clicksToWork == clickCount;
	}
	
}
