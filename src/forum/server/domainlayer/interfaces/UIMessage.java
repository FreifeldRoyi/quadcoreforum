package forum.server.domainlayer.interfaces;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

/**
 * This interface is used to present the data of a ForumMessage object
 * 
 * The interface allows the UI only revealing the data needed for the presentation,
 * without changing the Message state.
 */
//@Searchable
public abstract class UIMessage {
	
	/**
	 * @return
			The unique id of the message
	 */
	//@SearchableId
	public abstract long getMessageID();

	/**
	 * @return
			The id of the user who wrote the message - the message author
	 */
	//@SearchableProperty (name = "authorID")
	public abstract long getAuthorID();

	/**
	 * 
	 * @return
	 * 		The title of the message
	 */
//	@SearchableProperty (name = "title")
	public abstract String getTitle();
	
	/**
	 * @return
	 * 		The content of the message
	 */
	//@SearchableProperty (name = "content")
	public abstract String getContent();

	/**
	 * @return
			The date when this message was created, formatted as dd/mm/yyyy
	 */
	public abstract String getDate();
	
	/**
	 * @return
			The time when this message was created, formatted as hh:mm:ss
	 */
	public abstract String getTime();
	
	/**
	 * 
	 * @return
	 * 		A string representation of the message
	 */
	public abstract String toString();
	
	public abstract long getFatherID();
}
