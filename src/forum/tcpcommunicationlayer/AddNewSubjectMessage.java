package forum.tcpcommunicationlayer;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.message.NotPermittedException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.message.exceptions.SubjectAlreadyExistsException;
import forum.server.persistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;

public class AddNewSubjectMessage extends ClientMessage {

	private static final long serialVersionUID = -2417678029351227054L;


	/* The id of the user who adds the subject */
	private long userID;	
	/*
	 *	The id of the root subject (to which a new sub-subject will be added),
	 *	can be -1 in case the subject should be added as one of the root subjects - at the top level. 
	 */
	private long fatherID;
	/*
	 *	The name of the new subject 
	 */
	private String subjectName;	
	/*
	 *	The Subject Description
	 */
	private String subjectDescription;

	public AddNewSubjectMessage(final long userID, final long fatherID, final String name, final String description){
		this.userID = userID;
		this.fatherID = fatherID;
		this.subjectName = name;
		this.subjectDescription = description;
	}

	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse("", true); 
		try {
			forum.addNewSubject(this.userID, this.fatherID, this.subjectName, this.subjectDescription);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("The new subject was added successfuly to the forum");
		} 
		catch (SubjectNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		catch (SubjectAlreadyExistsException e) {
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
