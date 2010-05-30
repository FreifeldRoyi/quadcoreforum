package forum.swingclient.ui.events;

/**
 * @author Tomer Heber
 *
 */
public class ForumGUIErrorEvent extends GUIEvent {

	/**
	 * The error message to show the user in the GUI.
	 */
	private String errorMessage;
	
	public ForumGUIErrorEvent(String errorMessage, EventType type) {
		super(type);
		this.errorMessage = errorMessage;
	}

	/* (non-Javadoc)
	 * @see forumtree.contol.ForumTreeEvent#respondToEvent(forumtree.ForumTree)
	 */
	@Override
	public void respondToEvent(GUIHandler handler) {
		handler.notifyError(this.errorMessage);
	}

}
