package forum.client.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 * This class represents a forum cell (overriding the default TreeCellRenderer).
 * 
 * @author Tomer Heber
 */
public class ForumTreeCellRenderer implements TreeCellRenderer { 

	private NonSelectedForumTreeCellPanel m_nonselectedPanel;
	private SelectedForumTreeCellPanel m_selectedPanel;
	private ForumTreeRootPanel m_rootPanel;
	
	public ForumTreeCellRenderer(ForumTree forumTree) {
		m_nonselectedPanel = new NonSelectedForumTreeCellPanel();
		m_selectedPanel = new SelectedForumTreeCellPanel(forumTree);
		m_rootPanel = new ForumTreeRootPanel(forumTree);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(
			JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		if (row == 0) {
			return m_rootPanel;
		}
		
		if (selected) {
			m_selectedPanel.updatePanel((ForumCell)node.getUserObject());
			return m_selectedPanel;			
		}
		else {
			m_nonselectedPanel.updatePanel((ForumCell)node.getUserObject());
			return m_nonselectedPanel;
		}
	}

}
