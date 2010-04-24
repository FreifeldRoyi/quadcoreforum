package testing.domain.search;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllSearchTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.testing.domainlayer.search");
		//$JUnit-BEGIN$
		suite.addTestSuite(SearchIndexTest.class);
		suite.addTestSuite(SearchAgentTest.class);
		
		//$JUnit-END$
		return suite;
	}
}
