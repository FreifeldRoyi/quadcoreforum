package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.WrongPasswordException;

/**
 * @author Lital Badash
 *
 */
public class LoginMessage extends ClientMessage {

	private static final long serialVersionUID = -2723317717299435031L;
	
	/* The id of the previous guest - to be removed from the forum. */
	private long guestID;
	
	/* The user-name of the user. */
	private String username;
	/* The password of the user. */
	private String password;

	public LoginMessage(long guestID, String username, String password) {
		this.guestID = guestID;
		this.username = username;
		this.password = password;
		
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - again ot is not clear what the return value is in case of a failure - I assumed it is null.
		// response (Vitali) --> No! exception will be thrown.

		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		try {
			UIMember tResponseUser = forum.login(this.username, this.password);
			returnObj.setHasExecuted(true);

			String type = null;
			if (tResponseUser.isAllowed(Permission.SET_MODERATOR))
				type = "ADMIN";
			else
				if (tResponseUser.isAllowed(Permission.DELETE_MESSAGE))
					type = "MODERATOR";
				else
					type = "MEMBER";
			
			String tResponse = tResponseUser.getID() + "\t" + tResponseUser.getUsername() + "\t" + tResponseUser.getLastName() + "\t" +
			tResponseUser.getFirstName() + "\t" + type + "\n";
			
			for (Permission tCurrentPermission : tResponseUser.getPermissions())
				tResponse += tCurrentPermission.toString() + "\n";
			forum.removeGuest(guestID);
			returnObj.setResponse(tResponse);
			returnObj.setMemberUsernameChanged();
			returnObj.setMemberUsername(tResponseUser.getUsername());
		}
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		} 
		catch (WrongPasswordException e) {
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
