/**
 * 
 */
package forum.tcpcommunicationlayer;

import forum.server.ForumFacade;
import forum.server.exceptions.subject.SubjectNotFoundException;


/**
 * @author Lital Badash
 *
 */
public class AddNewThread extends ClientMessage {

	private static final long serialVersionUID = 8912617401305761411L;

	/* The content of the message to add. */
	private String m_content;
	private String m_title; 
	private String m_username;
	private long m_subjectId;

	public AddNewThread(long subid,String username ,String title, String content) {
		m_subjectId = subid;
		m_username=username;
		m_title=title;
		m_content = content;

	}

	/* (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.domainlayer.ForumFacade)
	 * 
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		
		//TODO : the UIThread object should include a method which will announce if the operation did well.
		 
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			forum.openNewThread(m_subjectId, m_username, m_title, m_content);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("The Forum added a new thread with a started message successfuly");

		}
		catch(SubjectNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum could'nt add a new thread with a started message successfuly");

		}
		
		return returnObj;
	}

}
