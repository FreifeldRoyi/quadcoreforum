/**
 * 
 */
package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.HashSet;


import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

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
		try {
			long tActiveGuestsNumber = forum.getActiveGuestsNumber();
			long tActiveMembersNumber = forum.getActiveMemberUserNames().size();
			Collection<String> tConnected = forum.getActiveMemberUserNames();
			Collection<String> tConnectedNames = new HashSet<String>();
			for (String username : tConnected) {
				long id = 0;
				try {
					id = forum.getMemberIdByUsernameAndOrEmail(username, null);
				} catch (NotRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					UIMember member = forum.getMemberByID(id);
					tConnectedNames.add(member.getFirstName() + " " + member.getLastName());
				} catch (NotRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String tConnectedAsString = "";
			for (String tCurrent : tConnectedNames)
				tConnectedAsString += "\t" + tCurrent;
			returnObj.setHasExecuted(true);
			returnObj.setResponse("activenumbers\t" + 
					tActiveGuestsNumber + "\t" + tActiveMembersNumber + tConnectedAsString);
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("activenumberserror\t");
		}
		return returnObj;
	}
}
