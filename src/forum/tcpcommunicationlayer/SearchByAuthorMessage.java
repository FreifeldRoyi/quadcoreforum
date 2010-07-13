/**
 * 
 */
package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.search.SearchHit;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

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
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		long userID = -1;
		SearchHit[] tHits = null;
		try {
			userID = forum.getMemberIdByUsernameAndOrEmail(this.authorUsername, null);
			System.out.println("UserID = " + userID + " username = " + this.authorUsername);
			tHits = forum.searchByAuthor(userID, 0, Integer.MAX_VALUE);

			if (tHits == null) {
				returnObj.setHasExecuted(false);
				returnObj.setResponse("for some reason the search can't be performed");
			}
			else if (tHits.length == 0) {
				returnObj.setHasExecuted(true);
				System.out.println("No Hits!!!!!");
				returnObj.setResponse("searchnotmessages");
			}
			else {
				returnObj.setHasExecuted(true);
				String tResponse = "searchresult";
				for (int i = 0; i < tHits.length; i++)
					tResponse += "\n\tARESULTMESSAGE: " + tHits[i].getScore() + 
					"\t" + this.authorUsername + "\t" + tHits[i].getMessage().toString();
				returnObj.setResponse(tResponse);
			}
		}
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(true);
			returnObj.setResponse("searchnotmessages");
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("for some reason the search can't be performed");
		}
		return returnObj;
	}
}
