/**
 * This class is responsible of performing an acceptance test of search use case
 */
package testing.acceptancetests.stories;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author sepetnit
 *
 */
public class DeleteMessageTestStory extends GeneralMethodsTest {

	/**
	 * @see
	 * 		{@link #setUp()}
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	/**
	 * An acceptance test for message deleting use-cases.
	 * 
	 * The test performs messages deleting and replying under different conditions and assures that the 
	 * application responds as it is expected by the client.
	 */
	public void testDeleteMessage() {
		// registers two new users to the forum - the method should succeed because we start from an empty
		// database and the two users details are different
		long tFirstUserID = super.register("user10", "pass10", "last10", "first10", "user1@gmail.com");
		assertTrue(tFirstUserID != -1);
		// adds a new subject to the root level
		long tSubject1ID = super.addNewSubject(tFirstUserID, -1, "subject1", "description1");
		// checks whether the subject was added - should always succeed
		assertNotSame(tSubject1ID, -1);
		// adds a new thread
		long tThread1FirstMessageID =
			super.openNewThread(tFirstUserID, tSubject1ID, "topic1", "title11", "content11");
		// checks whether the thread and its first message were added - should always succeed
		assertFalse(tThread1FirstMessageID == -1);	// if it is - an exception was thrown
		// adds a new reply to the message
		long tSecondMessageID = super.addNewReply(tFirstUserID, tThread1FirstMessageID, "title22", "content22");
		assertFalse(tSecondMessageID == -1);
		long tThirdMessageID = super.addNewReply(tFirstUserID, tSecondMessageID, "title33", "content33");
		assertFalse(tThirdMessageID == -1);
		long tFourthMessageID = super.addNewReply(tFirstUserID, tThirdMessageID, "title44", "content44");
		assertFalse(tFourthMessageID == -1);
		// here the test begins:
		// deletion of the fourth message from the third message
		assertTrue(super.deleteMessage(tFirstUserID, tFourthMessageID, tThirdMessageID));
		Collection<String> tReplies = super.getReplies(tThirdMessageID);
		// the third message should contain no replies
		assertTrue(tReplies.isEmpty());
		// deletion of the second message from the first message (the root)
		assertTrue(super.deleteMessage(tFirstUserID, tSecondMessageID, tThread1FirstMessageID));
		// the third message shouldn't be found - it was deleted with the second message
		assertNull(super.getReplies(tSecondMessageID));
		// the first message should contain no replies
		tReplies = super.getReplies(tThread1FirstMessageID);
		assertTrue(tReplies.isEmpty());
		// deletion of the first message (the thread will be deleted too)
		assertTrue(super.deleteMessage(tFirstUserID, tThread1FirstMessageID, -1));
		// the message was deleted - it can't be deleted again
		assertFalse(super.deleteMessage(tFirstUserID, tThread1FirstMessageID, -1));
	}

	/**
	 * @see
	 * 		{@link #tearDown()}
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
