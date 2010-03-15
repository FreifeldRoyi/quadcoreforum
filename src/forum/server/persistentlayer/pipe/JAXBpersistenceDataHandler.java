/**
 * This class implements the persistenceDataHandler interface, it contains methods which allows database updating.
 * 
 * The interface is intended to the programmers who write the higher layers
 */
package forum.server.persistentlayer.pipe;

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

	public ForumType getForumFromDatabase() throws JAXBException
	{
		return this.unmarshalDatabase();
	}

	private ForumType unmarshalDatabase() throws JAXBException
	{
		ForumType tForum = (ForumType)this.unmarshaller.unmarshal(new File(
				DB_FILE_FULL_LOCATION));
		return tForum;
	}

	private void marshalDatabase(ForumType forum) throws JAXBException
	{
		marshaller.marshal(forum, new File(DB_FILE_FULL_LOCATION));
	}

	public void registerToForum(String username, String password,
			String lastName, String firstName, String email)
	throws JAXBException, IOException, UserAlreadyExistsException
	{
		ForumType tForum = this.getForumFromDatabase();
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

	// performs a DFS
	private SubjectType findSubject(List<SubjectType> subjects, long subjectID) throws SubjectNotFoundException
	{	
		if (subjects.size() == 0)
			throw new SubjectNotFoundException(subjectID);

		SubjectType tAnsSubject = null;
		for (SubjectType tSubject : subjects)
		{			
			if (tSubject.getSubjectID() == subjectID)
				return tSubject;
			try
			{
				tAnsSubject = findSubject(tSubject.getSubSubjects(), subjectID); 
				return tAnsSubject;
			}
			catch (SubjectNotFoundException e)
			{
				continue; // do nothing - subject wasn't found in deeper levels
			}
		}

		throw new SubjectNotFoundException(subjectID); // the required subject wasn't found in all the levels
	}


	/**
	 * Looks for the a UserType object instance according to the given username and returns it
	 * 
	 * @param forum
	 * 		The forum type object, where the username should be found
	 * @param authorUsername
	 * 		The username of the message author
	 * @return
	 * 		The found UserType object 
	 * @throws NotRegisteredException
	 */
	private UserType getMessageAuthor(ForumType forum, String authorUsername)
	throws NotRegisteredException
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
		return tMsgAuthor;
	}

	public void addNewMessage(long messageID, long subjectID, String authorUsername, String msgTitle, String msgContent)
	throws JAXBException, IOException, NotRegisteredException, SubjectNotFoundException
	{

		ForumType tForum = this.getForumFromDatabase();
		UserType tMsgAuthor = this.getMessageAuthor(tForum, authorUsername);

		// an exception will be thrown if the message won't be found
		SubjectType tMsgSubject = this.findSubject(tForum.getForumSubjects(), subjectID);

		MessageType tMsg = ExtendedObjectFactory.createMessageType(messageID, tMsgAuthor, msgTitle, 
				msgContent);

		tForum.setNumOfMessages(tForum.getNumOfMessages() + 1);

		ThreadType tThread = ExtendedObjectFactory.createThreadType(tMsg);
		//tMsg.setThread(tThread);

		tMsgSubject.getSubThreads().add(tThread);
		tMsgSubject.setNumOfThreads(tMsgSubject.getNumOfThreads() + 1);
		tMsgSubject.setLastAddedMessage(tMsg);
		this.marshalDatabase(tForum);		
	}

	public void addNewSubject(long subjectID, String subjectName, String subjectDescription)
	throws JAXBException, IOException, SubjectAlreadyExistsException
	{
		ForumType tForum = this.getForumFromDatabase();	
		
		this.checkDuplicateSubject(tForum.getForumSubjects(), subjectName);
		
		SubjectType tNewSubject = ExtendedObjectFactory.createSubject(subjectID, subjectName, subjectDescription);
		tForum.getForumSubjects().add(tNewSubject);		
		this.marshalDatabase(tForum);
	}

	private void checkDuplicateSubject(Collection<SubjectType> subjectsCol, String subjectName) throws
	SubjectAlreadyExistsException {
		for (SubjectType tSubSubj : subjectsCol) {
			if (tSubSubj.getName().equals(subjectName))
				throw new SubjectAlreadyExistsException(subjectName);
		}
	}

	public void addNewSubSubject(long fatherID, long subjectID, String subjectName, String subjectDescription) 
	throws JAXBException, IOException, SubjectAlreadyExistsException, SubjectNotFoundException
	{
		ForumType tForum = this.getForumFromDatabase();
		SubjectType tAnsestorSubject = this.findSubject(tForum.getForumSubjects(), fatherID);
		checkDuplicateSubject(tAnsestorSubject.getSubSubjects(), subjectName);
		SubjectType tNewSubject = ExtendedObjectFactory.createSubject(subjectID, subjectName, subjectDescription);
		tAnsestorSubject.getSubSubjects().add(tNewSubject);
		this.marshalDatabase(tForum);
	}

	/*	public void login(String username, String password)
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
	 */
	/*	public void logoutUser(String username) throws JAXBException, IOException, NotConnectedException
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
	 */
	private MessageType findMessage(MessageType msgTypeToLookIn, long msgIDtoFind) 
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

	private MessageType findMessage(ForumType forum, long msgIDtoFind) throws MessageNotFoundException 
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

	public void replyToMessage(long fatherID, long messageID, String authorUsername, String replyTitle, String replyContent)
	throws JAXBException, IOException, MessageNotFoundException, NotRegisteredException
	{
		ForumType tForum = this.getForumFromDatabase();

		MessageType father = this.findMessage(tForum, fatherID);
		UserType tMsgAuthor = this.getMessageAuthor(tForum, authorUsername);		

		MessageType tReply = ExtendedObjectFactory.createMessageType(messageID,
				tMsgAuthor, replyTitle, replyContent);

//		tReply.getThread().setNumOfResponses(tReply.getThread().getNumOfResponses() + 1);
//		tReply.getThread().setLastMessage(tReply);

		father.getReplies().add(tReply);

		tMsgAuthor.setNumOfPostedMessages(tMsgAuthor.getNumOfPostedMessages() + 1);
		tMsgAuthor.getPostedMessages().add(tReply);

		this.marshalDatabase(tForum);		
	}

	public void updateMessage(long messageID, String newTitle, String newContent) throws JAXBException, IOException,
	MessageNotFoundException
	{
		ForumType tForum = this.getForumFromDatabase();

		MessageType tMsgToEdit = this.findMessage(tForum, messageID);
		tMsgToEdit.setTitle(newTitle);
		tMsgToEdit.setContent(newContent);		
		this.marshalDatabase(tForum);
	}
}