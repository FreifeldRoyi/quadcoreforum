package forum.client.controllerlayer;

/**
 * This Exception is thrown in cases where a bad command has been received from the user.
 * 
 * @author Tomer Heber
 */
public class BadCommandException extends Exception {
	
	private static final long serialVersionUID = -7397323378113203424L;

	public BadCommandException(String msg) {
		super(msg);
	}

}
