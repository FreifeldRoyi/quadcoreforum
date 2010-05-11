package testing.domain.message;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.message.ForumThread;

public class ForumThreadTest extends TestCase {

	private static final long DEFAULT_THREAD_ID = 10;
	private static final String DEFAULT_THREAD_TOPIC = "test topic";
	private static final long DEFAULT_THREAD_ROOT_MESSAGE = 14;

	private ForumThread threadUnderTest;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.threadUnderTest = new ForumThread(ForumThreadTest.DEFAULT_THREAD_ID, 
				ForumThreadTest.DEFAULT_THREAD_TOPIC, ForumThreadTest.DEFAULT_THREAD_ROOT_MESSAGE, -1);
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumThread#ForumThread(long, String, long)
	 */
	@Test
	public void testThreadConstructor() {
		assertEquals(this.threadUnderTest.getID(), ForumThreadTest.DEFAULT_THREAD_ID);
		assertEquals(this.threadUnderTest.getTopic(), ForumThreadTest.DEFAULT_THREAD_TOPIC);
		assertEquals(this.threadUnderTest.getRootMessageID(), ForumThreadTest.DEFAULT_THREAD_ROOT_MESSAGE);
		assertEquals(this.threadUnderTest.getNumOfResponses(), 0);
		assertEquals(this.threadUnderTest.getNumOfViews(), 0);
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumThread#incNumOfResponses()
	 * 
	 * Test method for {@link forum.server.domainlayer.message.ForumThread#decNumOfResponses()
	 * 
     * Test method for {@link forum.server.domainlayer.message.ForumThread#getNumOfResponses()
	 * 
	 */
	@Test
	public void testNumOfResponsesHandling() {
		long tCurrentNumOfResponses = this.threadUnderTest.getNumOfResponses();
		this.threadUnderTest.incNumOfResponses();
		assertEquals(this.threadUnderTest.getNumOfResponses(), tCurrentNumOfResponses + 1);
		this.threadUnderTest.incNumOfResponses();
		assertEquals(this.threadUnderTest.getNumOfResponses(), tCurrentNumOfResponses + 2);
		this.threadUnderTest.decNumOfResponses();
		assertEquals(this.threadUnderTest.getNumOfResponses(), tCurrentNumOfResponses + 1);
		this.threadUnderTest.decNumOfResponses();
		assertEquals(this.threadUnderTest.getNumOfResponses(), tCurrentNumOfResponses);
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.ForumThread#incNumOfViews()
	 * 
	 * Test method for {@link forum.server.domainlayer.message.ForumThread#getNumOfViews()
	 * 
     * 
	 */
	@Test
	public void numOfViewsHandling() {
		long tCurrentNumOfViews = this.threadUnderTest.getNumOfViews();
		this.threadUnderTest.incNumOfViews();
		this.threadUnderTest.incNumOfViews();		
		assertEquals(this.threadUnderTest.getNumOfViews(), tCurrentNumOfViews + 2);
		this.threadUnderTest.incNumOfViews();
		assertEquals(this.threadUnderTest.getNumOfResponses(), tCurrentNumOfViews + 3);
	}
}
