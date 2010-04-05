/**
 * 
 */
package forum.server.persistentlayer;

/**
 * @author sepetnit
 *
 */
public class DatabaseRetrievalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 349560411412455622L;
	
	public DatabaseRetrievalException() {
		super("An error occured while retrieving from database");
	}
}