package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UISubject;
import forum.server.domainlayer.interfaces.UIThread;
import forum.server.persistentlayer.DatabaseRetrievalException;

import forum.server.persistentlayer.pipe.message.exceptions.*;

public class ViewSubjectContentMessage extends ClientMessage {

	private static final long serialVersionUID = -6416381943980457966L;

	/* The id of the subject whose sub-subjects and threads should be represented. */
	private long subjectID;

	public ViewSubjectContentMessage(final long subjectID){
		this.subjectID = subjectID;
	}

	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - I consider failure only in the case of an exception. Is it o.k??? s 
		// Response (Vitali) --> Yes!!!

		ServerResponse returnObj = new ServerResponse("", true); 
		try {
			Collection<UISubject> tSubSubjects = forum.getSubjects(this.subjectID);
			Collection<UIThread> tThreads = forum.getThreads(this.subjectID);

			String tResponse = "";

			if (tSubSubjects.isEmpty())
				tResponse += "There are no sub-subjects to present under the root subject with id " + this.subjectID + ".\n";
			else {
				Iterator<UISubject> tIter = tSubSubjects.iterator();
				while(tIter.hasNext()) {
					tResponse += tIter.next().toString() + "\n";
				}
			}
			tResponse += "\n";

			if (tThreads.isEmpty())
				tResponse += "There are no threads to present under the root subject with id " + this.subjectID + ".\n";
			else {
				Iterator<UIThread> tIter = tThreads.iterator();
				while(tIter.hasNext()) {
					tResponse += tIter.next().toString() + "\n";
				}
			}
			returnObj.setHasExecuted(true);
			returnObj.setResponse(tResponse);
		}
		catch (SubjectNotFoundException e) {
			returnObj.setHasExecuted(true);
			returnObj.setResponse(e.getMessage());
		} 
		catch (DatabaseRetrievalException e) {
			returnObj.setHasExecuted(true);
			returnObj.setResponse(e.getMessage());
		}
		return returnObj;
	}
}