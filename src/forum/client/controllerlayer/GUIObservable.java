/**
 * 
 */
package forum.client.controllerlayer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import forum.client.ui.events.GUIEvent;
import forum.client.ui.events.GUIHandler;
import forum.client.ui.events.GUIEvent.EventType;

/**
 * @author sepetnit
 *
 */
public class GUIObservable extends Observable {
	private Collection<GUIObserver> userObservers;
	private Collection<GUIObserver> subjectObservers;
	private Collection<GUIObserver> threadsObservers;
	private Collection<GUIObserver> messagesTreeObservers;
	private Collection<GUIObserver> searchObservers;

	public GUIObservable() {
		this.userObservers = new Vector<GUIObserver>();
		this.subjectObservers = new Vector<GUIObserver>();
		this.threadsObservers = new Vector<GUIObserver>();
		this.messagesTreeObservers = new Vector<GUIObserver>();
		this.searchObservers = new Vector<GUIObserver>();
	}

	public void addObserver(GUIObserver toAdd, EventType event) {
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

		case SEARCH_UPDATED:
			synchronized (searchObservers) {
				searchObservers.add(toAdd);
			}
		}
	}

	public synchronized void deleteObserver(GUIHandler handler) {
		GUIObserver toDelete = null;
		EventType tDeleteFrom = EventType.MESSAGES_UPDATED;

		for (GUIObserver tCurrentObserver : messagesTreeObservers)
			if (tCurrentObserver.getHandler() == handler) {
				toDelete = tCurrentObserver;
				break;
			}
		if (toDelete == null)
			for (GUIObserver tCurrentObserver : subjectObservers)
				if (tCurrentObserver.getHandler() == handler) {
					toDelete = tCurrentObserver;
					tDeleteFrom  = EventType.SUBJECTS_UPDATED;
					break;
				}
		if (toDelete == null)

			for (GUIObserver tCurrentObserver : threadsObservers)
				if (tCurrentObserver.getHandler() == handler) {
					toDelete = tCurrentObserver;
					tDeleteFrom  = EventType.THREADS_UPDATED;
					break;
				}
		if (toDelete == null)

			for (GUIObserver tCurrentObserver : userObservers)
				if (tCurrentObserver.getHandler() == handler) {
					toDelete = tCurrentObserver;
					tDeleteFrom  = EventType.USER_CHANGED;
					break;
				}
		if (toDelete == null)

			for (GUIObserver tCurrentObserver : searchObservers)
				if (tCurrentObserver.getHandler() == handler) {
					toDelete = tCurrentObserver;
					tDeleteFrom  = EventType.SEARCH_UPDATED;
					break;
				}


		if (toDelete != null){
			switch (tDeleteFrom) {
			case MESSAGES_UPDATED: {
				messagesTreeObservers.remove(toDelete);
				break;
			}
			case SUBJECTS_UPDATED: {
				subjectObservers.remove(toDelete);
				break;
			}
			case THREADS_UPDATED: {
				threadsObservers.remove(toDelete);
				break;
			}
			case USER_CHANGED: {
				userObservers.remove(toDelete);
				break;
			}
			case SEARCH_UPDATED: {
				searchObservers.remove(toDelete);
				break;
			}
			
			}
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
			case SEARCH_UPDATED:
				toUpdate = searchObservers;
				break;
			default: {
				this.clearChanged();
				return;
			}
			}
			Collection<GUIObserver> tObserversToUpdate = new Vector<GUIObserver>();
			tObserversToUpdate.addAll(toUpdate);
			synchronized (tObserversToUpdate) {
				Iterator<GUIObserver> tObserversIter = tObserversToUpdate.iterator();
				while (tObserversIter.hasNext()) {
					tObserversIter.next().update(this, event);
				}
			}
			this.clearChanged();
		}
	}
}