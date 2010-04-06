package testing;

import junit.framework.*;

import testing.domain.AllDomainTests;
import testing.persistent.AllPersistenceTests;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.testing.domainlayer.message");
		//$JUnit-BEGIN$
		suite.addTest(AllPersistenceTests.suite());
		suite.addTest(AllDomainTests.suite());
		//$JUnit-END$
		return suite;
	}
}