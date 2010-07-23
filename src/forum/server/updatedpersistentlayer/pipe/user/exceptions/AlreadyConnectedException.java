/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.user.exceptions;

import java.io.Serializable;

/**
 * @author sepetnit
 *
 */
public class AlreadyConnectedException extends Exception implements Serializable {

	private static final long serialVersionUID = -3193246183594257892L;

	private String username;

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public AlreadyConnectedException() { }
	
	public AlreadyConnectedException(String username) {
		super("A user with a username " + username + " is already connected!");
		this.username = username;
	}
	
}
