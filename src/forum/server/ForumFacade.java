package forum.server;

/**
 * With this interface the Controller layer of the server communicates with the
 * domain layer of the server.  
 * 
 * This interface contains all the forum logic and provides all the forum functionallities to
 * the upper layers.
 * 
 * @author Vitali Sepetnitsky 
 */
public interface ForumFacade {

	// User related methods:

	/**
	 * Login function is responsible of login an existing member to the forum system .
	 *
	 * @param name the username of the member
	 * @param password the password of the member
	 *
	 * @return the member
	 *
	 * @throws NotFoundException in case the username does not exist.
	 * @throws BadPasswordException in case the password is wrong
	 * @throws RemoteException if connection to the forum has failed.
	 */
	UIUser login(final String name, final String password) throws NotFoundException,
	BadPasswordException, RemoteException ;

	/**
	 * Register function is responsible of register the new member to the forum. And also updating the
	 * UserServer for a new member.
	 * The action does not makes the user active!!
	 * which means that the user has to login in order to use the privileges of a registered member!
	 *
	 * @param name the username of the new member
	 * @param password the password of the new member
	 *
	 * @throws DuplicationException occur when there are two same username's.
	 * @throws RemoteException if connection to the forum has failed.
	 */
	void register(final String name, final String password) throws DuplicationException, RemoteException ;


	/**
	 * 
	 * @param username
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param dob
	 * @param chosenGender
	 * @param residence
	 * @throws DuplicationException
	 * @throws RemoteException
	 */
	void register(String username, String password, String firstName, String lastName, String dob,
			String chosenGender, String residence) throws DuplicationException, RemoteException ;



	/**
	 * create new guest in the system.
	 * creates a User object and puts it in the guests set in the user serve.
	 * @return the newly created guest
	 * @throws RemoteException if connection to the forum has failed.
	 */
	UIUser registerGuest() throws RemoteException ;


	/**
	 * unregister a guest (used when a user stops using a guest id it was given)
	 * @param userId the guest id
	 * @throws RemoteException if connection failed
	 */
	public void unregisterGuest(long userId) throws  RemoteException;

	/**
	 * logout a member by changing its 'active' flag to false
	 * (the new state will be seen from all places that holds a reference to this member)
	 * @param userId the id of the user that asked to logout
	 * @throws NotFoundException if the user was not found, or it was not found as a member
	 * @throws RemoteException if connection to the forum has failed.
	 */
	void logout(final long userId) throws NotFoundException, RemoteException ;

	/**
	 * @return number of active guests
	 * @throws RemoteException if connection failed
	 */
	public int getActiveGuests() throws  RemoteException;

	/**
	 * @return the usernames of active members
	 * @throws RemoteException if connection failed
	 */
	public Set<String> getActiveMemberNames() throws  RemoteException;

	/**
	 * @param userId id of the user to find
	 * @return a member by its id
	 * @throws NotFoundException if no such member exists
	 * @throws RemoteException if connection to the forum has failed.
	 */
	UIMember getMemberById(final long userId) throws NotFoundException, RemoteException ;


	/**
	 * 
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @throws NotFoundException
	 * @throws BadPasswordException
	 * @throws RemoteException 
	 */
	void updatePassword(long userId, String oldPassword, String newPassword) throws
	NotFoundException, BadPasswordException,RemoteException;


	/**
	 * 
	 * @param userId
	 * @param newFirstName
	 * @param newLastName
	 * @param dob
	 * @param chosenGender
	 * @param newResidence
	 * @throws NotFoundException
	 * @throws RemoteException
	 */
	void updateMemberDetails(long userId, String newFirstName, String newLastName, String dob, String chosenGender,
			String newResidence) throws NotFoundException,RemoteException;


	/**
	 * @param username name of the user to find
	 * @return a member id by its username
	 * @throws NotFoundException if no such member exists
	 * @throws RemoteException if connection to the forum has failed.
	 */
	long getMemberId(final String username) throws NotFoundException, RemoteException ;

