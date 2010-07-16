/**
 * 
 */
package forum.swingclient.ui.events;

import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * @author sepetnit
 *
 */
public class JExpansionStatusSavingTree extends JTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8349008262436176806L;

	public boolean isDescendant(TreePath path1, TreePath path2){
		int tPath1Count = path1.getPathCount();
		int tPath2Count = path2.getPathCount();
		// if path2 has more elements than path1, path1 can't be a descendant of path2
		if(tPath1Count <= tPath2Count)
			return false;
		// remove the last elements of path1 by climbing up
		while (tPath1Count != tPath2Count){
			path1 = path1.getParentPath();
			tPath1Count--;
		}
		// now path1 can be descendant of path2 only if they have the same number of elements
		return path1.equals(path2);
	}
	
	public String getExpansionState(int row){
		TreePath tRowPath = this.getPathForRow(row);
		StringBuffer buf = new StringBuffer();
		int tRowCount = this.getRowCount();
		for(int i = row; i < tRowCount; i++) { // for each row down
			TreePath tPath = this.getPathForRow(i); // takes the path from root to the row
			if (i == row || isDescendant(tPath, tRowPath)){ // if the path is a descendant path
				if (this.isExpanded(tPath)) // and it is expanded
					buf.append("," + String.valueOf(i - row)); // we save its row as expanded
			} else
				break; // no paths down can be expanded if the upper paths are collapsed
		}
		return buf.toString();
	}
	
	public void restoreExpanstionState(int row, String expansionState){
		StringTokenizer tStrTok = new StringTokenizer(expansionState, ","); // splits by ","
		while (tStrTok.hasMoreTokens()) {
			int token = row + Integer.parseInt(tStrTok.nextToken());
			this.expandRow(token);
		}
	}
}
