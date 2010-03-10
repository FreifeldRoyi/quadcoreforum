/**
 * 
 */

package forum.server.domainlayer.interfaces;


import java.io.IOException;
import javax.xml.bind.JAXBException;

import forum.server.exceptions.message.*;
import forum.server.exceptions.user.*;
import forum.server.exceptions.subject.*;


/**
 * @author Vitali Sepetnitsky
 *
 */
public interface persistenceDataHandler
{
	
	/**
	 * Performs a login of a given user to the forum, according to a given username and password
	 * 
	 * @param username
	 * 		The username of the user who tries to login the system
	 * @param password
	 * 		The password of the user who tries to login the system
	 * @throws AlreadyConnectedException
	 * 		In case a user with the given username is already connected to the forum
	 * @throws NotRegisteredException
	 * 		In case the user with the given username isn't registered to the forum
	 * @throws
	 * 		In case the given password is wrong
	 */
	public void login(String username, String password) throws AlreadyConnectedException,
		NotRegisteredException, WrongPasswordException;
	
	/**
	 * Performs a logout of a user with the given username, by updating its status in the database as disconnected
	 * 
	 * @param username
	 * 		The username of the user who should be sign out the forum
	 * @throws NotConnectedException
	 * 		In case the given username isn't connected
	 */
	public void logoutUser(String username) throws NotConnectedException;

	/**
	 * This method updates the database with a new registered user
	 * 
	 * @param username
	 * 		The given username
	 * @param password
	 * 		The given password
	 * @param lastName
	 * 		The given lastName
	 * @param firstName
	 * 		The given firstName
	 * @param email
	 * 		The given e-mail
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem with the database xml file
	 */
	public void registerToForum(String username, String password, String lastName, String firstName,
			String email) throws JAXBException, IOException;
	
	/**
	 * This method updates the database with a new subject
	 * 
	 * @param subjectName
	 * 		The name of the new subject
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem with the database xml file
	 * @throws SubjectAlreadyExistsException
	 * 		In case father isn't null, the exception will be thrown if a subject with the given name
	 * 		already exists in the father subject, otherwise, the exception will be thrown if there exists
	 * 		a subject with the given name in the top level of the forum
	 */
	public void addNewSubject(String father, String subjectName) throws JAXBException, IOException, SubjectAlreadyExistsException;
	
	/**
	 * Adds a new message, by openning a new messages thread within a given subject, and updates the database with the given message
	 * 
	 * @param subjectName
	 * 		The given subject
	 * @param authorUsername
	 * 		The username of the message author
	 * @param msgTitle
	 * 		The title of the message
	 * @param msgContent
	 * 		The content of the message
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem during the database updating
	 * @throws SubjectNotFoundException
	 * 		In case the given subject name is invalid
	 */
	public void addNewMessage(String subjectName, String authorUsername, String msgTitle, String msgContent) 
		throws JAXBException, IOException, SubjectNotFoundException;
	
	/**
	 * Adds a reply to an existing message which is identificated by the given id
	 * 
	 * @param fatherID
	 * 		The created message will be a reply to a message with this id
	 * @param authorUsername
	 * 		The reply author
	 * @param replyTitle
	 * 		The title of the reply message
	 * @param replyContent
	 * 		The content of the reply message
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem during the database updating
	 * @throws MessageNotFoundException
	 * 		In case the given message id which is supposed to be the reply father, wasn't found
	 */
	public void replyToMessage(int fatherID, String authorUsername, String replyTitle, String replyContent) 
		throws JAXBException, IOException, MessageNotFoundException;
	
	/**
	 * Updates the title of a specific message (identificated by the given id) to be the given one
	 * 
	 * @param messageID
	 * 		The ID of the message
	 * @param newTitle
	 * 		The updated title of the message
	 */
	public void updateMessageTitle(int messageID, String newTitle);
	
	/**
	 * Updates the content of a specific message (identificated by the given id) to be the given one
	 *
	 * @param messageID
	 * 		The ID of the messsage
	 * @param newContent
	 * 		The updated content of the message
	 */
	public void updateMessageContent(int messageID, String newContent);
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
