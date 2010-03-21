package forum.server.presentationlayer;

/**
 * This interface is used to present the data of a ForumThread object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Thread state.
 */
public interface UIThread {
	/**
	 * @return
			The id of the thread, this is the id of its root message
	 */
	public long getId();

	/**
	 * @return
	 * 		The title of the thread, this is the title of its root message 
	 */
	public String getTitle();

}
