package forum.server.domainlayer;

import forum.server.domainlayer.impl.message.ForumMessage;

/**
 * A single search hit returned after a search operation.
 * 
 * @author Tomer Heber
 */
public class SearchHit {
	
	private ForumMessage message;
	private double score;

	public SearchHit(ForumMessage message, double score) {
		this.message = message;
		this.score = score;
	}
	
	public double getScore() {
		return this.score;
	}
	
	public ForumMessage getMessage() {
		return this.message;
	}

}