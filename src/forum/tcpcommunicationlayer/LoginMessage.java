package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;
import forum.server.persistentlayer.pipe.user.exceptions.WrongPasswordException;

/**
 * @author Lital Badash
 *
 */
public class LoginMessage extends ClientMessage {

	private static final long serialVersionUID = -2723317717299435031L;

	/* The user-name of the user. */
	private String username;
	/* The password of the user. */
	private String password;

	public LoginMessage(String username, String password) {
		this.username = username;
		this.password = password; 
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - again ot is not clear what the return value is in case of a failure - I assumed it is null.
		// response (Vitali) --> No! exception will be thrown.
				
		ServerResponse returnObj = new ServerResponse("", true); 
		try {
			UIMember tResponse = forum.login(this.username, this.password);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("Welcome " + "\t" + tResponse.getID() + "\t" + 
					tResponse.getUsername() + "\t" +
					tResponse.getLastName() + " " +
					tResponse.getFirstName());
		} 
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		} 
		catch (WrongPasswordException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}		
		return returnObj;
	}
}
