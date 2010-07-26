/**
 * 
 */
package testing.persistent;

import java.util.Collection;
import java.util.Vector;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.Settings;
import forum.server.domainlayer.message.ForumMessage;
import forum.server.domainlayer.message.ForumSubject;
import forum.server.domainlayer.message.ForumThread;
import forum.server.updatedpersistentlayer.SessionFactoryUtil;
import forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.ThreadNotFoundException;

/**
 * @author sepetnit
 *
 */
public class MessagesPersistentHandlerTest extends TestCase {

	private SessionFactory factory; // connects to the database directly via a JDBC driver
	private MessagesPersistenceHandler messagesPersistentHandlerUnderTest;

	public MessagesPersistentHandlerTest() {
		messagesPersistentHandlerUnderTest = new MessagesPersistenceHandler();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Settings.switchToTestMode();
		factory = SessionFactoryUtil.getInstance();	
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	}

	// subjects tests

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#getFirstFreeSubjectID(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetFirstFreeSubjectID() {
		try {
			// tests that the next Ids are generated from 0
			assertTrue(messagesPersistentHandlerUnderTest.getFirstFreeSubjectID(factory) == 0);
			assertTrue(messagesPersistentHandlerUnderTest.getFirstFreeSubjectID(factory) == 0);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#getTopLevelSubjects(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetTopLevelSubjects() {
		try {
			// at first there is no top level subjects
			assertTrue(messagesPersistentHandlerUnderTest.getTopLevelSubjects(factory).isEmpty());
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			// after the addition of two levels of top level subjects, only 
			// the single top level subject is returned
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 1, "c", 
					"d", 0);
			Collection<ForumSubject> tTopLevelSubjects = 
				messagesPersistentHandlerUnderTest.getTopLevelSubjects(factory);
			assertEquals(tTopLevelSubjects.size(), 1);
			assertEquals(tTopLevelSubjects.iterator().next().getID(), 0);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#getSubjectByID(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testGetSubjectByID() {
		try {
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 1, "c", 
					"d", 0);
			ForumSubject tSecondSubject = messagesPersistentHandlerUnderTest.getSubjectByID(factory, 1);
			ForumSubject tFirstSubject = messagesPersistentHandlerUnderTest.getSubjectByID(factory, 0);
			assertEquals(tFirstSubject.getID(), 0);
			assertEquals(tSecondSubject.getID(), 1);
			try {
				messagesPersistentHandlerUnderTest.getSubjectByID(factory, 34);
				fail("The subject with id 34 doesn't exist in the database but was found");
			}
			catch (SubjectNotFoundException e) {
				// its ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#addNewSubject(org.hibernate.SessionFactory, long, java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testAddNewSubject() {
		try {
			// a simple addition of a subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			ForumSubject tFirstSubject = messagesPersistentHandlerUnderTest.getSubjectByID(factory, 0);
			assertEquals(tFirstSubject.getID(), 0);
			assertEquals(tFirstSubject.getName(), "a");
			assertEquals(tFirstSubject.getDescription(), "b");
			assertEquals(tFirstSubject.getFatherID(), -1);
			assertEquals(tFirstSubject.getSubSubjects().size(), 0);
			assertEquals(tFirstSubject.getThreads().size(), 0);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#updateSubject(org.hibernate.SessionFactory, long, java.lang.String, java.lang.String, java.util.Collection, java.util.Collection, long, long)}.
	 */
	@Test
	public void testUpdateSubject() {
		try {
			// a simple addition of a subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 1, "d", 
					"c", 0);
			ForumSubject tFirstSubject = messagesPersistentHandlerUnderTest.getSubjectByID(factory, 0);
			assertEquals(tFirstSubject.getID(), 0);
			assertEquals(tFirstSubject.getName(), "a");
			assertEquals(tFirstSubject.getDescription(), "b");
			assertEquals(tFirstSubject.getFatherID(), -1);
			assertEquals(tFirstSubject.getSubSubjects().size(), 0);
			assertEquals(tFirstSubject.getThreads().size(), 0);
			// modification of this subject

			Collection<Long> tSubSubjects = new Vector<Long>();
			tSubSubjects.add(1L);

			messagesPersistentHandlerUnderTest.updateSubject(factory, 0, 
					"c", "d", tSubSubjects, new Vector<Long>(), 
					1, 3);
			tFirstSubject = messagesPersistentHandlerUnderTest.getSubjectByID(factory, 0);

			assertEquals(tFirstSubject.getID(), 0);
			assertEquals(tFirstSubject.getName(), "c");
			assertEquals(tFirstSubject.getDescription(), "d");
			assertEquals(tFirstSubject.getFatherID(), -1);
			assertEquals(tFirstSubject.getSubSubjects().size(), 1);
			assertTrue(tFirstSubject.getSubSubjects().iterator().next() == 1);
			assertEquals(tFirstSubject.getThreads().size(), 0);
			assertEquals(tFirstSubject.getDeepNumOfSubSubjects(), 1);
			assertEquals(tFirstSubject.getDeepNumOfMessages(), 3);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#deleteASubject(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testDeleteASubject() {
		try {
			// adds two new subjects
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 1, "d", 
					"c", 0);
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 2, "d", 
					"c", -1);

			// checks that the subjects exist in the database
			messagesPersistentHandlerUnderTest.getSubjectByID(factory, 0);
			messagesPersistentHandlerUnderTest.getSubjectByID(factory, 1);
			// deletes the first subject
			messagesPersistentHandlerUnderTest.deleteASubject(factory, 0);
			try {
				messagesPersistentHandlerUnderTest.getSubjectByID(factory, 0);
				fail("subject 0 deleted but found");
			}
			catch (SubjectNotFoundException e) {
				// its ok
			}
			// the sub-subject of the deleted subject should be deleted too
			try {
				messagesPersistentHandlerUnderTest.getSubjectByID(factory, 1);
				fail("subject 0 deleted but its sub-subject 1 was found");
			}
			catch (SubjectNotFoundException e) {
				// its ok
			}
			// the second subject still exists in the database
			messagesPersistentHandlerUnderTest.getSubjectByID(factory, 2);
			// deletes the second subject
			messagesPersistentHandlerUnderTest.deleteASubject(factory, 2);
			try {
				messagesPersistentHandlerUnderTest.getSubjectByID(factory, 2);
				fail("subject 2 deleted but found");
			}
			catch (SubjectNotFoundException e) {
				// its ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	// threads tests

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#getFirstFreeThreadID(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetFirstFreeThreadID() {
		try {
			// tests that the next Ids are generated from 0
			assertTrue(messagesPersistentHandlerUnderTest.getFirstFreeThreadID(factory) == 0);
			assertTrue(messagesPersistentHandlerUnderTest.getFirstFreeThreadID(factory) == 0);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#getThreadByID(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testGetThreadByID() {
		try {
			// adds a new subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			// adds two new messages
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 1, -1, "x", 
					"f", -1);
			// adds two new threads
			messagesPersistentHandlerUnderTest.openNewThread(factory, 0, "a", 0, 0);
			messagesPersistentHandlerUnderTest.openNewThread(factory, 1, "b", 1, 0);
			// tests the method - the two threads should exist in the database and be retreived
			// successfully
			ForumThread tSecondThread = messagesPersistentHandlerUnderTest.getThreadByID(factory, 1);
			ForumThread tFirstThread = messagesPersistentHandlerUnderTest.getThreadByID(factory, 0);
			assertEquals(tFirstThread.getID(), 0);
			assertEquals(tSecondThread.getID(), 1);
			try {
				messagesPersistentHandlerUnderTest.getThreadByID(factory, 334);
				fail("The thread with id 334 doesn't exist in the database but was found");
			}
			catch (ThreadNotFoundException e) {
				// its ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#openNewThread(org.hibernate.SessionFactory, long, java.lang.String, long, long)}.
	 */
	@Test
	public void testOpenNewThread() {
		try {
			// a simple addition of a subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			// a simple addition of a message
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			// a simple addition of a thread
			messagesPersistentHandlerUnderTest.openNewThread(factory, 0, "a", 0, 0);

			ForumThread tFirstThread = messagesPersistentHandlerUnderTest.getThreadByID(factory, 0);
			assertEquals(tFirstThread.getID(), 0);
			assertEquals(tFirstThread.getTopic(), "a");
			assertEquals(tFirstThread.getRootMessageID(), 0);
			assertEquals(tFirstThread.getFatherID(), 0);
			assertEquals(tFirstThread.getNumOfResponses(), 0);
			assertEquals(tFirstThread.getNumOfViews(), 0);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#deleteAThread(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testDeleteAThread() {
		try {
			// adds two new subjects
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 1, "d", 
					"c", 0);
			// adds two new messages
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 1, -1, "x", 
					"f", -1);
			// adds two new threads
			messagesPersistentHandlerUnderTest.openNewThread(factory, 0, "a", 0, 0);
			messagesPersistentHandlerUnderTest.openNewThread(factory, 1, "b", 1, 0);

			// checks that the threads exist in the database
			messagesPersistentHandlerUnderTest.getThreadByID(factory, 0);
			messagesPersistentHandlerUnderTest.getThreadByID(factory, 1);
			// deletes the first thread
			messagesPersistentHandlerUnderTest.deleteAThread(factory, 0);
			try {
				messagesPersistentHandlerUnderTest.getThreadByID(factory, 0);
				fail("thread 0 deleted but found");
			}
			catch (ThreadNotFoundException e) {
				// its ok
			}
			try {
				// the first thread's message should be deleted to according to the cascade rules
				messagesPersistentHandlerUnderTest.getMessageByID(factory, 0);
				fail("thread 0 deleted but its message was found");
			}
			catch (MessageNotFoundException e) {
				// its ok
			}
			// the second thread still exists in the database
			messagesPersistentHandlerUnderTest.getThreadByID(factory, 1);
			// deletes the second thread
			messagesPersistentHandlerUnderTest.deleteAThread(factory, 1);
			try {
				messagesPersistentHandlerUnderTest.getThreadByID(factory, 1);
				fail("thread 1 deleted but found");
			}
			catch (ThreadNotFoundException e) {
				// its ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#updateThread(org.hibernate.SessionFactory, long, java.lang.String, long, long)}.
	 */
	@Test
	public void testUpdateThread() {
		try {
			// a simple addition of a subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			// a simple addition of a message
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			// a simple addition of a thread
			messagesPersistentHandlerUnderTest.openNewThread(factory, 0, "a", 0, 0);

			ForumThread tFirstThread = messagesPersistentHandlerUnderTest.getThreadByID(factory, 0);
			assertEquals(tFirstThread.getID(), 0);
			assertEquals(tFirstThread.getTopic(), "a");
			assertEquals(tFirstThread.getRootMessageID(), 0);
			assertEquals(tFirstThread.getFatherID(), 0);
			assertEquals(tFirstThread.getNumOfResponses(), 0);
			assertEquals(tFirstThread.getNumOfViews(), 0);

			// modification of this thread

			messagesPersistentHandlerUnderTest.updateThread(factory, 0, 
					"a", 22, 222);
			tFirstThread = messagesPersistentHandlerUnderTest.getThreadByID(factory, 0);

			assertEquals(tFirstThread.getID(), 0);
			assertEquals(tFirstThread.getTopic(), "a");
			assertEquals(tFirstThread.getRootMessageID(), 0);
			assertEquals(tFirstThread.getFatherID(), 0);
			assertEquals(tFirstThread.getNumOfResponses(), 22);
			assertEquals(tFirstThread.getNumOfViews(), 222);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	// messages tests

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#getFirstFreeMessageID(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetFirstFreeMessageID() {
		try {
			// tests that the next Ids are generated from 0
			assertTrue(messagesPersistentHandlerUnderTest.getFirstFreeMessageID(factory) == 0);
			assertTrue(messagesPersistentHandlerUnderTest.getFirstFreeMessageID(factory) == 0);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#getAllMessages(org.hibernate.SessionFactory)}.
	 */
	@Test
	public void testGetAllMessages() {
		try {
			// adds a new subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			// adds two new messages
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 1, -1, "x", 
					"f", -1);			
			// retrieves the two messages
			ForumMessage tSecondMessage = messagesPersistentHandlerUnderTest.getMessageByID(factory, 1);
			ForumMessage tFirstMessage = messagesPersistentHandlerUnderTest.getMessageByID(factory, 0);
			// tests the method - the two messages should exist in the database and no other
			// messages should be there
			Collection<ForumMessage> tAllMessages = 
				messagesPersistentHandlerUnderTest.getAllMessages(factory);
			assertTrue(tAllMessages.size() == 2);
			assertTrue(tAllMessages.contains(tSecondMessage));
			assertTrue(tAllMessages.contains(tFirstMessage));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#getMessageByID(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testGetMessageByID() {
		try {
			// adds a new subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			// adds two new messages
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 1, -1, "x", 
					"f", -1);

			// tests the method - the two messages should exist in the database and be retrieved
			// successfully
			ForumMessage tSecondMessage = messagesPersistentHandlerUnderTest.getMessageByID(factory, 1);
			ForumMessage tFirstMessage = messagesPersistentHandlerUnderTest.getMessageByID(factory, 0);
			assertEquals(tFirstMessage.getMessageID(), 0);
			assertEquals(tSecondMessage.getMessageID(), 1);
			try {
				messagesPersistentHandlerUnderTest.getMessageByID(factory, 334);
				fail("The message with id 334 doesn't exist in the database but was found");
			}
			catch (MessageNotFoundException e) {
				// its ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#addNewMessage(org.hibernate.SessionFactory, long, long, java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testAddNewMessage() {
		try {
			// a simple addition of a subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			// a simple addition of a message
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			ForumMessage tFirstMessage = messagesPersistentHandlerUnderTest.getMessageByID(factory, 0);
			assertEquals(tFirstMessage.getMessageID(), 0);
			assertEquals(tFirstMessage.getTitle(), "c");
			assertEquals(tFirstMessage.getContent(), "d");
			assertEquals(tFirstMessage.getAuthorID(), -1);
			assertEquals(tFirstMessage.getFatherID(), -1);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#updateMessage(org.hibernate.SessionFactory, long, java.lang.String, java.lang.String, java.util.Collection, long)}.
	 */
	@Test
	public void testUpdateMessage() {
		try {
			// a simple addition of a subject
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			// a simple addition of two messages
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 1, -1, "f", 
					"g", 0);
			ForumMessage tFirstMessage = messagesPersistentHandlerUnderTest.getMessageByID(factory, 0);
			assertEquals(tFirstMessage.getMessageID(), 0);
			assertEquals(tFirstMessage.getTitle(), "c");
			assertEquals(tFirstMessage.getContent(), "d");
			assertEquals(tFirstMessage.getAuthorID(), -1);
			assertEquals(tFirstMessage.getFatherID(), -1);
			// modification of this message
			Collection<Long> tReplies = new Vector<Long>();
			tReplies.add(1L);
			messagesPersistentHandlerUnderTest.updateMessage(
					factory, 0, "s", "p", tReplies, -1);
			tFirstMessage = messagesPersistentHandlerUnderTest.getMessageByID(factory, 0);
			assertEquals(tFirstMessage.getMessageID(), 0);
			assertEquals(tFirstMessage.getTitle(), "s");
			assertEquals(tFirstMessage.getContent(), "p");
			assertEquals(tFirstMessage.getAuthorID(), -1);
			assertEquals(tFirstMessage.getFatherID(), -1);
			assertEquals(tFirstMessage.getReplies().size(), 1);
			assertTrue(tFirstMessage.getReplies().iterator().next() == 1);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.updatedpersistentlayer.pipe.message.MessagesPersistenceHandler#deleteAMessage(org.hibernate.SessionFactory, long)}.
	 */
	@Test
	public void testDeleteAMessage() {
		try {
			// adds two new subjects
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 0, "a", 
					"b", -1);
			messagesPersistentHandlerUnderTest.addNewSubject(factory, 1, "d", 
					"c", 0);
			// adds four new messages in the following hierarchy:
			// 		0, 1 -> 2 -> 3
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 0, -1, "c", 
					"d", -1);
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 1, -1, "x", 
					"f", -1);
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 2, -1, "o", 
					"x", 1);
			messagesPersistentHandlerUnderTest.addNewMessage(factory, 3, -1, "p", 
					"s", 2);
			// adds two new threads
			messagesPersistentHandlerUnderTest.openNewThread(factory, 0, "a", 0, 0);
			messagesPersistentHandlerUnderTest.openNewThread(factory, 1, "b", 1, 0);
			// checks that the messages exist in the database
			for (int i = 0; i < 4; i++)
				messagesPersistentHandlerUnderTest.getMessageByID(factory, i);
			// deletes the message with id 1
			messagesPersistentHandlerUnderTest.deleteAMessage(factory, 1);
			// checks the the whole hierarchy (1 -> 2 -> 3 was deleted)
			for (int i = 1; i < 4; i++) {
				try {
					messagesPersistentHandlerUnderTest.getMessageByID(factory, 1);
					fail("message " + i + " deleted but found");
				}
				catch (MessageNotFoundException e) {
					// its ok
				}
			}
			try {
				// checks that the second thread was deleted after the deleting its 
				// first message
				messagesPersistentHandlerUnderTest.getThreadByID(factory, 1);
				fail("the root message of thread 1 was deleted but the thread was found");
			}
			catch (ThreadNotFoundException e) {
				// its ok
			}
			// checks that the first message still exists in the database
			messagesPersistentHandlerUnderTest.getMessageByID(factory, 0);
			// deletes the first message
			messagesPersistentHandlerUnderTest.deleteAMessage(factory, 0);
			try {
				messagesPersistentHandlerUnderTest.getMessageByID(factory, 0);
				fail("message 0 deleted but found");
			}
			catch (MessageNotFoundException e) {
				// its ok
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
