package forum.tcpcommunicationlayer;

import forum.server.ForumFacade;

/**
 * @author Tomer Heber
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
		// TODO Auto-generated method stub
		return null;
	}

}
