package forum.server.domainlayer.interfaces;

import forum.server.exceptions.message.MessageNotFoundException;

public interface ForumMessage
{

	/* Setters */

	/**
	 * Sets the title of the message, to be the given one
	 * 
	 * @param t
	 * 		A new title, to which the message title should be changed
	 */
	public void setMessageTitle (String t);	
	
	/**
	 * Sets the content of the message, to be the given one
	 * 
	 * @param content
	 * 		A new content, to which the message content should be changed
	 */
	public void setMessageContent (String content);
	

	/* Getters */

	/**
	 * 
	 * @return
	 * 		The unique id of the message 
	 */
	public long getMessageID();
	
	/**
	 * 
	 * @return
	 * 		The title of the message
	 */
	public String getMessageTitle();
	
	/**
	 * 
	 * @return
	 * 		The content of the message
	 */
	public String getMessageContent();
	
	/**
	 * 
	 * @return
	 * 		The posting time of the message
	 */
	public String getTime();
	
	
	public String getDate();
	public RegisteredUser getAuthor();
	
	
	/*Methods*/
	
	/**
	 * Adds a given reply to the this message, without updating the data!!!
	 * This method is used in order to construct the domain objects at the first time
	 */
	public void addMessageReplyData(ForumMessage forumMessage);
	
	/**
	 * Adds a new reply to this message, and updates the database with the this message data
	 * 
	 * @param fm
	 * 		The message which should be added to the forum
	 */
	public void addReplyToMe(ForumMessage fm);
	
	/**
	 * 
	 * Finds and returns a message which id equals to the given id
	 * 
	 * @param msgID
	 * 		The id of the message which w
	 * @return
	 * @throws MessageNotFoundException
	 */
	public ForumMessage findMessage(long msgID) throws MessageNotFoundException;
	public String msgToString();
}

/**
 * TODO don't forget to change time/date return type.
 * TODO write proper JavaDoc
 * TODO private member timeDate of type GregorianCalendar
 */
