/**
 * This class is responsible of performing an acceptance test of registration use case
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
public class RegistrationTestStory extends GeneralMethodsTest {

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
	 * An acceptance test for registration use-case
	 * 
	 * The test performs registrations under different conditions and assures that the 
	 * application responds as it is expected by the client.
	 */
	public void testRegistration() {
		assertTrue(super.register("user10", "pass10", "l1", "f1", "ez@ez") != -1);
		assertNotNull(super.login("user10", "pass10"));
		// an existing user
		assertEquals(super.register("user10", "pass2", "l2", "f2", "ere@fgf"), -1);
		assertEquals(super.register("user20", "pass2", "l2", "f2", "ez@ez"), -1);
		// new valid user
		assertFalse(super.register("user2", "pass2", "l2", "f2", "ere@fgf") == -1);
		assertNotNull(super.login("user2", "pass2"));
		// user3 isn't a registered user
		assertNull(super.login("user3", "pass3"));
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
