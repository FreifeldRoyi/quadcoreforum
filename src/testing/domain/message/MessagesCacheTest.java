/**
 * 
 */
package testing.domain.message;

import java.util.*;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import forum.server.Settings;
import forum.server.domainlayer.message.*;
import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.*;

/**
 * @author sepetnit
 *
 */
public class MessagesCacheTest extends TestCase {

	private MessagesCache messagesCacheUnderTest;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Settings.switchToTestMode();
		this.messagesCacheUnderTest = new MessagesCache(true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	}


	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#getTopLevelSubjects()}.
	 */
	@Test
	public void testGetTopLevelSubjects() {
		final int tNumberOfCreatedTopLevel = 10;
		final Collection<ForumSubject> tCreatedTopLevel = new Vector<ForumSubject>();

		for (int i = 0; i < tNumberOfCreatedTopLevel; i++) {
			try {
				// adds top level subject
				tCreatedTopLevel.add(this.messagesCacheUnderTest.createNewSubject("name" + i,
						"description" + i, -1));
				// adds not top level subject (subjects of the first to level subject)
				this.messagesCacheUnderTest.createNewSubject("name" + (i + tNumberOfCreatedTopLevel),
						"description" + (i + tNumberOfCreatedTopLevel), 0);
			} 
			catch (DatabaseUpdateException e) {
				fail(e.getMessage());
			}
		}
		try {
			Collection<ForumSubject> tObtainedTopLevel = this.messagesCacheUnderTest.getTopLevelSubjects();
			assertEquals(tCreatedTopLevel, tObtainedTopLevel);
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#getSubjectByID(long)}.
	 */
	@Test
	public void testGetSubjectByID() {
		try {
			final ForumSubject tNewSubject = this.messagesCacheUnderTest.createNewSubject("name1", "description1", -1);
			final ForumSubject tObtainedSubject = this.messagesCacheUnderTest.getSubjectByID(tNewSubject.getID());
			assertEquals(tNewSubject, tObtainedSubject);
		}
		catch (SubjectNotFoundException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}	
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#createNewSubject(java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testCreateNewSubject() {
		try {
			final ForumSubject tNewSubject = this.messagesCacheUnderTest.createNewSubject("sub1", "desc1", -1);
			assertEquals(tNewSubject.getName(), "sub1");
			assertEquals(tNewSubject.getDescription(), "desc1");
			assertEquals(tNewSubject, this.messagesCacheUnderTest.getSubjectByID(tNewSubject.getID()));
		}
		catch (SubjectNotFoundException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#updateInDatabase(forum.server.domainlayer.message.ForumSubject)}.
	 */
/*	@Test
	public void testUpdateInDatabaseForumSubject() {
		fail("Not yet implemented");
	}
*/
	
	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#getThreadByID(long)}.
	 */
	@Test
	public void testGetThreadByID() {
		try {
			long tSubjectID = this.messagesCacheUnderTest.createNewSubject("a", "b", -1).getID();
			long tMessageID = this.messagesCacheUnderTest.createNewMessage(-1, "a", "b", -1).getMessageID();
			final ForumThread tNewThread = this.messagesCacheUnderTest.openNewThread("topic1", tMessageID, tSubjectID);
			final ForumThread tObtainedThread = this.messagesCacheUnderTest.getThreadByID(tNewThread.getID());
			assertEquals(tNewThread, tObtainedThread);
		}
		catch (ThreadNotFoundException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			System.exit(-1);
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}		
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#openNewThread(java.lang.String, long)}.
	 */
	@Test
	public void testOpenNewThread() {
		try {
			long tSubjectID = this.messagesCacheUnderTest.createNewSubject("a", "b", -1).getID();
			long tMessageID = this.messagesCacheUnderTest.createNewMessage(-1, "a", "b", -1).getMessageID();
			ForumThread tNewThread = this.messagesCacheUnderTest.openNewThread("topic1", tMessageID, tSubjectID);
			assertEquals(tNewThread.getTopic(), "topic1");
			assertEquals(tNewThread.getRootMessageID(), tMessageID);
			ForumThread tThread = this.messagesCacheUnderTest.getThreadByID(tNewThread.getID());
			assertEquals(tNewThread.getID(), tThread.getID());
			assertEquals(tNewThread.getTopic(), tThread.getTopic());
			assertEquals(tNewThread.getRootMessageID(), tThread.getRootMessageID());
		}
		catch (ThreadNotFoundException e) {
			fail("the thread hasn't been added successfuly");
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#deleteATread(long)}.
	 */
	@Test
	public void testDeleteATread() {
		try {
			// adds a new thread
			ForumMessage tRootMessage = this.messagesCacheUnderTest.createNewMessage(-1, "title", "content", -1);

			ForumThread tNewThread = this.messagesCacheUnderTest.openNewThread("topic1", tRootMessage.getMessageID(), -1);

			final int tNumOfMessages = 5; 
			final Collection<Long> tCreatedMessagesIDs = new Vector<Long>();			

			ForumMessage tNewMessage = this.messagesCacheUnderTest.createNewMessage(-1, "title0", "content0", -1);
			tRootMessage.addReply(tNewMessage.getMessageID());
			this.messagesCacheUnderTest.updateInDatabase(tRootMessage);

			tCreatedMessagesIDs.add(tRootMessage.getMessageID());

			for (int i = 1; i < tNumOfMessages; i++) {
				ForumMessage tNextMessage = this.messagesCacheUnderTest.createNewMessage(-1, "title" + i, "content" + i, -1);
				tNextMessage.addReply(tNewMessage.getMessageID());
				tCreatedMessagesIDs.add(tNewMessage.getMessageID());
				this.messagesCacheUnderTest.updateInDatabase(tNextMessage);
				tNewMessage = tNextMessage;
			}
			try {
				this.messagesCacheUnderTest.deleteATread(tNewThread.getID());

				try {
					this.messagesCacheUnderTest.getThreadByID(tNewThread.getID());
					fail("the thread wasn't deleted");
				}
				catch (ThreadNotFoundException e) {
					// do nothing - the exception should be thrown
				}
				try {
					this.messagesCacheUnderTest.getMessageByID(tRootMessage.getMessageID());
					fail("the thread root message wasn't deleted");
				}
				catch (MessageNotFoundException e1) {
					// do nothing - the exception should be thrown
				}
				for (long tMessageID : tCreatedMessagesIDs) {
					try {
						this.messagesCacheUnderTest.getMessageByID(tMessageID);
					} 
					catch (MessageNotFoundException e) {
						continue; // the exception should be thrown
					} 
				}
			} 
			catch (ThreadNotFoundException e) {
				fail("the open new thread method doesn't work");
			}
		}
		catch (MessageNotFoundException e) {
			fail("the update in database method doesn't work");
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#getMessageByID(long)}.
	 */
	@Test
	public void testGetMessageByID() {
		try {
			ForumMessage tNewMessage = this.messagesCacheUnderTest.createNewMessage(-1, "title1", "content1", -1);
			ForumMessage tObtainedMessage = this.messagesCacheUnderTest.getMessageByID(tNewMessage.getMessageID());
			assertEquals(tNewMessage, tObtainedMessage);
		}
		catch (MessageNotFoundException e) {
			fail("the thread hasn't been added successfuly");
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#createNewMessage(long, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateNewMessage() {
		try {
			final ForumMessage tNewMessage = this.messagesCacheUnderTest.createNewMessage(-1, "title1", "content1", -1);
			assertEquals(tNewMessage.getAuthorID(), -1);
			assertEquals(tNewMessage.getTitle(), "title1");
			assertEquals(tNewMessage.getContent(), "content1");
			assertEquals(tNewMessage, this.messagesCacheUnderTest.getMessageByID(tNewMessage.getMessageID()));
		} 
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (MessageNotFoundException e) {
			fail("the message hasn't been added successfuly");
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#deleteAMessage(long)}.
	 */
	@Test
	public void testDeleteAMessage() {
		// TODO: test recursive deletion
		try {
			final ForumMessage tNewMessage = this.messagesCacheUnderTest.createNewMessage(-1, "title1", "content1", -1);
			final long tMessageID = tNewMessage.getMessageID();
			try {
				this.messagesCacheUnderTest.getMessageByID(tNewMessage.getMessageID());
				this.messagesCacheUnderTest.deleteAMessage(tMessageID);
			}
			catch (MessageNotFoundException e) {
				fail("the message hasn't been added successfuly");
			}
			try {
				this.messagesCacheUnderTest.getMessageByID(tNewMessage.getMessageID());
				fail("the message hasn't been deleted successfuly");
			}
			catch (MessageNotFoundException e) {
				// do nothing - the exception indicated that the message doesn't exist
			}
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesCache#updateInDatabase(forum.server.domainlayer.message.ForumMessage)}.
	 */
/*	@Test
	public void testUpdateInDatabaseForumMessage() {
		fail("Not yet implemented");
	}
*/
}
