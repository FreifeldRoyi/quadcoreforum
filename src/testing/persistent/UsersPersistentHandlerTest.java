/**
 * This test test the UsersPersistentHandler class
 */
package testing.persistent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.Settings;
import forum.server.domainlayer.user.ForumMember;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.SessionFactoryUtil;
import forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author sepetnit
 *
 */
public class UsersPersistentHandlerTest extends TestCase {

	private SessionFactory factory; // connects to the database directly via a JDBC driver
	private UsersPersistenceHandler usersPersistentHandlerUnderTest;

	private static final Permission[] DEFAULT_PERMISSIONS_ARRAY = {Permission.VIEW_ALL,
		Permission.OPEN_THREAD, Permission.REPLY_TO_MESSAGE};

	public UsersPersistentHandlerTest() {
		usersPersistentHandlerUnderTest = new UsersPersistenceHandler();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Settings.switchToTestMode();
		factory = SessionFactoryUtil.getInstance();	
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#getGuestsNumber(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetGuestsNumber() {
		try {
			// at first there is no guests
			assertEquals(usersPersistentHandlerUnderTest.getGuestsNumber(factory), 0);
			// adds a guest and assures that the guests number is 1
			long tID1 = usersPersistentHandlerUnderTest.getNextFreeGuestID(factory);
			assertEquals(usersPersistentHandlerUnderTest.getGuestsNumber(factory), 1);
			// adds another guest (now guests number should be 2)
			long tID2 = usersPersistentHandlerUnderTest.getNextFreeGuestID(factory);
			assertEquals(usersPersistentHandlerUnderTest.getGuestsNumber(factory), 2);
			// removes the guests
			usersPersistentHandlerUnderTest.removeGuest(factory, tID2);
			assertEquals(usersPersistentHandlerUnderTest.getGuestsNumber(factory), 1);
			usersPersistentHandlerUnderTest.removeGuest(factory, tID1);
			assertEquals(usersPersistentHandlerUnderTest.getGuestsNumber(factory), 0);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#getNextFreeGuestID(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetNextFreeGuestID() {
		try {
			// checks that the guests IDs are allocated in a decreasing order from -2
			assertTrue(usersPersistentHandlerUnderTest.getNextFreeGuestID(factory) == -2);
			assertTrue(usersPersistentHandlerUnderTest.getNextFreeGuestID(factory) == -3);
			usersPersistentHandlerUnderTest.removeGuest(factory, -2);
			assertTrue(usersPersistentHandlerUnderTest.getNextFreeGuestID(factory) == -4);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#removeGuest(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testRemoveGuest() {
		try {
			// assures there are no exceptions and that removing of guests decreases the number of 
			// connected users
			long tGuestID = usersPersistentHandlerUnderTest.getNextFreeGuestID(factory);
			assertEquals(usersPersistentHandlerUnderTest.getGuestsNumber(factory), 1);
			usersPersistentHandlerUnderTest.removeGuest(factory, tGuestID);
			assertEquals(usersPersistentHandlerUnderTest.getGuestsNumber(factory), 0);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#getFirstFreeMemberID(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetFirstFreeMemberID() {
		try {
			// checks that the members IDs are allocated in an increasing order from 
			// 1 (at the test database an admin will exist)
			assertTrue(usersPersistentHandlerUnderTest.getFirstFreeMemberID(factory) == 1);
			assertTrue(usersPersistentHandlerUnderTest.getFirstFreeMemberID(factory) == 2);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#getActiveMemberUserNames(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetActiveMemberUserNames() {
		try {
			// adds two new members
			usersPersistentHandlerUnderTest.addNewMember(factory, 1,
					"a", "b", "c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			usersPersistentHandlerUnderTest.addNewMember(factory, 2,
					"f", "g", "h", "i", "j@j", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			// makes the member active
			usersPersistentHandlerUnderTest.addActiveMemberID(factory, 1);
			// checks whether the member is marked as active
			Collection<String> tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertEquals(tActiveUsernames.size(), 1);
			assertTrue(tActiveUsernames.iterator().next().equals("a"));
			// makes the member to be active again
			usersPersistentHandlerUnderTest.addActiveMemberID(factory, 1);
			// the number of active members and user-names should be the same as the previous
			tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertEquals(tActiveUsernames.size(), 1);
			assertTrue(tActiveUsernames.iterator().next().equals("a"));
			// makes the second member active
			usersPersistentHandlerUnderTest.addActiveMemberID(factory, 2);
			// checks whether the collection of active user-name contains the two user-names of 
			// the added members
			tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertEquals(tActiveUsernames.size(), 2);
			assertTrue(tActiveUsernames.contains("a"));
			assertTrue(tActiveUsernames.contains("f"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#addActiveMemberID(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testAddActiveMemberID() {
		try {
			// adds two new members
			usersPersistentHandlerUnderTest.addNewMember(factory, 1,
					"a", "b", "c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			usersPersistentHandlerUnderTest.addNewMember(factory, 2,
					"f", "g", "h", "i", "j@j", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			// makes the member active
			usersPersistentHandlerUnderTest.addActiveMemberID(factory, 1);
			// checks whether the member is marked as active
			Collection<String> tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertEquals(tActiveUsernames.size(), 1);
			assertTrue(tActiveUsernames.iterator().next().equals("a"));
			// makes the member to be active again
			usersPersistentHandlerUnderTest.addActiveMemberID(factory, 1);
			// the number of active members and user-names should be the same as the previous
			tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertEquals(tActiveUsernames.size(), 1);
			assertTrue(tActiveUsernames.iterator().next().equals("a"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#removeActiveMemberID(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testRemoveActiveMemberID() {
		try {
			// adds two new members
			usersPersistentHandlerUnderTest.addNewMember(factory, 1,
					"a", "b", "c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			usersPersistentHandlerUnderTest.addNewMember(factory, 2,
					"f", "g", "h", "i", "j@j", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			// makes the member active
			usersPersistentHandlerUnderTest.addActiveMemberID(factory, 1);
			// checks whether the member is marked as active
			Collection<String> tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertEquals(tActiveUsernames.size(), 1);
			assertTrue(tActiveUsernames.iterator().next().equals("a"));
			// makes the member to be active again
			usersPersistentHandlerUnderTest.addActiveMemberID(factory, 1);
			// the number of active members and user-names should be the same as the previous
			tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertEquals(tActiveUsernames.size(), 1);
			assertTrue(tActiveUsernames.iterator().next().equals("a"));
			usersPersistentHandlerUnderTest.removeActiveMemberID(factory, 1);
			// the number of active members and user-names should be the same as the previous
			// because one member (with the same user-name is connected - according to the database
			// value)
			tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertEquals(tActiveUsernames.size(), 1);
			assertTrue(tActiveUsernames.iterator().next().equals("a"));
			// after the next command there will be no connected members
			usersPersistentHandlerUnderTest.removeActiveMemberID(factory, 1);
			tActiveUsernames = usersPersistentHandlerUnderTest.getActiveMemberUserNames(factory);
			assertTrue(tActiveUsernames.isEmpty());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#getAllMembers(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetAllMembers() {
		try {
			// adds two new members
			usersPersistentHandlerUnderTest.addNewMember(factory, 1,
					"a", "b", "c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			usersPersistentHandlerUnderTest.addNewMember(factory, 2,
					"f", "g", "h", "i", "j@j", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			// retrieves all the forum members and checks the returned collection contains only
			// the new added members
			Collection<ForumMember> tAllMembers = usersPersistentHandlerUnderTest.getAllMembers(factory);
			assertEquals(tAllMembers.size(), 3);
			Iterator<ForumMember> tAllMembersIter = tAllMembers.iterator();
			long tFirstMemberID = tAllMembersIter.next().getID();
			long tSecondMemberID = tAllMembersIter.next().getID();
			long tThirdMemberID = tAllMembersIter.next().getID();
			assertTrue(tFirstMemberID == 0 || tSecondMemberID == 0 || tThirdMemberID == 0);
			assertTrue(tFirstMemberID == 1 || tSecondMemberID == 1 || tThirdMemberID == 1);
			assertTrue(tFirstMemberID == 2 || tSecondMemberID == 2 || tThirdMemberID == 2);
			assertTrue((tFirstMemberID != tSecondMemberID) && 
					(tSecondMemberID != tThirdMemberID) &&
					(tFirstMemberID != tThirdMemberID));

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#getMemberByID(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testGetMemberByID() {
		try {
			// adds two new members
			usersPersistentHandlerUnderTest.addNewMember(factory, 1,
					"a", "b", "c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			usersPersistentHandlerUnderTest.addNewMember(factory, 2,
					"f", "g", "h", "i", "j@j", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			// retrieves the members by the id
			ForumMember tSecondMember = usersPersistentHandlerUnderTest.getMemberByID(factory, 2);
			ForumMember tFirstMember = usersPersistentHandlerUnderTest.getMemberByID(factory, 1);
			assertEquals(tSecondMember.getID(), 2);
			assertEquals(tFirstMember.getID(), 1);
			// retrieves a non existing member and assures that an exception is thrrown
			try {
				usersPersistentHandlerUnderTest.getMemberByID(factory, 232);
				fail("a non-existing member was retrieved");
			}
			catch (NotRegisteredException e) {
				// it's ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#getMemberByUsername(org.hibernate.SessionFactory, java.lang.String)}.
	 */
	@Test
	public void testGetMemberByUsername() {
		try {
			// adds two new members
			usersPersistentHandlerUnderTest.addNewMember(factory, 1,
					"a", "b", "c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			usersPersistentHandlerUnderTest.addNewMember(factory, 2,
					"f", "g", "h", "i", "j@j", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			ForumMember tSecondMember = usersPersistentHandlerUnderTest.getMemberByUsername(factory, "f");
			ForumMember tFirstMember = usersPersistentHandlerUnderTest.getMemberByUsername(factory, "a");
			assertEquals(tSecondMember.getID(), 2);
			assertEquals(tFirstMember.getID(), 1);
			assertEquals(tSecondMember.getUsername(), "f");
			assertEquals(tFirstMember.getUsername(), "a");
			try {
				usersPersistentHandlerUnderTest.getMemberByUsername(factory, "dfadcvcv");
				fail("a non-existing member was retrieved");
			}
			catch (NotRegisteredException e) {
				// it's ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#getMemberByEmail(org.hibernate.SessionFactory, java.lang.String)}.
	 */
	@Test
	public void testGetMemberByEmail() {
		try {
			// adds two new members
			usersPersistentHandlerUnderTest.addNewMember(factory, 1,
					"a", "b", "c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			usersPersistentHandlerUnderTest.addNewMember(factory, 2,
					"f", "g", "h", "i", "j@j", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			ForumMember tSecondMember = usersPersistentHandlerUnderTest.getMemberByEmail(factory, "j@j");
			ForumMember tFirstMember = usersPersistentHandlerUnderTest.getMemberByEmail(factory, "e@e");
			assertEquals(tSecondMember.getID(), 2);
			assertEquals(tFirstMember.getID(), 1);
			assertEquals(tSecondMember.getEmail(), "j@j");
			assertEquals(tFirstMember.getEmail(), "e@e");
			try {
				usersPersistentHandlerUnderTest.getMemberByEmail(factory, "dfdf@cvcv");
				fail("a non-existing member was retrieved");
			}
			catch (NotRegisteredException e) {
				// it's ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.user.UsersPersistenceHandler#addNewMember(org.hibernate.SessionFactory, long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Collection)}.
	 */
	@Test
	public void testAddNewMember() {
		try {
			// a simple addition of a new member
			usersPersistentHandlerUnderTest.addNewMember(factory, 1,
					"a", "b", "c", "d", "e@e", Arrays.asList(DEFAULT_PERMISSIONS_ARRAY));
			ForumMember tMember = usersPersistentHandlerUnderTest.getMemberByID(factory, 1);
			assertEquals(tMember.getID(), 1);
			assertEquals(tMember.getUsername(), "a");
			assertEquals(tMember.getPassword(), "b");
			assertEquals(tMember.getLastName(), "c");
			assertEquals(tMember.getFirstName(), "d");
			assertEquals(tMember.getEmail(), "e@e");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
