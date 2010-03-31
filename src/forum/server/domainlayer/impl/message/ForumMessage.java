package forum.server.domainlayer.impl.message;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Vector;

import forum.server.domainlayer.impl.interfaces.UIMessage;

public class ForumMessage implements UIMessage {
	private long messageID;

	private long authorID;
	private String title;
	private String content;

	private GregorianCalendar postTime;

	private Collection<Long> replies;

	// used for constructing from the database
	public ForumMessage(final long messageID, final long authorID, String title, String content, 
			GregorianCalendar postTime, Collection<Long> replies) {
		this.messageID = messageID;
		this.authorID = authorID;
		this.title = title;
		this.content = content;

		this.setPostTime(postTime);
		this.setReplies(replies);
	}


	public ForumMessage(final long messageID, final long authorID, String title, String content) {
		this(messageID, authorID, title, content, new GregorianCalendar(),
				new Vector<Long>());
	}

	/*	public String getAuthor() {
		return this.authorUsername;
	}
	 */

	public long getID() {
		return this.messageID;
	}



	public long getAuthorID() {
		return this.authorID;
	}

	/**
	 * returns a date formatted as dd/mm/yyyy
	 */
	public String getDate() {
		String toReturn = this.postTime.get(Calendar.DAY_OF_MONTH) + "//" +
		this.postTime.get(Calendar.MONTH) + "//" +
		this.postTime.get(Calendar.YEAR);
		return toReturn;
	}

	public String getContent() {
		return this.content;
	}

	public String getTitle() {
		return this.title;
	}


	public String getTime() {
		String toReturn = this.postTime.get(Calendar.HOUR_OF_DAY) + ":" +
		this.postTime.get(Calendar.MINUTE) + ":" +
		this.postTime.get(Calendar.SECOND);
		return toReturn;
	}



	public Collection<Long> getReplies() {
		return this.replies;
	}	


	public void setTitle(final String title) {
		this.title = title;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	private void setPostTime(GregorianCalendar time) {
		this.postTime = time;
	}

	// used by the constructor
	private void setReplies(Collection<Long> replies) {
		this.replies = replies;
	}

	public void updateMe(String newTitle, String newContent) {
		this.setTitle(newTitle);
		this.setContent(newContent);
	}

	public void addReply(final long replyID) {		
		this.replies.add(replyID);
	}

	public void deleteReply(final long replyID) {
		this.replies.remove(replyID);
	}

	/*
	public ForumMessage findMessage(long msgID) throws MessageNotFoundException {

		if (this.getMessageID() == msgID)
			return this;

		ForumMessage toReturn = null;



		for (ForumMessage tMsg : this.replyMessages) {
			if (tMsg.getMessageID() == msgID)
				return tMsg;
			try {
				toReturn = tMsg.findMessage(msgID);
				return toReturn;
			}
			catch (MessageNotFoundException e) {
				continue;
			}
		}
		throw new MessageNotFoundException(msgID);
	}

	 */

	/**
	 * Returns a string representation of the message
	 */
	public String toString() {
		return "title: " + this.getTitle() + "\n" +
		"author: " + this.getAuthorID() + "\n" +
		"content: " + this.getContent() + "\n" +
		"posting date: " + this.getDate() + "\n" +
		"posting time: " + this.getTime();
	}

	/*
	public Map<Long, String> getRepliesRepresentation() {
		Map<Long, String> toReturn = new HashMap<Long, String>();

		toReturn.put(this.getMessageID(), this.toString());
		for (ForumMessage tReply : this.replyMessages)
			toReturn.put(tReply.getMessageID(), tReply.toString());
		return toReturn;
	}

	public void updateMe(String newTitle, String newContent) {
		this.setMessageTitle(newTitle);
		this.setMessageContent(newContent);
		PersistenceFactory.getPipe().updateMessage(this.getMessageID(), newTitle, newContent);

	}
	 */
}