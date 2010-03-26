package forum.tcpcommunicationlayer;

import forum.server.ForumFacade;
import forum.server.exceptions.subject.SubjectNotFoundException;

public class AddNewSubject extends ClientMessage {
	
	private static final long serialVersionUID = -2417678029351227054L;
	/*
	 *	The id of the root subject (to which a new sub-subject will be added),
	 *	can be -1 in case the subject should be added as one of the root subjects - at the top level. 
	 */
	private long m_fatherMessageId;
	/*
 	*	The name of the new subject 
 	*/
	private String m_subjectName;	
	/*
 	*	The Subject Description
 	*/
	private String m_subjectDescription;
	
	public AddNewSubject(long id, String name, String desc){
		m_fatherMessageId=id;
		m_subjectDescription=desc;
		m_subjectName=name;
	}
	/*
 * (non-Javadoc)
 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
 */
	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			forum.addNewSubject(m_fatherMessageId, m_subjectName, m_subjectDescription);
			returnObj.setHasExecuted(true);
			returnObj.setResponse("The Forum added a new subject successfuly");

		}
		catch(SubjectNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum could'nt add a new subject check the father message ID correctness");

		}
		
		return returnObj;

	}

}
