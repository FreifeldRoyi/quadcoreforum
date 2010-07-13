/**
 * This class is a controller for all the forum content actions.
 *
 * Contains methods to get subjects, threads and messages location as domain objects.
 *
 * In addition, this class holds all the methods that are needed by the GUI to access messages
 * and present the forum pages (like getting content of subjects and threads by their id-s) 
 * and all other administrative methods of adding and deleting subjects, threads and messages.
 */
package forum.server.domainlayer.message ;

import java.util.*;

import forum.server.domainlayer.*;
import forum.server.domainlayer.interfaces.*;
import forum.server.domainlayer.user.*;

import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;


import forum.server.updatedpersistentlayer.pipe.message.exceptions.*;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;

public class MessagesController {

	private ForumDataHandler dataHandler;

	/**
	 * The class constructor
	 * 
	 * Initializes an instance of messages controller, that handles all the forum content activity
	 */
	public MessagesController(ForumDataHandler dataHandler) {
		this.dataHandler = dataHandler;
	}

	// Subject related methods

	/**
	 * @see
	 * 		ForumFacade#getSubjectByID(long)
	 */
	public UISubject getSubjectByID(long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		return this.dataHandler.getMessagesCache().getSubjectByID(subjectID);
	}

	/**
	 * @see
	 * 		ForumFacade#getSubjects(long)
	 */
	public Collection<UISubject> getSubjects(final long fatherID) throws SubjectNotFoundException, 
	DatabaseRetrievalException {
		final String tLoggerMessage = fatherID != -1 ? "Sub-subjects of a subject with id " + fatherID + " are requested to view." :
			"The forum top-level subjects are requested to view";
		SystemLogger.info(tLoggerMessage);
		final Collection<UISubject> toReturn = new Vector<UISubject>();

		if (fatherID == -1)
			toReturn.addAll(this.dataHandler.getMessagesCache().getTopLevelSubjects());
		else {
			final ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(fatherID);
			final Collection<Long> tSubSubjectsIDs = tFatherSubject.getSubSubjects();
			for (long tSubjectID : tSubSubjectsIDs)
				toReturn.add(this.dataHandler.getMessagesCache().getSubjectByID(tSubjectID));
		}
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#addNewSubject(long, long, String, String)
	 */
	public UISubject addNewSubject(final long userID, final long fatherID, final String name, final String description) throws 
	NotRegisteredException, NotPermittedException, SubjectAlreadyExistsException, SubjectNotFoundException, DatabaseUpdateException{
		try {
			String tLoggerMessageEnd = fatherID == -1 ? "the top level of the forum." : "subject with id " + fatherID;
			Permission tPermissionToCheck = fatherID == -1 ? Permission.ADD_SUBJECT : Permission.ADD_SUB_SUBJECT;

			SystemLogger.info("A user with id " + userID + " requests to add a new subject named " + name + " to " + 
					tLoggerMessageEnd + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(tPermissionToCheck)) {
				SystemLogger.info("permission granted for user " + userID + ".");
				// checks that there doesn't exist a subject whose id is same as the given one, in the required level
				Collection<UISubject> tRequiredLevelSubjects = this.getSubjects(fatherID);
				for (UISubject tCurrentSubject : tRequiredLevelSubjects)
					if (tCurrentSubject.getName().equals(name))
						throw new SubjectAlreadyExistsException(name);
				// adds the new subject to the forum
				if (fatherID == -1) {
					ForumSubject toReturn = this.dataHandler.getMessagesCache().createNewSubject(name, description, fatherID);
					SystemLogger.info("A subject named " + name + " was added to the top level of the forum");
					return toReturn;
				}	
				else {
					ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(fatherID);
					ForumSubject tNewSubject = this.dataHandler.getMessagesCache().createNewSubject(name, description, fatherID);

					tFatherSubject.addSubSubject(tNewSubject.getID());
					tFatherSubject.incDeepNumOfSubSubjects();
					this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);


					// Update of the sub-subjects numbers 
					while (tFatherSubject.getFatherID() != -1) {
						try {
							long tTopLevelFatherSubject = tFatherSubject.getFatherID();
							tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(tTopLevelFatherSubject);
							tFatherSubject.incDeepNumOfSubSubjects();
							this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);
						}
						catch (Exception e) {
							SystemLogger.warning("An exception was thrown while updating subjects number after adding the " +
									"subject " + tNewSubject.getID());
							break;
						}
					}
					// End of update of the sub-subjects numbers 

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

	/**
	 * @see
	 * 		ForumFacade#updateASubject(long, long, String, String)
	 */
	public UISubject updateASubject(long userID, long subjectID, String name, 
			String description) throws NotRegisteredException, NotPermittedException,
			SubjectNotFoundException, SubjectAlreadyExistsException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to edit a subject with id " +
					subjectID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			final ForumSubject tSubjectToEdit = this.dataHandler.getMessagesCache().getSubjectByID(subjectID);
			if (tApplicant.isAllowed(Permission.EDIT_SUBJECT)) {
				SystemLogger.info("Permission granted for user " + userID + ".");

				// Checks if there already exists a subject with the given name at the same level
				SystemLogger.info("Checks if the new name for the subject " + subjectID + " is unique.");

				Collection<UISubject> tRequiredLevelSubjects = this.getSubjects(tSubjectToEdit.getFatherID());
				for (UISubject tCurrentSubject : tRequiredLevelSubjects)
					if (tCurrentSubject.getName().equals(name) && tCurrentSubject.getID() != tSubjectToEdit.getID())
						throw new SubjectAlreadyExistsException(name);

				tSubjectToEdit.updateMe(name, description);
				this.dataHandler.getMessagesCache().updateInDatabase(tSubjectToEdit);
				SystemLogger.info("The subject with id " + subjectID + " was updated successfully.");
				return tSubjectToEdit;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.EDIT_SUBJECT);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		ForumFacade#deleteASubject(long, long, long)
	 */
	public Collection<Long> deleteASubject(final long userID, final long fatherID, final long subjectID)
	throws NotRegisteredException, NotPermittedException, SubjectNotFoundException, DatabaseUpdateException {

		try {

			SystemLogger.info("A user with id " + userID + " requests to delete a subject with id " +
					subjectID + ".");

			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);

			if (tApplicant.isAllowed(Permission.DELETE_SUBJECT)) {
				Collection<Long> toReturn = new HashSet<Long>();
				SystemLogger.info("Permission granted for user " + userID + ".");

				final ForumSubject tFatherSubject = fatherID != -1? this.dataHandler.getMessagesCache().getSubjectByID(fatherID) : null;

				final ForumSubject tSubjectToDelete = this.dataHandler.getMessagesCache().getSubjectByID(subjectID);

				Collection<Long> tThreadsToDelete = new HashSet<Long>(tSubjectToDelete.getThreads());

				for (long tThreadToDeleteID : tThreadsToDelete) {
					try {
						toReturn.addAll(this.deleteAMessage(userID, -1, tThreadToDeleteID));
					}
					catch (MessageNotFoundException e) {
						SystemLogger.warning("The message wasn't found while deleting the subject "  +  subjectID + ". Exception: " 
								+ e.getMessage());
					}
				}				
				
				this.dataHandler.getMessagesCache().deleteASubject(subjectID);

				if (tFatherSubject != null) {
					tFatherSubject.deleteSubSubject(subjectID);
					tFatherSubject.decDeepNumOfSubSubjectsBy(tSubjectToDelete.getDeepNumOfSubSubjects() + 1);
					this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);
				}

				// Update number of subjects in top level subjects
				long tSubjectToUpdateID = tFatherSubject.getFatherID();
				while (tSubjectToUpdateID != -1) {
					ForumSubject tSubjectToUpdate = 
						this.dataHandler.getMessagesCache().getSubjectByID(tSubjectToUpdateID);
					tSubjectToUpdate.decDeepNumOfSubSubjectsBy(tSubjectToDelete.getDeepNumOfSubSubjects() + 1);
					this.dataHandler.getMessagesCache().updateInDatabase(tSubjectToUpdate);
					tSubjectToUpdateID = tSubjectToUpdate.getFatherID();
				}


				SystemLogger.info("A subject with id " + subjectID + " was deleted successfuly by a user " + userID);

				return toReturn;
			}
			else {
				SystemLogger.info("unpermitted operation for user " + userID + ".");
				throw new NotPermittedException(userID, Permission.DELETE_SUBJECT);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}


	// Thread related methods

	/**
	 * @see
	 * 		ForumFacade#getThreadByID(long, boolean)
	 */
	public UIThread getThreadByID(long thread, final boolean shouldUpdateViews) throws ThreadNotFoundException, DatabaseRetrievalException {
		ForumThread toReturn = this.dataHandler.getMessagesCache().getThreadByID(thread);
		if (shouldUpdateViews) {
			toReturn.incNumOfViews();
			try {
				this.dataHandler.getMessagesCache().updateInDatabase(toReturn);
			}
			catch (Exception e) {
				SystemLogger.warning("Can't update the number of views of thread " + thread + " in the database.");
			}
		}
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#getThreads(long)
	 */
	public Collection<UIThread> getThreads(final long fatherID) throws SubjectNotFoundException, DatabaseRetrievalException {
		SystemLogger.info("Threads of a subject with id " + fatherID + " are requested to view.");
		final ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(fatherID);
		final Collection<Long> tThreadsIDs = tFatherSubject.getThreads();
		final Collection<UIThread> toReturn = new Vector<UIThread>();
		for (long tThreadID : tThreadsIDs) {
			try {
				toReturn.add(this.dataHandler.getMessagesCache().getThreadByID(tThreadID));
			}
			catch (ThreadNotFoundException e) {
				continue; // do nothing
			}
		}
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#openNewThread(long, long, String, String, String)
	 */
	public UIThread openNewThread(final long userID, final String topic, final long subjectID, 
			final String title, final String content) throws NotRegisteredException, NotPermittedException, 
			SubjectNotFoundException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to open a new thread under the subject with id " +
					subjectID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(Permission.OPEN_THREAD)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(subjectID);
				final ForumMessage tNewMessage = this.dataHandler.getMessagesCache().createNewMessage(userID, title, content, -1);

				final ForumThread tNewThread = this.dataHandler.getMessagesCache().openNewThread(topic, 
						tNewMessage.getMessageID(), tFatherSubject.getID());

				tFatherSubject.addThread(tNewThread.getID());
				tFatherSubject.incDeepNumOfMessages();
				this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);

				// Update of the messages numbers 
				while (tFatherSubject.getFatherID() != -1) {
					try {
						long tTopLevelFatherSubject = tFatherSubject.getFatherID();
						tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(tTopLevelFatherSubject);
						tFatherSubject.incDeepNumOfMessages();
						this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);
					}
					catch (Exception e) {
						SystemLogger.warning("An exception was thrown while updating messages number after openning the " +
								"thread " + tNewThread.getID());
						break;
					}
				}
				// End of update of the messages numbers 

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

	private void deleteAThread(final long userID, final long fatherID, final long threadID) throws NotRegisteredException, 
	NotPermittedException, SubjectNotFoundException, ThreadNotFoundException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to delete ther thread with id " +
					threadID + " from a subject with id " + fatherID);
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			if (tApplicant.isAllowed(Permission.DELETE_MESSAGE)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumThread tThreadToDelete = this.dataHandler.getMessagesCache().getThreadByID(threadID);
				final ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(fatherID);

				// delete the thread from the desired subject
				tFatherSubject.deleteThread(threadID);

				this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);

				this.dataHandler.getMessagesCache().deleteATread(tThreadToDelete.getID());

				System.out.println("After delete id = " + tFatherSubject.getID());
				System.out.println("After delete threads = " + tFatherSubject.getThreads().toString());



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

	public UIThread updateAThread(final long userID, final long threadID, final String newTopic) throws NotRegisteredException,
	NotPermittedException, ThreadNotFoundException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to edit a thread with id " +
					threadID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			final ForumThread tThreadToEdit = this.dataHandler.getMessagesCache().getThreadByID(threadID);
			if (tApplicant.isAllowed(Permission.EDIT_SUBJECT)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				tThreadToEdit.updateMe(newTopic);
				this.dataHandler.getMessagesCache().updateInDatabase(tThreadToEdit);
				return tThreadToEdit;
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

	// Message related methods:

	/**
	 * @see
	 * 		ForumFacade#getMessageByID(long)
	 */
	public UIMessage getMessageByID(final long messageID)
	throws MessageNotFoundException, DatabaseRetrievalException {
		return this.dataHandler.getMessagesCache().getMessageByID(messageID);
	}

	/**
	 * @see 
	 * 		ForumFacade#getReplies(long, boolean)
	 */
	public Collection<UIMessage> getReplies(final long messageID, 
			boolean shouldUpdateViews) throws MessageNotFoundException, DatabaseRetrievalException {
		SystemLogger.info("Replies of a message with id " + messageID + " are requested to view.");
		final ForumMessage tFatherMessage = this.dataHandler.getMessagesCache().getMessageByID(messageID);

		// Updates the number of views in case the message is a thread view request
		if (tFatherMessage.getFatherID() == -1 && shouldUpdateViews) { // the message is the first message of a thread
			try {
				ForumThread tThreadToUpdate = this.dataHandler.getMessagesCache().getThreadByID(tFatherMessage.getMessageID());
				tThreadToUpdate.incNumOfViews();
				this.dataHandler.getMessagesCache().updateInDatabase(tThreadToUpdate);
			}
			catch (Exception e) {
				SystemLogger.warning("Failed to update the number of views of the thread " + tFatherMessage.getMessageID());
			}
		}
		// End of views number update

		final Collection<Long> tRepliesIDs = tFatherMessage.getReplies();
		final Collection<UIMessage> toReturn = new Vector<UIMessage>();
		for (long tReplyID : tRepliesIDs) {
			try {
				toReturn.add(this.dataHandler.getMessagesCache().getMessageByID(tReplyID));
			}
			catch (MessageNotFoundException e) {
				continue; // do nothing
			}
		}
		return toReturn;
	}	

	/**
	 * @see
	 * 		ForumFacade#addNewReply(long, long, String, String)
	 */
	public UIMessage addNewReply(final long userID, final long fatherID, final String title,
			final String content) throws NotRegisteredException, NotPermittedException,
			MessageNotFoundException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to add a new reply to a message with id " +
					fatherID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			ForumMessage tFatherMessage = this.dataHandler.getMessagesCache().getMessageByID(fatherID);
			if (tApplicant.isAllowed(Permission.REPLY_TO_MESSAGE)) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				final ForumMessage tNewMessage = this.dataHandler.getMessagesCache().createNewMessage(userID, title, content, fatherID);
				// adds the new reply to the replied message
				tFatherMessage.addReply(tNewMessage.getMessageID());				
				this.dataHandler.getMessagesCache().updateInDatabase(tFatherMessage);

				// Updates the number of responses of the thread to which the new message belongs
				while (tFatherMessage.getFatherID() != -1) {
					try {
						tFatherMessage = this.dataHandler.getMessagesCache().getMessageByID(tFatherMessage.getFatherID());
					}
					catch (Exception e) {
						SystemLogger.warning("An exception was thrown while trying to update the number of responses of " +
								"a thread after adding the reply " + tNewMessage.getMessageID());
						break;
					}
				}
				if (tFatherMessage != null && (tFatherMessage.getFatherID() == -1)) {
					try {
						ForumThread tThreadToUpdate = this.dataHandler.getMessagesCache().getThreadByID(tFatherMessage.getMessageID());
						tThreadToUpdate.incNumOfResponses();
						this.dataHandler.getMessagesCache().updateInDatabase(tThreadToUpdate);

						long tTopLevelFatherSubject = tThreadToUpdate.getFatherID();

						// Update of the messages numbers 
						while (tTopLevelFatherSubject != -1) {
							try {
								ForumSubject tSubjectToUpdate = this.dataHandler.getMessagesCache().getSubjectByID(tTopLevelFatherSubject);
								tSubjectToUpdate.incDeepNumOfMessages();
								this.dataHandler.getMessagesCache().updateInDatabase(tSubjectToUpdate);
								tTopLevelFatherSubject = tSubjectToUpdate.getFatherID();

							}
							catch (Exception e) {
								SystemLogger.warning("An exception was thrown while updating messages number of subjects" +
										" after adding a reply to the thread " + tThreadToUpdate.getID());
								break;
							}
						}
						// End of update of the messages numbers 


					}
					catch (Exception e) {
						SystemLogger.warning("An exception was thrown while trying to update the number of responses of " +
								"a thread " + tFatherMessage.getMessageID() + " after adding the reply " + tNewMessage.getMessageID());
					}
				}



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

	/**
	 * @see
	 * 		ForumFacade#updateAMessage(long, long, String, String)
	 */
	public UIMessage updateAMessage(final long userID, final long messageID, final String newTitle, 
			final String newContent) throws NotRegisteredException, NotPermittedException, MessageNotFoundException,
			DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + userID + " requests to edit a message with id " +
					messageID + ".");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);
			final ForumMessage tMessageToEdit = this.dataHandler.getMessagesCache().getMessageByID(messageID);
			if (tApplicant.isAllowed(Permission.DELETE_MESSAGE) ||
					(tApplicant.isAllowed(Permission.EDIT_MESSAGE) && tMessageToEdit.getAuthorID() == tApplicant.getID())) {
				SystemLogger.info("Permission granted for user " + userID + ".");
				tMessageToEdit.updateMe(newTitle, newContent);
				this.dataHandler.getMessagesCache().updateInDatabase(tMessageToEdit);
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

	/**
	 * @see
	 * 		ForumFacade#deleteAMessage(long, long, long)
	 */
	public Collection<Long> deleteAMessage(final long userID, final long fatherID, final long messageID) throws NotRegisteredException, 
	MessageNotFoundException, NotPermittedException, DatabaseUpdateException {
		try {

			if (fatherID == -1)
				SystemLogger.info("A user with id " + userID + " requests to delete a thread with id " +
						messageID + ".");
			else
				SystemLogger.info("A user with id " + userID + " requests to delete a message with id " +
						messageID + " from being a reply of " + fatherID + ".");

			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(userID);

			if ((fatherID != -1 && tApplicant.isAllowed(Permission.DELETE_MESSAGE)) 
					|| 
					(fatherID == -1 && tApplicant.isAllowed(Permission.DELETE_THREAD))) {

				Collection<Long> toReturn = new HashSet<Long>();
				SystemLogger.info("Permission granted for user " + userID + ".");


				final ForumMessage tMessageToDelete = this.dataHandler.getMessagesCache().getMessageByID(messageID);


				if (fatherID != -1) { // not root message
					ForumMessage tFatherMessage = this.dataHandler.getMessagesCache().getMessageByID(fatherID);
					tFatherMessage.deleteReply(tMessageToDelete.getMessageID());
					this.dataHandler.getMessagesCache().updateInDatabase(tFatherMessage);
					Collection<Long> tDeletedMessagesIDs = this.dataHandler.getMessagesCache().deleteAMessage(messageID);
					SystemLogger.info("A message with id " + messageID + " was deleted successfuly from the message " +
							fatherID + " by a user " + userID);
					toReturn.addAll(tDeletedMessagesIDs);

					try {	
						while (tFatherMessage.getFatherID() != -1)
							tFatherMessage = this.dataHandler.getMessagesCache().getMessageByID(tFatherMessage.getFatherID());
						if (tFatherMessage != null) {

							// Update number of responses of the thread
							ForumThread tThreadToUpdate = this.dataHandler.getMessagesCache().getThreadByID(tFatherMessage.getMessageID());
							tThreadToUpdate.decNumOfResponsesBy(tDeletedMessagesIDs.size());
							this.dataHandler.getMessagesCache().updateInDatabase(tThreadToUpdate);

							// Update number of messages in top level subjects
							long tSubjectToUpdateID = tThreadToUpdate.getFatherID();
							while (tSubjectToUpdateID != -1) {
								ForumSubject tSubjectToUpdate = 
									this.dataHandler.getMessagesCache().getSubjectByID(tSubjectToUpdateID);
								tSubjectToUpdate.decDeepNumOfMessagesBy(tDeletedMessagesIDs.size());
								this.dataHandler.getMessagesCache().updateInDatabase(tSubjectToUpdate);
								tSubjectToUpdateID = tSubjectToUpdate.getFatherID();
							}
						}
					}
					catch (Exception e) {
						SystemLogger.warning("An exception was thrown while updating responses number after deleting the " +
								"message " + tMessageToDelete.getMessageID());
					}
				}
				else {
					// if the message is a root message its id is same as its thread id
					try {

						final ForumThread tThreadToDelete = this.dataHandler.getMessagesCache().getThreadByID(messageID);
						final ForumSubject tFatherSubject = this.dataHandler.getMessagesCache().getSubjectByID(tThreadToDelete.getFatherID());

						// delete the thread from the desired subject
						tFatherSubject.deleteThread(tMessageToDelete.getMessageID());
						tFatherSubject.decDeepNumOfMessagesBy(tThreadToDelete.getNumOfResponses() + 1);
						this.dataHandler.getMessagesCache().updateInDatabase(tFatherSubject);

						try {
							// Update number of messages in top level subjects
							long tSubjectToUpdateID = tFatherSubject.getFatherID();
							while (tSubjectToUpdateID != -1) {
								ForumSubject tSubjectToUpdate = 
									this.dataHandler.getMessagesCache().getSubjectByID(tSubjectToUpdateID);
								tSubjectToUpdate.decDeepNumOfMessagesBy(tThreadToDelete.getNumOfResponses() + 1);
								this.dataHandler.getMessagesCache().updateInDatabase(tSubjectToUpdate);
								tSubjectToUpdateID = tSubjectToUpdate.getFatherID();
							}
						}
						catch (Exception e) {
							SystemLogger.warning("An exception was thrown while updating responses number after deleting the " +
									"message " + tMessageToDelete.getMessageID());
						}


						Collection<Long> tDeletedMessagesIDs = this.dataHandler.getMessagesCache().deleteATread(messageID);

						SystemLogger.info("The thread with id " + messageID + " was deleted successfuly from the subject " +
								tFatherSubject.getID() + " by a user " + userID);

						toReturn.addAll(tDeletedMessagesIDs);

					}
					catch (ThreadNotFoundException e) {
						throw new MessageNotFoundException(messageID);
					}
					catch (SubjectNotFoundException e) {
						throw new MessageNotFoundException(messageID);
					}
				}				

				return toReturn;
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