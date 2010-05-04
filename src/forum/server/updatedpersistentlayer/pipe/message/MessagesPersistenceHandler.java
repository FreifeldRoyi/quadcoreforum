package forum.server.updatedpersistentlayer.pipe.message;

import java.util.Collection;
import java.util.Vector;

import forum.server.persistentlayer.*;
import forum.server.persistentlayer.pipe.*;
import forum.server.persistentlayer.pipe.message.exceptions.*;
import forum.server.domainlayer.message.*;

/**
 * This class is responsible of performing the operations of reading from and writing to the database
 * all the data which refers to the forum content, which means the forum subjects, threads and messages
 */

/**
 * 
 * @author Sepetnitsky Vitali
 *
 */

public class MessagesPersistenceHandler {

	// subject related methods
	
	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeSubjectID()
	 */
	public long getFirstFreeSubjectID(ForumType forum) {
		long toReturn = -1;
		for (SubjectType tCurrentSubject : forum.getSubjects())
			if (tCurrentSubject.getSubjectID() > toReturn)
				toReturn = tCurrentSubject.getSubjectID();
		return ++toReturn;
	}

	/**
	 * @param data
	 * 		The forum type from which the data should be read
	 * 
	 * @see
	 * 		PersistenceDataHandler#getTopLevelSubjects()
	 */
	public Collection<ForumSubject> getTopLevelSubjects(ForumType data) {
		Collection<ForumSubject> toReturn = new Vector<ForumSubject>();
		for (SubjectType tCurrentSubjectType : data.getSubjects())
			if (tCurrentSubjectType.isIsToLevel() && tCurrentSubjectType.getSubjectID() != 0)
				toReturn.add(PersistentToDomainConverter.convertSubjectTypeToForumSubject(tCurrentSubjectType));
		return toReturn;
	}

	/**
	 * @param data
	 * 		The forum type from which the data should be read
	 * 
	 * @see
	 * 		PersistenceDataHandler#getSubjectByID(long)
	 */
	public ForumSubject getSubjectByID(ForumType data, long subjectID) throws SubjectNotFoundException {
		SubjectType tSubjectType = this.getSubjectTypeByID(data, subjectID);		
		return PersistentToDomainConverter.convertSubjectTypeToForumSubject(tSubjectType);
	}

	/**
	 * Performs a lookup in the database and returns a {@link SubjectType} object, whose id equals to the given one
	 * 
	 * @param data
	 * 		The forum data from in which the required subject should be found
	 * @param subjectID
	 * 		The id of the subject which should be found
	 * 
	 * @return
	 * 		The found subject
	 * 
	 * @throws SubjectNotFoundException
	 * 		In case a subject with the given id doesn't exist in the database
	 */
	private SubjectType getSubjectTypeByID(ForumType data, long subjectID) throws SubjectNotFoundException {
		for (SubjectType tCurrentSubject : data.getSubjects())
			if (tCurrentSubject.getSubjectID() == subjectID)
				return tCurrentSubject;
		throw new SubjectNotFoundException(subjectID);
	}

	/**
	 * @param data
	 * 		The forum data to which the new created subject should be added
	 * 
	 * @see
	 * 		PersistenceDataHandler#addNewSubject(long, String, String, boolean)
	 */
	public void addNewSubject(ForumType data, long subjectID, String name, String description, 
			boolean isTopLevel) {
			SubjectType tNewSubject = ExtendedObjectFactory.createSubject(subjectID, name, description, isTopLevel);
			data.getSubjects().add(tNewSubject);
	}

	/**
	 * @param data
	 * 		The forum data in which the required subject should be updated
	 * 
	 * @see
	 * 		PersistenceDataHandler#updateSubject(long, Collection, Collection)
	 */
	public void updateSubject(ForumType data, long id, Collection<Long> subSubjects,
			Collection<Long> threads) throws SubjectNotFoundException {
			SubjectType tSubjectToUpdate = this.getSubjectTypeByID(data, id);
			// update the sub-subjects
			tSubjectToUpdate.getSubSubjectsIDs().clear();
			tSubjectToUpdate.getSubSubjectsIDs().addAll(subSubjects);
			// update the threads
			tSubjectToUpdate.getThreadsIDs().clear();
			tSubjectToUpdate.getThreadsIDs().addAll(threads);
	}

	// Thread related methods

	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeThreadID()
	 */
	public long getFirstFreeThreadID(ForumType forum) {
		long toReturn = -1;
		for (ThreadType tCurrentThread : forum.getThreads())
			if (tCurrentThread.getThreadID() > toReturn)
				toReturn = tCurrentThread.getThreadID();
		return ++toReturn;
	}

	/**
	 * @param data
	 * 		The forum data from which the required thread should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getThreadByID(long)
	 */
	public ForumThread getThreadByID(ForumType data, long threadID) throws ThreadNotFoundException {
		ThreadType tThreadType = this.getThreadTypeByID(data, threadID);		
		return PersistentToDomainConverter.convertThreadTypeToForumThread(tThreadType);
	}

