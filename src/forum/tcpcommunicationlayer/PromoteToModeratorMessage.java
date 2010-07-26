/**
 *  This represents a message of promoting a user to be a forum moderator which is sent by
 *  the client to the server.
 */
package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author sepetnit
 *
 */
public class PromoteToModeratorMessage extends ClientMessage {
	private static final long serialVersionUID = -2344331771299435031L ;

	/* The id of the user which is asking to promote a user to be a moderator. */
	private long appicantID;
	/* The user-name of the user which is asked to be promoted to be a moderator. */
	private String username;

	public PromoteToModeratorMessage(final long applicantID, final String username) {
		this.appicantID = applicantID;
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true);
		try {
			forum.promoteToBeModerator(this.appicantID, this.username);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("promoted\t" + this.username + " has been successfully promoted to be a moderator");
		} 
		catch (NotPermittedException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("promotionerror\tpermissions");			
		}
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("promotionerror\tregistration");			
		}
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("promotionerror\tdatabase");
		}
		return returnObj;
	}
}

