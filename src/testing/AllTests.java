package testing;

import junit.framework.Test;
import junit.framework.TestSuite;
import testing.acceptancetests.main.AllStoryTests;
import testing.domain.AllDomainTests;
import testing.persistent.AllPersistenceTests;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.server");
		//$JUnit-BEGIN$
		suite.addTest(AllStoryTests.suite()); // runs the acceptance tests
		suite.addTest(AllPersistenceTests.suite());
		suite.addTest(AllDomainTests.suite());
		//$JUnit-END$
		return suite;
	}
}