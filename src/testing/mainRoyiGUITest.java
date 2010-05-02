/**
 * 
 */
package testing;

import javax.swing.JDialog;
import javax.swing.JFrame;

import forum.client.panels.SearchDialog;

/**
 * @author Freifeld Royi
 *
 */
public class mainRoyiGUITest 
{
	
	public static void main(String[] args)
	{
		//JFrame frame = new JFrame();
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SearchDialog pnl = new SearchDialog();
		pnl.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
	//	frame.add(pnl);
		pnl.pack();
		pnl.setVisible(true);
	}
}
