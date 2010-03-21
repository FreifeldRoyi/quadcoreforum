package forum.server.presentationlayer;

/**
 * This interface is used to present the data of a ForumThread object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Thread state.
 */
public interface UIThread {
	/**
	 * @return id of Thread
	 */
	public long getId();

	/**
	 * @return the thread's topic
	 */
	public String getTh();

}
