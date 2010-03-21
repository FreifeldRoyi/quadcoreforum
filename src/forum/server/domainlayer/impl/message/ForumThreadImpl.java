package forum.server.domainlayer.impl.message;

import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.ForumThread;
import forum.server.exceptions.message.MessageNotFoundException;

public class ForumThreadImpl implements ForumThread 
{
	private ForumMessage rootMessage;
	private int numOfViews;
	private int numOfResponses;
	private ForumMessage latestPost;
	

	/**
	 * Used for the first time fill (from the database)
	 * @param root
	 */
	public ForumThreadImpl(ForumMessage root, ForumMessage latestPost, int numOfReponses, int numOfViews)
	{
		this.rootMessage = root;
		this.latestPost = latestPost; // TODO do we want a shallow or a deep copy???
		this.numOfResponses = numOfReponses;
		this.numOfViews = numOfViews;
	}

	public ForumThreadImpl(ForumMessage root)
	{
		this.rootMessage = root;
		this.latestPost = root; // TODO do we want a shallow or a deep copy???
		this.numOfResponses = 0;
		this.numOfViews = 0;
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

/*	@Override
	public ForumMessage getRootMessage() 
	{
		return this.rootMessage;
	}
*/	

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

	@Override
	public long getRootMessageID() {
		return this.rootMessage.getMessageID();
	}
	
	public String toString() {
		return this.rootMessage.getMessageTitle() + "\n" +
				"\tposted by: " + this.rootMessage.getAuthor().getUsername() + "\n" + 
				"\tposting date: " + this.rootMessage.getDate() + "\n" +
				"\tposting time: " + this.rootMessage.getTime() + "\n" +
				"\tnumber of responses: " + this.numOfResponses;
	}

	public int getNumOfResponese() {
		return this.numOfResponses;
	}

	public int getNumOfViews() {
		return this.numOfViews;
	}
	
}
