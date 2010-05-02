/**
 * 
 */
package forum.tcpcommunicationlayer;

import java.util.Collection;

import forum.server.domainlayer.ForumFacade;

/**
 * @author sepetnit
 *
 */
public class GuestsAndMembersNumberMessage extends ClientMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7842812804267568094L;

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		long tActiveGuestsNumber = forum.getActiveGuestsNumber();
		long tActiveMembersNumber = forum.getActiveMemberUserNames().size();
		Collection<String> tConnected = forum.getActiveMemberUserNames();
		String tConnectedAsString = "";
		for (String tCurrent : tConnected)
			tConnectedAsString += "\t" + tCurrent;
		returnObj.setHasExecuted(true);
		returnObj.setResponse("activenumbers\t" + 
				tActiveGuestsNumber + "\t" + tActiveMembersNumber + tConnectedAsString);
		return returnObj;
	}
}
