/**
 * This class contains general forum related methods which are used by the acceptance tests
 * which are derived from this class.
 */
package testing.acceptancetests.stories;

import java.util.Collection;

import forum.server.Settings;
import forum.server.domainlayer.SystemLogger;
import forum.server.updatedpersistentlayer.SessionFactoryUtil;

import testing.acceptancetests.bridges.*;
import junit.framework.TestCase;

/**
 * @author 
 * 		Sepetnitsky Vitali 310106745
 *
 */
public class GeneralMethodsTest extends TestCase {

	private GeneralForumBridge implementationBridge;

	/**
	 * This method is called before the tests execution, it switches the forum
	 * database to be a new test database used for testing purposes only
	 * 
	 */
	protected void setUp() throws Exception {
		SystemLogger.info("Switching to test database ...");
		Settings.switchToTestMode();
		this.implementationBridge = new ProxyBridge();
	}

	/**
	 * 
	 * Switches the database to be the regular one.
	 * 
	 * @throws 
	 * 		java.lang.Exception
	 */
	protected void tearDown() throws Exception {
		Settings.switchToRegularMode();
		SessionFactoryUtil.close();
		SystemLogger.info("The database was switched back to regular");
	}

	/**
	 * The constructor of all the tests, initializes the bridge by a proxy bridge which is common to
	 * all the tests and is connected to the implementation.
	 */
	public GeneralMethodsTest() {
		super();
	}

	/**
	 * @see
	 * 		GeneralForumBridge#login(String, String)
	 */
	protected String[] login(final String username, final String password) {
		return this.implementationBridge.login(username, password);
	}

	/**
	 * @see
	 * 		GeneralForumBridge#logout(String)
	 */
	protected boolean logout(final String username) {
		return this.implementationBridge.logout(username);
	}

	/**
	 * @see
	 * 		GeneralForumBridge#register(String, String, String, String, String)
	 */
	protected boolean register(final String username, final String password, final String lastName,
			final String firstName, final String email) {
		return this.implementationBridge.register(username, password, lastName, firstName, email);
	}	

	/**
	 * @see
	 * 		GeneralForumBridge#addNewSubject(long, long, String, String)
	 */
	protected long addNewSubject(final long userID, final long fatherID, final String name, 
			final String description){
		return this.implementationBridge.addNewSubject(userID, fatherID, name, description);
	}

	/**
	 * @see
	 * 		GeneralForumBridge#openNewThread(long, long, String, String, String)
	 */
	protected long openNewThread(final long userID, final long subjectID, final String topic, 
			final String title, final String content) {
		return this.implementationBridge.openNewThread(userID, subjectID, topic, title, content);
	}

	/**
	 * @see
	 * 		GeneralForumBridge#addNewReply(long, long, String, String)
	 */
	protected boolean addNewReply(final long authorID, final long fatherID, final String title,
			final String content) {
		return this.implementationBridge.addNewReply(authorID, fatherID, title, content);
	}

	/**
	 * @see
	 * 		GeneralForumBridge#getReplies(long)
	 */
	protected Collection<String> getReplies(final long fatherID) {
		return this.implementationBridge.getReplies(fatherID);
	}
}
