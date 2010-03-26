package forum.tcpcommunicationlayer;

import java.util.Collection;
import java.util.Iterator;

import forum.server.ForumFacade;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.presentationlayer.UIMessage;

public class ViewMessageReplies extends ClientMessage {

	private static final long serialVersionUID = 6154143641519365134L;
	/*
	 * The id of the message whose replies should be represented
	 */
	long m_messageId;
	public ViewMessageReplies(long messageId){
		m_messageId=messageId;
	}
	/*
	 * (non-Javadoc)
	 * @see forum.tcpcommunicationlayer.ClientMessage#doOperation(forum.server.ForumFacade)
	 */
	
	public ServerResponse doOperation(ForumFacade forum) {
		//TODO - I consider failure only in the case of an exception. Is it o.k??? s 
		ServerResponse returnObj=new ServerResponse("", true); 
		try{
			Collection<UIMessage> ans= forum.getReplies(m_messageId);
			//TODO - print the collection to the screen. 
			if (ans.isEmpty()){
				System.out.println("There are no replies under the root message with id "+ m_messageId +"to view");
			}
			else{
				Iterator<UIMessage> iter =ans.iterator();
				while(iter.hasNext()){
					System.out.println(iter.next().toString());
				}
			}
			returnObj.setHasExecuted(true);
			returnObj.setResponse("view messages content");

		}
		catch(MessageNotFoundException e) {
			returnObj.setHasExecuted(false);
			returnObj.setResponse("The Forum could'nt display the message content -check for the root message id ");

		}
		
		return returnObj;

	}

}
