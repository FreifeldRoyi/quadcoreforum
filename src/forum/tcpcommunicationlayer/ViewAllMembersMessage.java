/**
 * 
 */
package forum.tcpcommunicationlayer;

import java.util.Collection;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.persistentlayer.DatabaseRetrievalException;

/**
 * @author sepetnit
 *
 */
public class ViewAllMembersMessage extends ClientMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = -374607605363040422L;

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			Collection<UIMember> tUsernames = forum.getAllMembers();
			returnObj.setHasExecuted(true);
			String tResponse = "activeusernames\t";
			for (UIMember tCurrent : tUsernames)
				tResponse += tCurrent.toString() + "\n";
			returnObj.setResponse(tResponse);
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("Can't retrieve the forum members");
		}
		return returnObj;
	}
}
