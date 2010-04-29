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

	public GUIHandler getHandler() {
		return this.eventHandler;
	}
	
	public boolean equals(Object obj) {
		System.out.println("use equals");
		if (obj != null && (obj instanceof GUIObserver) &&
				((GUIObserver)obj).eventHandler == this.eventHandler)
				return true;
		return false;
	}
	
	//	public void update

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 * 
	 * This implementation is not OO/Modular. instance of is usually bad programming.
	 * If you add more code to update then consider changing this. 
	 */
	public void update(Observable obs, Object o) {
		if (o != null && (o instanceof GUIEvent))
			((GUIEvent)o).respondToEvent(eventHandler);
	}
}