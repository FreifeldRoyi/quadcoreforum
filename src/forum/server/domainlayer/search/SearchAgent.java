/**
 * This class is a proxy class, it delegates the calls to the search engine methods,
 * to an implementation class which it holds as a private field.
 */
package forum.server.domainlayer.search;

import forum.server.domainlayer.interfaces.UIMessage;

/**
 * @author sepetnit
 *
 */
public class SearchAgent implements SearchEngine {

	/* An implementation of the search engine which manages the engine internally */
	private SearchEngine searchEngine;
	
	/**
	 * Constructs new search agent
	 */
	public SearchAgent() {
		this.searchEngine = new BasicSearchEngine();
	}
	
	/**
	 * @see
	 * 		SearchEngine#addData(UIMessage)
	 */
	public void addData(UIMessage msg) {
		searchEngine.addData(msg);
		
	}

	/**
	 * @see
	 * 		SearchEngine#searchByAuthor(long, int, int)
	 */
	public SearchHit[] searchByAuthor(long usrID, int from, int to) {
		return searchEngine.searchByAuthor(usrID, from, to);
	}

	/**
	 * @see
	 * 		SearchEngine#searchByContent(String, int, int)
	 */
	public SearchHit[] searchByContent(String phrase, int from, int to) {
		return this.searchEngine.searchByContent(phrase, from, to);
	}

}
