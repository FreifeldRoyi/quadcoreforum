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
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author sepetnit
 *
 */
public class MemberDetailsMessage extends ClientMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = -374607605363040422L;

	private long memberID;

	public MemberDetailsMessage(final long memberID) {
		this.memberID = memberID;
	}

	/**
	 * @see
	 * 		forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			UIMember tMember = forum.getMemberByID(this.memberID);
			returnObj.setHasExecuted(true);
			String tResponse = "memberdetails\t" + tMember.getUsername() + "\t" +
			tMember.getFirstName() + "\t" + tMember.getLastName() + "\t" + tMember.getEmail();
			returnObj.setResponse(tResponse);
		}
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("memberdetailserror\tregistration");
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("memberdetailserror\tdatabase");
		}
		return returnObj;
	}
}
