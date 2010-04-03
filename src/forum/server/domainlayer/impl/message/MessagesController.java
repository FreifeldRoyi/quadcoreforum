package forum.server.domainlayer.impl.message ;

import java.util.*;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.impl.ForumDataHandler;
import forum.server.domainlayer.impl.user.Permission;
import forum.server.domainlayer.impl.user.User;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;

import forum.server.domainlayer.impl.interfaces.*;

import forum.server.persistentlayer.pipe.message.exceptions.*;
import forum.server.persistentlayer.pipe.user.exceptions.*;

/**
 * This class is a controller for all message's actions.
 *
 * Contains methods to get subjects, threads and messages location as domain objects.
 *
 * In addition, this class holds all the methods that are needed by the GUI to access messages
 * and present the forum pages: (like getting content of subjects and threads by their id-s) 
 * and all administrative methods of adding and deleting subjects, threads and messages.
 */
public class MessagesController {

	private ForumDataHandler dataHander;

	/**
	 * The class constructor.
	 * 
	 * Initializes a MessageController object that handles all the forum posts activity
	 */
	public MessagesController(ForumDataHandler dataHandler) {
		this.dataHander = dataHandler;
	}

	// Subject related methods:

	public Collection<UISubject> getSubjects(final long fatherID) throws SubjectNotFoundException, 
	DatabaseRetrievalException {
		final String tLoggerMessage = fatherID != -1 ? "Sub-subjects of a subject with id " + fatherID + " are requested to view." :
			"The forum top-level subjects are requested to view";
		SystemLogger.info(tLoggerMessage);
		final Collection<UISubject> toReturn = new Vector<UISubject>();

		if (fatherID == -1) 
			toReturn.addAll(this.dataHander.getMessagesCache().getToLevelSubjects());
		else {
			final ForumSubject tFatherSubject = this.dataHander.getMessagesCache().getSubjectByID(fatherID);
			final Collection<Long> tSubSubjectsIDs = tFatherSubject.getSubSubjects();
			for (long tSubjectID : tSubSubjectsIDs)
				toReturn.add(this.dataHander.getMessagesCache().getSubjectByID(tSubjectID));
		}
		return toReturn;
	}

	public UISubject addNewSubject(final long userID, final long fatherID, final String name, final String description) throws 
	SubjectAlreadyExistsException, SubjectNotFoundException, NotRegisteredException, NotPermittedException, DatabaseUpdateException{
		try {
			String tLoggerMessageEnd = fatherID == -1 ? "the top level of the forum." : "subject with id " + fatherID;
			Permission tPermissionToCheck = fatherID == -1 ? Permission.ADD_SUBJECT : Permission.ADD_SUB_SUBJECT;

			SystemLogger.info("A user with id " + userID + " requests to add a new subject named " + name + " to " + 
					tLoggerMessageEnd + ".");
			final User tApplicant = this.dataHander.getUsersCache().getUserByID(userID);

			if (tApplicant.isAllowed(tPermissionToCheck)) {
				SystemLogger.info("permission granted for user " + userID + ".");
				if (fatherID == -1) {
					ForumSubject toReturn = this.dataHander.getMessagesCache().createNewSubject(name, description, true);
					SystemLogger.info("A subject named " + name + " was added to the top level of the forum");
					return toReturn;
				}	
				else {	
					ForumSubject tFatherSubject = this.dataHander.getMessagesCache().getSubjectByID(fatherID);
					ForumSubject tNewSubject = this.dataHander.getMessagesCache().createNewSubject(name, description, false);
					tFatherSubject.addSubSubject(tNewSubject.getId());
					this.dataHander.getMessagesCache().updateInDatabase(tFatherSubject);
					SystemLogger.info("A subject named " + name + " was added as a sub-subject of a subject named " + 
							tFatherSubject.getName());
					return tNewSubject;
				}
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, tPermissionToCheck);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Thread related methods:

	public Collection<UIThread> getThreads(final long fatherID) throws SubjectNotFoundException, DatabaseRetrievalException {
		SystemLogger.info("Threads of a subject with id " + fatherID + " are requested to view.");
		final ForumSubject tFatherSubject = this.dataHander.getMessagesCache().getSubjectByID(fatherID);
		final Collection<Long> tThreadsIDs = tFatherSubject.getThreads();
		final Collection<UIThread> toReturn = new Vector<UIThread>();
		for (long tThreadID : tThreadsIDs) {
			try {
				toReturn.add(this.dataHander.getMessagesCache().getThreadByID(tThreadID));
			}
			catch (ThreadNotFoundException e) {
				continue; // do nothing
			}
		}
		return toReturn;
	}

	public UIThread openNewThread(final long userID, final String topic, final long subjectID, 
			final String title, final String content) throws NotRegisteredException, SubjectNotFoundException,
			NotPermittedException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to open a new thread under the subject with id " +
					subjectID + ".");
			final User tApplicant = this.dataHander.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(Permission.OPEN_THREAD)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumSubject tFatherSubject = this.dataHander.getMessagesCache().getSubjectByID(subjectID);

				final ForumMessage tNewMessage = this.dataHander.getMessagesCache().createNewMessage(userID, title, content);
				final ForumThread tNewThread = this.dataHander.getMessagesCache().openNewThread(topic, tNewMessage.getID());		
				tFatherSubject.addThread(tNewThread.getId());			
				this.dataHander.getMessagesCache().updateInDatabase(tFatherSubject);
				return tNewThread;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.OPEN_THREAD);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Message related methods:

