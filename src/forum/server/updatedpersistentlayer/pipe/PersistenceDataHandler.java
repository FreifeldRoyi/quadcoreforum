/**
 * This interface is the main interface of the persistent layer, it contains all the methods which are used by the
 * upper layers in order to update the forum database
 */

package forum.server.updatedpersistentlayer.pipe;


import java.util.Collection;

import forum.server.domainlayer.message.ForumMessage;
import forum.server.domainlayer.message.ForumSubject;
import forum.server.domainlayer.message.ForumThread;
import forum.server.domainlayer.user.ForumMember;
import forum.server.domainlayer.user.ForumUser;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.ThreadNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotConnectedException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author Vitali Sepetnitsky
 *
 */
public interface PersistenceDataHandler {

	// User related methods

	public long getGuestsNumber() throws DatabaseRetrievalException;

	public long getNextFreeGuestID() throws DatabaseRetrievalException;

	public void removeGuest(final long guestID) throws DatabaseUpdateException;	
	
	/**
	 * 
	 * @return
	 * 		The first member id which is free in the database and isn't assigned to any member
	 * 
	 * @throws DatabaseRetrievalException 
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public long getFirstFreeMemberID() throws DatabaseRetrievalException;

	public Collection<String> getActiveMemberUserNames() throws DatabaseRetrievalException;

	public void addActiveMemberID(long memberIDToAdd) throws DatabaseUpdateException;	
		
	public void removeActiveMemberID(final long memberID) throws NotConnectedException, DatabaseUpdateException;
	
	
	/**
	 * 
	 * @return
	 * 		A collection of all the forum members
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public Collection<ForumMember> getAllMembers() throws DatabaseRetrievalException;

	/**
	 * Finds and returns a member whose id equals to the given one
	 * 
	 * The returned member is returned in a format which allows to retrieve his permissions only since this
	 * method is used to check whether the member with the given id, has some permissions
	 * 
	 * @param id
	 * 		The id of the member which should be returned
	 * 
	 * @return
	 * 		The found member in a format which allows to get his permissions only
	 * 
	 * @throws NotRegisteredException
	 * 		In case a member with the given id isn't registered in the forum (doesn't exist in the database)
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public ForumUser getUserByID(final long id) throws NotRegisteredException, DatabaseRetrievalException;

	/**
	 * Finds and returns a member whose user-name is equal to the given one
	 * 
	 * (The user-names of the forum members should be unique)
	 * 
	 * @param username
	 * 		The user-name of the required member
	 * 
	 * @return
	 * 		The found member
	 * 
	 * @throws NotRegisteredException
	 * 		In case a member with the given user-name isn't registered in the forum (doesn't exist in the database)
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public ForumMember getMemberByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException;

	/**
	 * Finds and returns a member whose e-mail is equal to the given one
	 * 
	 * (The e-mails of the forum members should be unique)
	 * 
	 * @param email
	 * 		The e-mail of the required member
	 * 
	 * @return
	 * 		The found member
	 * 
	 * @throws NotRegisteredException
	 * 		In case a member with the given e-mail isn't registered in the forum (doesn't exist in the database)
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public ForumMember getMemberByEmail(final String email) throws NotRegisteredException, DatabaseRetrievalException;

	/**
	 * This method updates the database with a new registered user
	 * 
	 * @param id
	 * 		The id of the new member
	 * @param username
	 * 		The user-name of the new member
	 * @param password
	 * 		The password of the new member
	 * @param lastName
	 * 		The lastName of the new member
	 * @param firstName
	 * 		The firstName of the new member
	 * @param email
	 * 		The e-mail of the new member
	 * @param permissions
	 * 		The permissions set of the new member
	 * 
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void addNewMember(final long id, final String username, final String password,
			final String lastName, final String firstName, final String email, 
			final Collection<Permission> permissions) throws DatabaseUpdateException;	

	/**
	 * Updates the permissions of user with the given id to the given permissions
	 * 
	 * @param userID
	 * 		The id of the user whose permission should be be updated
	 * @param permissions
	 * 		A collection of permissions which should be assigned to the user
	 * 
	 * @throws NotRegisteredException
	 * 		In case the user which should be updated wasn't found
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void updateUser(final long userID, final Collection<Permission> permissions) throws
	NotRegisteredException, DatabaseUpdateException;


	/**
	 * This method updates the details of the given user in the database
	 * 
	 * @param userID
	 * 		The id of the user
	 * @param password
	 * 		The password of the user
	 * @param lastName
	 * 		The lastName of the user
	 * @param firstName
	 * 		The firstName of the user
	 * @param email
	 * 		The e-mail of the new member
	 * @param permissions
	 * 		The permissions set of the new member
	 * 
	 * @throws NotRegisteredException
	 * 		In case the user which should be updated wasn't found
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void updateUser(final long userID, final String password,
			final String lastName, final String firstName, final String email, 
			final boolean shouldAskChangePassword) throws NotRegisteredException, DatabaseUpdateException;

	// Subject related methods

	/**
	 * 
	 * @return
	 * 		The first subject id which is free and not assigned to any subject in the database
	 * 
	 * @throws DatabaseRetrievalException 
	 * 		In case the required data can't be retrieved due to a database connection error
s	 */
	public long getFirstFreeSubjectID() throws DatabaseRetrievalException;

