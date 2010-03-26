package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.ForumFacade;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.presentationlayer.UIThread;

public class ViewSubjectContent extends ClientMessage {

	private static final long serialVersionUID = -6416381943980457966L;
	/*
	 * The id of the subject whose threads should be represented
	 */
	long m_rootSubjectId;
	public ViewSubjectContent(long rootId){
		m_rootSubjectId=rootId;
	}
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - I consider failure only in the case of an exception. Is it o.k??? s 
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			Collection<UIThread> ans= forum.getThreads(m_rootSubjectId);
			//TODO - print the collection to the screen. 
			if (ans.isEmpty()){
				System.out.println("There is no data to present under the root subject with id "+ m_rootSubjectId);
			}
			else{
				Iterator<UIThread> iter =ans.iterator();
				while(iter.hasNext()){
					System.out.println(iter.next().toString());
				}
			}
			returnObj.setHasExecuted(true);
			returnObj.setResponse("view subject");

		}
		catch(SubjectNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum could'nt display the subject content -check for the root subject id ");

		}
		
		return returnObj;

	}

}
