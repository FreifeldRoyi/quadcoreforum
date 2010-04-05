/**
 * 
 */
package testing.domain.user;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.Settings;
import forum.server.domainlayer.ForumDataHandler;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.domainlayer.interfaces.UIUser;
import forum.server.domainlayer.user.UsersController;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;
import forum.server.persistentlayer.pipe.user.exceptions.NotConnectedException;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;
import forum.server.persistentlayer.pipe.user.exceptions.WrongPasswordException;

/**
 * @author sepetnit
 *
 */
public class UsersControllerTest extends TestCase {

	private ForumDataHandler dataHandler;
	private UsersController userControllerUnderTest;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Settings.switchToTestMode();
		this.dataHandler = new ForumDataHandler();
		this.userControllerUnderTest = new UsersController(dataHandler);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersController#getActiveGuestsNumber()}.
	 * 
	 * Test method for {@link forum.server.domainlayer.user.UsersController#addGuest()}.
	 * 
	 * Test method for {@link forum.server.domainlayer.user.UsersController#removeGuest(long)}.
	 */
	@Test
	public void testGuestMethods() {
		System.out.println("a");
		assertTrue(this.userControllerUnderTest.getActiveGuestsNumber() == 0);
		final UIUser tGuest1 = this.userControllerUnderTest.addGuest();
		assertTrue(this.userControllerUnderTest.getActiveGuestsNumber() == 1);
		final UIUser tGuest2 = this.userControllerUnderTest.addGuest();
		assertTrue(this.userControllerUnderTest.getActiveGuestsNumber() == 2);
		this.userControllerUnderTest.removeGuest(tGuest1.getID());
		assertTrue(this.userControllerUnderTest.getActiveGuestsNumber() == 1);
		this.userControllerUnderTest.removeGuest(tGuest2.getID());
		assertTrue(this.userControllerUnderTest.getActiveGuestsNumber() == 0);
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersController#getActiveMemberNames()}.
	 */
	@Test
	public void testGetActiveMemberNames() {
		try {
			assertTrue(this.userControllerUnderTest.getActiveMemberNames().isEmpty());
			this.userControllerUnderTest.registerNewMember("a", "b", "c", "d", "e@e");
			this.userControllerUnderTest.login("a", "b");
			assertTrue(this.userControllerUnderTest.getActiveMemberNames().size() == 1);
			this.userControllerUnderTest.logout("a");
			assertTrue(this.userControllerUnderTest.getActiveMemberNames().isEmpty());
		}
		catch (MemberAlreadyExistsException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (NotRegisteredException e) {
			fail(e.getMessage());
		}
		catch (WrongPasswordException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());		
		}
		catch (NotConnectedException e) {
			fail(e.getMessage());
		} 
	}

	/**
	 * Test method for {@link forum.server.domainlayer.user.UsersController#registerNewMember(java.lang.String, java.lang.String, 
	 * java.lang.String, java.lang.String, java.lang.String)}.
	 * 
	 * Checks that the new member is indeed created and that he isn't initially logged-in
	 * 
	 * Also tests that the password is saved encrypted
	 */
	@Test
	public void testRegisterNewMember() {
		try {
			final long tNewUserID1 = this.userControllerUnderTest.registerNewMember("a1", "b1", "c1", "d1", "e1@e1");
			assertTrue(this.dataHandler.getUsersCache().getMemberByUsername("a1").getUsername().equals("a1"));
			assertFalse(this.dataHandler.getUsersCache().getMemberByUsername("a1").getPassword().equals("b1"));
			final long tNewUserID2 = this.userControllerUnderTest.registerNewMember("a2", "b1", "c2", "d2", "e2@e2");
			assertTrue(this.dataHandler.getUsersCache().getMemberByUsername("a2").getUsername().equals("a2"));
			assertTrue(this.dataHandler.getUsersCache().getMemberByUsername("a1").
					getPassword().equals(this.dataHandler.getUsersCache().getMemberByUsername("a2").
							getPassword()));
			assertNotSame(tNewUserID1, tNewUserID2);
			try {
				this.userControllerUnderTest.logout("a2");
				fail("the user a2 shouldn't be logged-in after the registration!");
			}
			catch (NotConnectedException e) {
				assertTrue(true);
			}
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
	 * Test method for {@link forum.server.domainlayer.user.UsersController#login(java.lang.String, java.lang.String)}.
	 * 
	 * The test tests that the logged in member id is a realy existing member of the forum
	 * 
	 * The test tests that the password of the new member logged in member is indeed the password given in the login
	 * signature
	 */
	@Test
	public void testLogin() {
		try {
			final long tNewUserID = this.userControllerUnderTest.registerNewMember("a", "b", "c", "d", "e@e");
			final UIMember tLoggedIn = this.userControllerUnderTest.login("a", "b");
			assertEquals(tLoggedIn.getID(), tNewUserID);
			assertEquals("a", this.dataHandler.getUsersCache().getMemberByUsername("a").getUsername());
			assertNotSame("b", this.dataHandler.getUsersCache().getMemberByUsername("a").getPassword());
		}
		catch (MemberAlreadyExistsException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (NotRegisteredException e) {
			fail(e.getMessage());
		}
		catch (WrongPasswordException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}
}
