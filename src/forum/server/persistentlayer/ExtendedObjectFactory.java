/**
 * This class is a Factory of persistent layer objects. It contains constructors which use the
 * ObjectsFactory class, provided by the JAXB compiler xjc, in order to supply constructors which are different
 * from the default constructors.
 * 
 * The purpose of this class is adding more functionality to the ObjectsFactory class, without changing it.
 */
package forum.server.persistentlayer;

import java.util.GregorianCalendar;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class ExtendedObjectFactory {
	private static ObjectFactory factory = new ObjectFactory();
	
	/**
	 * Constructs a new {@link UserType} instance, initialized with the given parameters, in order to 
	 * register the created user to the forum.
	 *  
	 * @param username
	 * 		The desired username of the new user
	 * @param password
	 * 		The desired password of the new user
	 * @param lastName
	 * 		The lastName of the new user
	 * @param firstName
	 * 		The first name of the new user
	 * @param email
	 * 		The email of the new user
	 * 
	 * @return
	 * 		A new instance of {@link UserType} class which is initialized with the given parameters		
	 */
	public static UserType createUserType(String username, String password, String lastName,
			String firstName, String email) {
		UserType tUserType = factory.createUserType();
		tUserType.setUsername(username);
		tUserType.setPassword(password);
		tUserType.setLastName(lastName);
		tUserType.setFirstName(firstName);
		tUserType.setEMail(email);
		tUserType.setNumOfPostedMessages(0);
		return tUserType;
	}

	/**
	 * Constructs a new {@link MessageType} instance, initialized with the given parameters which should be
	 * added to the forum.
	 *
	 * @param msgID
	 * 		The identification number of the message
	 * @param author
	 * 		The message author
	 * @param title
	 * 		The title of the message
	 * @param content
	 * 		The content of the message
	 * 
	 * @return
	 * 		A new instance of {@link MessageType} class which is initialized with the given parameters		
	 */
	public static MessageType createMessageType(long msgID, UserType author,
			String title, String content) {
		GregorianCalendar tCurrDateTime = new GregorianCalendar();
		MessageType tMsgType = factory.createMessageType();
		tMsgType.setMessageID(msgID);
		tMsgType.setAuthor(author);
		tMsgType.setTitle(title);
		tMsgType.setContent(content);
		tMsgType.setDate(new XMLGregorianCalendarImpl(tCurrDateTime));
		tMsgType.setTime(new XMLGregorianCalendarImpl(tCurrDateTime));
		tMsgType.setIsReplyTo(null);
		author.setNumOfPostedMessages(author.getNumOfPostedMessages() + 1);
		return tMsgType;
	}
	
	/**
	 * Constructs a new {@link ThreadType} instance, initialized with the given parameters, in order to
	 * add the constructed thread to the forum.
	 * 
	 * @param startMessage
	 * 		The message which opens the thread
	 * @return
	 * 		A new instance of {@link ThreadType} class which is initialized with the given parameters
	 */
	public static ThreadType createThreadType(MessageType startMessage) {
		ThreadType tThreadType = factory.createThreadType();
		tThreadType.setStartMessage(startMessage);
		tThreadType.setLastMessage(startMessage);
		tThreadType.setNumOfViews(0);
		tThreadType.setNumOfResponses(0);
		return tThreadType;
	}
	
	/**
	 * Constructs a new {@link SubjectType} instance, initialized with the given parameters, in order to
	 * add the constructed subject to the forum.
	 *
	 * @param father
	 * 		The ancestor subject of the subject which should be created, it can be null if the new subject is
	 * 		a top level one.
	 * @param name
	 * 		The name of the new subject
	 * @param description
	 * 		The description of the new subject
	 * @return
	 * 		A new instance of a {@link SubjectType} which is initialized with the given parameters
	 */
	public static SubjectType createSubject(SubjectType father, long subjectID, String name, String description) {
		SubjectType tSubjectType = factory.createSubjectType();
		tSubjectType.setAnsestorSubject(father);
		tSubjectType.setSubjectID(subjectID);
		tSubjectType.setName(name);
		tSubjectType.setDescription(description);
		tSubjectType.setLastAddedMessage(null);
		tSubjectType.setNumOfMessages(0);
		tSubjectType.setNumOfThreads(0);
		return tSubjectType;
	}		
}