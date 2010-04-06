/**
 * This class implements the ForumFacase interface by delegating its methods to the users
 * and messages controllers.
 */
package forum.server.domainlayer;

import java.util.*;

import forum.server.domainlayer.SystemLogger;

import forum.server.domainlayer.interfaces.*;

import forum.server.domainlayer.user.*;
import forum.server.domainlayer.message.*;
import forum.server.domainlayer.search.*;


import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;

import forum.server.persistentlayer.pipe.user.exceptions.*;
import forum.server.persistentlayer.pipe.message.exceptions.*;

/**
 * @author Vitali Sepetnitsky
 *
 */
public class MainForumLogic implements ForumFacade {
	/* Handles all the forum data and is connected to the database */
	private final ForumDataHandler dataHandler;
	/* Handles subjects, threads and messages */
	private final MessagesController messagesController;
	/* Handles all the users of the forum: guests and members */
	private final UsersController usersController;
	
	/* Search Engine */
	private SearchAgent searcher;

	private static ForumFacade FORUM_FACADE_INSTANCE;

	/**
	 * 
	 * @return
	 * 		A single instance of the ForumFacade implementation, according to the
	 * 		Singleton design pattern
	 * @throws DatabaseUpdateException
	 */
	public static ForumFacade getInstance() throws DatabaseRetrievalException, DatabaseUpdateException {
		if (MainForumLogic.FORUM_FACADE_INSTANCE == null)
			MainForumLogic.FORUM_FACADE_INSTANCE = new MainForumLogic();
		return MainForumLogic.FORUM_FACADE_INSTANCE;
	}
	
	/**
	 * Constructs the forum objects according to the database
	 * 
	 * @throws DatabaseUpdateException 
	 * 		In case a connection with the forum database can't be established
	 * @throws DatabaseUpdateException 
	 * 		In case a connection error with the forum database has occurred
	 * @throws  
	 */
	private MainForumLogic() throws DatabaseRetrievalException, DatabaseUpdateException {
		try {
			this.dataHandler = new ForumDataHandler();
			this.searcher = new SearchAgent();
		}
		catch (DatabaseUpdateException e) {
			SystemLogger.severe(e.getMessage());
			throw e;
		}
		catch (DatabaseRetrievalException e) {
			SystemLogger.severe(e.getMessage());
			throw e;
		}
		
		this.usersController = new UsersController(this.dataHandler);
		this.messagesController = new MessagesController(this.dataHandler);	
	}


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
	 * 		ForumFacade#getActiveMemberUserNames()
	 */
	public Collection<String> getActiveMemberUserNames() {
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
	
	/**
	 * @see
	 * 		ForumFacade#promoteToBeModerator(long, long)
	 */
	public void promoteToBeModerator(final long applicantID, final String username) throws NotPermittedException, 
	NotRegisteredException, DatabaseUpdateException {
		this.usersController.promoteToBeModerator(applicantID, username);
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
			final String content) throws NotRegisteredException, NotPermittedException, SubjectNotFoundException,
			DatabaseUpdateException {
		UIThread toReturn = this.messagesController.openNewThread(userID, topic, subjectID, title, content);
		
		UIMessage tMsg = null;
		try {
			tMsg = getMessageByID(toReturn.getID());
			this.searcher.addData(tMsg);
		} catch (MessageNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return toReturn;
	}

	// Message related methods

	/**
	 * @see
	 * 		ForumFacade#searchByAuthor(String, int, int)
	 */
	public SearchHit[] searchByAuthor(String username, int from, int to) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see
	 * 		ForumFacade#searchByContent(String, int, int)
	 */
	public SearchHit[] searchByContent(String phrase, int from, int to) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @see
	 * 		ForumFacade#getMessageByID(long)
	 */
	public UIMessage getMessageByID(final long messageID)
			throws MessageNotFoundException, DatabaseRetrievalException {
		return this.messagesController.getMessageByID(messageID);
	}

	
	/**
	 * @see
	 * 		ForumFacade#getMessagesByUserID(long)
	 */
	public Collection<UIMessage> getMessagesByUserID(long authorID)
			throws DatabaseRetrievalException, NotRegisteredException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @see
	 * 		ForumFacade#getReplies(long)
	 */
	public Collection<UIMessage> getReplies(long fatherID)
	throws MessageNotFoundException, DatabaseRetrievalException {
		return this.messagesController.getReplies(fatherID);
	}

	/**
	 * @see
	 * 		ForumFacade#addNewReply(long, long, String, String)
	 */
	public UIMessage addNewReply(final long authorID, final long fatherID, final String title,
			final String content) throws NotRegisteredException, NotPermittedException, MessageNotFoundException,
			DatabaseUpdateException {
		UIMessage toReturn = this.messagesController.addNewReply(authorID, fatherID, title, content);
		
		this.searcher.addData(toReturn);
		
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#updateAMessage(long, long, String, String)
	 */
	public UIMessage updateAMessage(final long userID, final long messageID, final String newTitle, 
			final String newContent) throws NotRegisteredException, NotPermittedException, MessageNotFoundException,
			DatabaseUpdateException {
		return this.messagesController.updateAMessage(userID, messageID, newTitle, newContent);
	}

	/**
	 * @see
	 * 		ForumFacade#deleteAMessage(long, long, long)
	 */
	public void deleteAMessage(final long userID, final long fatherID, final long messageID) throws 
	NotRegisteredException, NotPermittedException, MessageNotFoundException, DatabaseUpdateException {
		this.messagesController.deleteAMessage(userID, fatherID, messageID);
	}
}