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
@Searchable
public interface UIMessage {
	
	/**
	 * @return
			The unique id of the message
	 */
	@SearchableId
	public long getID();

	/**
	 * @return
			The id of the user who wrote the message - the message author
	 */
	@SearchableProperty (name = "authorID")
	public long getAuthorID();

	/**
	 * 
	 * @return
	 * 		The title of the message
	 */
	@SearchableProperty (name = "title")
	public String getTitle();
	
	/**
	 * @return
	 * 		The content of the message
	 */
	@SearchableProperty (name = "content")
	public String getContent();

	/**
	 * @return
			The date when this message was created, formatted as dd/mm/yyyy
	 */
	public String getDate();
	
	/**
	 * @return
			The time when this message was created, formatted as hh:mm:ss
	 */
	public String getTime();
	
	/**
	 * 
	 * @return
	 * 		A string representation of the message
	 */
	public String toString();
}
