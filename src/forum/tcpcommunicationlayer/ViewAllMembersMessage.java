/**
 * 
 */
package forum.tcpcommunicationlayer;

import java.util.Arrays;
import java.util.Collection;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;

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
			
			for (UIMember tCurrent : tUsernames) {
				String type = null;
				if (tCurrent.isAllowed(Permission.SET_MODERATOR))
					type = "ADMIN";
				else
					if (tCurrent.isAllowed(Permission.DELETE_MESSAGE))
						type = "MODERATOR";
					else
						type = "MEMBER";

				tResponse += "\n" + tCurrent.getID() + "\t" + tCurrent.toString() + "\t" + type;
			}
			
			returnObj.setResponse(tResponse);
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("Can't retrieve the forum members");
		}
		return returnObj;
	}
}
