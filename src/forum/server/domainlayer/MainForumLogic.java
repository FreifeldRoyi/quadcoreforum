/**
 * This class implements the ForumFacase interface by delegating its methods to the users
 * and messages controllers.
 */
package forum.server.domainlayer;

import java.io.File;
import java.util.*;

import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;

import forum.server.domainlayer.SystemLogger;

import forum.server.domainlayer.interfaces.*;

import forum.server.domainlayer.user.*;
import forum.server.domainlayer.message.*;
import forum.server.domainlayer.search.*;
import forum.server.domainlayer.search.cmpssearch.CompassAdapter;


import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.*;

/**
 * @author Vitali Sepetnitsky
 *
 */
public class MainForumLogic implements ForumFacade {

	/* Handles all the users of the forum: guests and members */
	private final UsersController usersController;
	/* Handles subjects, threads and messages */
	private final MessagesController messagesController;
	/* Search Controller */
	private final SearchEngine searchController;

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
	 */
	private MainForumLogic() throws DatabaseRetrievalException, DatabaseUpdateException {
		try {
			ForumDataHandler tDataHandler = new ForumDataHandler();

			String tOutputPath = System.getProperty("java.class.path");
			int tIndexOfSemiColon = tOutputPath.indexOf(";");
			tOutputPath = tIndexOfSemiColon != -1? tOutputPath.substring(0, tIndexOfSemiColon) : tOutputPath;
			File tFile = new File(tOutputPath + System.getProperty("file.separator") + "forum/server/util/compassSettings.xml");

			CompassConfiguration tConf = CompassConfigurationFactory.newConfiguration().configure(tFile);               

			Compass compass = tConf.buildCompass();

			this.searchController = new CompassAdapter(compass);

			this.usersController = new UsersController(tDataHandler);
			this.messagesController = new MessagesController(tDataHandler);

			for (ForumMessage tCurrent : tDataHandler.getMessagesCache().getAllMessages()) {
				this.searchController.addData(tCurrent);
			}

		}
		catch (DatabaseUpdateException e) {
			SystemLogger.severe(e.getMessage());
			throw e;
		}
		catch (DatabaseRetrievalException e) {
			SystemLogger.severe(e.getMessage());
			throw e;
		}
	}

	// Guest related methods

	/**
	 * @throws DatabaseRetrievalException 
	 * @see
	 * 		ForumFacade#addGuest()
	 */
	public UIUser addGuest() throws DatabaseUpdateException {
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
	 * @throws DatabaseRetrievalException 
	 * @see
	 * 		ForumFacade#getActiveGuestsNumber()
	 */
	public long getActiveGuestsNumber() throws DatabaseRetrievalException {
		return this.usersController.getActiveGuestsNumber();
	}

	// User related methods

	/**
	 * @throws DatabaseRetrievalException 
	 * @see
	 * 		ForumFacade#getActiveMemberUserNames()
	 */
	public Collection<String> getActiveMemberUserNames() throws DatabaseRetrievalException {
		return this.usersController.getActiveMemberNames();
	}

	/**
	 * @see
	 * 		ForumFacade#getAllMembers()
	 */
	public Collection<UIMember> getAllMembers() throws DatabaseRetrievalException {
		return this.usersController.getAllMembers();
	}

	/**
	 * @see
	 * 		ForumFacade#getMemberIdByUsernameAndOrEmail(String, String)
	 */
	public long getMemberIdByUsernameAndOrEmail(final String username, final String email) throws NotRegisteredException,
	DatabaseRetrievalException {
		return this.usersController.getMemberIdByUsernameAndOrEmail(username, email);
	}

	/**
	 * @see
	 * 		ForumFacade#getMemberByID(long)
	 */
	public UIMember getMemberByID(long memberID) throws NotRegisteredException, DatabaseRetrievalException {
		return this.usersController.getMemberByID(memberID);
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
	 * 		ForumFacade#updateMemberProfile(long, String, String, String, String, String)
	 */
	public UIMember updateMemberProfile(final long memberID, final String username, final String password, final String lastName,
			final String firstName, final String email, boolean shouldAskPassword) throws NotRegisteredException, MemberAlreadyExistsException, DatabaseUpdateException {
		return this.usersController.updateMemberProfile(memberID, username, password, lastName, firstName, email, shouldAskPassword);
	}

	/**
	 * @see	
	 * 		ForumFacade#updateMemberPassword(long, String, String, boolean)
	 */
	public UIMember updateMemberPassword(final long memberID, final String prevPassword, 
			final String newPassword, final boolean askChangePassword) throws 
			NotRegisteredException, DatabaseUpdateException, WrongPasswordException{
		return this.usersController.updateMemberPassword(memberID, prevPassword, newPassword, askChangePassword);
	}

	/**
	 * @see
	 * 		ForumFacade#promoteToBeModerator(long, long)
	 */
	public void promoteToBeModerator(final long applicantID, final String username) throws NotPermittedException, 
	NotRegisteredException, DatabaseUpdateException {
		this.usersController.promoteToBeModerator(applicantID, username);
	}

	/**
	 * @see
	 * 		ForumFacade#demoteToBeMember(long, String)
	 */
	public void demoteToBeMember(final long applicantID, final String username) throws NotPermittedException, 
	NotRegisteredException, DatabaseUpdateException {
		this.usersController.demoteToBeMember(applicantID, username);
	}

	// Subject related methods

	/**
	 * @see
	 * 		ForumFacade#getSubjectByID(long)
	 */
	public UISubject getSubjectByID(long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		return this.messagesController.getSubjectByID(subjectID);
	}

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

	/**
	 * @see
	 * 		ForumFacade#updateASubject(long, long, String, String)
	 */
	public UISubject updateASubject(long userID, long subjectID, String name, 
			String description) throws NotRegisteredException, NotPermittedException,
			SubjectNotFoundException, SubjectAlreadyExistsException, DatabaseUpdateException {
		return this.messagesController.updateASubject(userID, subjectID, name, description);
	}

	/**
	 * @see
	 * 		ForumFacade#deleteASubject(long, long, long)
	 */
	public void deleteASubject(final long userID, final long fatherID, final long subjectID)
	throws NotRegisteredException, NotPermittedException, SubjectNotFoundException, DatabaseUpdateException {
		Collection<Long> tDeletedMessagesIDs = this.messagesController.deleteASubject(userID, fatherID, subjectID);
		for (long tMessageID : tDeletedMessagesIDs)
			this.searchController.removeData(tMessageID);
	}

	// Thread related methods

	/**
	 * @see
	 * 		ForumFacade#getThreadByID(long, boolean)
	 */
	public UIThread getThreadByID(long thread, final boolean shouldUpdateViews) throws ThreadNotFoundException, DatabaseRetrievalException {
		return this.messagesController.getThreadByID(thread, shouldUpdateViews);
	}

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
			this.searchController.addData(tMsg);
		}
		catch (MessageNotFoundException e) {
			// TODO Auto-generated catch block
		}
		catch (DatabaseRetrievalException e) {
			// TODO Auto-generated catch block
		}

		return toReturn;
	}

