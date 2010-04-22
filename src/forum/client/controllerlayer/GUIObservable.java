/**
 * 
 */
package forum.client.controllerlayer;

import java.util.Collection;
import java.util.Iterator;
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

	public synchronized void addObserver(GUIObserver toAdd, EventType event) {
		switch (event) {
		case USER_CHANGED:
			synchronized (userObservers) {
				userObservers.add(toAdd);
				break;					
			}
		case SUBJECTS_UPDATED:
			synchronized (subjectObservers) {
				subjectObservers.add(toAdd);
				break;
			}
		case THREADS_UPDATED:
			synchronized (threadsObservers) {
				threadsObservers.add(toAdd);
				break;
			}
		case MESSAGES_UPDATED:
			synchronized (messagesTreeObservers) {
				messagesTreeObservers.add(toAdd);
			}
		}
	}

	public synchronized void notifyObservers(GUIEvent event) {
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
			Collection<GUIObserver> tObserversToUpdate = new Vector<GUIObserver>();
			tObserversToUpdate.addAll(toUpdate);
			synchronized (toUpdate) {
				Iterator<GUIObserver> tObserversIter = tObserversToUpdate.iterator();
				while (tObserversIter.hasNext())
					tObserversIter.next().update(this, event);
			}
			this.clearChanged();
		}
	}
}