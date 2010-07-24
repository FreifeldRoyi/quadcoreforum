/**
 * Represents a subject (or directory) in the forum 
 */
package forum.server.domainlayer.message;

import java.util.*;

import forum.server.domainlayer.interfaces.UISubject;

public class ForumSubject implements UISubject {
	
	private long subjectID;
	private String name;
	private String description;
	private Collection<Long> subSubjectsIDs;
	private Collection<Long> threadsIDs;
	private long fatherSubjectID;
	private long numOfDeepSubSubjects;
	private long numOfDeepMessages;
	

	/**
	 * A full constructor of the forum subject which initializes all its attributes according to the given
	 * parameters
	 * 
	 * This constructor is used while constructing the subject according to the database
	 * 
	 * @param id
	 * 		The id of the subject
	 * @param name
	 * 		The name of the subject
	 * @param description
	 * 		The description of the subject
	 * @param subSubjectsIDs
	 * 		A collection of this subject sub-subject ids
	 * @param threadsIDs
	 * 		A collection of this subjects threads ids
	 * @param numOfDeepSubSubjects
	 * 		The number of the subjects under this subject
	 * @param numOfDeepMessages
	 * 		The number of the messages under this subject
	 */
	public ForumSubject(long id, final String name, final String description, final Collection<Long> subSubjectsIDs,
			final Collection<Long> threadsIDs, long numOfDeepSubSubjects, long numOfDeepMessages, long fatherSubjectID) {
		this(id, name, description, fatherSubjectID);
		this.subSubjectsIDs.addAll(subSubjectsIDs);
		this.threadsIDs.addAll(threadsIDs);
		this.numOfDeepSubSubjects = numOfDeepSubSubjects;
		this.numOfDeepMessages = numOfDeepMessages;
	}	

	/**
	 * 
	 * The class constructor which is used to construct a new forum subject which doesn't exist in the database
	 * and initializes some of the fields with default values.
	 * 
	 * @param id
	 * 		The unique identification number of this subject
	 * @param name
	 * 		The name of this subject
	 * @param description
	 * 		The description of this subject
	 */
	public ForumSubject(long id, final String name, final String description, long fatherSubjectID) {
		this.name = name;
		this.description = description;
		this.subjectID = id;
		this.subSubjectsIDs = new Vector<Long>();
		this.threadsIDs = new Vector<Long>();
		this.numOfDeepSubSubjects = 0;
		this.numOfDeepMessages = 0;
		this.fatherSubjectID = fatherSubjectID;
	}

	// getters
	
	/**
	 * @see
	 * 		UISubject#getID()
	 */
	public long getID() {
		return this.subjectID;
	}

	/**
	 * @see
	 * 		UISubject#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @see
	 * 		UISubject#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @see
	 * 		UISubject#getNumOfSubSubjects()
	 */
	public long getNumOfSubSubjects() {
		return this.subSubjectsIDs.size();
	}

	/**
	 * @see
	 * 		UISubject#getNumOfThreads()
	 */
	public long getNumOfThreads() {
		return this.threadsIDs.size();
	}
	
	/**
	 * 
	 * @return
	 * 		A collection of this subject sub-subjects ids
	 */
	public Collection<Long> getSubSubjects() {
		return this.subSubjectsIDs;
	}

	/**
	 * 
	 * @return
	 * 		A collection of this subject threads ids
	 */
	public Collection<Long> getThreads() {
		return this.threadsIDs;
	}

	public long getDeepNumOfSubSubjects() {
		return this.numOfDeepSubSubjects;
	}

	public long getDeepNumOfMessages() {
		return this.numOfDeepMessages;
	}
	
	public void decDeepNumOfSubSubjectsBy(long number) {
		if (this.numOfDeepSubSubjects - number >= 0)
			this.numOfDeepSubSubjects -= number;
	}

	public void decDeepNumOfMessagesBy(long number) {
		if (this.numOfDeepMessages - number >= 0)
			this.numOfDeepMessages -= number;
	}

	public void incDeepNumOfSubSubjects() {
		this.numOfDeepSubSubjects++;
	}

	public void incDeepNumOfMessages() {
		this.numOfDeepMessages++;
	}
	
	
	public long getFatherID() {
		return this.fatherSubjectID;
	}
	
	/**
	 * 
	 * This method overrides the standard equals method and 
	 * checks whether two subjects are the same one 
	 * 
	 * @see
	 * 		Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return (obj != null) && (obj instanceof ForumSubject) && (((ForumSubject)obj).getID() == 
			this.getID());
	}
	
	/**
	 * @see
	 * 		UISubject#toString()
	 */
	public String toString() {
		return this.getID() + "\t" + this.getName() + "\t" + this.getDescription() + "\t" + this.getDeepNumOfSubSubjects() +
		"\t" + this.getDeepNumOfMessages();
	}

	/**
	 * 
	 * @return
	 * 		Whether this subject is a top level one
	 */
	public boolean isTopLevel() {
		return this.fatherSubjectID == -1;
	}

	// methods
	
	/**
	 * 
	 * Adds a new id of a sub-subject of this subject
	 * 
	 * @param subjectID
	 * 		The id of the new sub-subject which should be added to this subject
	 */
	public void addSubSubject(final long subjectID) {
		this.subSubjectsIDs.add(subjectID);
	}

	/**
	 * 
	 * Removes an id of a sub-subject from this subject
	 * 
	 * @param subjectID
	 * 		The id of the sub-subject which should be removed
	 */
	public void deleteSubSubject(final long subjectID) {
		this.subSubjectsIDs.remove(subjectID);
	}

	/**
	 * 
	 * Adds a new id of a thread of this subject
	 * 
	 * @param threadID
	 * 		The id of the new thread which should be added to this subject
	 */
	public void addThread(final long threadID) {
		this.threadsIDs.add(threadID);
	}

	/**
	 * 
	 * Removes an id of a thread from this subject
	 * 
	 * @param threadID
	 * 		The id of the thread which should be removed
	 */
	public void deleteThread(final long threadID) {
		this.threadsIDs.remove(threadID);
	}
	
	public void updateMe(String newName, String newDescription) {
		this.name = newName;
		this.description = newDescription;
	}	
}