package forum.swingclient.ui;

import java.util.Vector;

/**
 * A forum cell is the an object that is attached to each node in the tree (JTree).
 * 
 * @author Tomer Heber
 */
public class ForumCell {
	
	/**
	 * A unique identifier of a message. 
	 */
	private long id;
	private String authorUsername;
	private String authorID;
	private String title;
	private String content;
	
	/**
	 * The sons of this ForumCell.
	 */
	private Vector<ForumCell> sons;
	
	public ForumCell(final long id, final String authorID, final String authorUsername, final String title, final String content) {
		this.id = id;
		this.authorID = authorID;
		this.authorUsername = authorUsername;
		this.title = title;
		this.content = content;
		
		sons = new Vector<ForumCell>();
	}

	public Vector<ForumCell> getSons() {
		return sons;
	}

	public String getTitle() {
		return this.title;
	}

	public String getContent() {
		return this.content;
	}
	
	public String toString() {
		return title + "  -- by " + this.authorUsername;
	}

	/**
	 * Add a ForumCell cell to the the sons vector of this cell.
	 * 
	 * @param cell The cell to be added as a son to this cell.
	 */
	public void add(ForumCell cell) {
		sons.add(cell);
	}

	/**
	 * 
	 * @return The id of this ForumCell(Message).
	 */
	public long getId() {
		return id;
	}

	/**
	 * 
	 * @return The id of the message author
	 */
	public String getAuthorID() {
		return this.authorID;
	}

	/**
	 * 
	 * @return The user-name of the message author
	 */
	public String getAuthorUsername() {
		return this.authorUsername;
	}
}