	public Collection<UIMessage> getReplies(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		SystemLogger.info("Replies of a message with id " + messageID + " are requested to view.");
		final ForumMessage tFatherMessage = this.dataHander.getMessagesCache().getMessageByID(messageID);
		final Collection<Long> tRepliesIDs = tFatherMessage.getReplies();
		final Collection<UIMessage> toReturn = new Vector<UIMessage>();
		for (long tReplyID : tRepliesIDs) {
			try {
				toReturn.add(this.dataHander.getMessagesCache().getMessageByID(tReplyID));
			}
			catch (MessageNotFoundException e) {
				continue; // do nothing
			}
		}
		return toReturn;
	}	

	public UIMessage addNewReply(final long userID, final long fatherID, final String title,
			final String content) throws NotRegisteredException, MessageNotFoundException, 
			NotPermittedException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to add a new reply to a message with id " +
					fatherID + ".");
			final User tApplicant = this.dataHander.getUsersCache().getUserByID(userID);
			final ForumMessage tFatherMessage = this.dataHander.getMessagesCache().getMessageByID(fatherID);
			if (tApplicant.isAllowed(Permission.REPLY_TO_MESSAGE)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumMessage tNewMessage = this.dataHander.getMessagesCache().createNewMessage(userID, title, content);
				// adds the new reply to the replied message
				tFatherMessage.addReply(tNewMessage.getID());
				this.dataHander.getMessagesCache().updateInDatabase(tFatherMessage);
				SystemLogger.info("A new reply was successfuly added to message " + fatherID + " by a user " + userID + ".");
				return tNewMessage;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.REPLY_TO_MESSAGE);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public UIMessage updateAMessage(final long userID, final long messageID, final String newTitle, 
			final String newContent) throws NotRegisteredException, MessageNotFoundException, NotPermittedException, 
			DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to edit a message with id " +
					messageID + ".");
			final User tApplicant = this.dataHander.getUsersCache().getUserByID(userID);
			final ForumMessage tMessageToEdit = this.dataHander.getMessagesCache().getMessageByID(messageID);
			if (tApplicant.isAllowed(Permission.EDIT_MESSAGE) && tMessageToEdit.getAuthorID() == tApplicant.getId()) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				tMessageToEdit.updateMe(newTitle, newContent);
				this.dataHander.getMessagesCache().updateInDatabase(tMessageToEdit);
				return tMessageToEdit;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.EDIT_MESSAGE);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public void deleteThread(final long userID, final long fatherID, final long threadID) throws NotRegisteredException, 
	SubjectNotFoundException, ThreadNotFoundException, NotPermittedException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to delete ther thread with id " +
					threadID + " from a subject with id " + fatherID);
			final User tApplicant = this.dataHander.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(Permission.DELETE_MESSAGE)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumThread tThreadToDelete = this.dataHander.getMessagesCache().getThreadByID(threadID);
				final ForumSubject tFatherSubject = this.dataHander.getMessagesCache().getSubjectByID(fatherID);

				// delete the thread from the desired subject
				tFatherSubject.deleteThread(threadID);
				this.dataHander.getMessagesCache().updateInDatabase(tFatherSubject);

				this.dataHander.getMessagesCache().deleteATread(tThreadToDelete.getId());

				SystemLogger.info("A thread with id " + threadID + " was deleted successfuly from the subject " +
						fatherID + " by a user " + userID);
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.DELETE_THREAD);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}

	}

	public void deleteAMessage(final long userID, final long fatherID, final long messageID) throws NotRegisteredException, 
	MessageNotFoundException, NotPermittedException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to delete a message with id " +
					messageID + " from being a reply of " + fatherID + ".");
			final User tApplicant = this.dataHander.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(Permission.DELETE_MESSAGE)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumMessage tMessageToDelete = this.dataHander.getMessagesCache().getMessageByID(messageID);
				final ForumMessage tFatherMessage = this.dataHander.getMessagesCache().getMessageByID(fatherID);
				tFatherMessage.deleteReply(tMessageToDelete.getID());
				this.dataHander.getMessagesCache().updateInDatabase(tFatherMessage);
				this.dataHander.getMessagesCache().deleteAMessage(messageID);
				SystemLogger.info("A message with id " + messageID + " was deleted successfuly from the message " +
						fatherID + " by a user " + userID);
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.DELETE_MESSAGE);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
}