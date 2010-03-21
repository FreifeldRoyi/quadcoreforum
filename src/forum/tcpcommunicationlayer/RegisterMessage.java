package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;

/**
 * @author Tomer Heber
 *
 */
public class RegisterMessage extends ClientMessage {

	private static final long serialVersionUID = -3267419208356408002L;
	
	/**
	 * The users real name.
	 */
	private String m_realname;
	
	/**
	 * The e-mail of the user.
	 */
	private String m_email;
	
	/** 
	 * The username of the user. 
	 */
	private String m_username;
	
	/** 
	 * The password of the user. 
	 */
	private String m_password;

	public RegisterMessage(String realname, String email, String username, String password) {
		m_realname = realname;
		m_email = email;
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
