/**
 * 
 */
package testing.domain.message;

import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.message.ForumMessage;

/**
 * @author sepetnit
 *
 */
public class ForumMessageTest extends TestCase {
	private ForumMessage messageUnderTest;
	
	private static final int DEFAULT_MESSAGE_ID = 56;
	private static final int DEFAULT_MESSAGE_AUTHOR = 10;
	private static final String DEFAULT_MESSAGE_TITLE = "test title";
	private static final String DEFAULT_MESSAGE_CONTENT = "test content";

	private static final String MODIFIED_MESSAGE_TITLE = "modified title";
	private static final String MODIFIED_MESSAGE_CONTENT = "modified content";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.messageUnderTest = new ForumMessage(ForumMessageTest.DEFAULT_MESSAGE_ID, ForumMessageTest.DEFAULT_MESSAGE_AUTHOR,
				ForumMessageTest.DEFAULT_MESSAGE_TITLE, ForumMessageTest.DEFAULT_MESSAGE_CONTENT);
	}
	
	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumMessage#ForumMessage(long, long, String, String)}
	 */
	@Test
	public void testMessageConsructor() {
		assertEquals(this.messageUnderTest.getMessageID(), ForumMessageTest.DEFAULT_MESSAGE_ID);
		assertEquals(this.messageUnderTest.getAuthorID(), ForumMessageTest.DEFAULT_MESSAGE_AUTHOR);
		assertEquals(this.messageUnderTest.getTitle(), ForumMessageTest.DEFAULT_MESSAGE_TITLE);
		assertEquals(this.messageUnderTest.getContent(), ForumMessageTest.DEFAULT_MESSAGE_CONTENT);
	}
	
	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumMessage#updateMe(String, String)
	 */
	public void testUpdateMe() {
		this.messageUnderTest.updateMe(ForumMessageTest.MODIFIED_MESSAGE_TITLE, this.messageUnderTest.getContent());
		assertEquals(this.messageUnderTest.getTitle(), ForumMessageTest.MODIFIED_MESSAGE_TITLE);
		assertEquals(this.messageUnderTest.getContent(), ForumMessageTest.DEFAULT_MESSAGE_CONTENT);
		this.messageUnderTest.updateMe(this.messageUnderTest.getTitle(), ForumMessageTest.MODIFIED_MESSAGE_CONTENT);
		assertEquals(this.messageUnderTest.getTitle(), ForumMessageTest.MODIFIED_MESSAGE_TITLE);
		assertEquals(this.messageUnderTest.getContent(), ForumMessageTest.MODIFIED_MESSAGE_CONTENT);
	}
	
	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumMessage#addReply(long)
	 * 
	 * Test method for {@link forum.server.domainlayer.message.ForumMessage#deleteReply(long)
	 * 
	 * Test method for {@link forum.server.domainlayer.message.ForumMessage#getReplies()
	 */
	@Test	
	public void testAddReplyToMe() {
		assertTrue(this.messageUnderTest.getReplies().isEmpty());
		this.messageUnderTest.addReply(15L);
		this.messageUnderTest.addReply(20L);
		Collection<Long> tMessageReplies = this.messageUnderTest.getReplies();
		assertTrue(tMessageReplies.contains(15L));
		assertTrue(tMessageReplies.contains(20L));
		assertEquals(tMessageReplies.size(), 2);
		this.messageUnderTest.deleteReply(15L);
		tMessageReplies = this.messageUnderTest.getReplies();
		assertEquals(tMessageReplies.size(), 1);
		assertTrue(tMessageReplies.contains(20L));
	}	
	
	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumMessage#toString()}
	 */
	@Test
	public void testMsgToString() {
		assertEquals(this.messageUnderTest.toString(), 
				56 + "\t" + ForumMessageTest.DEFAULT_MESSAGE_AUTHOR + "\t" +
				ForumMessageTest.DEFAULT_MESSAGE_TITLE + "\t" +
				ForumMessageTest.DEFAULT_MESSAGE_CONTENT);

	}
}
