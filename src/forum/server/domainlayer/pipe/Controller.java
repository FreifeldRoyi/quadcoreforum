/**
 * 
 */
package forum.server.domainlayer.pipe;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.impl.ForumImpl;
import forum.server.domainlayer.impl.ForumMessageImpl;
import forum.server.domainlayer.impl.ForumSubjectImpl;
import forum.server.domainlayer.impl.RegisteredUserImpl;
import forum.server.domainlayer.interfaces.Forum;
import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.ForumSubject;
import forum.server.domainlayer.interfaces.ForumThread;
import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.exceptions.subject.SubjectAlreadyExistsException;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.exceptions.user.AlreadyConnectedException;
import forum.server.exceptions.user.NotConnectedException;
import forum.server.exceptions.user.NotRegisteredException;
import forum.server.exceptions.user.UserAlreadyExistsException;
import forum.server.exceptions.user.WrongPasswordException;
import forum.server.persistentlayer.pipe.persistenceDataHandler;

/**
 * @author sepetnit
 *
 */
public class Controller implements DomainDataHandler {
	private static Forum forum;
	private RegisteredUser user;

	public Controller() {
		this.user = null;
		if (Controller.forum == null)
			Controller.forum = new ForumImpl();
	}	

	public String getForumSubjectByID(long id) {
		try {
			return Controller.forum.getForumSubjectByID(id).toString();
		} catch (SubjectNotFoundException e) {
			return "error: subject not found";
		}
	}


	public Map<Long, String> getForumSubjects() {
		return this.getSubjectsByRoot(-1);
	}

	public Map<Long, String> getSubjectsByRoot(long rootSubjectID) {
		Map<Long, String> toReturn = new HashMap<Long, String>();
		try {
			if (rootSubjectID != -1)
				for (ForumSubject tForumSubject : Controller.forum.getForumSubjectByID(rootSubjectID).getSubSubjects())
					toReturn.put(tForumSubject.getSubjectID(), tForumSubject.toString());
			else
				for (ForumSubject tForumSubject : Controller.forum.getForumSubjects())
					toReturn.put(tForumSubject.getSubjectID(), tForumSubject.toString());
		} catch (SubjectNotFoundException e) {
			System.out.println("error: subject wasn't found");
		}
		return toReturn;
	}

	public Map<Long, String> getSubjectThreads(long rootSubjectID) {
		try {
			return  Controller.forum.getForumThreadsBySubjectID(rootSubjectID);
		} catch (SubjectNotFoundException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public boolean isAUserLoggedIn() {
		return this.user != null;
	}

	// TODO: add more getters like firstName() ...
	public String getCurrentlyLoggedOnUserName() {
		return this.user != null ? this.user.getUsername() : "";
	}

	public String addNewMessage(long subjectID, String username,
			String title, String content) 
	{
		try 
		{
			ForumSubject tRoot = forum.getForumSubjectByID(subjectID);
			RegisteredUser tMsgUser;
			tMsgUser = forum.getUserByUsername(username);
			ForumMessage tMsg = new ForumMessageImpl(tMsgUser, title, content);
			tRoot.openNewThread(tMsg);
			return "success!";
		}
		catch (NotRegisteredException e) 
		{
			return e.getMessage();
		}

		catch (SubjectNotFoundException e) 
		{
			return e.getMessage();
		} 
		catch (JAXBException e) 
		{
			return "JAXB error!";
		}
		catch (IOException e) 
		{
			return "database error!";
		}
	}

	public String addNewSubSubject(long fatherID, String name,
			String description) 
	{
		try {
			ForumSubject tForumSubject = forum.getForumSubjectByID(fatherID);
			ForumSubject tNewSubject = new ForumSubjectImpl(description, name);
			tForumSubject.addSubSubject(tNewSubject);
			return "A subject " + name + " has successfully added as a sub-subject of " +
			tForumSubject.getName();
		} catch (SubjectNotFoundException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "JAXB error!";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "database error!";
		} catch (SubjectAlreadyExistsException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}

	}

	@Override
	public String addNewSubject(String name, String description) {
		ForumSubject tNewSubject = new ForumSubjectImpl(description, name);
		try {
			this.forum.addForumSubject(tNewSubject);
			return "the subject " + name + " was added successfuly!";
		} catch (JAXBException e) {
			return "JAXB error!";
		} catch (IOException e) {
			return "database error!";
		} catch (SubjectAlreadyExistsException e) {
			return e.getMessage();
		}
	}

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public String login(String username, String password){
		try {
			RegisteredUser tUser = forum.login(username, password);
			this.user = tUser;
			return "success!";
		} catch (AlreadyConnectedException e) {
			return e.getMessage();
		} catch (NotRegisteredException e) {
			return e.getMessage();
		} catch (WrongPasswordException e) {
			return e.getMessage();
		}

	}

	@Override
	public String logout(String username) {
		String toReturn = "success!";
		try {
			this.forum.logout(username);
			this.user = null;
		} catch (NotConnectedException e) {
			toReturn = e.getMessage();
		}
		return toReturn;

	}

	// TODO: Will be changed - a controller instance will be opened per connection
	public String registerToForum(String username, String password,
			String lastName, String firstName, String email) {
		String toReturn = "success!";
		RegisteredUser tUser = new RegisteredUserImpl(username, password, firstName, lastName, email);		
		try {
			this.forum.registerUser(tUser);
		}
		catch (UserAlreadyExistsException e) {
			toReturn = e.getMessage();
		} 
		catch (JAXBException e) {
			toReturn = e.getMessage();
		}
		catch (IOException e) {
			toReturn = e.getMessage();
		}
		return toReturn;
	}

	@Override
	public void replyToMessage(long fatherID, String username, String title,
			String content) {
		ForumMessage tFather;
		try {
			tFather = this.forum.getMessageByID(fatherID);
			RegisteredUser tUser = this.forum.getUserByUsername(username);
			ForumMessage tNewMsg = new ForumMessageImpl(tUser, title, content);

			tFather.addReplyToMe(tNewMsg);
		}

		catch (MessageNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotRegisteredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
