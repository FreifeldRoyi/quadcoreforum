package forum.server.domainlayer.pipe;

public interface DomainDataHandler 
{		
	/**
	 * 
	 * Registers a new user, with the given parameters, to the forum
	 * 
	 * @param username
	 * 		The username of the new user
	 * @param password
	 * 		The password of the new user
	 * @param lastName
	 * 		The last name of the new user
	 * @param firstName
	 * 		The first name of the new user
	 * @param email
	 * 		The e-mail of the new user
	 * 
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful registration
	 * 			An error message which describes the failure, in case it occurred
	 */
	public String registerToForum(String username, String password, String lastName, String firstName,
			String email);
	
	/**
	 * Adds a new subject with the given parameters to the forum - in the top level
 	 * 
	 * @param name
	 * 		The name of the new subject
	 * @param description
	 * 		The description of the new subject
	 * 
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful subject adding
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public String addNewSubject(String name, String description);
	
	/* TODO: Consider fusion of addNewSubject and addNewSubSubject */
	
	/**
	 * 
	 * Adds a new sub-subject to a subject whose id is the given one
	 * 
	 * @param fatherID
	 * 		The id of the root subject (to which a new sub-subject will be added)
	 * @param name
	 * 		The name of the new sub-subject
	 * @param description
	 * 		The description of the new sub-subject
	 *
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful subject adding
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public String addNewSubSubject(long fatherID, String name, String description);
	
	/**
	 * Adds a new message to the given subject - opens new threads whose root message will be the created one
	 * 
	 * @param subjectId
	 * 		The id of the subject to which the new message should be added
	 * @param userName
	 * 		The username of the message author
	 * @param title	
	 * 		The title of the new message
	 * @param content
	 * 		The content of the new message 
	 * 
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful message adding
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public String addNewMessage(long subjectID, String userName, String title, String content);
	
	/**
	 *
	 * Adds a new message as a reply to the given one - doesn't open a new thread
	 * 
	 * @param fatherID
	 * 		A message to which the reply should be added 
	 * @param userName
	 * 		The username of the reply author
	 * @param title
	 * 		The title of the new reply
	 * @param content
	 * 		The content of the new reply
	 * 
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful reply adding
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public void replyToMessage(long fatherID, String userName, String title, String content);

	/**
	 * 
	 * logs-in a user with the given parameters
	 * 
	 * @param username
	 * 		The username of the required user
	 * @param password
	 * 		The password of the required user
	 * 
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful login
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public String login(String username, String password);
	
	/**
	 * 
	 * Logs out a user whose username is the given one
	 * 
	 * @param username
	 * 		The user name of the user who should be logged out
	 *
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful logout
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public String logout(String username);	
}