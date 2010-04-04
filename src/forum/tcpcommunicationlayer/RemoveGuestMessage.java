package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;

public class RemoveGuestMessage extends ClientMessage {

	/* The guest id to remove. */
	private long guestID;
	
	private static final long serialVersionUID = 711339298859261826L;

	public RemoveGuestMessage(final long guestID) {
		this.guestID = guestID;
	}

	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse("", true); 
		forum.removeGuest(guestID);
		returnObj.setHasExecuted(true);
		returnObj.setResponse("removed");
		return returnObj;
	}
}
