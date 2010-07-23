/**
 * 
 */
package testing.domain.user;


import java.util.*;

import org.junit.Test;

import junit.framework.TestCase;
import forum.server.domainlayer.user.*;

/**
 * @author sepetnit
 *
 */
public class ForumUserTest extends TestCase {

	protected Collection<Permission> emptyPermissionsContainer;
	protected Collection<Permission> singletonPermissionsContainer;
	protected Collection<Permission> fullPermissionsContainer;
	
	public ForumUserTest() {
		super();
		this.emptyPermissionsContainer = new HashSet<Permission>();
		this.singletonPermissionsContainer = new HashSet<Permission>();
		this.singletonPermissionsContainer.add(Permission.ADD_SUBJECT);
		this.fullPermissionsContainer = new HashSet<Permission>();
		this.fullPermissionsContainer.addAll(Arrays.asList(Permission.values()));
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.ForumUser#ForumUser(long, Collection)
	 */
	@Test
	public void testUserConstructor() {
		final int tNumOfForumUsersUnderTest = 3;
		final ForumUser[] tUsersArray = new ForumUser[tNumOfForumUsersUnderTest];
		
		tUsersArray[0] = new ForumUser(0, this.emptyPermissionsContainer);
		assertEquals(tUsersArray[0].getPermissions(), this.emptyPermissionsContainer);
		
		tUsersArray[1] = new ForumUser(1, this.singletonPermissionsContainer);
		assertEquals(tUsersArray[1].getPermissions(), this.singletonPermissionsContainer);
		
		tUsersArray[2] = new ForumUser(2, this.fullPermissionsContainer);
		assertEquals(tUsersArray[2].getPermissions(), this.fullPermissionsContainer);
		
		
		 // Checks the invariant that all the users ids are different
		 // The size of the created set should be the size of the users array
		 
		final Collection<Long> tUsersIDs = new HashSet<Long>();
		for (final ForumUser tCurrentUser : tUsersArray)
			tUsersIDs.add(tCurrentUser.getID());
			
		assertEquals(tUsersIDs.size(), tNumOfForumUsersUnderTest);
	}
	
	
	/**
	 * Test methods for {@link forum.server.domainlayer.user.ForumUser#addPermission(Permission)}
	 * Test methods for {@link forum.server.domainlayer.user.ForumUser#removePermission(Permission)}
	 * Test methods for {@link forum.server.domainlayer.user.ForumUser#isAllowed(Permission)}
	 */
	@Test
	public void testAddAndRemovePrivilege() {
		final ForumUser tForumUserUnderTest = new ForumUser(0, this.emptyPermissionsContainer);
		tForumUserUnderTest.addPermission(Permission.ADD_SUBJECT);
		assertTrue(tForumUserUnderTest.isAllowed(Permission.ADD_SUBJECT));
		tForumUserUnderTest.addPermission(Permission.ADD_SUB_SUBJECT);
		assertTrue(tForumUserUnderTest.isAllowed(Permission.ADD_SUB_SUBJECT));
		tForumUserUnderTest.addPermission(Permission.VIEW_ALL);
		assertTrue(tForumUserUnderTest.isAllowed(Permission.VIEW_ALL));
		tForumUserUnderTest.removePermission(Permission.ADD_SUB_SUBJECT);
		assertFalse(tForumUserUnderTest.isAllowed(Permission.ADD_SUB_SUBJECT));
		tForumUserUnderTest.removePermission(Permission.ADD_SUBJECT);
		assertFalse(tForumUserUnderTest.isAllowed(Permission.ADD_SUBJECT));
		assertTrue(tForumUserUnderTest.isAllowed(Permission.VIEW_ALL));
		tForumUserUnderTest.removePermission(Permission.VIEW_ALL);
		assertFalse(tForumUserUnderTest.isAllowed(Permission.VIEW_ALL));
		tForumUserUnderTest.addPermission(Permission.ADD_SUB_SUBJECT);
		assertTrue(tForumUserUnderTest.isAllowed(Permission.ADD_SUB_SUBJECT));
	}
}
