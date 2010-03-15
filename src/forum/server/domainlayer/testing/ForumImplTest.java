/**
 * 
 */
package forum.server.domainlayer.testing;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.impl.ForumSubjectImpl;
import forum.server.domainlayer.interfaces.ForumSubject;

/**
 * @author sepetnit
 *
 */
public class ForumImplTest {
 
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#ForumImpl(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testForumImpl() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#addForumSubject(forum.server.domainlayer.interfaces.ForumSubject)}.
	 */
	@Test
	public void testAddForumSubject() {
		ForumSubject tSubj = new ForumSubjectImpl("subj1", "test1");
		
		
		
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#getConnectedUsers()}.
	 */
	@Test
	public void testGetConnectedUsers() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#getForumSubjects()}.
	 */
	@Test
	public void testGetForumSubjects() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#getNumOfConnectedUsers()}.
	 */
	@Test
	public void testGetNumOfConnectedUsers() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#login(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLogin() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#logout(java.lang.String)}.
	 */
	@Test
	public void testLogout() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#registerUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRegisterUser() {
		fail("Not yet implemented");
	}

}
