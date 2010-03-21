package forum.server.domainlayer.impl;

import java.io.IOException;
import java.util.*;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.impl.message.ForumSubjectImpl;
import forum.server.domainlayer.impl.message.ForumThreadImpl;
import forum.server.domainlayer.impl.message.NamedComponentImpl;
import forum.server.domainlayer.impl.user.ForumMessageImpl;
import forum.server.domainlayer.impl.user.RegisteredUserImpl;
import forum.server.domainlayer.interfaces.*;
import forum.server.exceptions.message.*;
import forum.server.exceptions.subject.*;
import forum.server.exceptions.user.*;

import forum.server.persistentlayer.pipe.*;
import forum.server.persistentlayer.*;

public class ForumImpl extends NamedComponentImpl implements Forum {
	private Vector<ForumSubject> subjects;
	private Map<String, RegisteredUser> registeredUsers;
	private Map<String, RegisteredUser> registeredUsersByEmail;

	private Map<String, RegisteredUser> connectedUsers;


	/**
	 * Constructs the forum objects according to the database
	 */
	public ForumImpl() {
		subjects = new Vector<ForumSubject>();
		registeredUsers = new HashMap<String, RegisteredUser>();
		connectedUsers = new HashMap<String, RegisteredUser>();
		registeredUsersByEmail = new HashMap<String, RegisteredUser>();
		try {
			updateForumByDatabase();
		}
		catch (JAXBException e) {
			// TODO: care the case when a database read occurred
			e.printStackTrace();
		}
	}

	/**
	 * Here are the methods for the forum initializing according to the database
	 */

	/**
	 * The main update method, reads the forum data from the database and constructs the appropriate instances
	 * of forum domain objects, acts as a converter from persistence to domain.
	 * 
	 * @throws JAXBException
	 * 		In case an error occured while trying read from the database
	 */
	private void updateForumByDatabase() throws JAXBException {
		ForumType tForumType = PersistenceFactory.getPipe().getForumFromDatabase();

		// first constructs all the users according to the database
		for (UserType tUserType : tForumType.getRegisteredUsers()) {
			RegisteredUser tUser = new RegisteredUserImpl(tUserType.getUsername(), tUserType.getPassword(),
					tUserType.getFirstName(), tUserType.getLastName(), tUserType.getEMail());
			this.registeredUsers.put(tUser.getUsername(), tUser);
			this.registeredUsersByEmail.put(tUser.getEMail(), tUser);	
		}

		// construct the forum content: subjects + threads + messages
		for (SubjectType tSubject : tForumType.getForumSubjects())
			this.subjects.add(constructForumSubject(tSubject));
	}

	/**
	 * Constructs a subject of type SubjectType according to a given database SubjectType object
	 * 
	 * @param subjType
	 * 		The database subject from which this subject is constructed
	 * @return
	 * 		The constructed ForumSubject which contains all the data of the given database subject
	 */
	private ForumSubject constructForumSubject(SubjectType subjType) {
		ForumSubject toReturn = new ForumSubjectImpl(subjType.getSubjectID(), subjType.getDescription(), 
				subjType.getName());

		// adds all the sub-subjects according to the database
		for (SubjectType tSubjectType : subjType.getSubSubjects())
			toReturn.addSubSubjectToData(constructForumSubject(tSubjectType));

		// adds all the threads according to the database
		for (ThreadType tThreadType : subjType.getSubThreads())
			toReturn.addThreadToData(constructForumThread(tThreadType));
		
		return toReturn;	
	}

	/**
	 * Constructs a ForumThread domain object instance, according to a ThreadType persistent object
	 * (from the database)
	 * 
	 * @param threadType
	 * 		The ThreadType persistence object, from which the data should be taken
	 * @return
	 * 		A ForumThread domain object, filled with the data of the given ThreadType object 
	 */
	private ForumThread constructForumThread(ThreadType threadType) {
		return new ForumThreadImpl(constructForumMessage(threadType.getStartMessage()),
				null, threadType.getNumOfResponses(),
				threadType.getNumOfViews());
	}

