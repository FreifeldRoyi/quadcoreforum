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
	 * @return member's day of birth
	 */
	public String getDateOfBirth();

	/**
	 * @return member's first name
	 */
	public String getFirstName();

	/**
	 * @return member's gender
	 */
	public String getGender();

	/**
	 * @return member's last name
	 */
	public String getLastName();

	/**
	 * @return member's residence
	 */
	public String getResidence();

	/**
	 * @return member's username
	 */
	public String getUsername();




}
