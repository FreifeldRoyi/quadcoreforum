package forum.server;

import java.util.Collection;
import java.util.Set;

import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.exceptions.user.NotRegisteredException;
import forum.server.presentationlayer.*;

/**
 * With this interface the Controller layer of the server communicates with the
 * domain layer of the server.  
 * 
 * This interface contains all the forum logic and provides all the forum functionalities to
 * the upper layers.
 * 
 * @author Vitali Sepetnitsky 
 */
public interface ForumFacade {

	// User related methods:

	/**
	 * Creates a new guest in the system and saves it in the guests set.
	 *
	 * @return
	 * 		The created guest
	 */
	UIUser registerGuest();


	/**
	 * Unregisters a guest with a given id. 
	 * 
	 * This method is used when a user stops using a guest id it was given)
	 * 
	 * @param userId
	 * 		The guest id
	 */
	public void unregisterGuest(final long userId);


	/**
	 * @return
	 * 		The number of active forum guests - who currently view the forum contents
	 */
	public int getActiveGuests();

	/**
	 * @return
	 * 		A set currently active forum members user-names
	 */
	public Set<String> getActiveMemberNames();

	/**
	 * 
	 * Registers a new user, with the given parameters, to the forum
	 * 
	 * @param username
	 * 		The user-name of the new user
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
	 *
	 * Note: The registration doesn't make the user logged-in, this means that the user has to login in order to 
	 * use the privileges of a registered user
	 */
	public String registerToForum(final String username, final String password, final String lastName,
			final String firstName, final String email);

	/**
	 * 
	 * logs-in a user with the given parameters
	 * 
	 * @param username
	 * 		The user-name of the required user
	 * @param password
	 * 		The password of the required user
	 * 
	 * @return
	 * 		The logged-in user data, accessible via the UIMember interface or
	 * 		an error message which describes the failure, in case it occurred	  		
	 */
	public UIMember login(final String username, final String password);


	/**
	 * 
	 * Logs out a user whose user-name is the given one
	 * 
	 * @param username
	 * 		The user-name of the user who should be logged out
	 *
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful logout
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public String logout(final String username);

	/**
	 * 
	 * Returns the user whose user-name equals to the given one
	 * 
	 * @param username
	 * 		The required user-name
	 * @return
	 * 		A reference to the RegistereedUser domain object, whose user-name equals to the given one
	 * 
	 * @throws NotRegisteredException
	 * 		In case a user with the given user-name isn't registered to the forum
	 */
	public UIMember getUserByUsername(final String username) throws NotRegisteredException;

	// Subject related methods:

	/**
	 * 
	 * Adds a new sub-subject under a subject whose id is the given one.
	 * In case the fatherID is -1 the new subject will be added as one of the root subjects of the
	 * whole forum.
	 * 
	 * @param fatherID
	 * 		The id of the root subject (to which a new sub-subject will be added),
	 * 		can be -1 in case the subject should be added as one of the root subjects - at the top level.
	 * @param name
	 * 		The name of the new subject
	 * @param description
	 * 		The description of the new subject
	 *
	 * @return
	 * 		A message which describes the response:
	 * 			A success message in case of a successful subject adding
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public UISubject addNewSubject(final long fatherID, final String name, final String description);

	/**
	 * Finds and returns all the subject's sub-subjects data, accessible via the UISubject interface.
	 * 
	 * @param fatherID
	 * 		The id of the root subject, whose sub-subjects' data should be returned.
	 * 		If the id is -1, then the forum root subjects data is returned
	 * @return
	 * 		A collection of all the sub-subjects of the subject with the given id, accessible via the
	 * 		UISubject interface.
	 * 
	 * @throws SubjectNotFoundException
	 *		In case the id of the father subject wasn't found
	 */
	public Collection<UISubject> getSubjects(final long fatherID) throws SubjectNotFoundException;

	// Thread related methods:

	/**
	 * Opens a new thread under the given subject and adds a new message as its root
	 * 
	 * @param subjectId
	 * 		The id of the subject under which the new thread should be created
	 * @param username
	 * 		The user-name of the user who opens the new thread
	 * @param title	
	 * 		The title of the new thread's root message
	 * @param content
	 * 		The content of the new thread's root message
	 * 
	 * @return
	 * 		A message which describes the domain layer response:
	 * 			A success message in case of a successful message adding
	 * 			An error message which describes the failure, in case it occurred	  		
	 */
	public String openNewThread(long subjectID, final String username, final String title, final String content);

	/**
	 * 
	 * Finds and returns all the subject's threads data, accessible via the UIThread interface.
	 * 
	 * @param rootSubjectID
	 * 		The if of the subject whose threads should be represented
	 * @return
	 * 		A collection of all the threads of the subject with the given id, accessible via the
	 * 		UIThread interface.
	 * 
	 * @throws SubjectNotFoundException
	 *		In case the id of the father subject wasn't found
	 */
	public Collection<UIThread> getThreads(final long fatherID) throws SubjectNotFoundException;

	// Message related methods:

	/**
	 *
	 * Adds a new message as a reply to the given one - doesn't open a new thread
	 * 
	 * @param fatherID
	 * 		A message to which the reply should be added 
	 * @param userName
	 * 		The user-name of the reply author
	 * @param title
	 * 		The title of the new reply
	 * @param content
	 * 		The content of the new reply
	 * 
	 * @return
	 * 		The data of the new reply, accessible via a UIMessage interface
	 */
	public UIMessage addNewReply(final long fatherID, final String userName, final String title,
			final String content);

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
	 */
	public void updateAMessage(final long messageID, final String newTitle, 
			final String newContent) throws MessageNotFoundException;

	/**
	 * 
	 * Finds and returns all the replies data, accessible via the UIMessage interface.
	 * 
	 * @param fatherID
	 * 		The id of the message whose replies should be represented
	 * @return
	 * 		A collection of all the replies of the message with the given id, accessible via the
	 * 		UIMessage interface.
	 * 
	 * @throws MessageNotFoundException
	 *		In case the id of the root message wasn't found
	 */
	public Collection<UIMessage> getReplies(final long fatherID) throws MessageNotFoundException;

}
	// Update related messages:

	// TODO: void updatePassword(long userId, String oldPassword, String newPassword);

	// TODO: void updateMemberDetails( ... )

	// Deletion related messages:

	// TODO: void deleteThread(final long userId, final long dirId, final long threadId);

	// TODO: void deleteMessage(final long userId, final long postId, final long threadId);

	// Search related methods:

	//TODO: Set<SearchResult> searchByContent(final String message);

	// TODO: Set<SearchResult> searchByAuthor(final String authorName);

	// TODO: Set<SearchResult> searchByDate(final Date fromDate, final Date toDate);

	// and there can be more and more ...