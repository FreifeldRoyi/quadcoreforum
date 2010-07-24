package testing.persistent;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllPersistenceTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.server.persistentlayer");
		//$JUnit-BEGIN$
		suite.addTestSuite(PersistentToDomainConverterTest.class);
		suite.addTestSuite(UsersPersistentHandlerTest.class);
		suite.addTestSuite(MessagesPersistentHandlerTest.class);
		//$JUnit-END$
		return suite;
	}

}
