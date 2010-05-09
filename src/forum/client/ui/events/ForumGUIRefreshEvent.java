package forum.client.ui.events;

import java.awt.Component;

/**
 * This event enables a component (if there is one). And updates the forum view in the GUI.
 * 
 * @author Tomer Heber
 */
public class ForumGUIRefreshEvent extends GUIEvent {

	private Component comp;
	private String forumUpdatedData;
	
	
	public ForumGUIRefreshEvent(String forumView, EventType type) {
		super(type);
		forumUpdatedData = forumView;
		comp = null;
	}
	
	public ForumGUIRefreshEvent(Component comp, String forumView, EventType type) {
		super(type);
		forumUpdatedData = forumView;
		this.comp = comp;
	}
	
	/* (non-Javadoc)
	 * @see forumtree.contol.ForumTreeEvent#respondToEvent(forumtree.ForumTree)
	 */
	@Override
	public void respondToEvent(GUIHandler handler) {
		if (comp != null) {
			comp.setEnabled(true);
		}
	
		handler.refreshForum(forumUpdatedData);

	}

}
