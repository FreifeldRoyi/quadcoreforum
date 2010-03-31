package forum.server.persistentlayer.pipe.message;

import java.util.Collection;
import java.util.Vector;

import forum.server.persistentlayer.*;
import forum.server.persistentlayer.pipe.*;
import forum.server.persistentlayer.pipe.message.exceptions.*;
import forum.server.domainlayer.impl.message.*;


public class MessagesPersistenceHandler {

	private JAXBInOutUtil inOutUtil;

	public MessagesPersistenceHandler(JAXBInOutUtil util) {
		this.inOutUtil = util;
	}

	// Subject related methods

	public ForumSubject getSubjectByID(long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		ForumType tForum = this.inOutUtil.unmarshalDatabase();
		SubjectType tSubjectType = this.getSubjectByID(tForum, subjectID);		
		ForumSubject toReturn = new ForumSubject(tSubjectType.getSubjectID(), 
				tSubjectType.getName(), tSubjectType.getDescription());
		return toReturn;
	}

	private SubjectType getSubjectByID(ForumType forum, long subjectID) throws SubjectNotFoundException {
		for (SubjectType tCurrentSubject : forum.getSubjects())
			if (tCurrentSubject.getSubjectID() == subjectID)
				return tCurrentSubject;
		throw new SubjectNotFoundException(subjectID);
	}

	public void addNewSubject(long subjectID, String name, String description) throws DatabaseUpdateException {
		try {
			ForumType tForum = this.inOutUtil.unmarshalDatabase();
			SubjectType tNewSubject = ExtendedObjectFactory.createSubject(subjectID, name, description);
			tForum.getSubjects().add(tNewSubject);
			this.inOutUtil.marshalDatabase(tForum);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public void updateSubject(long id, Collection<Long> subSubjects,
			Collection<Long> threads) throws SubjectNotFoundException, DatabaseUpdateException {
		try {
			ForumType tForum = this.inOutUtil.unmarshalDatabase();
			SubjectType tSubjectToUpdate = this.getSubjectByID(tForum, id);
			// update the sub-subjects
			tSubjectToUpdate.getSubSubjectsIDs().clear();
			tSubjectToUpdate.getSubSubjectsIDs().addAll(subSubjects);
			// update the threads
			tSubjectToUpdate.getThreadsIDs().clear();
			tSubjectToUpdate.getThreadsIDs().addAll(threads);
			this.inOutUtil.marshalDatabase(tForum);
		} catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}



	}

	// Thread related methods

	public ForumThread getThreadByID(long threadID) throws ThreadNotFoundException, DatabaseRetrievalException {
		ForumType tForum = this.inOutUtil.unmarshalDatabase();
		ThreadType tThreadType = this.getThreadByID(tForum, threadID);		
		ForumThread toReturn = new ForumThread(tThreadType.getThreadID(), 
				tThreadType.getTopic(), tThreadType.getStartMessageID());
		return toReturn;
	}

	private ThreadType getThreadByID(ForumType forum, long threadID) throws ThreadNotFoundException {
		for (ThreadType tCurrentThread : forum.getThreads())
			if (tCurrentThread.getThreadID() == threadID)
				return tCurrentThread;
		throw new ThreadNotFoundException(threadID);		
	}

	public void openNewThread(long threadID, String topic, long rootID) throws DatabaseUpdateException {
		try {
			ForumType tForum = this.inOutUtil.unmarshalDatabase();
			ThreadType tNewThread = ExtendedObjectFactory.createThreadType(threadID, topic, rootID);
			tForum.getThreads().add(tNewThread);
			this.inOutUtil.marshalDatabase(tForum);
		} catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public Collection<Long> deleteAThread(long threadID) throws ThreadNotFoundException, DatabaseUpdateException{
		try {
			ForumType tForum = this.inOutUtil.unmarshalDatabase();
			ThreadType tThreadToDelete = this.getThreadByID(tForum, threadID);
			// remove the thread
			tForum.getThreads().remove(tThreadToDelete);
			this.inOutUtil.marshalDatabase(tForum);
			// remove the thread's messages recursively
			try {
			return this.deleteAMessage(tThreadToDelete.getStartMessageID());
			}
			catch (MessageNotFoundException e) { // the root message wasn't found - maybe it had been already deleted
				Collection<Long> toReturn = new Vector<Long>();
				toReturn.add(tThreadToDelete.getStartMessageID());
				return toReturn;
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Message related methods

	public ForumMessage getMessageByID(long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		ForumType tForum = this.inOutUtil.unmarshalDatabase();
		MessageType tMessageType = this.getMessageByID(tForum, messageID);
		ForumMessage toReturn = new ForumMessage(messageID, tMessageType.getAuthor(), 
				tMessageType.getTitle(), tMessageType.getContent(), 
				tMessageType.getPostTime().toGregorianCalendar(), 
				tMessageType.getRepliesIDs());
		return toReturn;		
	}

	private MessageType getMessageByID(ForumType forum, long messageID) throws MessageNotFoundException {
		for (MessageType tCurrentMessage : forum.getMessages())
			if (tCurrentMessage.getMessageID() == messageID)
				return tCurrentMessage;
		throw new MessageNotFoundException(messageID);
	}	

	public void addNewMessage(long messageID, long userID, String title, String content) throws DatabaseUpdateException {
		try {
			ForumType tForum = this.inOutUtil.unmarshalDatabase();
			MessageType tNewMessage = ExtendedObjectFactory.createMessageType(messageID, userID, title, content);
			tForum.getMessages().add(tNewMessage);
			this.inOutUtil.marshalDatabase(tForum);
		} catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public void updateMessage(long messageID, String newTitle, String newContent) throws MessageNotFoundException, 
	DatabaseUpdateException {		
		try {
			ForumType tForum = this.inOutUtil.unmarshalDatabase();
			MessageType tMsgToEdit = this.getMessageByID(tForum, messageID);
			tMsgToEdit.setTitle(newTitle);
			tMsgToEdit.setContent(newContent);
			this.inOutUtil.marshalDatabase(tForum);
		} catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public Collection<Long> deleteAMessage(long messageID) throws MessageNotFoundException, DatabaseUpdateException {
		try {
			Collection<Long> tDeletedMessagesIDs = new Vector<Long>();
			ForumType tForum = this.inOutUtil.unmarshalDatabase();
			MessageType tMessageToDelete = this.getMessageByID(tForum, messageID);
			// remove the message
			tForum.getMessages().remove(tMessageToDelete);
			// remove the message replies recursively
			for (long tReplyID : tMessageToDelete.getRepliesIDs()) {
				try {
					tDeletedMessagesIDs.addAll(this.deleteAMessage(tReplyID));
				}
				catch (MessageNotFoundException e) {
					continue; // do nothing
				}
			}
			this.inOutUtil.marshalDatabase(tForum);
			return tDeletedMessagesIDs;
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
}
