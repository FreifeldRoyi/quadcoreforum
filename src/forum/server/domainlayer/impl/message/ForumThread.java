package forum.server.domainlayer.impl.message;

import forum.server.domainlayer.impl.interfaces.UIThread;

public class ForumThread implements UIThread {
	
	private long threadID;
	
	private long rootMessageID;
	private long latestPostID;

	private String topic;
	
	private int numOfViews;
	private int numOfResponses;
	
	/**
	 * Used for the first time fill (from the database)
	 * @param root
	 */
	public ForumThread(final long threadID, final long rootID, int numOfReponses, int numOfViews)
	{
		this.threadID = threadID;
		this.rootMessageID = rootID;
		this.numOfResponses = numOfReponses;
		this.numOfViews = numOfViews;
	}

	public ForumThread(final long threadID, final String topic, final long rootID)
	{
		this.threadID = threadID;
		this.rootMessageID = rootID;
		this.latestPostID = rootID;
		this.numOfResponses = 0;
		this.numOfViews = 0;
	}
	
	public void decNumOfResponses() {
		if (this.numOfResponses > 0)
			--this.numOfResponses;
	}

	public void incNumOfResponses() 
	{
		++this.numOfResponses;
	}

	public void incNumOfViews() 
	{
		++this.numOfViews;
	}

/*
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
*/
	
	public int getNumOfResponese() {
		return this.numOfResponses;
	}

	public int getNumOfViews() {
		return this.numOfViews;
	}

	
	public long getId() {
		return this.threadID;
	}

	public long getRootMessageID() {
		return this.rootMessageID;
	}
	
	public String getTitle() {
		return this.topic;
	}
	
}
