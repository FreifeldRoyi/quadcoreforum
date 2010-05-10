/**
 * 
 */

package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
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


	public GetPathMessage(final long messageID) {
		this.messageID = messageID;
	}


	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
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
}
