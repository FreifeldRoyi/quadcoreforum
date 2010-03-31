package forum.server.domainlayer.impl.message;

import java.util.*;

import forum.server.domainlayer.impl.interfaces.UISubject;

public class ForumSubject extends NamedComponentImpl implements UISubject {
	private long subjectID;
	private Collection<Long> subSubjectsIDs;
	private Collection<Long> threadsIDs;
	
	/**
	 * @param id
	 * 		The unique identification number of this subject.
	 * @param name
	 * 		The name of this subject.
	 * @param description
	 * 		The description of this subject.
	 */
	public ForumSubject(long id, final String name, final String description) {
		super(name, description);
		this.subjectID = id;
		this.subSubjectsIDs = new Vector<Long>();
		this.threadsIDs = new Vector<Long>();
	}
	
	public ForumSubject(long id, final String name, final String description, final Collection<Long> subSubjects,
			final Collection<Long> threads) {
		this(id, name, description);
		this.subSubjectsIDs.addAll(subSubjects);
		this.threadsIDs.addAll(threads);
	}	
	
	public void addSubSubject(final long subjectID) {
		this.subSubjectsIDs.add(subjectID);
	}

	public void addThread(final long threadID) {
		this.threadsIDs.add(threadID);
	}

	public void deleteThread(final long threadID) {
		this.threadsIDs.remove(threadID);
	}
	
	
	

	public Collection<Long> getSubSubjects() {
		return this.subSubjectsIDs;
	}

	public Collection<Long> getThreads() {
		return this.threadsIDs;
	}

	public long getSubjectID() {
		return this.subjectID;
	}

	public long getNumOfThreads() {
		return this.threadsIDs.size();
	}

	public long getId() {
		return this.subjectID;
	}

	public String toString() {
		return this.getName() + " " + this.getDescription();
	}


}