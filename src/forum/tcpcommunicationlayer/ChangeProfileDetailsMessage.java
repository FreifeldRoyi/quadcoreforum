package forum.tcpcommunicationlayer;


import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author Lital Badash
 *
 */
public class ChangeProfileDetailsMessage extends ClientMessage {

	private static final long serialVersionUID = -2723317717299435031L;

	private long memberID;
	
	/* The user-name of the user. */
	private String username;

	/* The password of the user. */
	private String password;

	/* The user last name. */
	private String lastname;

	/* The user first name. */
	private String firstname;

	/* The e-mail of the user. */
	private String email;

	private boolean shouldAskPassword;

	public ChangeProfileDetailsMessage(final long memberID, final String username, final String password, final String lastname, 
			final String firstname, final String email, final boolean shouldAskPassword) {
		this.memberID = memberID;
		this.username = username;
		this.password = password;		
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.shouldAskPassword = shouldAskPassword;
	}


	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - again ot is not clear what the return value is in case of a failure - I assumed it is null.
		// response (Vitali) --> No! exception will be thrown.

		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		try {
			
				if (memberID < 0)
					memberID = forum.getMemberIdByUsernameAndOrEmail(username, email);
				UIMember tUpdatedMember = 
					forum.updateMemberProfile(memberID, this.username, this.password, 
							this.lastname, this.firstname, this.email, this.shouldAskPassword);

				returnObj.setResponse("profiledetailsupdatesuccess\t" + tUpdatedMember.getID() + "\t" + 
						tUpdatedMember.getUsername() + "\t" + tUpdatedMember.getFirstName() + "\t" +
						tUpdatedMember.getLastName() + "\t" + tUpdatedMember.getEmail());
				returnObj.setHasExecuted(true);

		}
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("profiledetailsupdateerror\tregistration\t" + e.getMessage());
		} 
		catch (MemberAlreadyExistsException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("profiledetailsupdateerror\texistingemail\t" + e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("profiledetailsupdateerror\tdatabase\t" + e.getMessage());
		}		
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("profiledetailsupdateerror\tdatabase\t" + e.getMessage());
		}		
		return returnObj;
	}
}
