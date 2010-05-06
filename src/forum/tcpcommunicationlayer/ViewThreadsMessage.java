/**
 * 
 */
package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.interfaces.UIThread;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;

/**
 * @author sepetnit
 *
 */
public class ViewThreadsMessage extends ClientMessage {

	private static final long serialVersionUID = -6416381943980457966L;

	/* The id of the subject whose threads should be returned. */
	private long subjectID;

	public ViewThreadsMessage(final long subjectID){
		this.subjectID = subjectID;
	}

	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj = new ServerResponse(this.getID(), "", true); 
		try {
			Collection<UIThread> tThreads = forum.getThreads(this.subjectID);

			String tResponse = "";

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