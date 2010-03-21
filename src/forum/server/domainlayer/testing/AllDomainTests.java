package forum.server.domainlayer.testing;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllDomainTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.server.domainlayer.testing");
		//$JUnit-BEGIN$
		suite.addTestSuite(NamedComponentImplTest.class);
		suite.addTestSuite(ForumSubjectImplTest.class);
		suite.addTestSuite(ForumMessageImplTest.class);
		suite.addTestSuite(ForumThreadImplTest.class);
		suite.addTestSuite(RegisteredUserImplTest.class);
		suite.addTestSuite(ForumImplTest.class);
		//$JUnit-END$
		return suite;
	}

}
