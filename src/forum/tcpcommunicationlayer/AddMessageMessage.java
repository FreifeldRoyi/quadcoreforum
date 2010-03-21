/**
 * 
 */
package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;

/**
 * @author Tomer Heber
 *
 */
public class AddMessageMessage extends ClientMessage {

	private static final long serialVersionUID = 8912617401305761411L;
	
	/* The content of the message to add. */
	private String m_content;

	public AddMessageMessage(String content) {
		m_content = content;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		// TODO
		return null;
	}

}
