package forum.server.domainlayer.interfaces;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.exceptions.message.*;
import forum.server.exceptions.subject.*;
import forum.server.exceptions.user.*;


public interface ForumSubject extends NamedComponent {
	
	/* Getters */
	
	/**
	 * @return
	 * 		A collection of sub-subjects of this subject
	 */
	public Collection<ForumSubject> getSubSubjects();

	/**
	 *
	 * @return
	 * 		The total number of this subject thread (performs a recursive counting)
	 */
	public int getNumOfThreads();
	

	/**
	 * 
	 * @return
	 * 		A mapping of this forum threads (mapped to their ids)
	 */
	public Map<Long, String> getForumThreadsDesc();

	/**
	 * 
	 * @return
	 * 		The threads of this subject
	 */
	public Vector<ForumThread> getThreads();

	/**
	 * 
	 * @return
	 * 		The unique id of this subject
	 */
	public long getSubjectID();
	
	/**
	 * 
	 * Fins a sub-subject whose id is same as the given one
	 * 
	 * @param id
	 * 		The id of the subject which should be found
	 * @return
	 * 		The found subject
	 * 
	 * @throws SubjectNotFoundException
	 * 		If a subject with a same id as the given, wasn't found
	 */
	public ForumSubject getForumSubject(long id) throws SubjectNotFoundException;

	
	/* Methods */
	
	/**
	 * Opens a new thread in this subject
	 */
	public void openNewThread (ForumMessage root) throws JAXBException, IOException, NotRegisteredException, SubjectNotFoundException;
	
	/**
	 * 
	 * Adds a new sub-subject to this subject
	 * 
	 * @param fs
	 * 		The new sub-subject which should be added to this subject 
	 * 
	 * @throws JAXBException
	 * 		In case an error occurred while updating the database		
	 * @throws IOException
	 * 		In case an error occurred while updating the database		
	 * @throws SubjectAlreadyExistsException
	 * 		In case there already exists a sub-subject whose name is same as the given
	 */
	public void addSubSubject(ForumSubject fs) 
		throws JAXBException, IOException, SubjectAlreadyExistsException;
	
	
	/**
	 * Used only for the first time fill (adds a new sub-subject to this subject, without database updating)
	 */
	public void addSubSubjectToData(ForumSubject fs);

	/**
	 * Used only for the first time fill (adds a new thread to this subject, without database updating)
	 */
	public void addThreadToData(ForumThread ft);
		
	
	/**
	 * 
	 * @return
	 * 		A string representation of this subject, which contains all its contents, used escpecially for
	 * 		debugging
	 */
	public String subjToString();
	
	/**
	 * 
	 * Performs a recursive message search in this subject
	 * 
	 * @param msgID
	 * 		The id of the message which should be found
	 * @return
	 * 		A found message whose id is same as the given one
	 * 
	 * @throws MessageNotFoundException
	 * 		In case a message with id, same as the given, wasn't found
	 */
	public ForumMessage findMessage(long msgID) throws MessageNotFoundException; 
}