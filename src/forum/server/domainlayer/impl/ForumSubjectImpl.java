package forum.server.domainlayer.impl;

import java.io.IOException;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.interfaces.ForumSubject;
import forum.server.domainlayer.interfaces.ForumThread;
import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.exceptions.subject.SubjectNotFoundException;

public class ForumSubjectImpl extends NamedComponentImpl implements ForumSubject 
{
	public Vector<ForumSubject> subSubjects;
	public Vector<ForumThread> threads;
	
	public ForumSubjectImpl(String desc, String name) 
	{
		super(desc, name);
		this.subSubjects = new Vector<ForumSubject>();
		this.threads = new Vector<ForumThread>();
	}

	@Override
	public void addSubSubject(String desc, String subjectName) 
			throws JAXBException, IOException, SubjectNotFoundException 
	{
		this.subSubjects.add(new ForumSubjectImpl(desc, subjectName));
		/**
		 * TODO add to data base.. can't really figure out if i need to hold
		 * a data base object or not.
		 */
	}

	@Override
	public int getNumOfThreads() 
	{
		return this.threads.size();
	}

	@Override
	public Vector<ForumSubject> getSubSubjects() 
	{
		return this.subSubjects;
	}

	@Override
	public Vector<ForumThread> getThreads() 
	{
		return this.threads;
	}

	@Override
	public void openNewThread(RegisteredUser author, String msgTitle,
			String msgContent) throws JAXBException,
			IOException 
	{
		/**
		 * TODO implement Forum Message first
		 */
	}

	public String subjToString() {
		String tAns = this.getName() + " " + this.getDescription() + "\n\n";
		
		for (ForumThread tThread : this.threads)
			tAns += "\n\n" + tThread.threadToString();
		
		for (ForumSubject tSubject : this.subSubjects)
			tAns += "\n\n" + tSubject.subjToString();
		
		return tAns;
	}
	
}
