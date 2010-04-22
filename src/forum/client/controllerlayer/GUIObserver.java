/**
 * 
 */
package forum.client.controllerlayer;

import java.util.Observable;
import java.util.Observer;
import forum.client.ui.events.GUIEvent;
import forum.client.ui.events.GUIHandler;

/**
 * @author sepetnit
 *
 */
public class GUIObserver implements Observer {
	private GUIHandler eventHandler;

	public GUIObserver(GUIHandler handler) {
		this.eventHandler = handler;
	}

	//	public void update



	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * This implementation is not OO/Modular. instance of is usually bad programming.
	 * If you add more code to update then consider changing this. 
	 */
	@Override
	public void update(Observable obs, Object o) {
		if (o != null && (o instanceof GUIEvent))
			((GUIEvent)o).respondToEvent(eventHandler);
	}
}