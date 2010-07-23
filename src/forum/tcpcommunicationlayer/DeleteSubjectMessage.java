package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author Lital Badash
 *
 */
public class DeleteSubjectMessage extends ClientMessage {

	private static final long serialVersionUID = 6721172261483674344L;

	/* The id of the user who requests the deletion */
	private long userID;

	/* The id of the subject's father subject */
	private long fatherID;

	/* The id of the subject which should be deleted */
	private long subjectID;

	public DeleteSubjectMessage(final long userID, final long fatherID, final long subjectID) {
		this.userID = userID;
		this.fatherID = fatherID;
		this.subjectID = subjectID;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		try {
			forum.deleteASubject(userID, fatherID, subjectID);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("deletesubjectsuccess");
		}
		catch (SubjectNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("deletesubjecterror\n" + e.getMessage());
		}
		catch (NotRegisteredException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("deletesubjecterror\n" + e.getMessage());
		}
		catch (NotPermittedException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("deletesubjecterror\n" + e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("deletesubjecterror\n" + e.getMessage());
		}
		return returnObj;
	}
}