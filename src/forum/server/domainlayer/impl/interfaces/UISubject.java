package forum.server.domainlayer.impl.interfaces;

/**
 * This interface is used to present the data of a ForumSubject object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Subject state.
 */
public interface UISubject {
	/**
	 * @return
	 * 		The unique id of the subject
	 */
	public long getId();

	/**
	 * @return
	 * 		The name of the subject
	 */
	public String getName();
}
