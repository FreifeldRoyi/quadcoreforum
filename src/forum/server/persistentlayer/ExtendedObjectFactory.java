/**
 * This class is a Factory of persistent layer objects. It contains constructors which use the
 * ObjectsFactory class, provided by the JAXB compiler xjc, in order to supply constructors which are different
 * from the default constructors.
 * 
 * The purpose of this class is adding more functionality to the ObjectsFactory class, without changing it.
 */
package forum.server.persistentlayer;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import forum.server.domainlayer.impl.user.Permission;

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
	public static MemberType createMemberType(final long id, final String username, final String password, final String lastName,
			final String firstName, final String email, Collection<Permission> permissions) {
		MemberType tMemberType = factory.createMemberType();
		// parse the give permissions and create their string representation
		Collection<String> tSringPermissions = new Vector<String>();
		for (Permission tCurrentPermission : permissions)
			tSringPermissions.add(tCurrentPermission.toString());
		tMemberType.setUserID(id);
		tMemberType.setUsername(username);
		tMemberType.setPassword(password);
		tMemberType.setLastName(lastName);
		tMemberType.setFirstName(firstName);
		tMemberType.setEMail(email);
		tMemberType.setNumOfPostedMessages(0);
		tMemberType.getPrivileges().addAll(tSringPermissions);
		return tMemberType;
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
	public static MessageType createMessageType(final long id, final long authorID,
			String title, String content) {
		GregorianCalendar tCurrDateTime = new GregorianCalendar();
		MessageType tMsgType = factory.createMessageType();
		tMsgType.setMessageID(id);
		tMsgType.setAuthor(authorID);
		tMsgType.setTitle(title);
		tMsgType.setContent(content);
		tMsgType.setPostTime(new XMLGregorianCalendarImpl(tCurrDateTime));
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
	public static ThreadType createThreadType(final long id, final String topic, final long startMessageID) {
		ThreadType tThreadType = factory.createThreadType();
		tThreadType.setThreadID(id);
		tThreadType.setTopic(topic);
		tThreadType.setStartMessageID(startMessageID);
		tThreadType.setLastMessageID(startMessageID);
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
	public static SubjectType createSubject(final long subjectID, final String name, final String description, boolean isTopLevel) {
		SubjectType tSubjectType = factory.createSubjectType();
		tSubjectType.setSubjectID(subjectID);
		tSubjectType.setName(name);
		tSubjectType.setDescription(description);
		tSubjectType.setIsToLevel(isTopLevel);
		return tSubjectType;
	}		
}