/* This entire class will have the functionality of thread, subject and message adding.
 * it will send the correct input to the public methods of each and every object.
 * example: message handling will be synchronized in some point (in thread) but here it will search for
 * 			the correct subject in the forum, and send the message to the subject, while the message
 * 			will be added in ForumSubject. there the add message method will find the correct thread 
 * 			and in the thread, to the correct part of the tree.
 * 			this functionality can be changed in time
*/
package forum.server.domainlayer.interfaces;

import java.io.IOException;
import java.util.*;

import javax.xml.bind.JAXBException;
import forum.server.exceptions.message.*;
import forum.server.exceptions.subject.*;
import forum.server.exceptions.user.*;

public interface Forum 
{

	/* Methods */
	
	/**
	 * 
	 * Adds the given ForumSubject to the forum, at the top level
	 * 
	 * @param fs
	 * 		The subject which is requested to be added to the forum top level
	 * 
	 * @throws JAXBException
	 * 		In case a JAXB conversion error was occurred
	 * @throws IOException
	 * 		In case a database error was occurred
	 * 
	 * @throws SubjectAlreadyExistsException
	 * 		In case a subject with the given name has already exist in the top level
	 */
	public void addForumSubject(ForumSubject fs) throws 
	JAXBException, IOException,SubjectAlreadyExistsException;

	/**
	 * Registers the given user to the forum
	 * 
	 * @param user
	 * 		The user which should be registered
	 * 
	 * @throws UserAlreadyExistsException
	 * 		If the given user is already registered to the forukm
	 * @throws JAXBException
	 * 		In case a JAXB conversion error was occurred
	 * @throws IOException
	 * 		In case a database error was occurred
	 */
	public void registerUser (RegisteredUser user) throws UserAlreadyExistsException, JAXBException, IOException;

	/**
	 * 
	 * Performs a login of a user with the given username
	 * 
	 * @param username
	 * 		The username of the user
	 * @param password
	 * 		The password of the user
	 * @return
	 * 		A reference to a RegisteredUser domain object instance, with a username like the given
	 * 
	 * @throws AlreadyConnectedException
	 * 		If the user is already connected to the forum
	 * @throws NotRegisteredException
	 * 		If a user with the given username wasn't found - isn't registered to the forum
	 * @throws WrongPasswordException
	 * 		If the given password is wrong
	 */
	public RegisteredUser login(String username, String password) throws AlreadyConnectedException,
	NotRegisteredException, WrongPasswordException;

	/**
	 * 
	 * Logs out a user with the given username
	 * 
	 * @param username
	 * 		The username of the user who should be logged out
	 * 
	 * @throws NotConnectedException
	 * 		In case a user with the given username isn't connected to the forum
	 */
	public void logout(String username) throws NotConnectedException;
	
	/* Getters */

	/**
	 * 
	 * @return
	 * 		Returns a collection of all the users which are currently connected to the forum
	 */
	public Collection<RegisteredUser> getConnectedUsers(); // in case we'll want to display the users' names

	/**
	 * 
	 * @return
	 * 		The number of the users who are currently connected to the forum
	 */
	public int getNumOfConnectedUsers();

	/**
	 * 
	 * Returns the user whose username equals to the given one
	 * 
	 * @param username
	 * 		The required username
	 * @return
	 * 		A reference to the RegistereedUser domain object, whose username equals to the given one
	 * 
	 * @throws NotRegisteredException
	 * 		In case a user with the given username isn't registered to the forum
	 */
	public RegisteredUser getUserByUsername(String username) throws NotRegisteredException;

	/**
	 * 
	 * @return
	 * 		A collection with all the forum subjects
	 */
	public Collection<ForumSubject> getForumSubjects();

	/**
	 * 
	 * Finds a subject with the given id (performs a recursive search through the sub-subjects)
	 * 
	 * @param id
	 * 		The id of the subject which should be found
	 * @return
	 * 		A reference to a ForumSubject object whose id equals to the given one
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id wasn't found
	 */
	public ForumSubject getForumSubjectByID(long id) throws SubjectNotFoundException;

	/**
	 * Finds a message with the given id (performs a recursive search through the sub-subjects)
	 * 	
	 * @param msgID
	 * 		The id of the message which should be found
	 * @return
	 * 		A reference to a ForumMessage object whose id equals to the given one
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id wasn't found
	 */
	public ForumMessage getMessageByID(long msgID) throws MessageNotFoundException;
		
	/**
	 * 
	 * Constructs a map with all the string representations of a given subject's threads
	 * 
	 * @param rootSubjectID
	 * 		The if of the subject whose threads should be represnted
	 * @return
	 * 		A map with all the string representations of a given subject's threads 
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id wasn't found
	 */
	public Map<Long, String> getForumThreadsBySubjectID(long rootSubjectID) throws SubjectNotFoundException;
	
	
	/**
	 * Finds and updates a message with the given id, with the new title and content
	 * 
	 * @param messageID
	 * 		The id of the message which should be updated
	 * @param newTitle
	 * 		The new title of the message
	 * @param newContent
	 * 		The new content of the message
	 * 
	 * @throws MessageNotFoundException
	 * 		In case the message wasn't found in the database 
	 * @throws IOException 
	 * 		In case a database error occurred
	 * @throws JAXBException 
	 * 		In case a database error occurred
	 */
	public void updateAMessage(long messageID, String newTitle, String newContent) throws JAXBException, IOException, MessageNotFoundException;
	
}

/**
 * TODO write proper JavaDoc for Forum
 * TODO add methods
 */
