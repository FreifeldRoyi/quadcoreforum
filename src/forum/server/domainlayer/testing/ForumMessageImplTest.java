/**
 * 
 */
package forum.server.domainlayer.testing;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.impl.ForumMessageImpl;
import forum.server.domainlayer.impl.RegisteredUserImpl;
import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.RegisteredUser;

/**
 * @author sepetnit
 *
 */
public class ForumMessageImplTest {
	private RegisteredUser tUser; 
	private ForumMessage tMessage1; 
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		 tUser = new RegisteredUserImpl("user1", "pass1", "a", "b", "mail@mymail.com");
		
		 tMessage1 = new ForumMessageImpl(tUser, "title1", "content1");

	}

	
	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumMessageImpl#ForumMessageImpl(forum.server.domainlayer.interfaces.RegisteredUser, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testForumMessageImpl() {
		fail("Not yet implemented");
	}

	
	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumMessageImpl#replyToMessage(forum.server.domainlayer.interfaces.ForumMessage)}.
	 */
	@Test
	public void testAddMessage() {
		
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumMessageImpl#msgToString()}.
	 */
	@Test
	public void testMsgToString() {
		
		String msg1ToString = "user1" + "\n" + tMessage1.getDate() + "\n" + tMessage1.getTime() + "\n" +
		"title1" + "\n" + "content1" + "\nreplys\n";

		assertTrue(msg1ToString.equals(tMessage1.msgToString()));
		
		ForumMessage tMessage2 = new ForumMessageImpl(tUser, "title2", "content2");

		tMessage1.replyToMessage(tMessage2);

		String msg2ToString = "user1" + "\n" + tMessage1.getDate() + "\n" + tMessage1.getTime() + "\n" +
		"title1" + "\n" + "content1" + "\nreplys\n" +
		
		"user1" + "\n" + tMessage2.getDate() + "\n" + tMessage2.getTime() + "\n" +
		"title2" + "\n" + "content2" + "\nreplys\n";
		
		assertTrue(msg2ToString.equals(tMessage1.msgToString()));

		
	}
}
