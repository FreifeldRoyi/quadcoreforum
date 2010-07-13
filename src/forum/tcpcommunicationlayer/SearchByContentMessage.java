/**
 * 
 */
package forum.tcpcommunicationlayer;


import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.interfaces.UIMessage;
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
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		SearchHit[] tHits = forum.searchByContent(this.phraseToSearch, 0, Integer.MAX_VALUE);
		if (tHits == null) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("for some reason the search can't be performed.");
		}
		else if (tHits.length == 0) {
			returnObj.setHasExecuted(true);
			returnObj.setResponse("searchnotmessages");
		}
		else {
			returnObj.setHasExecuted(true);
			String tResponse = "searchresult";
			for (int i = 0; i < tHits.length; i++) {
				UIMessage tCurrentMessage = tHits[i].getMessage();
				tResponse += "\n\tARESULTMESSAGE: " + tHits[i].getScore() + "\t" +
				this.getAuthorUsername(forum, tCurrentMessage) + "\t" + tCurrentMessage.toString();
			}
			returnObj.setResponse(tResponse);
		}
		return returnObj;
	}
	
	/**
	 * Finds and returns the user-name of the given message author
	 * 
	 * @param forum
	 * 		An instance of the ForumFacade from which the data should be retrieved
	 * @param message
	 * 		The message whose author user-name should be retrieved
	 * @return
	 * 		The user-name of the message author
	 */
	private String getAuthorUsername(ForumFacade forum, UIMessage message) {
		String toReturn = "<Author-Not-Found>";
		try {
			toReturn = forum.getMemberByID(message.getAuthorID()).getUsername();
		}
		catch (Exception e) {
			SystemLogger.warning("While retrieving the contents of message " + message.getMessageID() + " the username " +
					" of the user with id " + message.getAuthorID() + " wasn't found.");
		}
		return toReturn;		
	}
}
