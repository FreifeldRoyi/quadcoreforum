package forum.server.domainlayer.impl.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.*;
import forum.server.persistentlayer.pipe.message.exceptions.*;

public class MessagesCache {	
	private final PersistenceDataHandler pipe; // A pipe to the persistence layer

	private long nextFreeSubjectID;
	private long nextThreadID;
	private long nextFreeMessageID;

	private final Map<Long, ForumSubject> idsToSubjectsMapping;
	private final Map<Long, ForumThread> idsToThreadsMapping;
	private final Map<Long, ForumMessage> idsToMessagesMapping;

	private long getNextSubjectID() {
		return this.nextFreeSubjectID++;
	}

	private long getNextThreadID() {
		return this.nextThreadID++;
	}

	private long getNextMessageID() {
		return this.nextFreeMessageID++;
	}

	/**
	 * The class constructor.
	 * 
	 * Initializes a MessagesCache object that handles all the forum posts creation and modification against the database
	 */
	public MessagesCache() {
		this.pipe = PersistenceFactory.getPipe();
		this.idsToSubjectsMapping = new HashMap<Long, ForumSubject>();
		this.idsToThreadsMapping = new HashMap<Long, ForumThread>();
		this.idsToMessagesMapping = new HashMap<Long, ForumMessage>();
		this.nextFreeMessageID = 0;
		this.nextFreeSubjectID = 0;
	}

	// Subject related methods

	public ForumSubject getSubjectByID(long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		if (this.idsToMessagesMapping.containsKey(subjectID))
			return this.idsToSubjectsMapping.get(subjectID);
		else {
			ForumSubject toReturn = this.pipe.getSubjectByID(subjectID);
			this.idsToSubjectsMapping.put(toReturn.getId(), toReturn);
			return toReturn;
		}
	}

	public ForumSubject createNewSubject(final String name, final String description) throws SubjectAlreadyExistsException,
	DatabaseUpdateException {
		long tSubjectID = this.getNextSubjectID();
		this.pipe.addNewSubject(tSubjectID, name, description);
		ForumSubject tNewSubject = new ForumSubject(tSubjectID, name, description);
		this.idsToSubjectsMapping.put(tNewSubject.getId(), tNewSubject);
		return tNewSubject;			
	}

	public void updateInDatabase(ForumSubject subjectToUpdate) throws SubjectNotFoundException, DatabaseUpdateException {
		this.pipe.updateSubject(subjectToUpdate.getId(), subjectToUpdate.getSubSubjects(), subjectToUpdate.getThreads());
		this.idsToSubjectsMapping.put(subjectToUpdate.getId(), subjectToUpdate);
	}

	// Thread related methods

	public ForumThread getThreadByID(long threadID) throws ThreadNotFoundException, DatabaseRetrievalException {
		if (this.idsToThreadsMapping.containsKey(threadID))
			return this.idsToThreadsMapping.get(threadID);
		else {
			ForumThread toReturn = this.pipe.getThreadByID(threadID);
			this.idsToThreadsMapping.put(toReturn.getId(), toReturn);
			return toReturn;
		}
	}

	/**
	 * 
	 * @param subjectID
	 * @param id
	 * 
	 * @pre
	 * 		A message with the given id exists in the database
	 * @return
	 * @throws DatabaseUpdateException 
	 */
	public ForumThread openNewThread(final String topic, long rootID) throws DatabaseUpdateException {
		final long tThreadID = this.getNextThreadID();
		ForumThread tNewThread = new ForumThread(tThreadID, topic, rootID);
		this.idsToThreadsMapping.put(tThreadID, tNewThread);
		this.pipe.openNewThread(tThreadID, topic, rootID);
		return tNewThread;
	}

	public void deleteATread(final long threadID) throws ThreadNotFoundException, DatabaseUpdateException {
		Collection<Long> tRecDeletedMessages = this.pipe.deleteAThread(threadID); // recursively deleted messages
		this.idsToThreadsMapping.remove(threadID);
		for (long tMessageID : tRecDeletedMessages)
			this.idsToMessagesMapping.remove(tMessageID);
	}

	// Message related methods

	public ForumMessage getMessageByID(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		if (this.idsToMessagesMapping.containsKey(messageID))
			return this.idsToMessagesMapping.get(messageID);
		else {
			ForumMessage toReturn = this.pipe.getMessageByID(messageID);
			this.idsToMessagesMapping.put(messageID, toReturn);
			return toReturn;
		}
	}

	public ForumMessage createNewMessage(final long userID, final String title, final String content) throws DatabaseUpdateException {
		long tMessageID = this.getNextMessageID();
		ForumMessage tNewMessage = new ForumMessage(tMessageID, userID, title, content);
		this.pipe.addNewMessage(tMessageID, userID, title, content);
		this.idsToMessagesMapping.put(tMessageID, tNewMessage);
		return tNewMessage;
	}

	public void deleteAMessage(long messageID) throws MessageNotFoundException, DatabaseUpdateException {
		Collection<Long> tRecDeletedMessages = this.pipe.deleteAMessage(messageID); // recursively deleted messages
		for (long tMessageID : tRecDeletedMessages)
			this.idsToMessagesMapping.remove(tMessageID);
	}
	
	public void updateInDatabase(ForumMessage updatedMessage) throws MessageNotFoundException, DatabaseUpdateException {
		this.idsToMessagesMapping.put(updatedMessage.getID(), updatedMessage);
		this.pipe.updateMessage(updatedMessage.getID(), updatedMessage.getTitle(), updatedMessage.getContent());
	}
}