/**
 * 
 */
package testing.domain.user;

import java.util.*;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.Settings;
import forum.server.domainlayer.user.ForumMember;
import forum.server.domainlayer.user.ForumUser;
import forum.server.domainlayer.user.Permission;
import forum.server.domainlayer.user.UsersCache;
import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;

/**
 * @author sepetnit
 *
 */
public class UsersCacheTest extends TestCase {

	private UsersCache cache;

	private static final Permission[] DEFAULT_PERMISSIONS_ARRAY = {Permission.VIEW_ALL,
		Permission.OPEN_THREAD, Permission.REPLY_TO_MESSAGE};

	private static final Collection<Permission> DEFAULT_PERMISSIONS =
		Arrays.asList(UsersCacheTest.DEFAULT_PERMISSIONS_ARRAY);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Settings.switchToTestMode();
		this.cache = new UsersCache();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersCache#UsersCache()}.
	 */
	@Test
	public void testUsersCacheConstructor() {


	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersCache#getUserByID(long)}.
	 */
	@Test
	public void testGetUserByID() {
		try {
			final ForumUser tCurrentMember = this.cache.createNewMember("a", "b", "c", "d", "e@e",
					UsersCacheTest.DEFAULT_PERMISSIONS);
			final ForumUser tCurrentGuest = this.cache.createNewGuest(UsersCacheTest.DEFAULT_PERMISSIONS);

			final ForumUser tObtainedMember = this.cache.getUserByID(tCurrentMember.getID());
			final ForumUser tObtainedGuest = this.cache.getUserByID(tCurrentGuest.getID());
			assertEquals(tCurrentGuest, tObtainedGuest);
			assertEquals(tCurrentMember, tObtainedMember);
			try {
				this.cache.getUserByID(10);
				fail("a not created user with id 10 was obtained for some reason");
			}
			catch (NotRegisteredException e) {
				// success - do nothing
			}
			catch (DatabaseRetrievalException e) {
				fail(e.getMessage());
			}
		} 
		catch (MemberAlreadyExistsException e) {
			fail("can't create new member in order to test the GetUserByID() method");
		} 
		catch (DatabaseUpdateException e) {
			fail("can't create new member in order to test the GetUserByID() method");
		}
		catch (NotRegisteredException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());	
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersCache#getMemberByUsername(java.lang.String)}.
	 */
	@Test
	public void testGetMemberByUsername() {
		try {
			final ForumMember tForumMember = this.cache.createNewMember("a", "b", "c", "d", "e@e",
					UsersCacheTest.DEFAULT_PERMISSIONS);
			final ForumMember tObtainedMemberUnderTest = this.cache.getMemberByUsername(tForumMember.getUsername());
			assertEquals(tForumMember, tObtainedMemberUnderTest);
			final ForumMember tNotFoundMember = this.cache.getMemberByUsername("test");
			assertNull(tNotFoundMember);
		}
		catch (MemberAlreadyExistsException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersCache#getMemberByEmail(java.lang.String)}.
	 */
	@Test
	public void testGetMemberByEmail() {
		try {
			final ForumMember tForumMember = this.cache.createNewMember("a", "b", "c", "d", "e@e",
					UsersCacheTest.DEFAULT_PERMISSIONS);
			final ForumMember tObtainedMemberUnderTest = this.cache.getMemberByEmail(tForumMember.getEmail());
			assertEquals(tForumMember, tObtainedMemberUnderTest);
			final ForumMember tNotFoundMember = this.cache.getMemberByEmail("i@i");
			assertNull(tNotFoundMember);
		}
		catch (MemberAlreadyExistsException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}	
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersCache#getAllUsers()}.
	 */
	@Test
	public void testGetAllUsers() {
		final int tCreatedMembersNumber = 3;
		final Collection<ForumUser> tCreatedUsers = new HashSet<ForumUser>();

		try {

			for (int i = 1; i < tCreatedMembersNumber; i++) {
				final ForumUser tCurrentUser = this.cache.createNewGuest(UsersCacheTest.DEFAULT_PERMISSIONS);
				tCreatedUsers.add(tCurrentUser);
			}
			final Collection<ForumUser> tObtainedUsers = this.cache.getAllUsers();
			assertEquals(tCreatedUsers, tObtainedUsers);
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}

	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersCache#createNewGuest(java.util.Collection)}.
	 */
	@Test
	public void testCreateNewGuest() {
		try {
			final ForumUser tCreatedGuest = this.cache.createNewGuest(UsersCacheTest.DEFAULT_PERMISSIONS);
			assertEquals(tCreatedGuest.getPermissions(), UsersCacheTest.DEFAULT_PERMISSIONS);
			assertEquals(tCreatedGuest, this.cache.getUserByID(tCreatedGuest.getID()));
		} 
		catch (NotRegisteredException e) {
			fail(e.getMessage());
		} 
		catch (DatabaseRetrievalException e) {
			fail("A retrieval of guests shouldn't access to the database");
		}
		catch (DatabaseUpdateException e) {
			fail("Can't create a new guest");
		}

	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersCache#removeGuest(long)}.
	 */
	@Test
	public void testRemoveGuest() {
		try {
			final ForumUser tCurrentGuest = this.cache.createNewGuest(UsersCacheTest.DEFAULT_PERMISSIONS);
			final long tCurrentGuestID = tCurrentGuest.getID();
			assertNotNull(this.cache.getUserByID(tCurrentGuestID));
			this.cache.removeGuest(tCurrentGuestID);
			try {
				this.cache.getUserByID(tCurrentGuestID);
				fail("guest with id " + tCurrentGuestID + " after deletion.");
			}
			catch (NotRegisteredException e) {
				// success - do nothing
			}
		} 
		catch (NotRegisteredException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}

	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersCache#createNewMember(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Collection)}.
	 */
	@Test
	public void testCreateNewMember() {
		try {
			final ForumMember tForumMemberUnderTest = this.cache.createNewMember("member1", "password1",
					"a", "b", "c@c", UsersCacheTest.DEFAULT_PERMISSIONS);
			assertEquals(tForumMemberUnderTest.getUsername(), "member1");
			assertNotSame(tForumMemberUnderTest.getPassword(), "password1");
			assertEquals(tForumMemberUnderTest.getLastName(), "a");
			assertEquals(tForumMemberUnderTest.getFirstName(), "b");
			assertEquals(tForumMemberUnderTest.getEmail(), "c@c");
			assertEquals(tForumMemberUnderTest.getPermissions(), UsersCacheTest.DEFAULT_PERMISSIONS);
			assertNotSame(tForumMemberUnderTest, this.cache.getMemberByUsername("member1"));
		}
		catch (MemberAlreadyExistsException e) {
			fail(e.getMessage());
		} 
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}
}
