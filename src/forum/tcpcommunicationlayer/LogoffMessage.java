package forum.tcpcommunicationlayer;

import forum.server.ForumFacade;
import forum.server.exceptions.user.NotConnectedException;

/**
 * @author Tomer Heber
 *
 */
public class LogoffMessage extends ClientMessage {

	private static final long serialVersionUID = -5965616226069995574L;
	/*
	 *  The user-name of the user who should be logged out
	 */
	String m_usename;
	
	public LogoffMessage(String username){
		m_usename=username;
	}
	
	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		 //TODO - again: It is not clear from the interface what is the return value in case of a failure or a success
		//I assumed that if an exception didn't occurred then everything is O.K!
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			forum.logout(m_usename);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("The user is logged out.");

		}
		catch(NotConnectedException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("the user is not connected.");

		}
		
		return returnObj;

	}

}
