package forum.tcpcommunicationlayer;

import forum.server.ForumFacade;
import forum.server.exceptions.user.UserAlreadyExistsException;

/**
 * @author Lital Badash
 *
 */
public class RegisterMessage extends ClientMessage {

	private static final long serialVersionUID = -3267419208356408002L;
	
	/**
	 * The user last name.
	 */
	private String m_lastname;

	/**
	 * The user first name.
	 */
	private String m_firstname;
	
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

	public RegisterMessage(String username, String password , String lastname ,String firstname, String email) {
		m_firstname = firstname;
		m_lastname = lastname;
		m_email = email;
		m_username = username;
		m_password = password;		
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			forum.registerToForum(m_username, m_password, m_lastname, m_firstname, m_email);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("you successfuly registered the forum");

		}
		catch(UserAlreadyExistsException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum could'nt register you to the forum, the user name you chose already exist in the forum");

		}
		
		return returnObj;

	}

}
