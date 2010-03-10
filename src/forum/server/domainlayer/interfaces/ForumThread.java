package forum.server.domainlayer.interfaces;

import java.text.SimpleDateFormat;

public interface ForumThread 
{
	/* Methods */
	
	public void addMessage (ForumMessage father, ForumMessage newMsg);

	public void incNumOfViews();
	
	public void incNumOfResponses();
	
	public void decNumOfResponses();
	
	/**
	 * Will return the subject of the root message
	 * in the thread.
	 */
	public String getThreadSubject();
	
	public String getAuthor();

	public SimpleDateFormat getPostingTime(); // will be changed!!!

	public SimpleDateFormat getPostingDate(); // will be changed!!!

	
}


/**
 * TODO  write proper JavaDoc
 */