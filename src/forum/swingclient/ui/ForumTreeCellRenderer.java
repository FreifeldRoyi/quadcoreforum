package forum.swingclient.ui;

import java.awt.Color;
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

	private static final Color SELECTION_BACKGROUND_COLOR = new Color(231, 239, 214);

	
	private NonSelectedForumTreeCellPanel m_nonselectedPanel;
	private SelectedForumTreeCellPanel m_selectedPanel;
	private ForumTree forumTree;
	
	
	public ForumTreeCellRenderer(ForumTree forumTree, SelectedForumTreeCellPanel selected) {
		m_nonselectedPanel = new NonSelectedForumTreeCellPanel();
		//m_selectedPanel = new SelectedForumTreeCellPanel(forumTree);
		m_selectedPanel = selected;
		this.forumTree = forumTree;		
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(
			JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		
		if (node == null || node.getUserObject() instanceof String) {
			return m_nonselectedPanel;
		}
		
		if (selected) {
			m_nonselectedPanel.updatePanel((ForumCell)node.getUserObject());
			m_nonselectedPanel.select(SELECTION_BACKGROUND_COLOR);
			m_selectedPanel.updatePanel((ForumCell)node.getUserObject(), forumTree.getConnectedUser());
			return m_nonselectedPanel;			
		}
		else {
			m_nonselectedPanel.unselect();
			m_nonselectedPanel.updatePanel((ForumCell)node.getUserObject());
			return m_nonselectedPanel;
		}
	}

}
