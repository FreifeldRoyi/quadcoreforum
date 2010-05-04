package forum.client.ui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

/**
 * @author Tomer Heber
 *
 */
public class ForumTreeCellEditor extends AbstractCellEditor implements
		TreeCellEditor {

	private static final long serialVersionUID = 180002002369366043L;
	
	private ForumTreeCellRenderer m_ftcr;
	
	public ForumTreeCellEditor(ForumTreeCellRenderer renderer) {
		m_ftcr = renderer;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
	 */
	@Override
	public Component getTreeCellEditorComponent
			(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		return m_ftcr.getTreeCellRendererComponent(tree,value,true,expanded,leaf,row,true);		
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
		return null;
	}

}
