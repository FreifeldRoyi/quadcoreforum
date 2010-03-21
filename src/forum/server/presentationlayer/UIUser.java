package forum.server.presentationlayer;

/**
 * This interface is used to present the data of a ForumUser object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the User state.
 */
public interface UIUser {
	
	/**
	 * @return User id
	 */
	public long getId();
}
