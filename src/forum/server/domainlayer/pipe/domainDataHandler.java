package forum.server.domainlayer.pipe;

import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.exceptions.user.AlreadyConnectedException;
import forum.server.exceptions.user.NotConnectedException;
import forum.server.exceptions.user.NotRegisteredException;
import forum.server.exceptions.user.WrongPasswordException;

public interface DomainDataHandler 
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
	public String registerToForum(String username, String password, String lastName, String firstName,
			String email);
	
	/**
	 * 
	 * @param name
	 * @param description
	 */
	public String addNewSubject(String name, String description);
	
	/**
	 * 
	 * @param fatherName
	 * @param name
	 * @param description
	 */
	public String addNewSubSubject(long fatherID, String name, String description);
	
	/**
	 * 
	 * @param subjectName
	 * @param userName
	 * @param title
	 * @param content
	 */
	public String addNewMessage(long subjectID, String userName, String title, String content);
	
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
	 * @throws WrongPasswordException 
	 * @throws NotRegisteredException 
	 * @throws AlreadyConnectedException 
	 */
	public String login(String username, String password) throws AlreadyConnectedException, NotRegisteredException, WrongPasswordException;
	
	public String logout(String username) throws NotConnectedException;
	
	public String getContent();
}