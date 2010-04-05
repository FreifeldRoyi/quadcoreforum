package forum.tcpcommunicationlayer;

import java.io.Serializable;

import forum.server.domainlayer.ForumFacade;

/**
 * This is the interface for all the messages sent by the client to the server.
 * 
 * @author Tomer Heber
 */
public abstract class ClientMessage implements Serializable {
	
	private static final long serialVersionUID = 8544383780166627711L;

	/**
	 * The doOperation method usually does some operation on the forum (through the facade).
	 * After the operation is done a response is returned, and should be sent back to the client.
	 * 
	 * @param forum A facade to communicate with the Domain layer.
	 * @return ServerResponse A response that should be sent back to the client
	 */
	public abstract ServerResponse doOperation(ForumFacade forum);
		
}
