package forum.server.domainlayer.interfaces;

import java.io.IOException;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.exceptions.subject.*;


public interface ForumSubject extends NamedComponent
{
	/* Methods */
	
	public void openNewThread (RegisteredUser author,  
			String msgTitle, String msgContent) throws JAXBException, IOException;
	
	public void addSubSubject(String decc, String subjectName) 
		throws JAXBException, IOException, SubjectNotFoundException;
	
	/* Getters */
	public Vector<ForumSubject> getSubSubjects();
	
	public int getNumOfThreads();
	
	// new method
	
	public Vector<ForumThread> getThreads();
	
	public String subjToString();

}

/**
 * TODO write proper JavaDoc for ForumSubject 
 */