package testing.domain;

import testing.domain.user.ForumMemberTest;
import testing.domain.user.ForumUserTest;
import testing.domain.user.UsersCacheTest;
import testing.domain.user.UsersControllerTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUserTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.testing.domainlayer.user");
		//$JUnit-BEGIN$
		suite.addTestSuite(ForumUserTest.class);
		suite.addTestSuite(ForumMemberTest.class);
		suite.addTestSuite(UsersControllerTest.class);
		suite.addTestSuite(UsersCacheTest.class);
		
		//$JUnit-END$
		return suite;
	}

}
