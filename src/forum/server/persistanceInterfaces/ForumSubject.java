package forum.server.persistanceInterfaces;

public interface ForumSubject 
{
	/*Methods*/
	public void addThread (ForumMessage frmMsg);
	public void addMessage (ForumThread frmThrd, ForumMessage frmMsg);
	public void addNewThread (ForumMessage frmMsg);
	public void add1ToThreadCount();
	public void sub1FromThreadCount();
	
	/*Getters*/
	public int getNumOfThreads();
}

/**
 * TODO write proper JavaDoc for ForumSubject
 */