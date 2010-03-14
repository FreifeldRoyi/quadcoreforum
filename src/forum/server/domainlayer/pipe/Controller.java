/**
 * 
 */
package forum.server.domainlayer.pipe;


import java.io.IOException;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.impl.ForumImpl;
import forum.server.domainlayer.impl.ForumMessageImpl;
import forum.server.domainlayer.impl.ForumSubjectImpl;
import forum.server.domainlayer.impl.RegisteredUserImpl;
import forum.server.domainlayer.interfaces.Forum;
import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.ForumSubject;
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
public class Controller implements domainDataHandler {
	private 

	public Controller() {
		forum = new ForumImpl();
	}

	public void addNewMessage(long subjectID, String username,
			String title, String content) 
	{
		try 
		{
			ForumSubject tRoot = forum.getForumSubject(subjectID);
			RegisteredUser tMsgUser;
			tMsgUser = forum.getUserByUsername(username);
			ForumMessage tMsg = new ForumMessageImpl(tMsgUser, title, content);
			tRoot.openNewThread(tMsg);
		}
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addNewSubSubject(long fatherID, String name,
			String description) 
	{
		try {
			ForumSubject tForumSubject = forum.getForumSubject(fatherID);
			ForumSubject tNewSubject = new ForumSubjectImpl(description, name);
			tForumSubject.addSubSubject(tNewSubject);
		} catch (SubjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SubjectAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void addNewSubject(String name, String description) {
		ForumSubject tNewSubject = new ForumSubjectImpl(description, name);
		try {
			this.forum.addForumSubject(tNewSubject);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SubjectAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			tFather = this.forum.findMessage(fatherID);
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
