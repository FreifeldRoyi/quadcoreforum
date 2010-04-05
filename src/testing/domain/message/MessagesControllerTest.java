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

import forum.server.domainlayer.message.*;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.message.exceptions.MessageNotFoundException;
import forum.server.persistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.persistentlayer.pipe.message.exceptions.ThreadNotFoundException;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;

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
		
		ForumSubject tMainSubject1 = this.dataHandler.getMessagesCache().createNewSubject("main1", "main1", true);
		ForumSubject tSubSubject3 = this.dataHandler.getMessagesCache().createNewSubject("sub1", "sub1", false);
		ForumSubject tSubSubject4 = this.dataHandler.getMessagesCache().createNewSubject("sub2", "sub2", false);
		tMainSubject1.addSubSubject(tSubSubject3.getID());
		tMainSubject1.addSubSubject(tSubSubject4.getID());
		this.dataHandler.getMessagesCache().updateInDatabase(tMainSubject1);
		
		this.subjectsNamesToIDsMapping.put(tMainSubject1.getName(), tMainSubject1.getID());
		this.subjectsNamesToIDsMapping.put(tSubSubject3.getName(), tSubSubject3.getID());
		this.subjectsNamesToIDsMapping.put(tSubSubject4.getName(), tSubSubject4.getID());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#MessagesController(forum.server.domainlayer.ForumDataHandler)}.
	 */
	@Test
	public void testMessagesController() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#getSubjects(long)}.
	 */
	@Test
	public void testGetSubjects() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#addNewSubject(long, long, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddNewSubject() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#getThreads(long)}.
	 */
	@Test
	public void testGetThreads() {
		fail("Not yet implemented");
	}

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
	@Test
	public void testDeleteAThread() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#getReplies(long)}.
	 */
	@Test
	public void testGetReplies() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#addNewReply(long, long, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddNewReply() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#updateAMessage(long, long, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUpdateAMessage() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.message.MessagesController#deleteAMessage(long, long, long)}.
	 */
	@Test
	public void testDeleteAMessage() {
		fail("Not yet implemented");
	}
	
	

}
