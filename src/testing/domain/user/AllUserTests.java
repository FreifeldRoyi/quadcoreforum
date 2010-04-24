package testing.domain.user;


import junit.framework.*;

public class AllUserTests extends TestCase {

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
