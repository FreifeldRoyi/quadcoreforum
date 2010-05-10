package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException;

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

		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			UIMessage tCurrentMessage = forum.getMessageByID(this.messageID);
			Collection<UIMessage> tReplies = forum.getReplies(this.messageID);
			String tResponse = tCurrentMessage.toString() + "\n";

			Iterator<UIMessage> iter = tReplies.iterator();
			while (iter.hasNext()) {
				UIMessage tCurrentReply = iter.next();
				// TODO: reply the usernames of the authors
				tResponse += "\tAREPLYMESSAGE: " + tCurrentReply.toString() + "\n";
				Collection<UIMessage> tNextLevelReplies = forum.getReplies(tCurrentReply.getMessageID());
				for (UIMessage tNextLevelCurrentReply : tNextLevelReplies)
					tResponse += "\t\tASUBREPLYMESSAGE: " + tNextLevelCurrentReply.toString() + "\n";
			}
			returnObj.setHasExecuted(true);
			returnObj.setResponse(tResponse);
		}
		catch (MessageNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		} 
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		return returnObj;
	}
}