package forum.tcpcommunicationlayer;


import forum.server.domainlayer.ForumFacade;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;

/**
 * @author Lital Badash
 *
 */
public class RegisterMessage extends ClientMessage {

	private static final long serialVersionUID = -3267419208356408002L;

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

	public RegisterMessage(final String username, final String password, final String lastname, 
			final String firstname, final String email) {
		this.username = username;
		this.password = password;		
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			forum.registerNewMember(this.username, this.password, this.lastname, this.firstname, this.email);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("registersuccess\t" + "you successfuly registered the forum");
		}
		catch (MemberAlreadyExistsException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("registererror\t" + "The following data already exists: " + e.getMessage());
		} 
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("registererror\t" + e.getMessage());
		}
		return returnObj;
	}
}