	/**
	 * Adds the administrator privileges to a specific user (must be a member).
	 *
	 * @param adminId the admin id who gives the privileges
	 * @param userName the username of the user how received the privileges
	 *
	 * @throws NotFoundException the not found exception
	 * @throws UnpermittedActionException the unPermitted action exception
	 * @throws RemoteException if connection to the forum has failed.
	 */
	void addAdministratorPrivileges(final long adminId, final String userName)
	throws NotFoundException, UnpermittedActionException, RemoteException ;

	/**
	 * Adds the moderator privileges to a specific user (must be a member).
	 *
	 * @param adminId the admin id who gives the privileges
	 * @param userName the username of the user how received the privileges
	 *
	 * @throws NotFoundException the not found exception
	 * @throws UnpermittedActionException the unPermitted action exception
	 * @throws RemoteException if connection to the forum has failed.
	 */
	void addModeratorPrivileges(final long adminId, final String userName)
	throws NotFoundException, UnpermittedActionException, RemoteException ;

	// Message related methods:

	/**
	 * @return the root directory in presentation form
	 * @throws RemoteException if connection to the forum has failed.
	 */
	UIDirectory getRootDirectory() throws RemoteException ;

	/**
	 * create a new post in thread.
	 * @param threadId location of the new post
	 * @param userId the id of the user requested to perform this action
	 * @param msg the wanted message in the post
	 * @return the new post for presentation
	 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
	 * this action
	 * @throws NotFoundException if the thread or the user was not found
	 * @post messages has a new post with the wanted message, and the wanted thread holds its id.
	 * @throws RemoteException if connection to the forum has failed.
	 * @throws RemoteException if connection to the forum has failed.
	 */
	UIPost addPost(final long threadId, final long userId, final String msg)
	throws UnpermittedActionException, NotFoundException, RemoteException ;

	/**
	 * create a new thread in a sub-forum, with a first post.
	 * @param dirId location of the new thread
	 * @param userId the member requested to perform this action
	 * @param subject the thread subject
	 * @param msg the wanted message for the first post in the new created thread
	 * @return the new thread for presentation
	 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
	 * this action
	 * @throws NotFoundException if the directory or the user was not found
	 * @throws RemoteException if connection to the forum has failed.
	 * @post a new thread with the wanted subject was created in the the wanted thread, and its first post
	 * includes the given message.
	 */
	UIThread addThread(final long dirId, final long userId, final String subject,
			final String msg) throws UnpermittedActionException, NotFoundException, RemoteException ;

	/**
	 * view the wanted post
	 * @param postId the wanted post to view
	 * @return the post in a form of passing to the presentation layer
	 * for performing this action
	 * @throws NotFoundException if the post or the user was not found
	 * @throws RemoteException if connection to the forum has failed.
	 * @post no change is made in the system.
	 */
	UIPost viewPost(final long postId) throws NotFoundException, RemoteException ;

	/**
	 * edit the wanted post (only if member is the writer of the post)
	 * @param postId the wanted post to edit
	 * @param userId the member requested to perform this action
	 * @param msg the new message to edit instead of the current message in the post
	 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
	 * this action
	 * @throws NotFoundException if the post or the user was not found
	 * @throws RemoteException if connection to the forum has failed.
	 * @post the post holds the new message (msg) instead of the previous one.
	 */
	void editPost(final long postId, final long userId, final String msg)
	throws UnpermittedActionException, NotFoundException, RemoteException ;

	/**
	 * edit the wanted thread topic
	 * @param threadId the wanted thread to edit
	 * @param userId the member requested to perform this action
	 * @param topic the new topic to edit instead of the current topic in the thread
	 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
	 * this action
	 * @throws NotFoundException if the post or the user was not found
	 * @throws RemoteException if connection to the forum has failed.
	 * @post the thread holds the new topic instead of the previous one.
	 */
	void editThreadTopic(final long threadId, final long userId, final String topic)
	throws UnpermittedActionException, NotFoundException, RemoteException ;

