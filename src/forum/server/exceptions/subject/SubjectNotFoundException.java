/**
 * 
 */
package forum.server.exceptions.subject;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class SubjectNotFoundException extends Exception {

	private static final long serialVersionUID = -8637231750269406707L;

	public SubjectNotFoundException(String subjectName) {
		super("A subject with the subject name " + subjectName + " was not found!");
	}
}
