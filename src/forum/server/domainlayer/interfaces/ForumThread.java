package forum.server.domainlayer.interfaces;

public interface ForumThread 
{
	/* Methods */
	public void addMessage (ForumMessage father, ForumMessage newMsg);
	public void incNumOfViews();	
	public void incNumOfResponses();	
	public void decNumOfResponses();
	
	/*Getters*/
	/**
	 * Will return the subject of the root message
	 * in the thread.
	 */
	public String getThreadSubject();	
	public String getAuthor();
	public ForumMessage getRootMessage();
	public String getPostingTime(); 
	public String getPostingDate();
	public String getLatestPostTime();
	public String getLatestPostDate();
	public String getLatestPostAuthor();
	
	/*Setters*/
	public void setLatestPost(ForumMessage post);
	
}


/**
 * TODO  write proper JavaDoc
 */