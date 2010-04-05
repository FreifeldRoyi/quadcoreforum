package testing.domain.user;

import org.junit.Test;

import java.util.*;
import forum.server.domainlayer.user.ForumMember;

public class ForumMemberTest extends ForumUserTest {


	/**
	 * Test method for {@link forum.server.domainlayer.user.ForumMember#ForumMember(long, String, 
	 * String, String, String, String, Collection)
	 * 
	 * Test method for {@link forum.server.domainlayer.user.ForumMember#ForumMember(long, String, String,
	 * String, String, String, Collection, Collection)
	 */
	@Test
	public void testMemberConstructor() {
		final Collection<Long> tPostsIDs = new Vector<Long>();
		tPostsIDs.add(3L);
		tPostsIDs.add(2L);

		final ForumMember[] tMembersArray = {
				new ForumMember(0, "member0", "password0", "a", "a", "a@a", super.emptyPermissionsContainer),
				new ForumMember(1, "member1", "password1", "b", "b", "b@b", super.singletonPermissionsContainer),
				new ForumMember(2, "member2", "password2", "c", "c", "c@c", super.fullPermissionsContainer),
				new ForumMember(3, "member3", "password3", "d", "d", "d@d", super.emptyPermissionsContainer,
						tPostsIDs)

		};

		assertEquals(tMembersArray[0].getPermissions(), super.emptyPermissionsContainer);
		assertEquals(tMembersArray[1].getPermissions(), super.singletonPermissionsContainer);
		assertEquals(tMembersArray[2].getPermissions(), super.fullPermissionsContainer);
		assertEquals(tMembersArray[3].getPermissions(), super.emptyPermissionsContainer);
		assertEquals(tMembersArray[3].getPostsNumber(), 2);

		for (int i = 0; i < tMembersArray.length - 1; i++) {
			assertTrue(tMembersArray[i].getUsername().equals("member" + i));
			assertTrue(tMembersArray[i].getPassword().equals("password" + i));
			assertTrue(tMembersArray[i].getPostsNumber() == 0);
		}
		assertTrue(tMembersArray[tMembersArray.length - 1].getUsername().equals("member3"));
		assertTrue(tMembersArray[tMembersArray.length - 1].getPassword().equals("password3"));
	}


	/**
	 * Test method for {@link forum.server.domainlayer.user.ForumMember#addPostedMessage(long)
	 * 
	 * Test method for {@link forum.server.domainlayer.user.ForumMember#removePostedMessage(long)
	 * 
	 * Test method for {@link forum.server.domainlayer.user.ForumMember#getPostsNumber()
	 */
	@Test
	public void testAddPostedMessage() {
		ForumMember tMemberUnderTest = new ForumMember(0, "member0", "password0", "a",
				"a", "a@a", super.emptyPermissionsContainer);
		assertTrue(tMemberUnderTest.getPostsNumber() == 0);
		tMemberUnderTest.addPostedMessage(1L);
		assertTrue(tMemberUnderTest.getPostsNumber() == 1);
		tMemberUnderTest.addPostedMessage(5L);
		tMemberUnderTest.addPostedMessage(6L);
		assertTrue(tMemberUnderTest.getPostsNumber() == 3);
		tMemberUnderTest.removePostedMessage(6L);
		assertTrue(tMemberUnderTest.getPostsNumber() == 2);
		tMemberUnderTest.removePostedMessage(4L); // not exists
		assertTrue(tMemberUnderTest.getPostsNumber() == 2);
		tMemberUnderTest.removePostedMessage(1L);
		assertTrue(tMemberUnderTest.getPostsNumber() == 1);
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.ForumMember#setFirstName(String)
	 * 
	 * Test method for {@link forum.server.domainlayer.user.ForumMember#setLastName(String)
	 * 
	 * Test method for {@link forum.server.domainlayer.user.ForumMember#setPassword(String)
	 */
	@Test
	public void testSetters() {
		ForumMember tMemberUnderTest = new ForumMember(0, "member0", "password0", "a",
				"a", "a@a", super.emptyPermissionsContainer);
		assertEquals(tMemberUnderTest.getFirstName(), "a");
		tMemberUnderTest.setFirstName("b");
		assertEquals(tMemberUnderTest.getFirstName(), "b");
		tMemberUnderTest.setLastName("c");
		assertEquals(tMemberUnderTest.getLastName(), "c");
		assertEquals(tMemberUnderTest.getFirstName(), "b");
		assertEquals(tMemberUnderTest.getPassword(), "password0");
		tMemberUnderTest.setPassword("password2");
		assertEquals(tMemberUnderTest.getPassword(), "password2");
	}
}
