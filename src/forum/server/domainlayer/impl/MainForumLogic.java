package forum.server.domainlayer.impl;

import java.util.*;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.impl.message.*;
import forum.server.domainlayer.impl.user.*;

import forum.server.domainlayer.impl.interfaces.*;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;

import forum.server.persistentlayer.pipe.user.exceptions.*;
import forum.server.persistentlayer.pipe.message.exceptions.*;

public class MainForumLogic implements ForumFacade {
	/* Handles all the forum data and is connected to the database */
	private final ForumDataHandler dataHandler;
	/* Handles subjects, threads and messages */
	private final MessagesController messagesController;
	/* Handles all the users of the forum: guests and members */
	private final UsersController usersController;

	private static ForumFacade FORUM_FACADE_INSTANCE;
	
	public static ForumFacade getInstance() throws DatabaseUpdateException {
		if (MainForumLogic.FORUM_FACADE_INSTANCE == null)
			MainForumLogic.FORUM_FACADE_INSTANCE = new MainForumLogic();
		return MainForumLogic.FORUM_FACADE_INSTANCE;
	}
	
	/**
	 * Constructs the forum objects according to the database
	 * @throws DatabaseUpdateException 
	 */
	private MainForumLogic() throws DatabaseUpdateException {
		try {
			this.dataHandler = new ForumDataHandler();
		} catch (DatabaseUpdateException e) {
			SystemLogger.info(e.getMessage());
			throw e;
		}		
		this.usersController = new UsersController(this.dataHandler);
		this.messagesController = new MessagesController(this.dataHandler);	
	}

	/**
	 * Here are the methods for the forum initializing according to the database
	 */

	/**
	 * The main update method, reads the forum data from the database and constructs the appropriate instances
	 * of forum domain objects, acts as a converter from persistence to domain.
	 * 
	 * @throws JAXBException
	 * 		In case an error occured while trying read from the database
	 */
	/*	private void updateForumByDatabase() throws JAXBException {
		ForumType tForumType = PersistenceFactory.getPipe().getForumFromDatabase();

		// first constructs all the users according to the database
		for (UserType tUserType : tForumType.getRegisteredUsers()) {
			RegisteredUser tUser = new RegisteredUserImpl(tUserType.getUsername(), tUserType.getPassword(),
					tUserType.getFirstName(), tUserType.getLastName(), tUserType.getEMail());
			this.registeredUsers.put(tUser.getUsername(), tUser);
			this.registeredUsersByEmail.put(tUser.getEMail(), tUser);	
		}

		// construct the forum content: subjects + threads + messages
		for (SubjectType tSubject : tForumType.getForumSubjects())
			this.subjects.add(constructForumSubject(tSubject));
	}
	 */

	/**
	 * Constructs a subject of type SubjectType according to a given database SubjectType object
	 * 
	 * @param subjType
	 * 		The database subject from which this subject is constructed
	 * @return
	 * 		The constructed ForumSubject which contains all the data of the given database subject
	 */
	/*	private ForumSubject constructForumSubject(SubjectType subjType) {
		ForumSubject toReturn = new ForumSubjectImpl(subjType.getSubjectID(), subjType.getDescription(), 
				subjType.getName());

		// adds all the sub-subjects according to the database
		for (SubjectType tSubjectType : subjType.getSubSubjects())
			toReturn.addSubSubjectToData(constructForumSubject(tSubjectType));

		// adds all the threads according to the database
		for (ThreadType tThreadType : subjType.getSubThreads())
			toReturn.addThreadToData(constructForumThread(tThreadType));

		return toReturn;	
	}
	 */
	/**
	 * Constructs a ForumThread domain object instance, according to a ThreadType persistent object
	 * (from the database)
	 * 
	 * @param threadType
	 * 		The ThreadType persistence object, from which the data should be taken
	 * @return
	 * 		A ForumThread domain object, filled with the data of the given ThreadType object 
	 */
	/*	private ForumThread constructForumThread(ThreadType threadType) {
		return new ForumThreadImpl(constructForumMessage(threadType.getStartMessage()),
				null, threadType.getNumOfResponses(),
				threadType.getNumOfViews());
	}
	 */
	/**
	 * Constructs a new ForumMessage object instance, according to the given MessageType persistent object
	 * (from the database)
	 * 
	 * @param msgType
	 * 		The MessageType persistence object, from which the data should be taken
	 * @return
	 * 		A ForumMessage domain object, filled with the data of the given MessageType object
	 */
	/*	private ForumMessage constructForumMessage(MessageType msgType) {
		// creates the new message
		ForumMessage toReturn = new ForumMessageImpl(this.registeredUsers.get(msgType.getAuthor()),
				msgType.getTitle(), msgType.getContent());
		// add all the replies to the message
		for (MessageType tMsgType : msgType.getReplies()) {
			toReturn.addMessageReplyData(constructForumMessage(tMsgType));
		}
		return toReturn;
	}
	 */

