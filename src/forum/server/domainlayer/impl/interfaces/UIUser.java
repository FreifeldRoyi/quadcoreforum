package forum.server.domainlayer.impl.interfaces;

import forum.server.domainlayer.impl.user.Permission;

/**
 * This interface is used to present the data of a ForumUser object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the User state.
 */
public interface UIUser {
	
	/**
	 * @return
	 * 		The unique id of the user
	 */
	public long getId();
	
	/**
	 * 
	 * @param permissionToCheck
	 * 		The permission which should be checked.
	 * 
	 * @return
	 * 		Whether the user is allowed to perform an operation specified by the given permission.
	 * 
	 */
	public boolean isAllowed(final Permission permissionToCheck);
}
