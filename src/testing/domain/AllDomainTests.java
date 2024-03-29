package testing.domain;

import junit.framework.*;

import testing.domain.message.AllMessageTests;
import testing.domain.search.AllSearchTests;
import testing.domain.user.AllUserTests;

public class AllDomainTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.server.domainlayer");
		//$JUnit-BEGIN$
		suite.addTest(AllUserTests.suite());
		suite.addTest(AllMessageTests.suite());
		suite.addTest(AllSearchTests.suite());
		//$JUnit-END$
		return suite;
	}
}