	/**
	 * 
	 * @return
	 * 		A collection of the top-level (root) subjects of the forum
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public Collection<ForumSubject> getTopLevelSubjects() throws DatabaseRetrievalException;

	/**
	 * Finds and returns a subject whose id equals to the given one
	 * 
	 * @param subjectID
	 * 		The id of the subject which should be returned
	 * 
	 * @return
	 * 		The found subject
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id hasb't been found in the database
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public ForumSubject getSubjectByID(final long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException;

	/**
	 * This method updates the database with a new subject
	 *
	 * @param subjectID
	 * 		A unique id of the new subject 
	 * @param subjectName
	 * 		The name of the new subject
	 * @param subjectDescription
	 * 		The description of the new subject
	 * @param fatherID
	 * 		The id of the father subject
	 * 
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void addNewSubject(final long subjectID, final String name, final String description, long fatherID) throws DatabaseUpdateException;

	/**
	 * Updates the content of a specific subject to be the content of the given one
	 * 
	 * @param subjectID
	 * 		The id of the subject to be updated
	 * @param name
	 * 		The name of the subject to be update
	 * @param description
	 * 		The description of the subject to be updated
	 * @param subSubjects
	 * 		The collection of the subject sub-subjects' IDs
	 * @param threads
	 * 		The collection of the subject threads' IDs
	 * @param deepSubSubjectsNum
	 * 		The number of subjects under this subject
	 * @param deepMessagesNum
	 * 		The number of messages under this subject
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case the subject which should be updated wasn't found
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void updateSubject(final long subjectID, final String name, final String description,
			final Collection<Long> subSubjects, 
			final Collection<Long> threads, final long deepSubSubjectsNum, 
			final long deepMessagesNum) throws SubjectNotFoundException, DatabaseUpdateException;

	/**
	 * Deletes a subject whose id equals to the given one, from the database
	 * 
	 * @param subjectID
	 * 		The id of the subject which should be removed from the database
	 *  
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id hasn't been found in the database
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void deleteASubject(final long subjectID) throws SubjectNotFoundException, DatabaseUpdateException;

	// Thread related methods

	/**
	 * 
	 * @return
	 * 		The next thread id which is free and isn't assigned to any thread in the database
	 * 
	 * @throws DatabaseRetrievalException 
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public long getFirstFreeThreadID() throws DatabaseRetrievalException;

	/**
	 * Finds and returns a thread whose id equals to the given one
	 * 
	 * @param threadID
	 * 		The id of the thread which should be returned
	 * 
	 * @return
	 * 		The found thread
	 * 
	 * @throws ThreadNotFoundException
	 * 		In case a thread with the given id hasb't been found in the database
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public ForumThread getThreadByID(final long threadID) throws ThreadNotFoundException, DatabaseRetrievalException;

	/**
	 * Opens a new messages thread with a given root message
	 * 
	 * @param threadID
	 * 		The id of the new thread
	 * @param topic
	 * 		The topic of the new thread
	 * @param rootID
	 * 		A id of the thread's root message
	 * @param fatherSubjectID
	 * 		The id of the father subject 	
	 *
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void openNewThread(final long threadID, final String topic, final long rootID, final long fatherSubjectID) throws DatabaseUpdateException;

	/**
	 * Deletes a thread whose id equals to the given one, from the database
	 * 
	 * @param threadID
	 * 		The id of the thread which should be removed from the database
	 * 
	 * @return
	 * 		A collection of all the messages ids which has been deleted as a consequences of the thread removal,
	 * 		these messages are the thread's root message and its replies (recursively)
	 * 
	 * @throws ThreadNotFoundException
	 * 		In case a thread with the given id hasn't been found in the database
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public Collection<Long> deleteAThread(final long threadID) throws ThreadNotFoundException,
	DatabaseUpdateException;

	public void updateThread(long threadID, String topic, 
			long numOfResponses, long numOfViews) throws ThreadNotFoundException, DatabaseUpdateException;

	// Message related methods	

	/**
	 * 
	 * @return
	 * 		The first message id which is free and isn't assigned to any message in the database
	 * 
	 * @throws DatabaseRetrievalException 
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public long getFirstFreeMessageID() throws DatabaseRetrievalException;

	/**
	 * Returns a collection of all the messages of the forum
	 *  
	 * @return
	 * 		A collection of all the messages the forum have right now
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */	
	public Collection<ForumMessage> getAllMessages() throws DatabaseRetrievalException;

