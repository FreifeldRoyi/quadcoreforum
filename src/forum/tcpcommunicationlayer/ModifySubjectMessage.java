package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectAlreadyExistsException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author Lital Badash
 *
 */
public class ModifySubjectMessage extends ClientMessage {

	private static final long serialVersionUID = -2723317717299435031L;
	
	private long userID;
	private long subjectID;
	private String name;
	private String description;

	public ModifySubjectMessage(long userID, long subjectID, String name, String description) {
		this.userID = userID;
		this.subjectID = subjectID;
		this.name = name;
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		
		ServerResponse returnObj = new ServerResponse(this.getID(),"", true); 
		
		try {
			forum.updateASubject(userID, subjectID, name, description);
			returnObj.setResponse("subjectupdatesuccess\t" + subjectID);
			returnObj.setHasExecuted(true);
		}
		catch (NotRegisteredException e) {
			returnObj.setResponse("subjectupdatefailed");
			returnObj.setHasExecuted(false);
		} 
		catch (NotPermittedException e) {
			returnObj.setResponse("subjectupdatefailed");
			returnObj.setHasExecuted(false);
		}
		catch (SubjectNotFoundException e) {
			returnObj.setResponse("subjectupdatefailed");
			returnObj.setHasExecuted(false);
		}
		catch (SubjectAlreadyExistsException e) {
			returnObj.setResponse("subjectupdatefailed");
			returnObj.setHasExecuted(false);
		}
		catch (DatabaseUpdateException e) {
			returnObj.setResponse("subjectupdatefailed");
			returnObj.setHasExecuted(false);
		}
		return returnObj;
	}
}
