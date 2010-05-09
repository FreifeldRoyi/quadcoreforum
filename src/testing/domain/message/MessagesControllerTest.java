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
import forum.server.domainlayer.ForumDataHandler;
import forum.server.domainlayer.user.*;

import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.interfaces.UISubject;
import forum.server.domainlayer.message.*;
import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.*;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author sepetnit
 *
 */
public class MessagesControllerTest extends TestCase {

	private ForumDataHandler dataHandler;

	private MessagesController messagesControllerUnderTest;


	private Map<String, Long> subjectsNamesToIDsMapping;

	private ForumUser[] createdGuests;
	private ForumMember[] createdMembers;


	/**
	 * @return
	 * 		A collection of permission which should be assigned to a new member of the forum
	 */
	private Collection<Permission> getPermissionsForNewMember() {
		Collection<Permission> toReturn = new HashSet<Permission>();
		toReturn.add(Permission.VIEW_ALL);
		toReturn.add(Permission.OPEN_THREAD);
		toReturn.add(Permission.REPLY_TO_MESSAGE);
		toReturn.add(Permission.EDIT_MESSAGE);
		return toReturn;
	}


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Settings.switchToTestMode();

		this.dataHandler = new ForumDataHandler();

		this.messagesControllerUnderTest = new MessagesController(dataHandler);

		// constructs forum guests

		this.createdGuests = new ForumUser[2];

		final Collection<Permission> tGuestsPermissions = new HashSet<Permission>();
		this.createdGuests[0] = new ForumUser(-1, tGuestsPermissions);
		this.createdGuests[1] = new ForumUser(-2, tGuestsPermissions);

		// constructs forum members and save them in the database

		this.createdMembers = new ForumMember[4];

		for (int i = 0; i < this.createdMembers.length; i++)
			this.createdMembers[i] = this.dataHandler.getUsersCache().createNewMember(i + "",
					i + "", i + "", i + "", i + "@" + i, this.getPermissionsForNewMember());

		// constructs a hierarchy of forum subjects and save them to the database

		this.subjectsNamesToIDsMapping = new HashMap<String, Long>();

