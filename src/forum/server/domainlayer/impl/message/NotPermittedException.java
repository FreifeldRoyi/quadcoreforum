/**
 * 
 */
package forum.server.domainlayer.impl.message;

import forum.server.domainlayer.impl.user.Permission;

/**
 * @author sepetnit
 *
 */
public class NotPermittedException extends Exception {
	
	private static final long serialVersionUID = -1239416816486707410L;

	public NotPermittedException(long userID, Permission permission) {
		super("A user with id " + userID + " doesn't have the permission " + permission);
	}
}
