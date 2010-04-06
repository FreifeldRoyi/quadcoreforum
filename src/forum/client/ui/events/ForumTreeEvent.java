package forum.client.ui.events;

import forum.client.ui.ForumTreeHandler;

/**
 * Classes that implement this interface are events related to the ForumTree.
 * 
 * @author Tomer Heber
 */
public interface ForumTreeEvent {
	
	/**	 	 	 
	 * @param handler The handler for the forum tree GUI.
	 */
	public void respondToEvent(ForumTreeHandler handler);

}
