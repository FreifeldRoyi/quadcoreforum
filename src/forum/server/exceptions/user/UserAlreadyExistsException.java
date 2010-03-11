/**
 * 
 */
package forum.server.exceptions.user;

/**
 * @author sepetnit
 *
 */
public class UserAlreadyExistsException extends Exception {

	private static final long serialVersionUID = -4533139714961245124L;

	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
