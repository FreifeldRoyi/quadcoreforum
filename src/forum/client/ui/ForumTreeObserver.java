package forum.client.ui;

import java.util.Observable;
import java.util.Observer;

import forum.client.ui.events.ForumTreeEvent;

/**
 * @author Tomer Heber
 *
 */
public class ForumTreeObserver implements Observer {
	
	private ForumTreeHandler m_handler;

	public ForumTreeObserver(ForumTreeHandler handler) {
		m_handler = handler;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * This implementation is not OO/Modular. instance of is usually bad programming.
	 * If you add more code to update then consider changing this. 
	 */
	@Override
	public void update(Observable obs, Object o) {
		if (o != null && o instanceof ForumTreeEvent) {
			((ForumTreeEvent)o).respondToEvent(m_handler);
		}
	}

}
