package forum.server.domainlayer.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import forum.server.exceptions.message.MessageNotFoundException;

public interface ForumMessage {

	/* Setters */

	/**
	 * Sets the title of the message, to be the given one
	 * 
	 * @param t
	 * 		A new title, to which the message title should be changed
	 */
	public void setMessageTitle(String t);	
	
	/**
	 * Sets the content of the message, to be the given one
	 * 
	 * @param content
	 * 		A new content, to which the message content should be changed
	 */
	public void setMessageContent(String content);
	

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
	
	
	/**
	 * 
	 * @return
	 * 		A string representation of the message and its replies, used for debugging
	 */
	public String msgToString();
	
	/**
	 * 
	 * @return
	 * 		A mapping which maps the replies of this message, to their representation
	 */
	public Map<Long, String> getRepliesRepresentation();
	
	
	/**
	 * Updates the title and the content of the message in the database layer and also delegates
	 * the update to the persistent layer (database)
	 * 
	 * @param newTitle
	 * 		The new title of the message
	 * @param newContent
	 * 		The new content of the message
	 *  
	 * @throws IOException 
	 * 		In case of database read error
	 * @throws JAXBException
	 * 		In case of database write error 
	 * @throws MessageNotFoundException 
	 * 		In case, the database doesn't contain the message
	 */
	public void updateMe(String newTitle, String newContent) throws JAXBException, IOException, MessageNotFoundException;
}