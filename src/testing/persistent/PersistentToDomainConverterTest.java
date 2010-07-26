/**
 * This test test the PersistentToDomainConverter class
 */
package testing.persistent;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;


import forum.server.domainlayer.user.ForumMember;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.ExtendedObjectFactory;
import forum.server.updatedpersistentlayer.MemberType;
import forum.server.updatedpersistentlayer.MessageType;
import forum.server.updatedpersistentlayer.SubjectType;
import forum.server.updatedpersistentlayer.ThreadType;
import forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter;

/**
 * @author sepetnit
 *
 */
public class PersistentToDomainConverterTest extends TestCase {

	private static final Permission[] DEFAULT_PERMISSIONS_ARRAY = {Permission.VIEW_ALL,
		Permission.OPEN_THREAD, Permission.REPLY_TO_MESSAGE};
	
	private MemberType memberTypeUnderTest;
	private SubjectType subjectTypeUnderTest;
	private MessageType messageTypeUnderTest;
	private ThreadType threadTypeUnderTest;


	
	public PersistentToDomainConverterTest() {
		memberTypeUnderTest = ExtendedObjectFactory.createMemberType(0, "a", "b",
				"c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
		subjectTypeUnderTest = ExtendedObjectFactory.createSubject(3, "f", "g", 34);
		messageTypeUnderTest = ExtendedObjectFactory.createMessageType(2, 3, "i", "j", 30);
		threadTypeUnderTest = ExtendedObjectFactory.createThreadType(345, "d", 45, 232);
	}
	
	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter#convertMemberTypeToForumMember(forum.server.updatedpersistentlayer.MemberType)}.
	 */
	@Test
	public void testConvertMemberTypeToForumMember() {
		ForumMember tForumMember = PersistentToDomainConverter.convertMemberTypeToForumMember(memberTypeUnderTest);
		assertEquals(tForumMember.getID(), memberTypeUnderTest.getUserID());
		assertEquals(tForumMember.getUsername(), memberTypeUnderTest.getUsername());
		assertEquals(tForumMember.getPassword(), memberTypeUnderTest.getPassword());
		assertEquals(tForumMember.getLastName(), memberTypeUnderTest.getLastName());
		assertEquals(tForumMember.getFirstName(), memberTypeUnderTest.getFirstName());
		assertEquals(tForumMember.getEmail(), memberTypeUnderTest.getEmail());
		assertEquals(memberTypeUnderTest.getPermissions().size(), tForumMember.getPermissions().size());
		for (Permission tCurrentPermission : tForumMember.getPermissions()) {
			assertTrue(memberTypeUnderTest.getPermissions() != null &&
					memberTypeUnderTest.getPermissions().contains(tCurrentPermission.toString()));
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter#convertSubjectTypeToForumSubject(forum.server.updatedpersistentlayer.SubjectType)}.
	 */
	@Test
	public void testConvertSubjectTypeToForumSubject() {
		assertEquals(subjectTypeUnderTest.getSubjectID(), subjectTypeUnderTest.getSubjectID());
		assertEquals(subjectTypeUnderTest.getName(), subjectTypeUnderTest.getName());
		assertEquals(subjectTypeUnderTest.getDescription(), subjectTypeUnderTest.getDescription());
		assertEquals(subjectTypeUnderTest.getFatherID(), subjectTypeUnderTest.getFatherID());
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter#convertThreadTypeToForumThread(forum.server.updatedpersistentlayer.ThreadType)}.
	 */
	@Test
	public void testConvertThreadTypeToForumThread() {
		assertEquals(threadTypeUnderTest.getThreadID(), threadTypeUnderTest.getThreadID());
		assertEquals(threadTypeUnderTest.getTopic(), threadTypeUnderTest.getTopic());
		assertEquals(threadTypeUnderTest.getStartMessageID(), threadTypeUnderTest.getStartMessageID());
		assertEquals(threadTypeUnderTest.getFatherSubjectID(), threadTypeUnderTest.getFatherSubjectID());
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter#convertMessageTypeToForumMessage(forum.server.updatedpersistentlayer.MessageType)}.
	 */
	@Test
	public void testConvertMessageTypeToForumMessage() {
		assertEquals(messageTypeUnderTest.getMessageID(), messageTypeUnderTest.getMessageID());
		assertEquals(messageTypeUnderTest.getAuthorID(), messageTypeUnderTest.getAuthorID());
		assertEquals(messageTypeUnderTest.getTitle(), messageTypeUnderTest.getTitle());
		assertEquals(messageTypeUnderTest.getContent(), messageTypeUnderTest.getContent());
		assertEquals(messageTypeUnderTest.getFatherID(), messageTypeUnderTest.getFatherID());
	}
}
