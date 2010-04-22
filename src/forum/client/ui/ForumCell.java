package forum.client.ui;

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
	private String username;
	private String title;
	private String content;
	
	/**
	 * The sons of this ForumCell.
	 */
	private Vector<ForumCell> sons;
	
	public ForumCell(long id, String userName, String title, String content) {
		this.id = id;
		this.username = userName;
		this.title = title;
		this.content = content;
		
		sons = new Vector<ForumCell>();
	}

	public Vector<ForumCell> getSons() {
		return sons;
	}
	
	@Override
	public String toString() {
		return username+ "  --  " + title.substring(0, Math.min(1,content.length()));
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

}
