package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException;

public class ViewMessageAndRepliesMessage extends ClientMessage {

	private static final long serialVersionUID = 6154143641519365134L;

	/* The id of the message whose replies should be represented. */

	private long messageID;

	/* Whether the views number of the thread should be incremented */
	private boolean shouldUpdateViews;
	
	public ViewMessageAndRepliesMessage(final long messageId, boolean shouldUpdateViews) {
		this.messageID = messageId;
		this.shouldUpdateViews = shouldUpdateViews;
	}
	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */

	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			UIMessage tCurrentMessage = forum.getMessageByID(this.messageID);
			Collection<UIMessage> tReplies = forum.getReplies(this.messageID, this.shouldUpdateViews);

			String tResponse = this.getAuthorUsername(forum, tCurrentMessage) + "\t" + tCurrentMessage.toString();

			Iterator<UIMessage> iter = tReplies.iterator();
			while (iter.hasNext()) {
				UIMessage tCurrentReply = iter.next();
				// TODO: reply the usernames of the authors
				tResponse += "\n" + "\tAREPLYMESSAGE: " + this.getAuthorUsername(forum, tCurrentReply) + "\t" + 
				tCurrentReply.toString();
				Collection<UIMessage> tNextLevelReplies = forum.getReplies(tCurrentReply.getMessageID(), shouldUpdateViews);
				for (UIMessage tNextLevelCurrentReply : tNextLevelReplies)
					tResponse += "\n" +
						"\t\tASUBREPLYMESSAGE: " + this.getAuthorUsername(forum, tNextLevelCurrentReply) + "\t" + 
						tNextLevelCurrentReply.toString();
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
			SystemLogger.warning("While retrieving the contents of message " + messageID + " the username " +
					" of the user with id " + message.getAuthorID() + " wasn't found.");
		}
		return toReturn;		
	}
}