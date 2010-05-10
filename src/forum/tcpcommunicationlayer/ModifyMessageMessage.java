package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author Tomer Heber
 *
 */
public class ModifyMessageMessage extends ClientMessage {

	private static final long serialVersionUID = -4738980852130566587L;

	/* The id of the user who requests to modify the message. */
	private long userID;
	/* The id of the message which the client wants to modify. */
	private long messageID;
	/* The new title of the message. */
	private String newTitle;
	/* The new content of the message. */
	private String newContent;


	public ModifyMessageMessage(final long userID, final long messageId, 
			final String title, final String content) {
		this.userID = userID;
		this.messageID = messageId;
		this.newTitle = title;
		this.newContent = content;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		// TODO: I assumed failure only in case of exception. Is it o.k??
		// Response (Vitali) ---> Yes!!!

		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			forum.updateAMessage(userID, messageID, newTitle, newContent);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("modifysuccess");
		}
		catch (MessageNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		catch (NotPermittedException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		return returnObj;
	}
}
