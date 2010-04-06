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
	private long m_id;
	
	private String m_userName;
	private String m_content;
	
	/**
	 * The sons of this ForumCell.
	 */
	private Vector<ForumCell> m_sons;
	
	public ForumCell(long id, String userName, String content) {
		m_id = id;
		m_userName = userName;
		m_content = content;
		
		m_sons = new Vector<ForumCell>();
	}

	public Vector<ForumCell> getSons() {
		return m_sons;
	}
	
	@Override
	public String toString() {
		return m_userName+"  --  "+m_content.substring(0,Math.min(45,m_content.length()));
	}

	/**
	 * Add a ForumCell cell to the the sons vector of this cell.
	 * 
	 * @param cell The cell to be added as a son to this cell.
	 */
	public void add(ForumCell cell) {
		m_sons.add(cell);
	}

	/**
	 * 
	 * @return The id of this ForumCell(Message).
	 */
	public long getId() {
		return m_id;
	}

}
