/**
 * This class implements the persistenceDataHandler interface, it contains methods which allows database updating.
 * 
 * The interface is intended to the programmers who write the higher layers
 */
package forum.server.domainlayer.impl;

import forum.server.domainlayer.interfaces.persistenceDataHandler;

import java.util.*;
import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import forum.server.exceptions.user.*;
import forum.server.exceptions.subject.*;
import forum.server.exceptions.message.*;

import forum.server.persistentlayer.*;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class JAXBpersistenceDataHandler implements persistenceDataHandler
{

	private static String DB_FILES_LOCATION = 
		"src" + System.getProperty("file.separator") +
		"forum" + System.getProperty("file.separator") +
		"server" + System.getProperty("file.separator");
	private static String DB_FILE_NAME = "QuadCoreForumDB";

	private static String SCHEMA_FILE_FULL_LOCATION = DB_FILES_LOCATION + DB_FILE_NAME + ".xsd";
	private static String DB_FILE_FULL_LOCATION 	= DB_FILES_LOCATION + DB_FILE_NAME + ".xml";

	private JAXBContext jaxbContent;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;


	public JAXBpersistenceDataHandler() throws JAXBException, SAXException
	{
		this.jaxbContent = JAXBContext.newInstance("forum.server.persistentlayer");
		this.unmarshaller = this.jaxbContent.createUnmarshaller();
		this.unmarshaller.setSchema(SchemaFactory.newInstance(
				XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(
						SCHEMA_FILE_FULL_LOCATION)));
		this.marshaller = this.jaxbContent.createMarshaller();
		this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	}

	private Forum getForumFromDatabase() throws JAXBException
	{
		return this.unmarshalDatabase();
	}

	private Forum unmarshalDatabase() throws JAXBException
	{
		Forum tForum = (Forum)unmarshaller.unmarshal(new File(
				DB_FILE_FULL_LOCATION));
		return tForum;
	}

	private void marshalDatabase(Forum forum) throws JAXBException
	{
		marshaller.marshal(forum, new File(DB_FILE_FULL_LOCATION));
	}

	public void registerToForum(String username, String password,
			String lastName, String firstName, String email)
	throws JAXBException, IOException, UserAlreadyExistsException
	{
		Forum tForum = this.getForumFromDatabase();
		for (UserType tUserType : tForum.getRegisteredUsers())
		{
			if (tUserType.getUsername().equals(username))
				throw new UserAlreadyExistsException("There already exists a user with the username " + username);
			if (tUserType.getEMail().equals(email))
				throw new UserAlreadyExistsException("There already exists a user with the email " + email);
		}		
		UserType tNewUser = ExtendedObjectFactory.createUserType(username, password, lastName, firstName, email);
		tForum.getRegisteredUsers().add(tNewUser);
		this.marshalDatabase(tForum);		
	}

	// performs a BFS
	private SubjectType findSubject(List<SubjectType> subjects, String subjName) throws SubjectNotFoundException
	{		
		for (SubjectType tSubject : subjects)
		{ // first, look in the top level
			if (tSubject.getName().equals(subjName))
				return tSubject;
		}

		SubjectType tAnsSubject = null;
		for (SubjectType tSubject : subjects)
		{ // look in the deeper levels
			try
			{
				tAnsSubject = findSubject(tSubject.getSubSubjects(), subjName); 
				return tAnsSubject;
			}
			catch (SubjectNotFoundException e)
			{
				continue; // do nothing - subject wasn't found in deeper levels
			}
		}
		throw new SubjectNotFoundException(subjName); // the required subject wasn't found in all the levels
	}


	private UserType getMessageAuthor(Forum forum, String authorUsername)
	throws NotRegisteredException, NotConnectedException
	{
		UserType tMsgAuthor = null;
		// checks that the message author exists and is connected to the system
		for (UserType tUserType : forum.getRegisteredUsers())
			if (tUserType.getUsername().equals(authorUsername))
			{
				tMsgAuthor = tUserType;
				break;
			}		
		if (tMsgAuthor == null) throw new NotRegisteredException(authorUsername);
		else if (tMsgAuthor.getConnectionStatus() == ConnectionStatusType.DISCONECTED)
			throw new NotConnectedException(authorUsername);		
		return tMsgAuthor;
	}

	public void addNewMessage(String subjectName, String authorUsername, String msgTitle, String msgContent)
	throws JAXBException, IOException, NotRegisteredException, NotConnectedException, SubjectNotFoundException
	{

		Forum tForum = this.getForumFromDatabase();
		UserType tMsgAuthor = this.getMessageAuthor(tForum, authorUsername);

		// an exception will be thrown if the message won't be found
		SubjectType tMsgSubject = this.findSubject(tForum.getForumSubjects(), subjectName);

		MessageType tMsg = ExtendedObjectFactory.createMessageType(tForum.getNumOfMessages(), tMsgAuthor, msgTitle, 
				msgContent);

		tForum.setNumOfMessages(tForum.getNumOfMessages() + 1);

		ThreadType tThread = ExtendedObjectFactory.createThreadType(tMsg);
		tMsg.setThread(tThread);

		tMsgSubject.getSubThreads().add(tThread);
		tMsgSubject.setNumOfThreads(tMsgSubject.getNumOfThreads() + 1);
		tMsgSubject.setLastAddedMessage(tMsg);
		this.marshalDatabase(tForum);		
	}

	public void addNewSubject(String subjectName, String subjectDescription)
	throws JAXBException, IOException, SubjectAlreadyExistsException
	{
		Forum tForum = this.getForumFromDatabase();	
		try
		{ 
			this.findSubject(tForum.getForumSubjects(), subjectName);
			throw new SubjectAlreadyExistsException(subjectName);
		}
		catch (SubjectNotFoundException e)
		{
			SubjectType tNewSubject = ExtendedObjectFactory.createSubject(null, subjectName, subjectDescription);
			tForum.getForumSubjects().add(tNewSubject);		
			this.marshalDatabase(tForum);
		}
	}

	public void addNewSubSubject(String father, String subjectName, String subjectDescription) 
	throws JAXBException, IOException, SubjectAlreadyExistsException, SubjectNotFoundException
	{
		Forum tForum = this.getForumFromDatabase();	
		try
		{ 
			this.findSubject(tForum.getForumSubjects(), subjectName);
			throw new SubjectAlreadyExistsException(subjectName);
		}
		catch (SubjectNotFoundException e)
		{
			SubjectType tAnsestorSubject = this.findSubject(tForum.getForumSubjects(), father);
			SubjectType tNewSubject = ExtendedObjectFactory.createSubject(tAnsestorSubject, subjectName, subjectDescription);
			tAnsestorSubject.getSubSubjects().add(tNewSubject);
			this.marshalDatabase(tForum);
		}		
	}

	public void login(String username, String password)
	throws JAXBException, IOException, AlreadyConnectedException, NotRegisteredException,
	WrongPasswordException
	{
		Forum tForum = this.getForumFromDatabase();	
		for (UserType tRegUser : tForum.getRegisteredUsers())
			if (tRegUser.equals(username)) 
				if (tRegUser.getPassword().equals(password))
					if (tRegUser.getConnectionStatus() == ConnectionStatusType.CONNECTED)
						throw new AlreadyConnectedException(username);
					else
					{
						tRegUser.setConnectionStatus(ConnectionStatusType.CONNECTED);
						this.marshalDatabase(tForum);
						break;
					}
				else throw new WrongPasswordException();
		throw new NotRegisteredException(username);
	}

	public void logoutUser(String username) throws JAXBException, IOException, NotConnectedException
	{
		Forum tForum = this.getForumFromDatabase();	
		for (UserType tRegUser : tForum.getRegisteredUsers())
			if (tRegUser.equals(username))
				if (tRegUser.getConnectionStatus() == ConnectionStatusType.DISCONECTED)
					throw new NotConnectedException(username);
				else
				{
					tRegUser.setConnectionStatus(ConnectionStatusType.CONNECTED);
					this.marshalDatabase(tForum);
					break;
				}
		throw new NotConnectedException(username);
	}

	private MessageType findMessage(MessageType msgTypeToLookIn, int msgIDtoFind) 
	throws MessageNotFoundException
	{
		if (msgTypeToLookIn.getMessageID() == msgIDtoFind)
			return msgTypeToLookIn;
		for (MessageType tMessageType : msgTypeToLookIn.getReplies())
			try
		{
				MessageType tMsgToReturn = findMessage(tMessageType, msgIDtoFind);
				return tMsgToReturn;
		}
		catch (MessageNotFoundException e)
		{
			continue;
		}
		throw new MessageNotFoundException(msgIDtoFind);		
	}

	private MessageType findMessage(Forum forum, int msgIDtoFind) throws MessageNotFoundException 
	{
		for (SubjectType tSubjectType : forum.getForumSubjects())
		{
			for (ThreadType tThreadType : tSubjectType.getSubThreads())
			{
				try
				{
					MessageType tMsgToReturn = this.findMessage(tThreadType.getStartMessage(), msgIDtoFind);
					return tMsgToReturn;
				}
				catch (MessageNotFoundException e) {
					continue;
				}
			}
		}
		throw new MessageNotFoundException(msgIDtoFind);
	}

	public void replyToMessage(int fatherID, String authorUsername, String replyTitle, String replyContent)
	throws JAXBException, IOException, MessageNotFoundException, NotRegisteredException, NotConnectedException
	{
		Forum tForum = this.getForumFromDatabase();

		MessageType father = this.findMessage(tForum, fatherID);
		UserType tMsgAuthor = this.getMessageAuthor(tForum, authorUsername);		

		MessageType tReply = ExtendedObjectFactory.createMessageType(tForum.getNumOfMessages(),			
				tMsgAuthor, replyTitle, replyContent);

		tReply.setIsReplyTo(father);
		tReply.setThread(father.getThread());
		tReply.getThread().setNumOfResponses(tReply.getThread().getNumOfResponses() + 1);
		tReply.getThread().setLastMessage(tReply);

		father.getReplies().add(tReply);

		tMsgAuthor.setNumOfPostedMessages(tMsgAuthor.getNumOfPostedMessages() + 1);
		tMsgAuthor.getPostedMessages().add(tReply);

		this.marshalDatabase(tForum);		
	}

	public void updateMessage(int messageID, String newTitle, String newContent) throws JAXBException, IOException,
	MessageNotFoundException
	{
		Forum tForum = this.getForumFromDatabase();

		MessageType tMsgToEdit = this.findMessage(tForum, messageID);
		tMsgToEdit.setTitle(newTitle);
		tMsgToEdit.setContent(newContent);		
		this.marshalDatabase(tForum);
	}
}