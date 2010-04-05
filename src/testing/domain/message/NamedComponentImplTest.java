package forum.server.domainlayer.testing;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import forum.server.domainlayer.impl.NamedComponentImpl;

public class NamedComponentImplTest extends TestCase {
	
	NamedComponentImpl nc;

	@Before
	public void setUp() throws Exception {
		String desc = "The Description";
		String name = "User";
		nc = new NamedComponentImpl(desc,name);
	}


	@Test
	public void testGetDescription() {
		if (!nc.getDescription().equals("The Description")){
			fail("Description is not Equal");
		}
	}

	@Test
	public void testGetName() {
		if (!nc.getName().equals("User")){
			fail("Name is not Equal");
		}
	}

	@Test
	public void testGetNumOfMessages() {
		assertEquals(0, nc.getNumOfMessages());
	}

	@Test
	public void testIncMessagesNumber() {
		assertEquals(0, nc.getNumOfMessages());
		nc.incMessagesNumber();
		assertEquals(1, nc.getNumOfMessages());
	}

	@Test
	public void testDecMessagesNumber() {
		nc.incMessagesNumber();
		assertEquals(1, nc.getNumOfMessages());
		nc.decMessagesNumber();
		assertEquals(0, nc.getNumOfMessages());
	}
	@Test
	public void testSetDescription() {
		if (!nc.getDescription().equals("The Description")){
			fail("Description is not Equal");
		}
		nc.setDescription("New Description");
		if (!nc.getDescription().equals("New Description")){
			fail("Description wasnt set");
		}
	}

	@Test
	public void testSetName() {
		if (!nc.getName().equals("User")){
			fail("Name is not Equal");
		}
		nc.setName("New User");
		if (!nc.getName().equals("New User")){
			fail("Name wasnt set");
		}
		
	}

}
