package forum.server.presentationlayer;

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
			The id of the message
	 */
	public long getId();

	/**
	 * @return
			The id of the user who wrote the message - the message author
	 */
	public long getAuthorId();

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
	public Date getDate();
	
	/**
	 * @return
			The time when this message was created
	 */
	public Date getTime();
}
