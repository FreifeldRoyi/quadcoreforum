package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIUser;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotConnectedException;

/**
 * @author Tomer Heber
 *
 */
public class LogoffMessage extends ClientMessage {

	private static final long serialVersionUID = -5965616226069995574L;

	/* The user-name of the user who should be logged out */
	String username;

	public LogoffMessage(String username){
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			forum.logout(this.username);
			/* register the user as guest again */
			UIUser tNewGuest = forum.addGuest();
			returnObj.setHasExecuted(true);
			returnObj.setGuestIDChanged();
			returnObj.setConnectedGuestID(tNewGuest.getID());

			returnObj.setResponse("loggedout\t" + tNewGuest.getID());
		} 
		catch (NotConnectedException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("notconnected");
		}
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("loogedouterror\tdatabase");
		}
		return returnObj;
	}
}
