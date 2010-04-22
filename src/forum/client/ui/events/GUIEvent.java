package forum.client.ui.events;

/**
 * Classes that implement this interface are events related to the Forum GUI.
 */ 

public abstract class GUIEvent {

	public static enum EventType {
		USER_CHANGED, THREADS_UPDATED, SUBJECTS_UPDATED, MESSAGES_UPDATED;
	}

	protected EventType eventType;


	public EventType getEventType() {
		return this.eventType;
	}
	
	public GUIEvent(EventType type) {
		this.eventType = type;
	}
	
	/**	 	 	 
	 * @param handler The handler for the forum GUI.
	 */
	 
	public abstract void respondToEvent(GUIHandler handler);
}

/*
public interface GUIEvent {
	
	public static enum EventHandlingType {
		USER_CHANGED, TREE_UPDATED, SUBJECT_UPDATED;
	}
	
	/**	 	 	 
	 * @param handler The handler for the forum GUI.
	 
	public abstract void respondToEvent(GUIHandler handler);
}*/




