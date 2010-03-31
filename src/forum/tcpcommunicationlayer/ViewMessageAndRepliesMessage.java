package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.domainlayer.impl.ForumFacade;
import forum.server.domainlayer.impl.interfaces.UIMessage;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.pipe.message.exceptions.MessageNotFoundException;

public class ViewMessageAndRepliesMessage extends ClientMessage {

	private static final long serialVersionUID = 6154143641519365134L;

	/* The id of the message whose replies should be represented. */

	private long messageID;

	public ViewMessageAndRepliesMessage(final long messageId){
		this.messageID = messageId;
	}
	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */

	public ServerResponse doOperation(ForumFacade forum) {
		// TODO - I consider failure only in the case of an exception. Is it o.k???
		// Response (Vitali) --> Yes

		ServerResponse returnObj = new ServerResponse("", true); 
		try {
			Collection<UIMessage> tRepliesResponse = forum.getReplies(this.messageID);
			String tResponse = "";
			if (tRepliesResponse.isEmpty())
				tResponse = "There are no replies under the root message with id " + this.messageID + " to view.";
			else {
				Iterator<UIMessage> iter = tRepliesResponse.iterator();
				while(iter.hasNext())
					tResponse += iter.next().toString() + "\n";
			}
			returnObj.setHasExecuted(true);
			returnObj.setResponse(tResponse);
		}
		catch (MessageNotFoundException e) {
			returnObj.setHasExecuted(true);
			returnObj.setResponse(e.getMessage());
		} 
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(true);
			returnObj.setResponse(e.getMessage());
		}
		return returnObj;
	}
}