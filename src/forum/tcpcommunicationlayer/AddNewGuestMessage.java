package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIUser;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;

public class AddNewGuestMessage extends ClientMessage {

	private static final long serialVersionUID = 6747957712828971641L;


	public AddNewGuestMessage() {}

	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			UIUser tNewGuest = forum.addGuest();
			returnObj.setGuestIDChanged();
			returnObj.setConnectedGuestID(tNewGuest.getID());
			returnObj.setHasExecuted(true);
			returnObj.setResponse(tNewGuest.getID() + "");
		}
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("guestregistrationerror\t");
		}
		return returnObj;
	}
}
