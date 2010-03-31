/**
 * 
 */
package forum.server.persistentlayer.pipe.message.exceptions;

/**
 * @author sepetnit
 *
 */
public class ThreadNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9183868442639991790L;

	public ThreadNotFoundException(long threadID) {
		super("A thread with the thread id " + threadID + " wasn't found!");
	}
}