	/**
	 * Performs a lookup in the database and returns a {@link ThreadType} object, whose id equals to the given one
	 * 
	 * @param data
	 * 		The forum data from in which the required thread should be found
	 * @param threadID
	 * 		The id of the thread which should be found
	 * 
	 * @return
	 * 		The found thread
	 * 
	 * @throws ThreadNotFoundException
	 * 		In case a thread with the given id doesn't exist in the database
	 */
	private ThreadType getThreadTypeByID(ForumType forum, long threadID) throws ThreadNotFoundException {
		for (ThreadType tCurrentThread : forum.getThreads())
			if (tCurrentThread.getThreadID() == threadID)
				return tCurrentThread;
		throw new ThreadNotFoundException(threadID);		
	}

	/**
	 * @param data
	 * 		The forum data in which the required thread should be opened
	 * 
	 * @see
	 * 		PersistenceDataHandler#openNewThread(long, String, long)
	 */
	public void openNewThread(ForumType data, long threadID, String topic, long rootID) {
			ThreadType tNewThread = ExtendedObjectFactory.createThreadType(threadID, topic, rootID);
			data.getThreads().add(tNewThread);
	}

	/**
	 * @param data
	 * 		The forum data from which the required thread should be deleted
	 * 
	 * @see
	 * 		PersistenceDataHandler#deleteAThread(long)
	 */
	public Collection<Long> deleteAThread(ForumType data, long threadID) throws ThreadNotFoundException {
			ThreadType tThreadToDelete = this.getThreadTypeByID(data, threadID);
			// remove the thread
			data.getThreads().remove(tThreadToDelete);
			try {
				// remove the thread's messages recursively
				return this.deleteAMessage(data, tThreadToDelete.getStartMessageID());
			}
			catch (MessageNotFoundException e) {
				return new Vector<Long>();
			}
	}

	// Message related methods

	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeMessageID()
	 */
	public long getFirstFreeMessageID(ForumType forum) {
		long toReturn = -1;
		for (MessageType tCurrentMessage : forum.getMessages())
			if (tCurrentMessage.getMessageID() > toReturn)
				toReturn = tCurrentMessage.getMessageID();
		return ++toReturn;
	}

	/**
	 * @param data
	 * 		The forum data from which the required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getAllMessages()
	 */
	public Collection<ForumMessage> getAllMessages(ForumType data) {
		Collection<ForumMessage> toReturn = new Vector<ForumMessage>();
		for (MessageType tCurrentMessage : data.getMessages())
			toReturn.add(
					PersistentToDomainConverter.convertMessageTypeToForumMessage(tCurrentMessage));
		return toReturn;
	}
	
	/**
	 * @param data
	 * 		The forum data from which the required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#deleteAMessage(long)
	 */
	public ForumMessage getMessageByID(ForumType data, long messageID) throws MessageNotFoundException {
		MessageType tMessageType = this.getMessageTypeByID(data, messageID);
		return PersistentToDomainConverter.convertMessageTypeToForumMessage(tMessageType);	
	}

	/**
	 * Performs a lookup in the database and returns a {@link MessageType} object, whose id equals to the given one
	 * 
	 * @param data
	 * 		The forum data from in which the required message should be found
	 * @param messageID
	 * 		The id of the message which should be found
	 * 
	 * @return
	 * 		The found message
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with the given id doesn't exist in the database
	 */
	private MessageType getMessageTypeByID(ForumType forum, long messageID) throws MessageNotFoundException {
		for (MessageType tCurrentMessage : forum.getMessages())
			if (tCurrentMessage.getMessageID() == messageID)
				return tCurrentMessage;
		throw new MessageNotFoundException(messageID);
	}	

	/**
	 * @param data
	 * 		The forum data to which the new message should be added
	 * 
	 * @see
	 * 		PersistenceDataHandler#addNewMessage(long, long, String, String)
	 */
	public void addNewMessage(ForumType data, long messageID, long userID, String title, String content) {
			MessageType tNewMessage = ExtendedObjectFactory.createMessageType(messageID, userID, title, content);
			data.getMessages().add(tNewMessage);
	}

	/**
	 * @param data
	 * 		The forum data from which the required thread should be deleted
	 * 
	 * @see
	 * 		PersistenceDataHandler#deleteAMessage(long)
	 */
	public void updateMessage(ForumType data, long messageID, String newTitle, String newContent, Collection<Long> replies) throws MessageNotFoundException {
			MessageType tMsgToEdit = this.getMessageTypeByID(data, messageID);
			tMsgToEdit.setTitle(newTitle);
			tMsgToEdit.setContent(newContent);
			tMsgToEdit.getRepliesIDs().clear();
			tMsgToEdit.getRepliesIDs().addAll(replies);
	}

	/**
	 * @param data
	 * 		The forum data from which required message should be deleted
	 * 
	 * @see
	 * 		PersistenceDataHandler#deleteAMessage(long)
	 */	
	public Collection<Long> deleteAMessage(ForumType data, long messageID) throws MessageNotFoundException {
			Collection<Long> tDeletedMessagesIDs = new Vector<Long>();
			MessageType tMessageToDelete = this.getMessageTypeByID(data, messageID);
			// remove the message
			data.getMessages().remove(tMessageToDelete);
			// remove the message replies recursively
			for (long tReplyID : tMessageToDelete.getRepliesIDs()) {
				try {
					tDeletedMessagesIDs.addAll(this.deleteAMessage(data, tReplyID));
				}
				catch (MessageNotFoundException e) {
					continue; // do nothing
				}
			}
			tDeletedMessagesIDs.add(tMessageToDelete.getMessageID());
			return tDeletedMessagesIDs;
	}
}