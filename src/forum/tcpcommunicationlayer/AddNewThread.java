/**
 * 
 */
package forum.tcpcommunicationlayer;

import forum.server.ForumFacade;

/**
 * @author Tomer Heber
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
	 *\\TODO what is the convention of the success / failure message of the open new thread operation.
	 *\\ it should be written in the facade interface 
	 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj=new ServerResponse("", true); 
		String operation_resault = forum.openNewThread(m_subjectId, m_username, m_title, m_content);
		if (operation_resault.contains("success"))
		{
			returnObj.setHasExecuted(true);
			returnObj.setResponse(operation_resault);
			
		}
		else
		{
			returnObj.setHasExecuted(false);
			returnObj.setResponse(operation_resault);
		}
		
		return returnObj;
	}

}
