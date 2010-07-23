/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.message.exceptions;

import java.io.Serializable;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class ThreadNotFoundException extends Exception implements Serializable {

	private static final long serialVersionUID = -6083962457516364040L;

	private long threadID;
	
	public ThreadNotFoundException() { }
	
	public ThreadNotFoundException(long threadID) {
		super("A thread with the id " + threadID + " wasn't found!");
		this.threadID = threadID;
	}

	/**
	 * @return the threadID
	 */
	public long getThreadID() {
		return threadID;
	}

	/**
	 * @param threadID the threadID to set
	 */
	public void setThreadID(long threadID) {
		this.threadID = threadID;
	}
}
