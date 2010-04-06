/**
 * 
 */
package forum.tcpcommunicationlayer;


import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.search.SearchHit;

/**
 * @author sepetnit
 *
 */
public class SearchByContentMessage extends ClientMessage {
	private static final long serialVersionUID = -2474331771299435031L ;

	/* The content according to which, messages should be found. */
	private String phraseToSearch;

	public SearchByContentMessage(String phrase) {
		this.phraseToSearch = phrase;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse("", true); 
		SearchHit[] tHits = forum.searchByContent(this.phraseToSearch, 0, Integer.MAX_VALUE);
		if (tHits == null) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("for some reason the search can't be performed");
		}
		else if (tHits.length == 0) {
			returnObj.setHasExecuted(true);
			returnObj.setResponse("no messages were found");
		}
		else {
			returnObj.setHasExecuted(true);
			String tResponse = "The found messages IDs are:" + "\n";
			for (int i = 0; i < tHits.length; i++)
				tResponse += tHits[i].getMessage().getID() + "\n";
			returnObj.setResponse(tResponse);
		}
		return returnObj;
	}
}
