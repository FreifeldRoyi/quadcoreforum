/**
 * 
 */
package forum.server.domainlayer.pipe;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import forum.server.domainlayer.impl.*;
import forum.server.domainlayer.impl.message.ForumSubjectImpl;
import forum.server.domainlayer.impl.user.ForumMessageImpl;
import forum.server.domainlayer.impl.user.RegisteredUserImpl;
import forum.server.domainlayer.interfaces.*;
import forum.server.exceptions.message.*;
import forum.server.exceptions.subject.*;
import forum.server.exceptions.user.*;

/**
 * @author sepetnit
 *
 */
public class Controller implements DomainDataHandler {
	private static Forum FORUM;
	private RegisteredUser user;

	public Controller() {
		this.user = null;
		if (Controller.FORUM == null)
			Controller.FORUM = new ForumImpl();
	}	

	public String getForumSubjectByID(long id) {
		try {
			return Controller.FORUM.getForumSubjectByID(id).toString();
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
				for (ForumSubject tForumSubject : Controller.FORUM.getForumSubjectByID(rootSubjectID).getSubSubjects())
					toReturn.put(tForumSubject.getSubjectID(), tForumSubject.toString());
			else
				for (ForumSubject tForumSubject : Controller.FORUM.getForumSubjects())
					toReturn.put(tForumSubject.getSubjectID(), tForumSubject.toString());
		} catch (SubjectNotFoundException e) {
			System.out.println("error: subject wasn't found");
		}
		return toReturn;
	}

	public Map<Long, String> getSubjectThreads(long rootSubjectID) {
		try {
			return  Controller.FORUM.getForumThreadsBySubjectID(rootSubjectID);
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
			ForumSubject tRoot = FORUM.getForumSubjectByID(subjectID);
			RegisteredUser tMsgUser;
			tMsgUser = FORUM.getUserByUsername(username);
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
			ForumSubject tForumSubject = FORUM.getForumSubjectByID(fatherID);
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
			Controller.FORUM.addForumSubject(tNewSubject);
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
	public String login(String username, String password){
		try {
			String tEncryptedPass = MessageEncryption.encryptMD5(password);
			RegisteredUser tUser = FORUM.login(username, tEncryptedPass);
			this.user = tUser;
			return "success!";
		} catch (AlreadyConnectedException e) {
			return e.getMessage();
		} catch (NotRegisteredException e) {
			return e.getMessage();
		} catch (WrongPasswordException e) {
			return e.getMessage();
		} catch (NoSuchAlgorithmException e) {
			return e.getMessage();
		}

	}

	@Override
	public String logout(String username) {
		String toReturn = "success!";
		try {
			Controller.FORUM.logout(username);
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
				
		try {
			String tEncryptedPass = MessageEncryption.encryptMD5(password);
			RegisteredUser tUser = new RegisteredUserImpl(username, tEncryptedPass, firstName, lastName, email);
			Controller.FORUM.registerUser(tUser);
		}
		catch (UserAlreadyExistsException e) {
			toReturn = e.getMessage();
		} 
		catch (JAXBException e) {
			toReturn = e.getMessage();
		}
		catch (IOException e) {
			toReturn = e.getMessage();
		} catch (NoSuchAlgorithmException e) {
			toReturn = e.getMessage();
		}
		return toReturn;
	}

	@Override
	public void replyToMessage(long fatherID, String username, String title,
			String content) {
		ForumMessage tFather;
		try {
			tFather = Controller.FORUM.getMessageByID(fatherID);
			RegisteredUser tUser = Controller.FORUM.getUserByUsername(username);
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
	
	public Map<Long, String> getMessageAndRepliesByRoot(long rootMessage) {
		try {
			return Controller.FORUM.getMessageByID(rootMessage).getRepliesRepresentation();
		} catch (MessageNotFoundException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public String updateAMessage(long messageID, String newTitle, String newContent) {
		try {
			Controller.FORUM.updateAMessage(messageID, newTitle, newContent);
			return "the message was succcessfully updated!";
		} catch (JAXBException e) {
			return "database error!";
		} catch (IOException e) {
			return "database error!";
		} catch (MessageNotFoundException e) {
			return "database error - the message wasn't found in the database!";
		}	
	}

	

}
