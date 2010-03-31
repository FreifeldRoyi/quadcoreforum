package forum.server.domainlayer.impl.interfaces;

import java.util.Date;

/**
 * This interface is used to present the data of a ForumMessage object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Message state.
 */
public interface UIMessage {
	
	/**
	 * @return
			The unique id of the message
	 */
	public long getID();

	/**
	 * @return
			The id of the user who wrote the message - the message author
	 */
	public long getAuthorID();

	/**
	 * 
	 * @return
	 * 		The title of the message
	 */
	public String getTitle();
	
	/**
	 * @return
	 * 		The content of the message
	 */
	public String getContent();

	/**
	 * @return
			The date when this message was created
	 */
	public String getDate();
	
	/**
	 * @return
			The time when this message was created
	 */
	public String getTime();
}
