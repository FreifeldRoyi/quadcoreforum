package forum.client.ui.events;

/**
 * @author Tomer Heber
 *
 */
public interface GUIHandler {
	
	/**
	 * Refreshes the forum. Meaning it retrieves from the controller the forum data.<br>
	 * And uses the returned data to update the JTree object accordingly.<br>
	 * 
	 * This method should be called by the controller as well.
	 * 
	 * @param encodedView The encoding of the forum view.
	 */
	public void refreshForum(String encodedView);
	
	/**
	 * An error message that appears in case of an error (e.g. an operation failed).
	 * This function is called by the controller layer.
	 * 
	 * @param errorMessage The error message to appear.
	 */
	public void notifyError(String errorMessage);

	
}
