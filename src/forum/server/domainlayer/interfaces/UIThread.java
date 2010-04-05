package forum.server.domainlayer.interfaces;

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
	public long getID();

	/**
	 * @return
	 * 		The topic of the thread
	 */
	public String getTopic();

	/**
	 * 
	 * @return
	 * 		The number of responses to the threads messages, this is the number of messages
	 * 		posted in the thread - 1 (its root message)
	 */
	public int getNumOfResponese();

	/**
	 * 
	 * @return
	 * 		The number of views of this thread
	 */
	public int getNumOfViews();
}
