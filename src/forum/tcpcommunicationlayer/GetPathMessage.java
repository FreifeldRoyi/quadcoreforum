/**
 * 
 */

package forum.tcpcommunicationlayer;

import java.util.Arrays;
import java.util.Collection;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.interfaces.UISubject;
import forum.server.domainlayer.interfaces.UIThread;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException;

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

			UIMessage tPreviousMessage = forum.getMessageByID(messageID);


			long tCurrentFatherID = tPreviousMessage.getFatherID();
			String tIDs =  messageID + ""; /*(tCurrentFatherID == -1)?*/ /*+ "" : "";*/

			while (tCurrentFatherID != -1) {
				tIDs = tCurrentFatherID + "\t" + tIDs;
				tPreviousMessage = forum.getMessageByID(tCurrentFatherID);
				tCurrentFatherID = tPreviousMessage.getFatherID();
			}

			String response = "MESSAGES\n";
			String[] tMessageIDs = tIDs.split("\t");

			response += this.getMessageRepliesAndSubReplies(forum, 1, "", 0, tMessageIDs);
			
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

	private String getCloneOf(String s, int number) {
		StringBuilder toReturn = new StringBuilder();
		for (int i = 0; i < number; i++)
			toReturn.append(s);
		return toReturn.toString();
	}

	private String getMessageRepliesAndSubReplies(ForumFacade forum, int numOfCalls, String firstLineDelimiter, int i, String[] IDs) 
	throws MessageNotFoundException, DatabaseRetrievalException {
		String response = "";

		System.out.println(Arrays.toString(IDs));

		if (i == IDs.length) {
			System.out.println("i = ids[length]");
			return response;
		}
		else {
			UIMessage tCurrentMessage = forum.getMessageByID(Long.parseLong(IDs[i]));
			response += ((numOfCalls == 0)? "" : getCloneOf("\t", numOfCalls - 1)) + firstLineDelimiter + 
			this.getAuthorUsername(forum, tCurrentMessage) + "\t" + tCurrentMessage.toString() + "\n";

			
			Collection<UIMessage> tReplies = forum.getReplies(tCurrentMessage.getMessageID(), false);

			for (UIMessage tCurrentReply : tReplies) {

				System.out.println("i = " + i);


				if ((i < IDs.length - 1) && (Long.parseLong(IDs[i + 1]) == tCurrentReply.getMessageID()))
					response += getMessageRepliesAndSubReplies(forum, numOfCalls + 1, "AREPLYMESSAGE: ", i + 1, IDs);
				else {
					response += getCloneOf("\t", numOfCalls) + "AREPLYMESSAGE: " +
					this.getAuthorUsername(forum, tCurrentReply) + "\t" + tCurrentReply.toString() + "\n";

					// adds next level replies
				
					Collection<UIMessage> tNextLevelReplies = forum.getReplies(tCurrentReply.getMessageID(), false);

					for (UIMessage tNextLevelCurrentReply : tNextLevelReplies)
						response += getCloneOf("\t", numOfCalls + 1) + "AREPLYMESSAGE: " + 
						this.getAuthorUsername(forum, tNextLevelCurrentReply) + "\t" + tNextLevelCurrentReply.toString() + "\n";
				}

			}

			return response;
		}
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
