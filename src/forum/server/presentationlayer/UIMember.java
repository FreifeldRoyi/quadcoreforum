package forum.server.presentationlayer;

/**
 * This interface is used to present the data of a ForumUser object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Member state.
 */
public interface UIMember extends UIUser
{

	/**
	 * @return
	 * 		The username of the member
	 */
	public String getUsername();

	/**
	 * @return
	 * 		The first name of the member
	 */
	public String getFirstName();

	/**
	 * @return
	 * 		The last name of the member
	 */
	public String getLastName();

	/**
	 * 
	 * @return
	 * 		The e-mail address of the member
	 */
	public String getEmail();	
}