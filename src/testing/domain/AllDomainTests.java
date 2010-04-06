package testing.domain;

import junit.framework.*;

import testing.domain.message.AllMessageTests;
import testing.domain.user.AllUserTests;

public class AllDomainTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.testing.domainlayer.message");
		//$JUnit-BEGIN$
		suite.addTest(AllUserTests.suite());
		suite.addTest(AllMessageTests.suite());
		//$JUnit-END$
		return suite;
	}
}
