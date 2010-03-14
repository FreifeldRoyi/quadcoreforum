package forum.server.domainlayer.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.RegisteredUser;

public class ForumMessageImpl implements ForumMessage 
{
	private RegisteredUser author;
	private GregorianCalendar postTime;
	private String title;
	private String content;
	private Vector<ForumMessage> replyMessages; 
	
	public ForumMessageImpl(RegisteredUser usr, String ttl, String cnt)
	{
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

	@Override
	public void addMessage(ForumMessage fm) 
	{
		this.replyMessages.add(fm);
		/**
		 * TODO figure out what to do with Vitali's interface
		 */
	}
}
