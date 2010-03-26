package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.ForumFacade;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.presentationlayer.UISubject;

public class ViewSubjects extends ClientMessage {

	private static final long serialVersionUID = -6669968859637812944L;
	/*
	 * The id of the root subject, whose sub-subjects' data should be returned.
	 * 	If the id is -1, then the forum root subjects data is returned
	 */
	long m_rootSubjectId;
	
	public ViewSubjects(long id){
		m_rootSubjectId=id;
	}
	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
	//TODO - I consider failure only in the case of an exception. Is it o.k??? s 
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			Collection<UISubject> ans= forum.getSubjects(m_rootSubjectId);
			// print the collection to the screen.
			if (ans.isEmpty()){
				System.out.println("There are no subjects under the root subject with id "+ m_rootSubjectId +"to view");
			}
			else{
				Iterator<UISubject> iter =ans.iterator();
				while(iter.hasNext()){
					System.out.println(iter.next().toString());
				}
			}
			returnObj.setHasExecuted(true);
			returnObj.setResponse("view subject");

		}
		catch(SubjectNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum could'nt display the subjects -check for the root subject id ");

		}
		
		return returnObj;

	}

}
