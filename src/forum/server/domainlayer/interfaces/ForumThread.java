package forum.server.domainlayer.interfaces;

import forum.server.exceptions.message.*;

public interface ForumThread {
	
	/* Methods */

	/**
	 * 
	 * Adds a new message as a reply to a given message
	 * 
	 * @param father
	 * 		newMsg should be a reply to the given message
	 * @param newMsg
	 * 		The reply
	 */
	public void addMessage (ForumMessage father, ForumMessage newMsg);

	/**
	 * increases the number of this thread views
	 */
	public void incNumOfViews();	
	
	/**
	 * Increases the number of this thread responses
	 */
	public void incNumOfResponses();	
	
	/**
	 * Decreases the number of this thread responses
	 */
	public void decNumOfResponses();
	
	/**
	 * 
	 * Performs a recursive search, starting from the root message and finds a message whose id equals to the
	 * given one
	 * 
	 * @param msgID
	 * 		The id of the message which should be found
	 * @return
	 * 		The found message
	 * 
	 * @throws MessageNotFoundException
	 * 		If a message with same id as the given one wasn't found
	 */
	public ForumMessage findMessage(long msgID) throws MessageNotFoundException; 
	
	/*Getters*/
	
	/**
	 * @return
	 *  	The subject name of the root message in the thread.
	 */
	public String getThreadSubject();	

	/**
	 * 
	 * @return
	 * 		The thread author (the author of the root message)
	 */
	public String getAuthor();

	/**
	 * 
	 * @return
	 * 		The id of the root message
	 */
	public long getRootMessageID();
	
	/**
	 * 
	 * @return
	 * 		The posting time of the thread, this time is the posting time of the thread's root message
	 */
	public String getPostingTime(); 

	
	/**
	 * 
	 * @return
	 * 		The posting date of the thread, this time is the posting date of the thread's root message
	 */
	public String getPostingDate();
	
	/**
	 * 
	 * @return
	 * 		The latest time, this thread was updated
	 */
	public String getLatestPostTime();
	
	/**
	 * 
	 * @return
	 * 		The latest date, this thread was updated
	 */
	public String getLatestPostDate();
	
	/**
	 * 
	 * @return
	 * 		The last user who updated one of this thread's messages
	 */
	public String getLatestPostAuthor();
	
	/**
	 * @return the number of responses
	 */
	public int getNumOfResponese();
	
	/**
	 * @return the number of views
	 */
	public int getNumOfViews();

	
	/* Setters */

	/**
	 * Sets the latest posted message of this thread 
	 * 
	 * @param post
	 * 		The message which was posted last to this thread
	 */
	public void setLatestPost(ForumMessage post);

	/**
	 * 
	 * @return
	 * 		A string representation of this thread
	 */
	public String threadToString();
}