	/**
	 * Finds and returns a message whose id equals to the given one
	 * 
	 * @param messageID
	 * 		The id of the message which should be returned
	 * 
	 * @return
	 * 		The found message
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id hasb't been found in the database
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved due to a database connection error
	 */
	public ForumMessage getMessageByID(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException;

	/**
	 * 
	 * Adds a new message to the forum
	 * 
	 * @param messageID
	 * 		A unique id of the message
	 * @param userID
	 * 		The id of the message author
	 * @param title
	 * 		The title of the message
	 * @param content
	 * 		The content of the message
	 * @param fatherID
	 * 		The id of the father message 
	 * 
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void addNewMessage(final long messageID, final long userID, final String title,
			final String content, long fatherID) throws DatabaseUpdateException;

	/**
	 * Updates the title and the content of a specific message to be the given one
	 * 
	 * @param messageID
	 * 		The ID of the message
	 * @param newTitle
	 * 		The updated title of the message
	 * @param newContent
	 * 		The updated content of the message
	 * @param replies
	 * 		The updated replies ids of the message
	 * 
	 * @throws MessageNotFoundException
	 * 		In case the message which should be updated wasn't found
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public void updateMessage(final long messageID, final String newTitle, final String newContent, 
			final Collection<Long> replies, long fatherID) throws MessageNotFoundException, DatabaseUpdateException;

	/**
	 * Deletes a message whose id equals to the given one, from the database
	 * 
	 * @param messageID
	 * 		The id of the message which should be removed from the database
	 * 
	 * @return
	 * 		A collection of all the messages ids which has been deleted as a consequences of this message removal,
	 * 		these messages are all the message replies (recursively)
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id hasn't been found in the database
	 * @throws DatabaseUpdateException
	 * 		In case there is a problem with the database updating
	 */
	public Collection<Long> deleteAMessage(final long messageID) throws MessageNotFoundException, DatabaseUpdateException;
}