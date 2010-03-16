package forum.server.domainlayer.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.interfaces.*;
import forum.server.exceptions.message.*;
import forum.server.exceptions.user.*;
import forum.server.persistentlayer.pipe.*;

public class ForumMessageImpl implements ForumMessage 
{
	private static long MESSAGE_ID_COUNTER = 0;

	private long messageID;
	private RegisteredUser author;
	private GregorianCalendar postTime;
	private String title;
	private String content;
	private Vector<ForumMessage> replyMessages;

	public ForumMessageImpl(RegisteredUser usr, String ttl, String cnt)
	{
		this.messageID = (MESSAGE_ID_COUNTER++);
		this.author = usr;
		this.title = ttl;
		this.content = cnt;
		this.replyMessages = new Vector<ForumMessage>();

		Calendar tTimeNow = Calendar.getInstance();
		this.postTime = new GregorianCalendar(tTimeNow.get(Calendar.YEAR),
				tTimeNow.get(Calendar.MONTH),
				tTimeNow.get(Calendar.DAY_OF_MONTH),
				tTimeNow.get(Calendar.HOUR_OF_DAY),
				tTimeNow.get(Calendar.MINUTE),
				tTimeNow.get(Calendar.SECOND));
	}

	@Override
	public RegisteredUser getAuthor() 
	{
		return this.author;
	}

	/**
	 * returns a date formatted as DD/MM/YYYY
	 */
	@Override
	public String getDate() 
	{
		String toReturn = this.postTime.get(Calendar.DAY_OF_MONTH) + "//" +
		this.postTime.get(Calendar.MONTH) + "//" +
		this.postTime.get(Calendar.YEAR);
		return toReturn;
	}

	@Override
	public String getMessageContent() 
	{
		return this.content;
	}

	@Override
	public String getMessageTitle() 
	{
		return this.title;
	}

	@Override
	public String getTime() 
	{
		String toReturn = this.postTime.get(Calendar.HOUR_OF_DAY) + ":" +
		this.postTime.get(Calendar.MINUTE) + ":" +
		this.postTime.get(Calendar.SECOND);
		return toReturn;
	}

	@Override
	public void setMessageContent(String body) 
	{
		this.content = body;
	}

	@Override
	public void setMessageTitle(String t) 
	{
		this.title = t;
	}

	/**
	 * Used only for the first time fill
	 * @param forumMessage
	 */
	public void addMessageReplyData(ForumMessage forumMessage) {
		this.replyMessages.add(forumMessage);
	}
	
	@Override
	public void addReplyToMe(ForumMessage forumMessage) 
	{		
		this.replyMessages.add(forumMessage);

		PersistenceDataHandler pipe = PersistenceFactory.getPipe();

		try {
			pipe.replyToMessage(this.messageID, forumMessage.getMessageID(), forumMessage.getAuthor().getUsername(),
					forumMessage.getMessageTitle(), getMessageContent());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessageNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotRegisteredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getMessageID() {
		return this.messageID;
	}

	public String msgToString() {
		String tAns = this.getAuthor() + "\n" + 
		this.getDate() + "\n" + this.getTime() + "\n" +
		this.getMessageTitle() + "\n" + this.getMessageContent();

		Iterator<ForumMessage> iter = this.replyMessages.iterator();

		tAns += "\nreplys\n";
		while (iter.hasNext()) {
			ForumMessage tReply = (ForumMessage)iter.next();
			tAns += tReply.msgToString();
		}
		return tAns;	
	}

	@Override
	public ForumMessage findMessage(long msgID) throws MessageNotFoundException 
	{
		ForumMessage toReturn = null;
		
		for (ForumMessage tMsg : this.replyMessages)
		{
			if (tMsg.getMessageID() == msgID)
				return tMsg;
			try 
			{
				toReturn = tMsg.findMessage(msgID);
				return toReturn;
			}
			catch (MessageNotFoundException e)
			{
				continue;
			}
		}
		throw new MessageNotFoundException(msgID);
	}
}
