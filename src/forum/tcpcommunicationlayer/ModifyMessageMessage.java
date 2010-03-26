package forum.tcpcommunicationlayer;
import forum.server.ForumFacade;
import forum.server.exceptions.message.MessageNotFoundException;

/**
 * @author Tomer Heber
 *
 */
public class ModifyMessageMessage extends ClientMessage {

	private static final long serialVersionUID = -4738980852130566587L;
	
	/**
	 * The id of the message which the client wants to modify.
	 * (The forum is nested).
	 */
	private long m_messageId;
	
	/**
	 * The new content of the message.
	 */
	private String m_content;

	/**
	 * The new title of the message.
	 */
	private String m_title;

	public ModifyMessageMessage(long messageId, String title, String content) {
		m_messageId = messageId;
		m_content = content;
		m_title=title;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		//I assumed failure only in case of exception. Is it o.k??/  
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			forum.updateAMessage(m_messageId, m_title, m_content);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("The Forum changed the specified message data successfuly");

		}
		catch(MessageNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum could'nt change the message with the specified id" );
			
		}
		
		return returnObj;

	}

}
