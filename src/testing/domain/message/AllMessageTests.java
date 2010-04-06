package testing.domain.message;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllMessageTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.testing.domainlayer.message");
		//$JUnit-BEGIN$
		suite.addTestSuite(ForumSubjectTest.class);
		suite.addTestSuite(ForumThreadTest.class);
		suite.addTestSuite(ForumMessageTest.class);
		suite.addTestSuite(MessagesControllerTest.class);
		suite.addTestSuite(MessagesCacheTest.class);
		
		//$JUnit-END$
		return suite;
	}
}
