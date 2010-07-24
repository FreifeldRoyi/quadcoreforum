/**
 * This class is a test suite which is used in order to run automatically all the acceptance
 * tests contained in this AT unit.
 */
package testing.acceptancetests.main;

import junit.framework.Test;
import junit.framework.TestSuite;
import testing.acceptancetests.stories.*;

/**
 * @author 
 * 		Sepetnitsky Vitali 310106745
 *
 */

public class AllStoryTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("acceptance-tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ReplyToMessageTestStory.class);
		suite.addTestSuite(DeleteMessageTestStory.class);
		suite.addTestSuite(RegistrationTestStory.class);
		suite.addTestSuite(LoginLogoutTestStory.class);
		//$JUnit-END$
		return suite;
	}
}
