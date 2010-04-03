/**
 * 
 */

package forum.server.persistentlayer.pipe;


import java.util.*;

import forum.server.domainlayer.impl.message.ForumMessage;
import forum.server.domainlayer.impl.message.ForumSubject;
import forum.server.domainlayer.impl.message.ForumThread;
import forum.server.domainlayer.impl.user.Member;
import forum.server.domainlayer.impl.user.Permission;
import forum.server.domainlayer.impl.user.User;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;

import forum.server.persistentlayer.pipe.user.exceptions.*;
import forum.server.persistentlayer.pipe.message.exceptions.*;


/**
 * @author Vitali Sepetnitsky
 *
 */
public interface PersistenceDataHandler
{
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
	 * 
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
//	public void registerToForum(String username, String password, String lastName, String firstName,
//			String email) throws DatabaseUpdateException;


	/**
	 * This method updates the database with a new subject which is added to the top level
     *
     * @param subjectID
     * 		A unique id of the new subject 
	 * @param subjectName
	 * 		The name of the new subject
	 * @param subjectDescription
	 * 		The description of the new subject
	 * 		
	 * @throws SubjectAlreadyExistsException
	 * 		If there already exists a subject with the given name in the top level of the forum
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	//public void addNewSubject(long subjectID, String subjectName, String subjectDescription) throws SubjectAlreadyExistsException,
	//DatabaseUpdateException;

	
	/**
	 * This method updates the database with a new sub-subject of a given ancestor subject

	 * @param father
	 * 		The name of the ancestor subject
	 * @param subjectName
	 * 		The name of the new subject
	 * @param subjectDescription
	 * 		The description of the new subject
	 * 		
	 * @throws SubjectAlreadyExistsException
	 * 		If the root subject already has a sub-subject with the given name
	 * @throws SubjectNotFoundException
	 * 		In case the new subject should be a sub-subject of a non existing one
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 * 
	 */
	//public void addNewSubSubject(long fatherID, long subjectID, String subjectName, String subjectDescription)
	//throws SubjectAlreadyExistsException, SubjectNotFoundException, DatabaseUpdateException;	
	
	/**
	 * Adds a new message, by openning a new messages thread within a given subject, and updates the database with the given message
	 * 
	 * @param messageID
	 * 		A unique id of the new message
	 * @param subjectName
	 * 		The given subject
	 * @param authorUsername
	 * 		The username of the message author
	 * @param msgTitle
	 * 		The title of the message
	 * @throws NotRegisteredException
	 * 		In case the author isn't one of the registered users
	 * @throws NotConnectedException 
	 * 		In case the author is a registered forum user, but he isn't connected now and therefore can't
	 * 		post messages in the forum
	 * @throws SubjectNotFoundException
	 * 		In case the given subject name is invalid
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database update
	 */
	//public void addNewMessage(long messageID, long subjectID, String authorUsername, String msgTitle) 
	//throws DatabaseUpdateException;

	/**
	 * Adds a reply to an existing message which is identificated by the given id
	 * 
	 * @param fatherID
	 * 		The created message will be a reply to a message with this id
	 * @param messageID
	 * 		A unique ID of the created message
	 * @param authorUsername
	 * 		The reply author
	 * @param replyTitle
	 * 		The title of the reply message
	 * @param replyContent
	 * 		The content of the reply message
	 * 
	 * @throws MessageNotFoundException
	 * 		In case the given message id which is supposed to be the reply father, wasn't found
	 * @throws NotRegisteredException
	 * 		In case the author isn't one of the registered users
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database update
	 */
	//public void replyToMessage(long fatherID, long messageID, String authorUsername, String replyTitle, String replyContent) 
	//throws MessageNotFoundException, DatabaseUpdateException;

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
	 * @throws DatabaseRetrievalException 
	 * 
	 * @throws MessageNotFoundException
	 * 		In case the message which should be updated wasn't found
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database update
	 */
	
	
	
	
	
	
	
	
	
	
	
	
	

	// User related methods
	
	public Collection<Member> getAllMembers() throws DatabaseRetrievalException;

	public User getMemberByID(final long id) throws NotRegisteredException, DatabaseRetrievalException;
	
	public Member getMemberByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException;

	public Member getMemberByEmail(final String email) throws NotRegisteredException, DatabaseRetrievalException;

	public void addNewMember(final long id, final String username, final String password,
			final String lastName, final String firstName, final String email, 
			final Collection<Permission> permissions) throws DatabaseUpdateException;	

	// Subject related methods

	public Collection<ForumSubject> getTopLevelSubjects() throws DatabaseRetrievalException;

	public ForumSubject getSubjectByID(final long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException;

	public void addNewSubject(final long subjectID, final String name, final String description, boolean isTopLevel) throws DatabaseUpdateException;

	public void updateSubject(final long id, final Collection<Long> subSubjects,
			final Collection<Long> threads) throws SubjectNotFoundException, DatabaseUpdateException;

	// Thread related methods

	public ForumThread getThreadByID(final long threadID) throws ThreadNotFoundException, DatabaseRetrievalException;

	public void openNewThread(final long threadID, final String topic, final long rootID) throws DatabaseUpdateException;

	public Collection<Long> deleteAThread(final long threadID) throws ThreadNotFoundException,
	DatabaseUpdateException;
	
	// Message related methods	

	public ForumMessage getMessageByID(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException;

	public void addNewMessage(final long messageID, final long userID, final String title,
			final String content) throws DatabaseUpdateException;
	
	public void updateMessage(final long messageID, final String newTitle, final String newContent) throws MessageNotFoundException, 
	DatabaseUpdateException;

	public Collection<Long> deleteAMessage(final long messageID) throws MessageNotFoundException, DatabaseUpdateException;

}