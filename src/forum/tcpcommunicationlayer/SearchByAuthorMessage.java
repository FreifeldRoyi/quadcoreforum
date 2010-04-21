/**
 * 
 */
package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.search.SearchHit;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author sepetnit
 *
 */
public class SearchByAuthorMessage extends ClientMessage {
	private static final long serialVersionUID = -2474415767162935039L ;

	/* The user-name of the author according to which, messages should be found. */
	private String authorUsername;

	public SearchByAuthorMessage(String authorUsername) {
		this.authorUsername = authorUsername;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse("", true); 
		long userID = -1;
		SearchHit[] tHits = null;
		try {
			userID = forum.getMemberIdByUsername(this.authorUsername);
			forum.searchByAuthor(userID, 0, Integer.MAX_VALUE);
		} catch (NotRegisteredException e) {
			// TODO please add your handling here
			e.printStackTrace();
		} catch (DatabaseRetrievalException e) {
			// TODO please add your handling here.
			e.printStackTrace();
		}
		
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
