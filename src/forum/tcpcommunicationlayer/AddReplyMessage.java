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
public class AddReplyMessage extends ClientMessage {

	private static final long serialVersionUID = 6721172261483674344L;

	/* The id of the new reply author */
	private long authorID;
	/* The id of The message to which the reply should be added */
	private long fatherID;
	/* The title of the reply message. */
	private String title;
	/* The content of the reply message. */
	private String content;


	public AddReplyMessage(final long authorID, final long fatherID, final String title, final String content) {
		this.authorID = authorID;
		this.fatherID = fatherID;
		this.title = title;
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		try {
			forum.addNewReply(this.authorID, this.fatherID, this.title, this.content);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("replysuccess");
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