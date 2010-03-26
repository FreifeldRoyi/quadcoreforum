package forum.tcpcommunicationlayer;

import java.util.Set;

import forum.server.ForumFacade;
/**
 * @author lital Badash
 */
public class ViewActiveMemberNames extends ClientMessage {
	private static final long serialVersionUID = 7605975254525695088L;

	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj=new ServerResponse("", true);
		//TODO the return value not specified in the case of failure...I considered failure of this operation if the set returned is null
		//also - the method getActiveMemberNames should also print the list.It is not specified.  
		Set<String> answer =forum.getActiveMemberNames();
		if (answer==null){
			returnObj.setHasExecuted(false);
			returnObj.setResponse("Can't view the active member names in the Forum");
		}
		else{
			//TODO -print the set to the screen
			returnObj.setHasExecuted(true);
			returnObj.setResponse("the Active member names can be viewed");
		}
			
		return returnObj;

	}

}
