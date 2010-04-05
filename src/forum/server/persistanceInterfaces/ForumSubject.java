package forum.server.persistanceInterfaces;

public interface ForumSubject 
{
	/*Methods*/
	public void addThread (ForumMessage frmMsg);
	public void addMessage (ForumThread frmThrd, ForumMessage frmMsg);
	public void addNewThread (ForumMessage frmMsg);
	public void incToThreadCount();
	public void decFromThreadCount();
	
	/*Getters*/
	public int getNumOfThreads();
}

/**
 * TODO write proper JavaDoc for ForumSubject
 */