package forum.server.persistanceInterfaces;

public interface ForumThread 
{
	/*Methods*/
	public void addMessage (ForumMessage father, ForumMessage newMsg);
	public void incNumOfViews();
	public void incNumOfResponses();
	public void decNumOfResponses();
	
	/**
	 * Will return the subject of the root message
	 * in the thread.
	 */
	public void getThreadSubject();
	
}


/**
 * TODO  write proper JavaDoc
 */