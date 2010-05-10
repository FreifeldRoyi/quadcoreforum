package testing;

import junit.framework.*;

import testing.acceptancetests.main.AllStoryTests;
import testing.domain.AllDomainTests;
import testing.persistent.AllPersistenceTests;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.testing");
		//$JUnit-BEGIN$
		suite.addTest(AllPersistenceTests.suite());
		suite.addTest(AllDomainTests.suite());
		suite.addTest(AllStoryTests.suite());
		//$JUnit-END$
		return suite;
	}
}