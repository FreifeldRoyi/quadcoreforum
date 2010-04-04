/**
 * This class is responsible of getting persistence objects and convert them to domain objects
 * 
 * All the methods of this class are static methods since this class is a general utility used by few classes
 */
package forum.server.persistentlayer.pipe;

import java.util.*;

import forum.server.domainlayer.impl.message.*;
import forum.server.domainlayer.impl.user.*;

import forum.server.persistentlayer.*;


/**
 * @author sepetnit
 *
 */
public class PersistentToDomainConverter {

	/**
	 * Parses and converts a collection of string permissions to a collection of permissions of type
	 * {@link Permission}
	 * 
	 * @param permissions
	 * 		The collection of permissions which should be parsed and converted to collection of 
	 * 		{@link Permission} objects
	 * 
	 * @return
	 * 		The created collection of {@link Permission} objects
	 */
	private static Collection<Permission> parseStringPermissions(Collection<String> permissions) {
		Collection<Permission> toReturn = new HashSet<Permission>();
		for (String tCurrentPermission : permissions)
			try {
				toReturn.add(Permission.valueOf(tCurrentPermission));
			}
		catch (IllegalArgumentException e) {
			continue; // do nothing
		}
		return toReturn;
	}

	/**
	 * Converts a {@link MemberType} object to a {@link ForumMember} object
	 * 
	 * @param toConvert
	 * 		The instance of {@link MemberType} which should be converted to a {@link ForumMember}
	 * 
	 * @return
	 * 		The created {@link ForumMember} instance which contains all the data of the given
	 * 		{@link MemberType} instance
	 */
	public static ForumMember convertMemberTypeToForumMember(MemberType toConvert) {
		return new ForumMember(toConvert.getUserID(), toConvert.getUsername(), 
				toConvert.getPassword(), toConvert.getLastName(), toConvert.getFirstName(),
				toConvert.getEMail(), PersistentToDomainConverter.parseStringPermissions(toConvert.getPrivileges()));
	}

	/**
	 * Converts a {@link SubjectType} object to a {@link ForumSubject} object
	 * 
	 * @param toConvert
	 * 		The instance of {@link SubjectType} which should be converted to a {@link ForumSubject}
	 * 
	 * @return
	 * 		The created {@link ForumSubject} instance which contains all the data of the given
	 * 		{@link SubjectType} instance
	 */
	public static ForumSubject convertSubjectTypeToForumSubject(SubjectType toConvert) {
		return new ForumSubject(toConvert.getSubjectID(), 
				toConvert.getName(), toConvert.getDescription(), toConvert.isIsToLevel());
	}
	
	/**
	 * Converts a {@link ThreadType} object to a {@link ForumThread} object
	 * 
	 * @param toConvert
	 * 		The instance of {@link ThreadType} which should be converted to a {@link ForumThread}
	 * 
	 * @return
	 * 		The created {@link ForumThread} instance which contains all the data of the given
	 * 		{@link ThreadType} instance
	 */
	public static ForumThread convertThreadTypeToForumThread(ThreadType toConvert) {
		return new ForumThread(toConvert.getThreadID(), 
				toConvert.getTopic(), toConvert.getStartMessageID());
	}

	
	
	/**
	 * Converts a {@link MessageType} object to a {@link ForumMessage} object
	 * 
	 * @param toConvert
	 * 		The instance of {@link MessageType} which should be converted to a {@link ForumMessage}
	 * 
	 * @return
	 * 		The created {@link ForumMessage} instance which contains all the data of the given
	 * 		{@link MessageMessage} instance
	 */
	public static ForumMessage convertMessageTypeToForumMessage(MessageType toConvert) {
		return new ForumMessage(toConvert.getMessageID(), toConvert.getAuthor(), 
				toConvert.getTitle(), toConvert.getContent(), 
				toConvert.getPostTime().toGregorianCalendar(), 
				toConvert.getRepliesIDs());	
	}
}
