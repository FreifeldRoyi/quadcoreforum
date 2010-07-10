package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UISubject;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;

public class ViewSubjectsMessage extends ClientMessage {

	private static final long serialVersionUID = -6669968859637812944L;

	/*
	 * The id of the root subject, whose sub-subjects' data should be returned.
	 * 	If the id is -1, then the forum root subjects data is returned
	 */
	private long fatherID;

	public ViewSubjectsMessage(final long fatherID){
		this.fatherID = fatherID;
	}

	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */
	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - I consider failure only in the case of an exception. Is it o.k???
		// Response (Vitali) --> Yes!!!

		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			Collection<UISubject> tRetrievedSubjects = forum.getSubjects(this.fatherID);
			// return a String representation of the retrieved subjects
			String tResponse = "";
			if (tRetrievedSubjects.isEmpty())
				tResponse = "There are no subjects under the root subject with id " + this.fatherID + " to view";
			else {
				Iterator<UISubject> iter = tRetrievedSubjects.iterator();
				while(iter.hasNext())
					tResponse += iter.next().toString() + "\n\t\r";
			}
			returnObj.setHasExecuted(true);
			returnObj.setResponse(tResponse);
		}
		catch (SubjectNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse(e.getMessage());
		}
		return returnObj;
	}
}
