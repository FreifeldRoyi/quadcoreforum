/**
 * 
 */
package forum.server.exceptions.user;

/**
 * @author sepetnit
 *
 */
public class NotRegisteredException extends Exception {

	private static final long serialVersionUID = 3459306523924172336L;

	public NotRegisteredException(String username) {
		super("A user with a username " + username + " is not registered!");
	}
}
