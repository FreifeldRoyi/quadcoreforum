package forum.tcpcommunicationlayer;

import java.util.Iterator;
import java.util.Set;

import forum.server.ForumFacade;
import forum.server.presentationlayer.UIUser;
/**
 * @author Lital Badash
 *
 */
public class ViewActiveGuests extends ClientMessage {

	private static final long serialVersionUID = -5877989735408740590L;

	@Override
	public ServerResponse doOperation(ForumFacade forum) {
		ServerResponse returnObj=new ServerResponse("", true);
		//TODO the return value not specified in the case of failure...I considered failure of this operation if the set returned is null 
		Set<UIUser> answer =forum.getActiveGuests();
		if (answer==null){
			returnObj.setHasExecuted(false);
			returnObj.setResponse("Can't view the active guests in the Forum");
		}
		else{
			if (answer.isEmpty()){
				System.out.println("There are no active guests at the moment");
			}
			else{
				Iterator<UIUser> iter =answer.iterator();
				while(iter.hasNext()){
					System.out.println(iter.next().toString());
				}
			}
			returnObj.setHasExecuted(true);
			returnObj.setResponse("the Active guests can be viewed");
		}
			
		return returnObj;
	}

}