		ForumSubject tMainSubject = this.dataHandler.getMessagesCache().createNewSubject("main1", "main1", true);
		ForumSubject tSubSubject1 = this.dataHandler.getMessagesCache().createNewSubject("sub1", "sub1", false);
		ForumSubject tSubSubject11 = this.dataHandler.getMessagesCache().createNewSubject("sub11", "sub11", false);
		tMainSubject.addSubSubject(tSubSubject1.getID());
		tSubSubject1.addSubSubject(tSubSubject11.getID());
		this.dataHandler.getMessagesCache().updateInDatabase(tMainSubject);
		this.dataHandler.getMessagesCache().updateInDatabase(tSubSubject1);
		this.subjectsNamesToIDsMapping.put(tMainSubject.getName(), tMainSubject.getID());
		this.subjectsNamesToIDsMapping.put(tSubSubject1.getName(), tSubSubject1.getID());
		this.subjectsNamesToIDsMapping.put(tSubSubject11.getName(), tSubSubject11.getID());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#getSubjects(long)}.
	 */
	@Test
	public void testGetSubjects() {
		try {
			final Collection<UISubject> tTopLevelSubjects = this.messagesControllerUnderTest.getSubjects(-1);
			assertTrue(tTopLevelSubjects.size() == 1);
			final UISubject tRootSubject = tTopLevelSubjects.iterator().next();
			assertTrue(tRootSubject.getID() == (Long)this.subjectsNamesToIDsMapping.get(tRootSubject.getName()));
			final Collection<UISubject> tLevel1Subjects = this.messagesControllerUnderTest.getSubjects(tRootSubject.getID());
			assertTrue(tLevel1Subjects.size() == 1);
			final UISubject tSubSubject1 = tLevel1Subjects.iterator().next();
			assertTrue(tSubSubject1.getID() == (Long)this.subjectsNamesToIDsMapping.get(tSubSubject1.getName()));
			final Collection<UISubject> tLevel2Subjects = this.messagesControllerUnderTest.getSubjects(tSubSubject1.getID());
			assertTrue(tLevel2Subjects.size() == 1);
			final UISubject tSubSubject11 = tLevel2Subjects.iterator().next();
			assertTrue(tSubSubject11.getID() == (Long)this.subjectsNamesToIDsMapping.get(tSubSubject11.getName()));
		}
		catch (SubjectNotFoundException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#addNewSubject(long, long, java.lang.String, java.lang.String)}.
	 */
/*	@Test
	public void testAddNewSubject() {
		fail("Not yet implemented");
	}
*/	

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#getThreads(long)}.
	 */
/*	@Test
	public void testGetThreads() {
		fail("Not yet implemented");
	}
*/

	/**
	 * Test method for {@link forum.server.domainlayer.message.
	 * MessagesController#openNewThread(long, java.lang.String, long, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testOpenNewThread() {
		final long tSubject1ID = this.subjectsNamesToIDsMapping.get("main1");
		final long tSubject2ID = this.subjectsNamesToIDsMapping.get("sub1");

		// adding threads by registered members which have the permissions to add subjects

		try {
			this.messagesControllerUnderTest.openNewThread(this.createdMembers[2].getID(), "test1",
					tSubject1ID, "message1", "content1");
			this.messagesControllerUnderTest.openNewThread(this.createdMembers[3].getID(), "test2",
					tSubject1ID, "message2", "content2");
			this.messagesControllerUnderTest.openNewThread(this.createdMembers[3].getID(), "test2",
					tSubject2ID, "message2", "content2");
		}
		catch (NotRegisteredException e) {
			fail("unregistered exception for a registered forum member");
		}
		catch (NotPermittedException e) {
			fail("unpermitted exception for a registered member");
		}
		catch (SubjectNotFoundException e) {
			fail("subjecy-not-found exception for an existing subject");
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}

		try {
			final ForumSubject tSubject1 = this.dataHandler.getMessagesCache().getSubjectByID(tSubject1ID);
			final Collection<Long> tSubject1Threads = tSubject1.getThreads();
			assertTrue(tSubject1Threads.size() == 2);
			final long tThread1ID = tSubject1Threads.iterator().next();

			final ForumSubject tSubject2 = this.dataHandler.getMessagesCache().getSubjectByID(tSubject2ID);
			final Collection<Long> tSubject2Threads = tSubject2.getThreads();
			assertTrue(tSubject2Threads.size() == 1);
			final long tThread2ID = tSubject2Threads.iterator().next();

			try {
				final ForumThread tThread1 = this.dataHandler.getMessagesCache().getThreadByID(tThread1ID);
				assertNotNull(tThread1);
				assertEquals(tThread1.getTopic(), "test1");

				final ForumThread tThread2 = this.dataHandler.getMessagesCache().getThreadByID(tThread2ID);
				assertNotNull(tThread2);

				final long tThread1RootMessage = tThread1.getRootMessageID();
				final ForumMessage tMessage1 = this.dataHandler.getMessagesCache().getMessageByID(tThread1RootMessage);
				assertNotNull(tMessage1);
				assertEquals(tMessage1.getTitle(), "message1");
				assertEquals(tMessage1.getContent(), "content1");
				assertEquals(tMessage1.getAuthorID(), this.createdMembers[2].getID());
			}
			catch (ThreadNotFoundException e) {
				fail("first added thread wasn't found");
			}
			catch (MessageNotFoundException e) {
				fail("the root message of the first added thread wasn't found");
			}
		}
		catch (SubjectNotFoundException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#deleteAThread(long, long, long)}.
	 */
	/*	@Test
	public void testDeleteAThread() {
		fail("Not yet implemented");
	}
	 */	

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#getReplies(long)}.
	 */
	@Test
	public void testGetReplies() {
		try {
			final long tSubSubject11ID = this.subjectsNamesToIDsMapping.get("sub11");

			final ForumSubject tSubSubject11 = this.dataHandler.getMessagesCache().getSubjectByID(tSubSubject11ID);
			this.messagesControllerUnderTest.openNewThread(this.createdMembers[3].getID(), "thread1",
					tSubSubject11ID, "title1", "content1");
			this.messagesControllerUnderTest.openNewThread(this.createdMembers[3].getID(), "thread2",
					tSubSubject11ID, "title2", "content2");

			Iterator<Long> tThreadsIter = tSubSubject11.getThreads().iterator();

			final long tThread1ID = tThreadsIter.next();
			final long tThread2ID = tThreadsIter.next();

			final ForumThread tThread1 = this.dataHandler.getMessagesCache().getThreadByID(tThread1ID);
			final ForumThread tThread2 = this.dataHandler.getMessagesCache().getThreadByID(tThread2ID);

			final long tRootMessage1ID = tThread1.getRootMessageID();
			final long tRootMessage2ID = tThread2.getRootMessageID();

			UIMessage tMessage1 = this.messagesControllerUnderTest.addNewReply(this.createdMembers[2].getID(),
					tRootMessage1ID, "title3", "content3");
			UIMessage tMessage2 = this.messagesControllerUnderTest.addNewReply(this.createdMembers[3].getID(),
					tRootMessage1ID, "title4", "content4");

			UIMessage tMessage3 = this.messagesControllerUnderTest.addNewReply(this.createdMembers[0].getID(),
					tRootMessage2ID, "title5", "content5");

			try {
				Collection<UIMessage> tRoot1Replies = this.messagesControllerUnderTest.getReplies(tRootMessage1ID);
				Collection<UIMessage> tRoot2Replies = this.messagesControllerUnderTest.getReplies(tRootMessage2ID);
				assertTrue(tRoot1Replies.size() == 2);
				assertTrue(tRoot2Replies.size() == 1);

				Iterator<UIMessage> tMessage1RepliesIter = tRoot1Replies.iterator();

				UIMessage tReply = tMessage1RepliesIter.next();
				assertEquals(tMessage1.getID(), tReply.getID());
				assertEquals(tMessage1.getAuthorID(), tReply.getAuthorID());
				assertEquals(tMessage1.getTitle(), tReply.getTitle());
				assertEquals(tMessage1.getContent(), tReply.getContent());

				tReply = tMessage1RepliesIter.next();
				assertEquals(tMessage2.getID(), tReply.getID());
				assertEquals(tMessage2.getAuthorID(), tReply.getAuthorID());
				assertEquals(tMessage2.getTitle(), tReply.getTitle());
				assertEquals(tMessage2.getContent(), tReply.getContent());

				tReply = tRoot2Replies.iterator().next();
				assertEquals(tMessage3.getID(), tReply.getID());
				assertEquals(tMessage3.getAuthorID(), tReply.getAuthorID());
				assertEquals(tMessage3.getTitle(), tReply.getTitle());
				assertEquals(tMessage3.getContent(), tReply.getContent());
			}
			catch (MessageNotFoundException e) {
				fail(e.getMessage());
			}
		}	
		catch (NoSuchElementException e) {
			fail("the thread or one of the messages hasn't been added successfully");
		}
		catch (NotPermittedException e) {
			fail(e.getMessage());
		}
		catch (NotRegisteredException e) {
			fail(e.getMessage());

		} 
		catch (SubjectNotFoundException e) {
			fail(e.getMessage());
		}
		catch (ThreadNotFoundException e) {
			fail("the open new thread method doesn't work");
		}
		catch (MessageNotFoundException e) {
			fail("the open new thread method doesn't work");
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}



	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#addNewReply(long, long, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddNewReply() {
		try {
			final long tSubSubject11ID = this.subjectsNamesToIDsMapping.get("sub11");

			final ForumSubject tSubSubject11 = this.dataHandler.getMessagesCache().getSubjectByID(tSubSubject11ID);
			this.messagesControllerUnderTest.openNewThread(this.createdMembers[3].getID(), "thread1",
					tSubSubject11ID, "title1", "content1");
			this.messagesControllerUnderTest.openNewThread(this.createdMembers[3].getID(), "thread2",
					tSubSubject11ID, "title2", "content2");

			Iterator<Long> tThreadsIter = tSubSubject11.getThreads().iterator();

			final long tThread1ID = tThreadsIter.next();
			final long tThread2ID = tThreadsIter.next();

			final ForumThread tThread1 = this.dataHandler.getMessagesCache().getThreadByID(tThread1ID);
			final ForumThread tThread2 = this.dataHandler.getMessagesCache().getThreadByID(tThread2ID);

			final long tRootMessage1ID = tThread1.getRootMessageID();
			final long tRootMessage2ID = tThread2.getRootMessageID();

			final ForumMessage tRootMessage1 = this.dataHandler.getMessagesCache().getMessageByID(tRootMessage1ID);
			final ForumMessage tRootMessage2 = this.dataHandler.getMessagesCache().getMessageByID(tRootMessage2ID);

			// test adding a reply by a permitted member

			try {
				this.messagesControllerUnderTest.addNewReply(this.createdMembers[0].getID(),
						tRootMessage1ID, "title11", "content11");
				// tests that the first reply of the message is the added one
				final long tMessage3ID = tRootMessage1.getReplies().iterator().next();
				final ForumMessage tMessage3 = this.dataHandler.getMessagesCache().getMessageByID(tMessage3ID);
				assertEquals(tMessage3.getAuthorID(), this.createdMembers[0].getID());
				assertEquals(tMessage3.getTitle(), "title11");
				assertEquals(tMessage3.getContent(), "content11");

				// adds another reply by a permitted user (to the same message)

				this.messagesControllerUnderTest.addNewReply(this.createdMembers[1].getID(),
						tRootMessage1ID, "title12", "content12");
				// tests that the second reply of the message is the added one
				Iterator<Long> tRepliesIter = tRootMessage1.getReplies().iterator();
				tRepliesIter.next(); // overcome the first message
				final long tMessage4ID = tRepliesIter.next();
				final ForumMessage tMessage4 = this.dataHandler.getMessagesCache().getMessageByID(tMessage4ID);
				assertEquals(tMessage4.getAuthorID(), this.createdMembers[1].getID());
				assertEquals(tMessage4.getTitle(), "title12");
				assertEquals(tMessage4.getContent(), "content12");
			}
			catch (MessageNotFoundException e) {
				fail("the message hasn't been added successfuly");
			}
			catch (NotPermittedException e) {
				fail("notpermitted exception for a permitted member");
			}

			// adds a reply by a not permitted user

			this.createdMembers[0].removePermission(Permission.REPLY_TO_MESSAGE);

			boolean tWasNotPermitted = false;

			try {
				this.messagesControllerUnderTest.addNewReply(this.createdMembers[0].getID(), 
						tRootMessage2ID, "title21", "content21");
			}
			catch (NotPermittedException e) {
				tWasNotPermitted = true;
			}
			assertTrue("notpermitted exception wasn't thrown for a not permitted user", tWasNotPermitted);
			assertTrue(tRootMessage2.getReplies().isEmpty()); // checks that the message hasn't been added
		}	
		catch (NoSuchElementException e) {
			fail("the thread or one of the messages hasn't been added successfully");
		}
		catch (NotPermittedException e) {
			fail(e.getMessage());
		}
		catch (NotRegisteredException e) {
			fail(e.getMessage());

		} 
		catch (SubjectNotFoundException e) {
			fail(e.getMessage());
		}
		catch (ThreadNotFoundException e) {
			fail("the open new thread method doesn't work");
		}
		catch (MessageNotFoundException e) {
			fail("the open new thread method doesn't work");
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}
		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#updateAMessage(long, long, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUpdateAMessage() {
		try {
			final long tMain1SubjectID = this.subjectsNamesToIDsMapping.get("main1");
			final ForumSubject tMain1Subject = this.dataHandler.getMessagesCache().getSubjectByID(tMain1SubjectID);
			this.messagesControllerUnderTest.openNewThread(this.createdMembers[3].getID(), "thread1",
					tMain1SubjectID, "title1", "content1");
			final long tThreadID = tMain1Subject.getThreads().iterator().next();
			final ForumThread tThread = this.dataHandler.getMessagesCache().getThreadByID(tThreadID);
			final long tMessage1ID = tThread.getRootMessageID();
			this.messagesControllerUnderTest.addNewReply(this.createdMembers[2].getID(), 
					tMessage1ID, "title2", "content2");
			final ForumMessage tMessage1 = this.dataHandler.getMessagesCache().getMessageByID(tMessage1ID);
			final long tMessage2ID = tMessage1.getReplies().iterator().next();

			// tests edition of the first message by a permitted author

			try {
				this.messagesControllerUnderTest.updateAMessage(this.createdMembers[3].getID(),
						tMessage1ID, "title1 changed", "content1 changed");
			} 
			catch (NotRegisteredException e) {
				fail(e.getMessage());
			}
			catch (NotPermittedException e) {
				fail("unpermitted exception for a permitted author");
			}
			catch (MessageNotFoundException e) {
				fail("notfound exception for an existing message");
			}
			catch (DatabaseUpdateException e) {
				fail(e.getMessage());
			}
			try {
				final UIMessage tUpdatedMessage1 = this.messagesControllerUnderTest.getMessageByID(tMessage1ID);
				assertEquals(tMessage1ID, tUpdatedMessage1.getID());
				assertEquals(this.createdMembers[3].getID(), tUpdatedMessage1.getAuthorID());
				assertEquals(tUpdatedMessage1.getTitle(), "title1 changed");
				assertEquals(tUpdatedMessage1.getContent(), "content1 changed");
			} 
			catch (MessageNotFoundException e) {
				fail("message1 wasn't found after updating");
			}
			catch (DatabaseRetrievalException e) {
				fail(e.getMessage());
			}

			boolean tWasNotPermitted = false;

			// tests edition of the second message by a permitted user but not its author

			try {
				this.messagesControllerUnderTest.updateAMessage(this.createdMembers[3].getID(),
						tMessage2ID, "title2 updated", "content2 updated");
				assertTrue("notpermitted exception wasn't thrown for a not permitted user", tWasNotPermitted);
				final UIMessage tUpdatedMessage2 = this.messagesControllerUnderTest.getMessageByID(tMessage2ID);
				assertEquals(tMessage2ID, tUpdatedMessage2.getID());
				assertEquals(this.createdMembers[3].getID(), tUpdatedMessage2.getAuthorID());
				assertEquals(tUpdatedMessage2.getTitle(), "title2");
				assertEquals(tUpdatedMessage2.getContent(), "content2");			
			}
			catch (NotPermittedException e) {
				tWasNotPermitted = true;
			}

			// tests edition of the first message by its author but that isn't permitted to edit messages

			tWasNotPermitted = false;

			this.createdMembers[3].removePermission(Permission.EDIT_MESSAGE);
			try {
				this.messagesControllerUnderTest.updateAMessage(this.createdMembers[3].getID(),
						tMessage1ID, "title1", "content1");
				assertTrue("notpermitted exception wasn't thrown for a not permitted user", tWasNotPermitted);
				final UIMessage tUpdatedMessage1 = this.messagesControllerUnderTest.getMessageByID(tMessage1ID);
				assertEquals(tMessage1ID, tUpdatedMessage1.getID());
				assertEquals(this.createdMembers[3].getID(), tUpdatedMessage1.getAuthorID());
				assertEquals(tUpdatedMessage1.getTitle(), "title1 changed");
				assertEquals(tUpdatedMessage1.getContent(), "content1 changed");			
			}
			catch (NotPermittedException e) {
				tWasNotPermitted = true;
			}
		}
		catch (NoSuchElementException e) {
			fail("the thread or one of the messages hasn't been added successfully");
		}
		catch (MessageNotFoundException e) {
			fail("the add reply method doesn't work");
		}
		catch (ThreadNotFoundException e) {
			fail("the open thread method doesn't work");
		}
		catch (NotPermittedException e) {
			fail(e.getMessage());
		}
		catch (NotRegisteredException e) {
			fail(e.getMessage());
		} 
		catch (SubjectNotFoundException e) {
			fail(e.getMessage());
		}
		catch (DatabaseRetrievalException e) {
			fail(e.getMessage());
		}

		catch (DatabaseUpdateException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#deleteAMessage(long, long, long)}.
	 */
	/*	@Test
	public void testDeleteAMessage() {
		fail("Not yet implemented");
	}
	 */
}
