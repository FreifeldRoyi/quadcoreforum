package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIUser;

public class AddNewGuestMessage extends ClientMessage {

	private static final long serialVersionUID = 6747957712828971641L;


	public AddNewGuestMessage() {}

	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj=new ServerResponse("", true); 
		UIUser tNewGuest = forum.addGuest();
		returnObj.setHasExecuted(true);
		returnObj.setResponse(tNewGuest.getID() + "");
		return returnObj;
	}
}
