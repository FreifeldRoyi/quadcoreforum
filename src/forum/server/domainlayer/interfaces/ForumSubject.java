package forum.server.domainlayer.interfaces;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.pipe.Controller;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.exceptions.subject.*;
import forum.server.exceptions.user.NotRegisteredException;


public interface ForumSubject extends NamedComponent
{
	/* Methods */
	
	
	
	public void openNewThread (ForumMessage root) throws JAXBException, IOException, NotRegisteredException, SubjectNotFoundException;
	
	public void addSubSubject(ForumSubject fs) 
		throws JAXBException, IOException, SubjectAlreadyExistsException;
	
	/* Getters */
	public Vector<ForumSubject> getSubSubjects();
	
	public int getNumOfThreads();
	
	// new method
	
	public Map<Long, String> getForumThreadsDesc();
	
	
	
	/**
	 * Used only for the first time fill
	 */
	public void addSubSubjectToData(ForumSubject fs);
	
		
	
	public Vector<ForumThread> getThreads();
	
	public String subjToString();

	public long getSubjectID();
	
	public ForumSubject getForumSubject(long id) throws SubjectNotFoundException;

	public ForumMessage findMessage(long msgID) throws MessageNotFoundException; 
}

/**
 * TODO write proper JavaDoc for ForumSubject 
 */