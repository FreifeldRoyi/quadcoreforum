package forum.tcpcommunicationlayer;

import forum.server.ForumFacade;
import forum.server.exceptions.message.MessageNotFoundException;
/**
 * @author Lital Badash
 *
 */
public class AddReplyMessage extends ClientMessage {

	private static final long serialVersionUID = 6721172261483674344L;
	
	/**
	 * The id of The message to which the reply should be added
	 */
	private long m_parentMessageId;
	
	/**
	 * The content of the reply message.
	 */
	private String m_content;

	/**
	 * The title of the reply message.
	 */
	private String m_title;

	/**
	 * The user-name of the reply author 
	 */
	private String m_username;

	public AddReplyMessage(long parentMessageId,String username, String title, String content) {
		m_parentMessageId = parentMessageId;
		m_content = content;
		m_title=title;
		m_username=username;
		
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		//I assumed failure only in case of exception. Is it o.k??/  
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			forum.addNewReply(m_parentMessageId, m_username, m_title, m_content);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("The Forum added a new reply successfuly");

		}
		catch(MessageNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum could'nt add a new reply to the message with the specified id" );
			
		}
		
		return returnObj;
	}

}
