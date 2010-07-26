/**
 * 
 */
package forum.swingclient.panels;

import javax.swing.SwingUtilities;

import forum.swingclient.ui.JScrollableTable;

/**
 * @author sepetnit
 *
 */
public class TabularAdministrativeState extends TabularPanelState {
	
	public TabularAdministrativeState() {
		clicksToWork = 2;
	}
	
	@Override
	public void selectAndScrollToRow(final JScrollableTable table, final int row) {
		// By using SwingUtilities we are bypassing the problem to get the maximum rows value
		// before the size is changed

		table.getSelectionModel().setSelectionInterval(row, row);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				table.scrollToVisible(row, 0);
			}
		});
		

	}

}
