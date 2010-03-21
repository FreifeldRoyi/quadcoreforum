package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;

/**
 * @author Tomer Heber
 *
 */
public class AddReplyMessage extends ClientMessage {

	private static final long serialVersionUID = 6721172261483674344L;
	
	/**
	 * The id of the message to which the reply message is added to.
	 * (The forum is nested).
	 */
	private long m_parentMessageId;
	
	/**
	 * The content of the reply message.
	 */
	private String m_content;

	public AddReplyMessage(long parentMessageId, String content) {
		m_parentMessageId = parentMessageId;
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