	/**
	 * Here is the end of the initialize methods
	 */

	// Guest related methods

	/**
	 * @see
	 * 		ForumFacade#addGuest()
	 */
	public UIUser addGuest() {
		return this.usersController.addGuest();
	}

	/**
	 * @see
	 * 		ForumFacade#removeGuest(long)
	 */
	public void removeGuest(long userId) {
		this.usersController.removeGuest(userId);
	}

	/**
	 * @see
	 * 		ForumFacade#getActiveGuestsNumber()
	 */
	public long getActiveGuestsNumber() {
		return this.usersController.getActiveGuestsNumber();
	}

	// User related methods

	/**
	 * @see
	 * 		ForumFacade#getActiveMemberNames()
	 */
	public Set<String> getActiveMemberNames() {
		return this.usersController.getActiveMemberNames();
	}

	/**
	 * @see
	 * 		ForumFacade#getMemberIdByUsername(String)
	 */
	public long getMemberIdByUsername(final String username) throws NotRegisteredException,
	DatabaseRetrievalException {
		return this.usersController.getMemberIdByUsername(username);
	}

	/**
	 * @see
	 * 		ForumFacade#login(String, String)
	 */
	public UIMember login(String username, String password)
	throws NotRegisteredException, WrongPasswordException, DatabaseRetrievalException {
		return this.usersController.login(username, password);
	}

	/**
	 * @see
	 * 		ForumFacade#logout(String)
	 */
	public void logout(String username) throws NotConnectedException {
		this.usersController.logout(username);
	}

	/**
	 * @see
	 * 		ForumFacade#registerNewMember(String, String, String, String, String)
	 */
	public long registerNewMember(String username, String password,
			String lastName, String firstName, String email) throws MemberAlreadyExistsException, DatabaseUpdateException  {
		return this.usersController.registerNewMember(username, password, lastName, firstName, email);
	}
	
	// Subject related methods

	/**
	 * @see
	 * 		ForumFacade#getSubjects(long)
	 */
	public Collection<UISubject> getSubjects(long fatherID)
	throws SubjectNotFoundException, DatabaseRetrievalException {
		return this.messagesController.getSubjects(fatherID);
	}

	/**
	 * @see 
	 * 		ForumFacade#addNewSubject(long, long, String)
	 */
	public UISubject addNewSubject(final long userID, final long fatherID, final String name, 
			final String description) throws SubjectAlreadyExistsException, SubjectNotFoundException, NotRegisteredException, 
			NotPermittedException, DatabaseUpdateException {
		return this.messagesController.addNewSubject(userID, fatherID, name, description);
	}

	
	// Thread related methods
	
	/**
	 * @see
	 * 		ForumFacade#getThreads(long)
	 */
	public Collection<UIThread> getThreads(long fatherID)
	throws SubjectNotFoundException, DatabaseRetrievalException {
		return this.messagesController.getThreads(fatherID);
	}

	/**
	 * @see
	 * 		ForumFacade#openNewThread(long, long, String, String, String)
	 */
	public UIThread openNewThread(final long userID, long subjectID, final String topic, final String title,
			final String content) throws NotRegisteredException, SubjectNotFoundException, NotPermittedException,
			DatabaseUpdateException {
		return this.messagesController.openNewThread(userID, topic, subjectID, title, content);
	}

	// Message related methods

	/**
	 * @see
	 * 		ForumFacade#getReplies(long)
	 */
	public Collection<UIMessage> getReplies(long fatherID)
	throws MessageNotFoundException, DatabaseRetrievalException {
		return this.messagesController.getReplies(fatherID);
	}

	/**
	 * @throws DatabaseUpdateException 
	 * @throws NotPermittedException 
	 * @throws NotRegisteredException 
	 * @see
	 * 		ForumFacade#addNewReply(long, long, String, String)
	 */
	public UIMessage addNewReply(final long authorID, final long fatherID, final String title,
			final String content) throws NotRegisteredException, MessageNotFoundException, NotPermittedException, DatabaseUpdateException {
		return this.messagesController.addNewReply(authorID, fatherID, title, content);
	}

	/**
	 * @see
	 * 		ForumFacade#updateAMessage(long, long, String, String)
	 */
	public UIMessage updateAMessage(final long userID, final long messageID, final String newTitle, 
			final String newContent) throws NotRegisteredException, MessageNotFoundException, NotPermittedException,
			DatabaseUpdateException {
		return this.messagesController.updateAMessage(userID, messageID, newTitle, newContent);
	}

	/**
	 * @see
	 * 		ForumFacade#deleteAMessage(long, long, long)
	 */
	public void deleteAMessage(final long userID, final long fatherID, final long messageID) throws 
	NotRegisteredException, MessageNotFoundException, NotPermittedException, DatabaseUpdateException {
		this.messagesController.deleteAMessage(userID, fatherID, messageID);
	}
}