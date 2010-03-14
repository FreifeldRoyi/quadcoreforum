package forum.server.domainlayer.impl;

import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.ForumThread;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.persistentlayer.MessageType;

public class ForumThreadImpl implements ForumThread 
{
	private ForumMessage rootMessage;
	private int numOfViews;
	private int numOfResponses;
	private ForumMessage latestPost;
	
	public ForumThreadImpl(ForumMessage root)
	{
		this.rootMessage = root;
		this.latestPost = root; // TODO do we want a shallow or a deep copy???
		this.numOfResponses = 1;
		this.numOfViews = 1;
	}
	
	@Override
	public void addMessage(ForumMessage father, ForumMessage newMsg) 
	{
		father.addReplyToMe(newMsg);
	}

	@Override
	public void decNumOfResponses() 
	{
		if (this.numOfResponses > 0)
			--this.numOfResponses;

	}

	@Override
	public String getAuthor() 
	{
		return "" + this.rootMessage.getAuthor(); //calls toString
	}

	@Override
	public ForumMessage getRootMessage() 
	{
		return this.rootMessage;
	}

	@Override
	public String getThreadSubject() 
	{
		return this.rootMessage.getMessageTitle();
	}

	@Override
	public void incNumOfResponses() 
	{
		++this.numOfResponses;
	}

	@Override
	public void incNumOfViews() 
	{
		++this.numOfViews;
	}

	@Override
	public String getLatestPostAuthor() 
	{
		return "" + this.latestPost.getAuthor();
	}

	@Override
	public String getLatestPostDate() 
	{
		return this.latestPost.getDate();
	}

	@Override
	public String getLatestPostTime() 
	{
		return this.latestPost.getTime();
	}

	@Override
	public String getPostingDate() 
	{
		return this.rootMessage.getDate();
	}

	@Override
	public String getPostingTime() 
	{
		return this.rootMessage.getTime();
	}

	@Override
	public void setLatestPost(ForumMessage post) 
	{
		this.latestPost = post;
	}

	public String threadToString() {
		return this.rootMessage.msgToString();
	}

	@Override
	public ForumMessage findMessage(long msgID) throws MessageNotFoundException 
	{
		return this.rootMessage.findMessage(msgID);
	}
	
}
