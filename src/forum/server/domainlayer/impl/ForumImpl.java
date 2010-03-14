package forum.server.domainlayer.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.interfaces.Forum;
import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.ForumSubject;
import forum.server.domainlayer.interfaces.ForumThread;
import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.exceptions.subject.SubjectAlreadyExistsException;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.exceptions.user.*;
import forum.server.persistentlayer.ForumType;
import forum.server.persistentlayer.MessageType;
import forum.server.persistentlayer.SubjectType;
import forum.server.persistentlayer.ThreadType;
import forum.server.persistentlayer.UserType;
import forum.server.persistentlayer.pipe.PersistenceFactory;
import forum.server.persistentlayer.pipe.persistenceDataHandler;

public class ForumImpl extends NamedComponentImpl implements Forum 
{
	private Vector<ForumSubject> subjects;

	private Map<String, RegisteredUser> registeredUsers;
	private Map<String, RegisteredUser> registeredUsersByEmail;

	private Map<String, RegisteredUser> connectedUsers;



	public ForumImpl() 
	{
		subjects = new Vector<ForumSubject>();
		registeredUsers = new HashMap<String, RegisteredUser>();
		connectedUsers = new HashMap<String, RegisteredUser>();
		registeredUsersByEmail = new HashMap<String, RegisteredUser>();
		updateForumByDatabase();
	}

	private void updateForumByDatabase()
	{
		try {
			ForumType tForumType = PersistenceFactory.getPipe().getForumFromDatabase();

			for (UserType tUserType : tForumType.getRegisteredUsers()) {

				RegisteredUser tUser = new RegisteredUserImpl(tUserType.getUsername(), tUserType.getPassword(),
						tUserType.getFirstName(), tUserType.getLastName(), tUserType.getEMail());

				this.registeredUsers.put(tUser.getUsername(), tUser);
				this.registeredUsersByEmail.put(tUser.getEMail(), tUser);	
			}

			for (SubjectType tSubject : tForumType.getForumSubjects())
				this.subjects.add(constructSubSubject(tSubject));



		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private ForumSubject constructSubSubject(SubjectType subjType) {
		ForumSubject toReturn = new ForumSubjectImpl(subjType.getDescription(), subjType.getName());

		for (SubjectType tSubjectType : subjType.getSubSubjects())
			try {
				toReturn.addSubSubject(constructSubSubject(tSubjectType));
			} catch (JAXBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SubjectAlreadyExistsException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (ThreadType tThreadType : subjType.getSubThreads())
				try {
					toReturn.openNewThread(constructForumMessage(tThreadType.getStartMessage()));
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	

				return toReturn;	
	}

	/*	private ForumThread constructForumThread(ThreadType threadType) {
		return new ForumThreadImpl(constructForumMessage(threadType.getStartMessage()));
	}
	 */	
	private ForumMessage constructForumMessage(MessageType msgType) {
		ForumMessage toReturn = new ForumMessageImpl(this.registeredUsers.get(msgType.getAuthor().getUsername()),
				msgType.getTitle(), msgType.getContent());
		for (MessageType tMsgType : msgType.getReplies()) {
			toReturn.addReplyToMe(constructForumMessage(tMsgType));
		}
		return toReturn;
	}





	public void addForumSubject(ForumSubject fs) throws 
	JAXBException, IOException, SubjectAlreadyExistsException
	{
		for (ForumSubject tForumSubject : subjects)
			if (tForumSubject.getName().equals(fs.getName()))
				throw new SubjectAlreadyExistsException(fs.getName());
		subjects.add(fs);
		persistenceDataHandler pipe = PersistenceFactory.getPipe();		
		pipe.addNewSubject(fs.getSubjectID(), fs.getName(), fs.getDescription());
	}

	public Vector<RegisteredUser> getConnectedUsers() 
	{
		return new Vector<RegisteredUser>(this.connectedUsers.values());
	}

	public RegisteredUser getUserByUsername(String username) throws NotRegisteredException {
		RegisteredUser toReturn = this.registeredUsers.get(username);
		if (toReturn != null)
			return toReturn;
		throw new NotRegisteredException(username);
	}

	@Override
	public Vector<ForumSubject> getForumSubjects() 
	{
		return this.subjects;
	}

	public ForumSubject getForumSubject(long id) throws SubjectNotFoundException {
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


	@Override
	public int getNumOfConnectedUsers() 
	{
		return this.connectedUsers.size();
	}

	/*	private RegisteredUser findRegisteredUser(String username) throws  NotRegisteredException {
		RegisteredUser tUser = this.registeredUsers.get(username);
		if (tUser != null)
			return tUser; 
		throw new NotRegisteredException(username);
	}
	 */	
	@Override
	public RegisteredUser login(String username, String password)
	throws AlreadyConnectedException, NotRegisteredException, WrongPasswordException 
	{

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

	@Override
	public void logout(String username) throws NotConnectedException 
	{
		RegisteredUser tUser = this.connectedUsers.get(username);

		if (tUser == null)
			throw new NotConnectedException(username);

		this.connectedUsers.remove(username);
	}

	@Override
	public void registerUser(RegisteredUser user) throws UserAlreadyExistsException, JAXBException, IOException {
		if (this.registeredUsers.get(user.getUsername()) != null)
			throw new UserAlreadyExistsException("There already exists a user with the username " 
					+ user.getUsername());

		if (this.registeredUsersByEmail.get(user.getEMail()) != null)
			throw new UserAlreadyExistsException("There already exists a user with the email " 
					+ user.getEMail());


		this.registeredUsers.put(user.getUsername(), user);

		persistenceDataHandler pipe = PersistenceFactory.getPipe();

		pipe.registerToForum(user.getUsername(), user.getPassword(), user.getLastName(), 
				user.getPrivateName(), user.getEMail());	
	}

	@Override
	public ForumMessage findMessage(long msgID) throws MessageNotFoundException
	{
		ForumMessage toReturn = null;

		for (ForumSubject tSubj : this.subjects)
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

		throw new MessageNotFoundException(msgID);
	}
}
