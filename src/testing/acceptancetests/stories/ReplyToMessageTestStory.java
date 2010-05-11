/**
 * This class is responsible to perform an acceptance test of reply adding use case
 */
package testing.acceptancetests.stories;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 
 * 		Sepetnitsky Vitali 310106745
 *
 */
public class ReplyToMessageTestStory extends GeneralMethodsTest {

	/**
	 * @see
	 * 		{@link #setUp()}
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testReplyToMessage() {
		/* prepares for the test - adds subject, thread and user. */
		// registers two new users and logins them to the forum 
		assertTrue(super.register("user1", "pass1", "last1", "first1", "user1@gmail.com"));
		String[] tLoginInfo = super.login("user1", "pass1"); 
		assertNotNull(tLoginInfo);
		long tLoggedInID = Long.parseLong(tLoginInfo[4]);
		
		// adds a new subject to the root level
		long tSubject1ID = super.addNewSubject(tLoggedInID, -1, "subject1", "description1");
		// checks whether the subject was added - should always succeed
		assertNotSame(tSubject1ID, -1);
		// adds a new thread

		long tThread1FirstMessageID =
			super.openNewThread(3, tSubject1ID, "topic1", "title1", "content1");
		// checks whether the thread and its first message were added - should always succeed
		assertFalse(tThread1FirstMessageID == -1);	
		System.exit(-1);

		// checks that there are no replies to the new message
		assertEquals(super.getReplies(tThread1FirstMessageID).size(), 0);

		/* end of prepare - here the test starts. */
		// adds a reply by a non-existing author - should fail
		assertFalse(super.addNewReply(tLoggedInID + 10, tThread1FirstMessageID, "title1", "content1"));
		
		// adds a reply by a not permitted user - should fail
		assertFalse(super.addNewReply(tLoggedInID  + 1253, tThread1FirstMessageID, "title1", "content1"));

		assertTrue(super.register("user2", "pass2", "last2", "first2", "user2@gmail.com"));
		tLoginInfo = super.login("user2", "pass2");
		assertNotNull(tLoginInfo);
		tLoggedInID = Long.parseLong(tLoginInfo[4]);
		// adds a reply by an existing and logged-in user
		assertTrue(super.addNewReply(tLoggedInID, tThread1FirstMessageID, "title1", "content1"));
		// adds a reply to non-existing message - should fail
		assertFalse(super.addNewReply(tLoggedInID, 20, "title1", "content1"));
		// get a collection of string representations of thread1 first message replies
		Collection<String> replies = super.getReplies(tThread1FirstMessageID);
		assertNotNull(replies);
		// the collection should contain only one reply - the one that have added successfully by
		// this test method
		assertEquals(replies.size(), 1);
		// checks whether the details of the reply are same - as given
		assertEquals(replies.iterator().next(), "2title1content1");
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
