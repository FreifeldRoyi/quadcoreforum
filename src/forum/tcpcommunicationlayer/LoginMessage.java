package forum.tcpcommunicationlayer;

import forum.server.ForumFacade;
import forum.server.presentationlayer.UIMember;

/**
 * @author Lital Badash
 *
 */
public class LoginMessage extends ClientMessage {

	private static final long serialVersionUID = -2723317717299435031L;
	
	/** 
	 * The username of the user. 
	 */
	private String m_username;
	
	/** 
	 * The password of the user. 
	 */
	private String m_password;

	public LoginMessage(String username, String password) {
		m_username = username;
		m_password = password; 
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj=new ServerResponse("", true); 
		UIMember answer = forum.login(m_username, m_password);
		//TODO - again ot is not clear what the return value is in case of a failure - I assumed it is null.
		if (answer!=null){	
			returnObj.setHasExecuted(true);
			returnObj.setResponse("you are logged in");

		}
		else {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum couldn't logged you in-check for your credentials");

		}
		
		return returnObj;
	}

}
