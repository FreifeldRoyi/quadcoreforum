package persistanceInterfaces;

public interface ForumThread 
{
	/*Methods*/
	public void addMessage (ForumMessage father, ForumMessage newMsg);
	public void add1NumOfViews();
	public void add1NumOfResponses();
	public void sub1NumOfResponses();
	
	/**
	 * Will return the subject of the root message
	 * in the thread.
	 */
	public void getThreadSubject();
	
}


/**
 * TODO  write proper JavaDoc
 */