	/**
	 * get all directory's subDirectories in a presentation
	 * @param dirId the directory to retrieve its subDirectories
	 * @return vector of all subDirectories in a form of passing to the presentation layer
	 * @throws NotFoundException when the directory was not found
	 * @throws RemoteException if connection to the forum has failed.
	 */
	Vector<UIDirectory> getSubDirectories(final long dirId) throws NotFoundException, RemoteException ;

	/**
	 * get all directory's threads in a presentation form
	 * @param dirId the directory to retrieve its threads
	 * @return vector of all threads in a form of passing to the presentation layer
	 * @throws NotFoundException when the directory was not found
	 * @throws RemoteException if connection to the forum has failed.
	 */
	Vector<UIThread> getThreads(final long dirId) throws NotFoundException, RemoteException ;

	/**
	 * get all threads's posts in a presentation form
	 * @param threadId the thread to retrieve its threads
	 * @return vector of all posts in a form of passing to the presentation layer
	 * @throws NotFoundException when the thread was not found
	 * @throws RemoteException if connection to the forum has failed.
	 */
	Vector<UIPost> getPosts(final long threadId) throws NotFoundException, RemoteException ;

	/**
	 * add new subDirectory under an existing directory
	 * (the root is build when the forum is initialized)
	 * @param userId the user which ask to add this directory
	 * @param containerDirectoryId the id of the directory under it the new subDirectory will be added
	 * @param newDirName the name for the new subDirectory
	 * @return the new directory for presentation
	 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
	 * this action
	 * @throws NotFoundException if the container directory was not found
	 * @throws RemoteException if connection to the forum has failed.
	 */
	UIDirectory addDirectory(final long userId, final long containerDirectoryId,
			final String newDirName) throws UnpermittedActionException, NotFoundException, RemoteException ;

	/**
	 * delete a thread with all its content from a directory
	 * @param userId the user which ask to add this directory
	 * @param dirId the id of the directory under it the thread should be deleted
	 * @param threadId the id of the thread to delete
	 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
	 * this action
	 * @throws NotFoundException if the container directory was not found
	 * @throws RemoteException if connection to the forum has failed.
	 */
	void deleteThread(final long userId, final long dirId, final long threadId)
	throws UnpermittedActionException, NotFoundException, RemoteException ;

	/**
	 * delete a post with all its content from a thread
	 * @param userId the user which ask to add this directory
	 * @param threadId the id of the thread under it the post should be deleted
	 * @param postId the id of the post to delete
	 * @throws UnpermittedActionException if the user doesn't own the appropriate Privilege for performing
	 * this action
	 * @throws NotFoundException if the container directory was not found
	 * @throws RemoteException if connection to the forum has failed.
	 */
	void deletePost(final long userId, final long postId, final long threadId)
	throws UnpermittedActionException, NotFoundException, RemoteException ;

	// Search related methods:

	/**
	 * Search by content, searches for post according to content that the user wrote
	 * responsible for uploading all Posts that contain the same words/sentences that the client asks
	 *
	 * @param message the message that the user want to get messages according to
	 *
	 * @return set<SearchResult> all desirable messages
	 *
	 * @throws RemoteException if connection to the forum has failed.
	 */
	Set<SearchResult> searchByContent(final String message) throws RemoteException ;

	/**
	 * Search by author. searches for post according to author username that the user typed.
	 * responsible for uploading all Posts that contain the same author name matched to the user request.
	 *
	 * @param authorName the username of the authors of the messages that the user wants
	 *
	 * @return the set<SearchResult> all desirable messages
	 *
	 * @throws RemoteException if connection to the forum has failed.
	 */
	Set<SearchResult> searchByAuthor(final String authorName) throws  RemoteException ;

	/**
	 * Search by date. searches for post according to range of dates
	 *
	 * @param fromDate the date from which the user wants the messages
	 * @param toDate the date to which the user wants the messages
	 *
	 * @return the set< SearchResult> all desirable messages
	 *
	 * @throws RemoteException if connection to the forum has failed.
	 */
	Set<SearchResult> searchByDate(final Date fromDate, final Date toDate) throws RemoteException ;
















}