	public UIThread updateAThread(final long userID, final long threadID, final String newTopic) throws NotRegisteredException,
	NotPermittedException, ThreadNotFoundException, DatabaseUpdateException {
		return this.messagesController.updateAThread(userID, threadID, newTopic);
	}

	// Message related methods

	/**
	 * @see
	 * 		ForumFacade#searchByAuthor(long, int, int)
	 */
	public SearchHit[] searchByAuthor(long usrID, int from, int to) {
		return this.searchController.searchByAuthor(usrID, from, to);
	}

	/**
	 * @see
	 * 		ForumFacade#searchByContent(String, int, int)
	 */
	public SearchHit[] searchByContent(String phrase, int from, int to) {
		return this.searchController.searchByContent(phrase, from, to);
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
	 * 		ForumFacade#getReplies(long, boolean)
	 */
	public Collection<UIMessage> getReplies(long fatherID, boolean shouldUpdateViews)
	throws MessageNotFoundException, DatabaseRetrievalException {
		return this.messagesController.getReplies(fatherID, shouldUpdateViews);
	}

	/**
	 * @see
	 * 		ForumFacade#addNewReply(long, long, String, String)
	 */
	public UIMessage addNewReply(final long authorID, final long fatherID, final String title,
			final String content) throws NotRegisteredException, NotPermittedException, MessageNotFoundException,
			DatabaseUpdateException {
		UIMessage toReturn = this.messagesController.addNewReply(authorID, fatherID, title, content);
		this.searchController.addData(toReturn);		
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#updateAMessage(long, long, String, String)
	 */
	public UIMessage updateAMessage(final long userID, final long messageID, final String newTitle, 
			final String newContent) throws NotRegisteredException, NotPermittedException, MessageNotFoundException,
			DatabaseUpdateException {
		UIMessage toReturn = this.messagesController.updateAMessage(userID, messageID, newTitle, newContent);
		this.searchController.modifyData(toReturn);
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#deleteAMessage(long, long, long)
	 */
	public void deleteAMessage(final long userID, final long fatherID, final long messageID) throws 
	NotRegisteredException, NotPermittedException, MessageNotFoundException, DatabaseUpdateException {
		Collection<Long> tDeletedMessagesIDs = this.messagesController.deleteAMessage(userID, fatherID, messageID);
		for (long tMessageID : tDeletedMessagesIDs)
			this.searchController.removeData(tMessageID);
	}
}