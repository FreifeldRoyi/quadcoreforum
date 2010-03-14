package forum.server.domainlayer.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.ForumSubject;
import forum.server.domainlayer.interfaces.ForumThread;
import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.exceptions.subject.SubjectAlreadyExistsException;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.persistentlayer.pipe.PersistenceFactory;
import forum.server.persistentlayer.pipe.persistenceDataHandler;

public class ForumSubjectImpl extends NamedComponentImpl implements ForumSubject 
{

	private static long SUBJECT_ID_COUNTER = 0;

	private long subjectID;
	private Map<String, ForumSubject> subSubjects; // we assume that there are no two subjects with the same
	// in the same level
	private Vector<ForumThread> threads;

	public ForumSubjectImpl(String description, String name) 
	{

		super(description, name);
		this.subjectID = (SUBJECT_ID_COUNTER++);
		this.subSubjects = new HashMap<String, ForumSubject>();
		this.threads = new Vector<ForumThread>();
	}

	@Override
	public void addSubSubject(ForumSubject forumSubject) 
	throws JAXBException, IOException, SubjectAlreadyExistsException
	{
		if (this.subSubjects.get(forumSubject.getName()) != null)
			throw new SubjectAlreadyExistsException(forumSubject.getName());

		this.subSubjects.put(forumSubject.getName(), forumSubject);

		persistenceDataHandler pipe = PersistenceFactory.getPipe();

		try {
			pipe.addNewSubSubject(this.getSubjectID(), forumSubject.getSubjectID(), 
					forumSubject.getName(), forumSubject.getDescription());
		} catch (SubjectNotFoundException e) {
			e.printStackTrace();
		}
	}

	public long getSubjectID() {
		return this.subjectID;
	}

	@Override
	public int getNumOfThreads() 
	{
		int tAns = this.threads.size();
		for (ForumSubject tForumSubject : this.subSubjects.values())
			tAns += tForumSubject.getNumOfThreads();
		return tAns;
	}

	@Override
	public Vector<ForumSubject> getSubSubjects() 
	{
		return new Vector<ForumSubject>(this.subSubjects.values());
	}

	@Override
	public Vector<ForumThread> getThreads() 
	{
		return this.threads;
	}

	@Override
	public void openNewThread (ForumMessage root) throws JAXBException, IOException
	{
		this.threads.add(new ForumThreadImpl(root));
	}

	public String subjToString() 
	{
		String tAns = this.getName() + " " + this.getDescription();


		for (ForumThread tThread : this.threads)
			tAns += "\n\n" + tThread.threadToString();

		tAns += "\n" + "subSubjects {";
		for (ForumSubject tSubject : this.subSubjects.values())
			tAns += "\n\n" + tSubject.subjToString();

		tAns += "}";

		return tAns;
	}

	public ForumSubject getForumSubject(long id) throws SubjectNotFoundException {
		ForumSubject toReturn = this.subSubjects.get(id);
		if (toReturn == null) { 		
			for (ForumSubject tSubj : this.subSubjects.values()) {
				try 
				{
					toReturn = tSubj.getForumSubject(id);
					return toReturn;
				}
				catch (SubjectNotFoundException e)
				{
					continue;
				}

			}
			throw new SubjectNotFoundException(id);
		}
		return toReturn;
	}

	@Override
	public ForumMessage findMessage(long msgID) throws MessageNotFoundException 
	{
		ForumMessage toReturn = null;
		for (ForumSubject tSubj : this.subSubjects.values())
		{
			try 
			{
				toReturn = tSubj.findMessage(msgID);
				return toReturn;
			}
			catch (MessageNotFoundException e)
			{
				continue;
			}
		}
		for (ForumThread tThread : this.threads)
		{
			try 
			{
				toReturn = tThread.findMessage(msgID);
				return toReturn;
			}
			catch (MessageNotFoundException e)
			{
				continue;
			}
		}
		throw new MessageNotFoundException(msgID);
	}
}