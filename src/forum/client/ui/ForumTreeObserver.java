package forum.client.ui;

import java.util.Observable;
import java.util.Observer;

import forum.client.ui.events.ForumTreeEvent;
import forum.client.ui.events.GUIEvent;

/**
 * @author Tomer Heber
 *
 */
public class ForumTreeObserver implements Observer {

	private GUIHandler handler;

	public ForumTreeObserver(GUIHandler handler) {
		this.handler = handler;
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * @pre: o is instance of GUIEvent
	 */
	public void update(Observable obs, Object o) {
		if (o != null && (((GUIEvent)o).getEventType() == EventType.TREE_UPDATED))
			((ForumTreeEvent)o).respondToEvent(handler);
	}
}