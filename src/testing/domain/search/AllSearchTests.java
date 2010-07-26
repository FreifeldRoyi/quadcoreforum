package testing.domain.search;

import junit.framework.*;

public class AllSearchTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.server.domainlayer.search");
		//$JUnit-BEGIN$
		suite.addTestSuite(SearchAgentTest.class);
		//$JUnit-END$
		return suite;
	}
}
