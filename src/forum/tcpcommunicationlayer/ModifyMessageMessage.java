package forum.tcpcommunicationlayer;

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

	public ModifyMessageMessage(long messageId, String content) {
		m_messageId = messageId;
		m_content = content;
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
