package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.ThreadNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author Lital Badash
 *
 */
public class ModifyThreadMessage extends ClientMessage {

	private static final long serialVersionUID = -2723317717299435031L;
	
	private long userID;
	private long threadID;
	private String topic;

	public ModifyThreadMessage(long userID, long threadID, String topic) {
		this.userID = userID;
		this.threadID = threadID;
		this.topic = topic;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - again ot is not clear what the return value is in case of a failure - I assumed it is null.
		// response (Vitali) --> No! exception will be thrown.

		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		
		try {
			forum.updateAThread(userID, threadID, topic);
			returnObj.setResponse("threadupdatesuccess");
			returnObj.setHasExecuted(true);
		}
		catch (NotRegisteredException e) {
			returnObj.setResponse("threadupdatefailed");
			returnObj.setHasExecuted(false);
		} 
		catch (NotPermittedException e) {
			returnObj.setResponse("threadupdatefailed");
			returnObj.setHasExecuted(false);
		}
		catch (ThreadNotFoundException e) {
			returnObj.setResponse("threadupdatefailed");
			returnObj.setHasExecuted(false);
		} 
		catch (DatabaseUpdateException e) {
			returnObj.setResponse("threadupdatefailed");
			returnObj.setHasExecuted(false);
		}
		return returnObj;
	}
}