	/**
	 * Constructs a new ForumMessage object instance, according to the given MessageType persistent object
	 * (from the database)
	 * 
	 * @param msgType
	 * 		The MessageType persistence object, from which the data should be taken
	 * @return
	 * 		A ForumMessage domain object, filled with the data of the given MessageType object
	 */
	private ForumMessage constructForumMessage(MessageType msgType) {
		// creates the new message
		ForumMessage toReturn = new ForumMessageImpl(this.registeredUsers.get(msgType.getAuthor()),
				msgType.getTitle(), msgType.getContent());
		// add all the replies to the message
		for (MessageType tMsgType : msgType.getReplies()) {
			toReturn.addMessageReplyData(constructForumMessage(tMsgType));
		}
		return toReturn;
	}

	
	/**
	 * Here is the end of the initialize methods
	 */

	
	/* Getters */
	
	public Vector<RegisteredUser> getConnectedUsers() {
		return new Vector<RegisteredUser>(this.connectedUsers.values());
	}

	public int getNumOfConnectedUsers() {
		return this.connectedUsers.size();
	}
	

	public RegisteredUser getUserByUsername(String username) throws NotRegisteredException {
		RegisteredUser toReturn = this.registeredUsers.get(username);
		if (toReturn != null)
			return toReturn;
		throw new NotRegisteredException(username);
	}

	public Map<Long, String> getForumThreadsBySubjectID(long rootSubjectID) throws SubjectNotFoundException {		
		return this.getForumSubjectByID(rootSubjectID).getForumThreadsDesc();
	}
	
	public ForumSubject getForumSubjectByID(long id) throws SubjectNotFoundException {
		for (ForumSubject tSubj : this.subjects) {
			if (tSubj.getSubjectID() == id)
				return tSubj;
			try {
				ForumSubject toReturn = tSubj.getForumSubject(id);
				return toReturn;
			}
			catch (SubjectNotFoundException e) {
				continue;
			}		
		}
		throw new SubjectNotFoundException(id);
	}

	public Vector<ForumSubject> getForumSubjects() {
		return this.subjects;
	}

	
	public ForumMessage getMessageByID(long msgID) throws MessageNotFoundException {
		ForumMessage toReturn = null;

		for (ForumSubject tSubj : this.subjects) {
			try {
				toReturn = tSubj.findMessage(msgID);
				return toReturn;
			}
			catch (MessageNotFoundException e) {
				continue;
			}
		}

		throw new MessageNotFoundException(msgID);
	}

	/* Methods */
	
	public void addForumSubject(ForumSubject fs) throws 
	JAXBException, IOException, SubjectAlreadyExistsException {
		for (ForumSubject tForumSubject : subjects)
			if (tForumSubject.getName().equals(fs.getName()))
				throw new SubjectAlreadyExistsException(fs.getName());
		subjects.add(fs);
		PersistenceDataHandler pipe = PersistenceFactory.getPipe();		
		pipe.addNewSubject(fs.getSubjectID(), fs.getName(), fs.getDescription());
	}

	public RegisteredUser login(String username, String password)
	throws AlreadyConnectedException, NotRegisteredException, WrongPasswordException {

		if (this.connectedUsers.get(username) != null)
			throw new AlreadyConnectedException(username);


		RegisteredUser tUser = this.registeredUsers.get(username);

		if (tUser == null)
			throw new NotRegisteredException(username);

		if (tUser.getPassword().equals(password)) {
			this.connectedUsers.put(username, tUser);
			return tUser;
		}
		throw new WrongPasswordException();
	}

	public void logout(String username) throws NotConnectedException {
		RegisteredUser tUser = this.connectedUsers.get(username);

		if (tUser == null)
			throw new NotConnectedException(username);

		this.connectedUsers.remove(username);
	}

	public void registerUser(RegisteredUser user) throws UserAlreadyExistsException, JAXBException, IOException {
		if (this.registeredUsers.get(user.getUsername()) != null)
			throw new UserAlreadyExistsException("There already exists a user with the username " 
					+ user.getUsername());

		if (this.registeredUsersByEmail.get(user.getEMail()) != null)
			throw new UserAlreadyExistsException("There already exists a user with the email " 
					+ user.getEMail());


		this.registeredUsers.put(user.getUsername(), user);

		PersistenceDataHandler pipe = PersistenceFactory.getPipe();

		pipe.registerToForum(user.getUsername(), user.getPassword(), user.getLastName(), 
				user.getPrivateName(), user.getEMail());	
	}

	public void updateAMessage(long messageID, String newTitle,
			String newContent) throws JAXBException, IOException, MessageNotFoundException {
		this.getMessageByID(messageID).updateMe(newTitle, newContent);
		
	}
}