package forum.server.domainlayer.pipe;

public interface domainDataHandler 
{
	
	/**
	 * 
	 */
	public void initialize();
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param lastName
	 * @param firstName
	 * @param email
	 */
	public void registerToForum(String username, String password, String lastName, String firstName,
			String email);
	
	/**
	 * 
	 * @param name
	 * @param description
	 */
	public void addNewSubject(String name, String description);
	
	/**
	 * 
	 * @param fatherName
	 * @param name
	 * @param description
	 */
	public void addNewSubSubject(long fatherID, String name, String description);
	
	/**
	 * 
	 * @param subjectName
	 * @param userName
	 * @param title
	 * @param content
	 */
	public void addNewMessage(long subjectID, String userName, String title, String content);
	
	/**
	 * 
	 * @param fatherID
	 * @param userName
	 * @param title
	 * @param content
	 */
	public void replyToMessage(long fatherID, String userName, String title, String content);

	/**
	 * 
	 * @param username
	 * @param password
	 */
	public void login(String username, String password);
	
	public void logout(String username);
	
	public String getContent();
}