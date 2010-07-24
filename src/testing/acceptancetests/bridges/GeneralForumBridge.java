/**
 * This interface contains definitions of general forum related methods which should be
 * implemented by the developer in an implementation class in order to make the acceptance
 * tests pass.
 */
package testing.acceptancetests.bridges;

import java.util.Collection;

/**
 * @author 
 * 		Sepetnitsky Vitali 310106745
 *
 */
public interface GeneralForumBridge {

	/**
	 * Registers a new user to the forum
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
	 * @see
	 * 		ForumFacade#registerNewMember(String, String, String, String, String)
	 * 
	 * @return
	 * 		The id of the new registered user in case of successful registering and -1 otherwise.
	 */
	public long register(final String username, final String password, final String lastName,
			final String firstName, final String email);

	/**
	 * Performs a login to the forum
	 * 
	 * @param username
	 * 		The user-name of the user who wants to login
	 * @param password
	 * 		The password of the user who wants to login
	 * 
	 * @see	ForumFacade#login(String, String)
	 * 
	 * @return
	 * 		In successful login a String array of size 4 is returned. The array, lets denote it
	 * 		by x, should contain the following information:
	 * 			x[0] = the user-name of the logged in user
	 * 			x[1] = the last name of the logged in user
	 * 			x[2] = the first name of the logged in user
	 * 			x[3] = the e-mail of the logged in user
	 * 
	 * 		In unsuccessful login null should be returned 
	 */
	public String[] login(final String username, final String password);
	
	/**
	 * Performs a logout from the forum
	 * 
	 * @param username
	 * 		The user-name of the user who wants to logout
	 * 
	 * @see	
	 * 		ForumFacade#logout(String)
	 * 
	 * @return
	 * 		True in case of successful logout and false otherwise
	 */
	public boolean logout(final String username);
	
	/**
	 * 
	 * Adds a new sub-subject under a subject whose id is the given one.
	 * 
	 * In case the fatherID is -1 the new subject will be added as one of the root subjects of the
	 * whole forum.
	 * 
	 * @param userID
	 * 		The id of the user who asks to add the new subject to the forum. 
	 * 		The given id is used in order to check if the user has the permissions to add new subjects.
	 * 		
	 * @param fatherID
	 * 		The id of the root subject (to which a new sub-subject will be added),
	 * 		can be -1 in case the subject should be added as one of the root subjects - at the top level.
	 * @param name
	 * 		The name of the new subject
	 * @param description
	 * 		The description of the new subject
	 *
	 * @see
	 * 		ForumFacade#addNewSubject(long, long, String, String)
	 * 
	 * @return
	 * 		The identification number of the created subject (should be greater than 0) or -1
	 * 		in case the subject hasn't been created because of an error
	 * 
	 */
	public long addNewSubject(final long userID, final long fatherID, final String name, final String description);

	/**
	 * Opens a new thread under the given subject and adds a new message as its root
	 * 
	 * @param userID
	 * 		The id of the user who asks to open the new thread.
	 * 		The given id is used in order to check whether the user has the permission to open
	 * 		new threads in the forum.
	 * @param subjectID
	 * 		The id of the subject under which the new thread should be created
	 * @param topic
	 * 		The topic of the new thread
	 * @param title	
	 * 		The title of the new thread's root message
	 * @param content
	 * 		The content of the new thread's root message
	 * 
	 * @see
	 * 		ForumFacade#openNewThread(long, long, String, String, String)
	 * 
	 * @return
	 * 		The identification number of the created thread first message (should be greater than 0) or -1
	 * 		in case the thread or its subject haven't been created because of an error
	 */
	public long openNewThread(final long userID, final long subjectID, final String topic, final String title,
			final String content);

	/**
	 * 
	 * Finds and returns all the replies data as string represantations
	 * 
	 * @param fatherID
	 * 		The id of the message whose replies should be represented
	 * 
	 * @see
	 * 		ForumFacade#getReplies(long)
	 * 
	 * @return
	 * 		In case of a successful replies retrieval, a collection of all the replies of the message with the given id
	 * 		should be returned. 
	 * 		Each reply should be decoded to the following string representation:
	 * 				<reply-author><reply-title><reply-content> (without white-spaces)
	 * 		In case of an error - null should be returned
	 */
	public Collection<String> getReplies(final long fatherID);

	/**
	 *
	 * Adds a new message as a reply to the given one - doesn't open a new thread
	 * 
	 * @param authorID
	 * 	 	The id of the reply author
	 * @param fatherID
	 * 		A message to which the reply should be added 
	 * @param title
	 * 		The title of the new reply
	 * @param content
	 * 		The content of the new reply
	 * 
	 * @see
	 * 		ForumFacade#addNewReply(long, long, String, String)
	 * 
	 * @return
	 * 		The id of the reply the created reply or -1 if an exception was thrown
	 */
	public long addNewReply(final long authorID, final long fatherID, final String title,
			final String content);
	
	/**
	 * 
	 * Deletes a message with the given id from the forum
	 * 
	 * @param applicantID
	 * 		The id of the user who wants to delete the message
	 * @param messageID
	 * 		The id of the message which should be deleted
	 * @param fatherID
	 * 		The id of the message from which this message should be deleted,
	 * 		if the id is -1 than this message is a top-level one (- a root 
	 * 		message of a thread)
	 */
	public boolean deleteMessage(final long applicantID, final long messageID, 
			final long fatherID);
}