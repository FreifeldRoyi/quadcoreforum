/**
 * This class is a proxy class, it delegates the calls to the search engine methods,
 * to an implementation class which it holds as a private field.
 */
package forum.server.domainlayer.search;

import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;

/**
 * @author sepetnit
 *
 */
public class SearchAgent implements SearchEngine {

	/* An implementation of the search engine which manages the engine internally */
	private SearchEngine searchEngine;	
	
	/**
	 * Constructs new search agent
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case a connection to the search database isn't successful,
	 * 		a retrieval of information failed.
	 * @throws DatabaseUpdateException
	 * 		In case a connection to the search database isn't successful,
	 * 		an updating of information failed.
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
