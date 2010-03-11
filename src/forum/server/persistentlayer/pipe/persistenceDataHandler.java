/**
 * 
 */

package forum.server.persistentlayer.pipe;


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
	 * 
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem with the database xml file
	 * @throws AlreadyConnectedException
	 * 		In case a user with the given username is already connected to the forum
	 * @throws NotRegisteredException
	 * 		In case the user with the given username isn't registered to the forum
	 * @throws
	 * 		In case the given password is wrong
	 */
	public void login(String username, String password) throws JAXBException, IOException, AlreadyConnectedException,
	NotRegisteredException, WrongPasswordException;

	/**
	 * Performs a logout of a user with the given username, by updating its status in the database as disconnected
	 * 
	 * @param username
	 * 		The username of the user who should be sign out the forum
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem with the database xml file
	 * @throws NotConnectedException
	 * 		In case the given username isn't connected
	 */
	public void logoutUser(String username) throws JAXBException, IOException, NotConnectedException;

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
			String email) throws JAXBException, IOException, UserAlreadyExistsException;


	/**
	 * This method updates the database with a new subject which is added to the top level
     *
	 * @param subjectName
	 * 		The name of the new subject
	 * @param subjectDescription
	 * 		The description of the new subject
	 * 		
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem with the database xml file
	 * @throws SubjectAlreadyExistsException
	 * 		If there already exists a subject with the given name in the forum
	 */
	public void addNewSubject(String subjectName, String subjectDescription) throws JAXBException, IOException, 
	SubjectAlreadyExistsException;

	
	/**
	 * This method updates the database with a new sub-subject of a given ancestor subject

	 * @param father
	 * 		The name of the ancestor subject
	 * @param subjectName
	 * 		The name of the new subject
	 * @param subjectDescription
	 * 		The description of the new subject
	 * 		
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem with the database xml file
	 * @throws SubjectAlreadyExistsException
	 * 		If there already exists a subject with the given name in the forum
	 * @throws SubjectNotFoundException
	 * 		In case the new subject should be a sub-subject of a non existing one
	 */
	public void addNewSubSubject(String father, String subjectName, String subjectDescription) throws JAXBException, IOException, 
	SubjectAlreadyExistsException, SubjectNotFoundException;	
	
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
	 * 		if any unexpected errors occur while marshaling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem during the database updating
	 * @throws NotRegisteredException
	 * 		In case the author isn't one of the registered users
	 * @throws NotConnectedException 
	 * 		In case the author is a registered forum user, but he isn't connected now and therefore can't
	 * 		post messages in the forum
	 * @throws SubjectNotFoundException
	 * 		In case the given subject name is invalid
	 */
	public void addNewMessage(String subjectName, String authorUsername, String msgTitle, String msgContent) 
	throws JAXBException, IOException,  NotRegisteredException, NotConnectedException, SubjectNotFoundException;

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
	 * @throws NotRegisteredException
	 * 		In case the author isn't one of the registered users
	 * @throws NotConnectedException 
	 * 		In case the author is a registered forum user, but he isn't connected now and therefore can't
	 * 		post messages in the forum
	 */
	public void replyToMessage(int fatherID, String authorUsername, String replyTitle, String replyContent) 
	throws JAXBException, IOException, MessageNotFoundException, NotRegisteredException, NotConnectedException;

	/**
	 * Updates the title and the content of a specific message (the message is identified by the given id) 
	 * to be the given one
	 * 
	 * @param messageID
	 * 		The ID of the message
	 * @param newTitle
	 * 		The updated title of the message
	 * @param newContent
	 * 		The updated content of the message
	 * 
	 * @throws JAXBException
	 * 		In case there is a problem during the validation against the database schema or
	 * 		if any unexpected errors occur while marshalling / unmarshalling
	 * @throws IOException
	 * 		In case there is a problem during the database updating
	 * @throws MessageNotFoundException
	 * 		In case the message which should be updated wasn't found
	 */
	public void updateMessage(int messageID, String newTitle, String newContent) throws JAXBException,
	IOException, MessageNotFoundException;
}