package forum.server.persistanceInterfaces;

public interface ForumSubject 
{
	/* Methods */
	
	//public void addThread (ForumMessage frmMsg);
	//public void addMessage (ForumThread frmThrd, ForumMessage frmMsg);
	public void addNewThread (ForumMessage frmMsg);
	public void incThreadCount();
	public void decThreadCount();
	
	/* Getters */
	
	public int getNumOfThreads();
}

/**
 * TODO write proper JavaDoc for ForumSubject
 * 
 * TODO make names to be more expressive, for example: incThreadCount
 * 
 * TODO: may be sticky
 */