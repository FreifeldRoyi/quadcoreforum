package forum.server.persistentlayer.testing;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllPersistenceTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for forum.server.persistentlayer.testing");
		//$JUnit-BEGIN$
		suite.addTestSuite(JAXBpersistenceDataHandlerTest.class);
		//$JUnit-END$
		return suite;
	}

}
