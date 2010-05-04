package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.message.exceptions.MessageNotFoundException;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author Lital Badash
 *
 */
public class DeleteMessageMessage extends ClientMessage {

	private static final long serialVersionUID = 6721172261483674344L;

	/* The id of the user who requests the deletion */
	private long userID;

	/* The id of The father message whose reply with id messageID should be deleted */
	private long fatherMessageID;

	/* The id of The message which should be deleted */
	private long messageID;

	

	public DeleteMessageMessage(final long userID, final long fatherMessageID, final long messageID) {
		this.userID = userID;
		this.fatherMessageID = fatherMessageID;
		this.messageID = messageID;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		try {
			forum.deleteAMessage(userID, fatherMessageID, messageID);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("deletesuccess");
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