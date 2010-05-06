/**
 * 
 */
package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;


/**
 * @author Lital Badash
 *
 */
public class AddNewThreadMessage extends ClientMessage {

	private static final long serialVersionUID = 8912617401305761411L;

	/* The topic of the new thread. */
	private String topic;
	/* The id of the thread creator */
	private long userID;
	/* The id of the subject under which the new thread should be created */
	private long subjectId;
	/* The title of the thread's root message. */
	private String title; 
	/* The content of the thread's root message. */
	private String content;

	public AddNewThreadMessage(final long userID, final long subjectID, final String topic ,final String title, final String content) {
		this.userID = userID;
		this.subjectId = subjectID;
		this.topic = topic;
		this.title = title;
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 * 
	 */
	public ServerResponse doOperation(final ForumFacade forum) {

		//TODO : the UIThread object should include a method which will announce if the operation did well.
		// response (Vitali) --> made!!!

		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 

		try {
			forum.openNewThread(this.userID, this.subjectId, this.topic, this.title, this.content);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("addthreadsuccess");
		}
		catch (SubjectNotFoundException e) {
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
		}
		return returnObj;
	}
}
