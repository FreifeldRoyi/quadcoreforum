package forum.tcpcommunicationlayer;


import forum.server.domainlayer.ForumFacade;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.WrongPasswordException;

/**
 * @author Lital Badash
 *
 */
public class PasswordChangeMessage extends ClientMessage {

	private static final long serialVersionUID = -2723317717299435031L;

	private long memberID;
	private String prevPassword;
	private String newPassword;
	private boolean shouldAskNewPassword;

	public PasswordChangeMessage(final long memberID, final String prevPassword, final String newPassword, 
			final boolean shouldAskNewPassword) {
		this.memberID = memberID;
		this.prevPassword = prevPassword;		
		this.newPassword = newPassword;
		this.shouldAskNewPassword = shouldAskNewPassword;
	}


	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - again ot is not clear what the return value is in case of a failure - I assumed it is null.
		// response (Vitali) --> No! exception will be thrown.

		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		try {

			forum.updateMemberPassword(this.memberID, this.prevPassword, this.newPassword, this.shouldAskNewPassword);
			returnObj.setResponse("passwordupdatesuccess");
			returnObj.setHasExecuted(true);

		}
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("passwordupdateerror\tregistration\t" + e.getMessage());
		} 
		catch (WrongPasswordException e) {
			System.out.println("Got wrong password ...");
			returnObj.setHasExecuted(false);
			returnObj.setResponse("passwordupdateerror\tpassword\t" + e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("passwordupdateerror\tdatabase\t" + e.getMessage());
		}
		return returnObj;
	}
}
