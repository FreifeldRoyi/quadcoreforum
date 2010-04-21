/**
 * This class is responsible of performing an acceptance test of login and logout use cases
 */
package testing.acceptancetests.stories;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 
 * 		Sepetnitsky Vitali 310106745
 *
 */
public class LoginLogoutTestStory extends GeneralMethodsTest {

	/**
	 * @see
	 * 		{@link #setUp()}
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	/**
	 * An acceptance test for login and logout use-cases.
	 * 
	 * The test performs login and logout under different conditions and assures that the 
	 * application responds as it is expected by the client.
	 */
	public void testLoginAndLogoutFromTheSystem() {
		// registers two new users to the forum - the method should succeed because we start from an empty
		// database and the two users details are different
		assertTrue(super.register("user1", "pass1", "last1", "first1", "user1@gmail.com"));
		assertTrue(super.register("user2", "pass2", "last2", "first2", "user2@gmail.com"));
		String[] tUser1Information = {"user1", "last1", "first1", "user1@gmail.com"};
		// logs in the first user with correct user-name and password
		String[] tLoggedInInfo = super.login("user1", "pass1");
		// checks whether the login succeeded
		assertNotNull(tLoggedInInfo);
		// this assertion should always succeed, in case the bridges are implemented properly
		assertEquals(tLoggedInInfo.length, 4); 
		for (int i = 0; i < tLoggedInInfo.length; i++)
			assertEquals(tLoggedInInfo[i], tUser1Information[i]);
		// the logout should succeed
		assertTrue(super.logout("user1"));	
		// logs in the second user with incorrect password
		assertNull(super.login("user2", "pass3"));
		// the user hasn't been logged in - the logout should fail
		assertFalse(super.logout("user2"));
		// logs in the second user with correct user-name and password
		String[] tUser2Information = {"user2", "last2", "first2", "user2@gmail.com"};
		tLoggedInInfo = super.login("user2", "pass2");
		// checks whether the login succeeded
		assertNotNull(tLoggedInInfo);
		// this assertion should always succeed, in case the bridges are implemented properly
		assertEquals(tLoggedInInfo.length, 4); 
		for (int i = 0; i < tLoggedInInfo.length; i++)
			assertEquals(tLoggedInInfo[i], tUser2Information[i]);
		// the logout should succeed
		assertTrue(super.logout("user2"));
		// logs in a non-existing user - null should be returned
		assertEquals(super.login("user3", "pass3"), null);	
	}
	
	/**
	 * @see
	 * 		{@link #tearDown()}
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
