/**
 * 
 */
package forum.swingclient.panels;
import forum.swingclient.ui.*;

/**
 * @author sepetnit
 *
 */
public abstract class TabularPanelState {
	protected int clicksToWork; // the number of clicks in order to open the clicked issue

	public abstract void selectAndScrollToRow(JScrollableTable table, int row);
	
	public boolean shouldRespondToClick(int clickCount) {
		return this.clicksToWork == clickCount;
	}
	
}
