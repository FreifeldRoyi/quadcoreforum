/**
 * 
 */
package forum.server.updatedpersistentlayer.pipe.message.exceptions;

import java.io.Serializable;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class SubjectNotFoundException extends Exception implements Serializable {
	
	private static final long serialVersionUID = 2750422479190221642L;

	private long id;
	
	public SubjectNotFoundException(long subjectID) {
		super("A subject with the subject id " + subjectID + " wasn't found!");
		this.id = subjectID;
	}
	
	public long getID() {
		return id;
	}
}
