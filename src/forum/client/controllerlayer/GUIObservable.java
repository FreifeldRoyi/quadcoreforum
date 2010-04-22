/**
 * 
 */
package forum.client.controllerlayer;

import java.util.Collection;
import java.util.Observable;
import java.util.Vector;

import forum.client.ui.events.GUIEvent;
import forum.client.ui.events.GUIEvent.EventType;

/**
 * @author sepetnit
 *
 */
public class GUIObservable extends Observable {
	public Collection<GUIObserver> userObservers;
	public Collection<GUIObserver> subjectObservers;
	public Collection<GUIObserver> threadsObservers;
	public Collection<GUIObserver> messagesTreeObservers;

	public GUIObservable() {
		this.userObservers = new Vector<GUIObserver>();
		this.subjectObservers = new Vector<GUIObserver>();
		this.threadsObservers = new Vector<GUIObserver>();
		this.messagesTreeObservers = new Vector<GUIObserver>();
	}
	
	public void addObserver(GUIObserver toAdd, EventType event) {
		switch (event) {
			case USER_CHANGED:
				userObservers.add(toAdd);
				break;
			case SUBJECTS_UPDATED:
				subjectObservers.add(toAdd);
				break;
			case THREADS_UPDATED:
				threadsObservers.add(toAdd);
				break;
			case MESSAGES_UPDATED:
				messagesTreeObservers.add(toAdd);
				break;
		}
	}
	
	public void notifyObservers(GUIEvent event) {
		if (this.hasChanged()) {
			Collection<GUIObserver> toUpdate;
			switch (event.getEventType()) {
			case USER_CHANGED: 
				toUpdate = userObservers;
				break;
			case MESSAGES_UPDATED:
				toUpdate = messagesTreeObservers;
				break;
			case THREADS_UPDATED:
				toUpdate = threadsObservers;
				break;
			case SUBJECTS_UPDATED:
				toUpdate = subjectObservers;
				break;
			default: return;
			}
			for (GUIObserver tCurrentObserver : toUpdate)
				tCurrentObserver.update(this, event);
			this.clearChanged();
		}
	}
}