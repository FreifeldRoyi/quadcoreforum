package forum.server.domainlayer.interfaces;

import java.io.IOException;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.exceptions.subject.*;


public interface ForumSubject 
{
	/* Methods */
	
	public void openNewThread (RegisteredUser author,  String msgTitle, String msgContent);
	
	public void incThreadCount();
	public void decThreadCount();
	
	public void addSubject(String subjectName) throws JAXBException, IOException, SubjectNotFoundException;
	
	/* Getters */
	public Vector<ForumSubject> getSubSubjects();
	
	public int getNumOfThreads();
}

/**
 * TODO write proper JavaDoc for ForumSubject
 * 
 * TODO make names to be more expressive, for example: incThreadCount
 * 
 * TODO: may be sticky
 */