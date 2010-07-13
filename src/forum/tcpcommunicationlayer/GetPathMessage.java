/**
 * 
 */

package forum.tcpcommunicationlayer;

import java.util.Collection;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.interfaces.UISubject;
import forum.server.domainlayer.interfaces.UIThread;

/**
 * @author sepetnit
 *
 */
public class GetPathMessage extends ClientMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2340010193857268260L;

	/* The id of The message whose path should be received */
	private long messageID;

	/* Whether the nymber of views of the message's thread should be updated */
	private long prevFatherMessageID;

	
	public GetPathMessage(final long messageID, long prevFatherMessageID) {
		this.messageID = messageID;
		this.prevFatherMessageID = prevFatherMessageID;
	}


	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		try {
			
			String tIDs = messageID + "";
			UIMessage tPreviousMessage = forum.getMessageByID(messageID);
			
			long tCurrentFatherID = tPreviousMessage.getFatherID();
			while (tCurrentFatherID != -1) {
				tIDs = tCurrentFatherID + "\t" + tIDs;
				tPreviousMessage = forum.getMessageByID(tCurrentFatherID);
				tCurrentFatherID = tPreviousMessage.getFatherID();
			}
			
			String response = "MESSAGES\n";
			String[] tMessageIDs = tIDs.split("\t");
			String tRepliesDelimiter = "\tAREPLYMESSAGE: ";
			String tSubRepliesDelimiter = "\t\tASUBREPLYMESSAGE: ";
			long tID = Long.parseLong(tMessageIDs[0]);
			UIMessage tCurrentMessage = forum.getMessageByID(tID);
			response += this.getAuthorUsername(forum, tCurrentMessage) + "\t" + tCurrentMessage.toString() + "\n";
			
			
			
			
			for (int i = 1; i < tMessageIDs.length; i++) {
				Collection<UIMessage> tReplies = forum.getReplies(tID, false);
				tID = Long.parseLong(tMessageIDs[i]);
				for (UIMessage tCurrentReply : tReplies) {
					if (tCurrentReply.getMessageID() != tID) {
						response += tRepliesDelimiter + 
						this.getAuthorUsername(forum, tCurrentReply) + "\t" + tCurrentReply.toString() + "\n";
						Collection<UIMessage> tNextLevelReplies = forum.getReplies(tCurrentReply.getMessageID(), false);
						for (UIMessage tNextLevelCurrentReply : tNextLevelReplies)
							response += "\t" + tRepliesDelimiter + 
							this.getAuthorUsername(forum, tNextLevelCurrentReply) + "\t" + tNextLevelCurrentReply.toString() + "\n";
					}
				}
				tCurrentMessage = forum.getMessageByID(tID);
				response += tRepliesDelimiter + 
				this.getAuthorUsername(forum, tCurrentMessage) + "\t" + tCurrentMessage.toString() + "\n";
				tRepliesDelimiter = "\t" + tRepliesDelimiter;
				tSubRepliesDelimiter = "\t" + tSubRepliesDelimiter;
			}
			
			System.out.println("Is different? " + (tPreviousMessage.getMessageID() != this.prevFatherMessageID));
			UIThread tCurrentThread = forum.getThreadByID(tPreviousMessage.getMessageID(), 
					tPreviousMessage.getMessageID() != this.prevFatherMessageID);
			response = "THREAD\n" + tCurrentThread.getID() + "\n" + response;
			tCurrentFatherID = tCurrentThread.getFatherID();
			UISubject tPreviousSubject = null;
			while (tCurrentFatherID != -1) {
				tPreviousSubject = forum.getSubjectByID(tCurrentFatherID);
				response = tCurrentFatherID + "\t" + tPreviousSubject.getName() + "\n" + response;
				tCurrentFatherID = tPreviousSubject.getFatherID();
			}
			response = "getpathsuccess\n" + messageID + "\nSUBJECTS\n" + response;
			returnObj.setHasExecuted(true);
			returnObj.setResponse(response);
			System.out.println("\n\n------------------\n\nencoded view:\n"+response + "\n\n-----------------------\n\n");
		}
		catch (Exception e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("getpatherror");
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

	
/*	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		try {
			String response = messageID + "";
			UIMessage tPreviousMessage = forum.getMessageByID(messageID);
			
			long tCurrentFatherID = tPreviousMessage.getFatherID();
			while (tCurrentFatherID != -1) {
				response = tCurrentFatherID + "\n" + response;
				tPreviousMessage = forum.getMessageByID(tCurrentFatherID);
				tCurrentFatherID = tPreviousMessage.getFatherID();
			}
			response = "MESSAGES\n" + response;
			UIThread tCurrentThread = forum.getThreadByID(tPreviousMessage.getMessageID());
			response = "THREAD\n" + tCurrentThread.getID() + "\n" + response;
			tCurrentFatherID = tCurrentThread.getFatherID();
			UISubject tPreviousSubject = null;
			while (tCurrentFatherID != -1) {
				tPreviousSubject = forum.getSubjectByID(tCurrentFatherID);
				response = tCurrentFatherID + "\t" + tPreviousSubject.getName() + "\n" + response;
				tCurrentFatherID = tPreviousSubject.getFatherID();
			}
			response = "getpathsuccess\nSUBJECTS\n" + response;
			returnObj.setHasExecuted(true);
			returnObj.setResponse(response);
		}
		catch (Exception e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("getpatherror");
		}
		return returnObj;
	}
*/
	
	
}
