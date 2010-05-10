/**
 * 
 */
package testing.persistent;

import java.util.Vector;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.Settings;
import forum.server.domainlayer.user.ForumMember;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.*;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class JAXBpersistenceDataHandlerTest extends TestCase {
	private PersistenceDataHandler pipe;


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Settings.switchToTestMode();
		this.pipe = PersistenceFactory.getPipe();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		Settings.switchToRegularMode();
	}

	/**
	 * Test method for {@link forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler#
	 * addNewMember(long, String, String, String, String, String, java.util.Collection)
	 */
	@Test
	public void testRegisterToForum() {
		try {
			for (ForumMember tCurrentMember : this.pipe.getAllMembers()) {
				if (tCurrentMember.getUsername().equals("user1"))
					fail("user1 shouldn't be in the database");
			}
			pipe.addNewMember(10, "user1", "abc", "a1", "b1", "f1@f", new Vector<Permission>());
			for (ForumMember tCurrentMember : this.pipe.getAllMembers()) {
				if (tCurrentMember.getUsername().equals("user1")) {
					assertTrue(true);
					return;
				}
			}
			fail("user1 wasn't found after updating");

		} 
		catch (DatabaseUpdateException e) {
			fail("database update error");
		}
		catch (DatabaseRetrievalException e) {
			fail("database retrieve error");
		}
	}
}
	/**
	 * Test method for {@link forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler#addNewMessage(long, long, java.lang.String, java.lang.String, java.lang.String)}.
	 * 
	 * @pre: The addSubject, addSubSubject and registerToForum methods works properly
	 */
	/*	@Test
	public void testAddNewMessage() {
		
	
		try { // add new subjects to the forum
			this.pipe.addNewSubject(1, "subject1", "description1", true);
			this.pipe.registerToForum("user1", "123", "last", "first", "hello@hello");			

			try {
				// add a new message to subject1
				this.pipe.addNewMessage(0, 1, "user1", "test-message1", "test-content1");
				try {
					this.pipe.addNewMessage(1, 1, "user2", "test-message1", "test-content1"); // add a new message
					fail("succeeded to add the message, but user2 isn't registered!");				
				}
				catch (NotRegisteredException e) {
					// add a new message to subject2
					this.pipe.addNewMessage(2, 2, "user1", "test-message2", "test-content2"); // add a new message

					try {
						this.pipe.addNewMessage(2, 4, "user1", "test-message2", "test-content2"); // add a new message
						fail("the message was added, but subject4 doesn't exist!");
					}
					catch (SubjectNotFoundException e1) {
						ForumType tFt = this.pipe.getForumFromDatabase();
						SubjectType tSubject1 = null;
						SubjectType tSubject2 = null;
						// initializes tSubject1 according to the database 
						for (SubjectType tSubject : tFt.getForumSubjects()) {
							if (tSubject.getSubjectID() == 1) {
								tSubject1 = tSubject;
								break;
							}
						}
						// initializes tSubject2 according to the database 
						for (SubjectType tSubSubject : tSubject1.getSubSubjects()) {
							if (tSubSubject.getSubjectID() == 2) {
								tSubject2 = tSubSubject;
								break;
							}
						}
						// checks that the message was added to subject1
						assertTrue(tSubject1.getSubThreads().size() == 1 &&
								tSubject1.getSubThreads().get(0).getStartMessage().getMessageID() == 0 &&
								tSubject1.getSubThreads().get(0).getStartMessage().
								getAuthor().equals("user1") &&
								tSubject1.getSubThreads().get(0).getStartMessage().getTopic().equals("test-message1") &&
								tSubject1.getSubThreads().get(0).getStartMessage().getContent().equals("test-content1")
						);

						// checks that the message was added to subject2
						assertTrue(tSubject2.getSubThreads().size() == 1 &&
								tSubject2.getSubThreads().get(0).getStartMessage().getMessageID() == 2 &&
								tSubject2.getSubThreads().get(0).getStartMessage().
								getAuthor().equals("user1") &&
								tSubject2.getSubThreads().get(0).getStartMessage().getTopic().equals("test-message2") &&
								tSubject2.getSubThreads().get(0).getStartMessage().getContent().equals("test-content2")
						);


						// add a new message to subject2
						this.pipe.addNewMessage(10, 2, "user1", "test-message3", "test-content3"); // add a new message
						tFt = this.pipe.getForumFromDatabase();

						// initializes tSubject1 according to the database 
						for (SubjectType tSubject : tFt.getForumSubjects()) {
							if (tSubject.getSubjectID() == 1) {
								tSubject1 = tSubject;
								break;
							}
						}
						// initializes tSubject2 according to the database 
						for (SubjectType tSubSubject : tSubject1.getSubSubjects()) {
							if (tSubSubject.getSubjectID() == 2) {
								tSubject2 = tSubSubject;
								break;
							}
						}
						assertTrue(tSubject2.getSubThreads().size() == 2);
					}
				}
			} catch (NotRegisteredException e) {
				fail("user1 isn't registered in the forum, for some reason");				
			}
		} catch (JAXBException e) {
			fail("database error");
		} catch (IOException e) {
			fail("database error");
		} catch (SubjectAlreadyExistsException e) {
			fail("there is a problem with the subject or sub-subject add methods");
		}
		catch (SubjectNotFoundException e) {
			fail("there is a problem with the subject or sub-subject add methods");
		} catch (UserAlreadyExistsException e) {
			fail("there is a problem with the user registration methods");
		}
	}
*/
	/**
	 * Test method for {@link forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler#addNewSubject(long, java.lang.String, java.lang.String)}.
	 */
/*	@Test
	public void testAddNewSubject() {
		try {
			this.pipe.addNewSubject(1, "subject1", "description1");
			ForumType tFt = this.pipe.getForumFromDatabase();
			boolean tDidSubject1Added = false;
			// checks if subject1 was added properly to the database
			for (SubjectType tSubSubject : tFt.getForumSubjects()) {
				if (tSubSubject.getSubjectID() == 1) {
					tDidSubject1Added = true;
					break;
				}
			}
			if (!tDidSubject1Added)
				fail("subject1 wasn't added");

			try {
				this.pipe.addNewSubject(2, "subject1", "description1");
				fail("subject1 was added again to the database although it already exists!");
			}
			catch (SubjectAlreadyExistsException e1) {
				this.pipe.addNewSubject(2, "subject2", "description2");
				tFt = this.pipe.getForumFromDatabase();
				boolean tDidSubject2Added = false;
				// checks if subject2 was added properly to the database
				for (SubjectType tSubSubject : tFt.getForumSubjects()) {
					if (tSubSubject.getSubjectID() == 2) {
						tDidSubject2Added = true;
						break;
					}
				}
				if (!tDidSubject2Added)
					fail("subject2 wasn't added");
			}
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e1) {
			fail("database error!");
		} catch (SubjectAlreadyExistsException e1) {
			fail("fore some reason, subject1 or subject2 already exist in the database");
		}
	}
*/
	/**
	 * Test method for {@link forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler#addNewSubSubject(long, long, java.lang.String, java.lang.String)}.
	 * 
	 * @pre: addNewSubject method works properly
	 */
	/*	@Test
	public void testAddNewSubSubject() {
		try {
			this.pipe.addNewSubject(1, "subject1", "description1");
			this.pipe.addNewSubSubject(1, 3, "subject3", "description3");
			this.pipe.addNewSubSubject(3, 4, "subject4", "description4");
			try {			
				this.pipe.addNewSubSubject(3, 5, "subject4", "description4");
				fail("subject4 was added again as a sub-subject of subject3");
			} catch (SubjectAlreadyExistsException e) {
				ForumType tFt = this.pipe.getForumFromDatabase();
				// checks that subject3 has already been added as a sub-subject of subject1
				assertTrue(tFt.getForumSubjects().get(0).getSubSubjects().get(0).getSubjectID() == 3);
				// checks that subject4 has already been added as a sub-subject of subject3
				assertTrue(tFt.getForumSubjects().get(0).getSubSubjects().get(0).
						getSubSubjects().get(0).getSubjectID() == 4);
			}
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		} catch (SubjectAlreadyExistsException e) {
			fail("for some reason, one of the given subjects' names already appears in the database," +
			"but the names are all different");
		} catch (SubjectNotFoundException e) {
			fail("the addNewSubject method doesn't work properly!");
		}
	}
*/
	/**
	 * Test method for {@link forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler#replyToMessage(long, long, java.lang.String, java.lang.String, java.lang.String)}.
	 * 
	 * @pre: addNewMessage method works properly
	 */
	/*	@Test
	public void testReplyToMessage() {
		try {
			this.pipe.addNewSubject(1, "subject1", "description1");
			this.pipe.registerToForum("user1", "123", "test1", "test1", "hello@hello");
			this.pipe.addNewMessage(0, 1, "user1", "title1", "content1"); // adds a new message to subject1
			this.pipe.replyToMessage(0, 1, "user1", "reply1", "replyContent1");
			try {
				// check reply to a non existing message
				this.pipe.replyToMessage(2, 2, "user1", "reply2", "replyContent2");
				fail("the reply to message with id 2 succeeded, but this message doesn't exist");
			}
			catch (MessageNotFoundException e) {
				try {
				// check reply from a non registered user
				this.pipe.replyToMessage(0, 3, "user2", "reply3", "replyContent3");
				fail("the reply from user2 succceeded, but this user isn't registered to the forum");
				}
				catch (NotRegisteredException e1) {
					this.pipe.replyToMessage(0, 3, "user1", "reply3", "replyContent3");
					this.pipe.replyToMessage(3, 5, "user1", "reply4", "replyContent4");
					// the test can succeed only if the previous reply was added to the database,
					// otherwise, a message with id 3 wouldn't be found
				}
			}
		} 
		catch (JAXBException e) {
			e.printStackTrace();
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		} catch (SubjectAlreadyExistsException e) {
			fail("the subjects or sub-subjects adding method don't work properly!");
		} catch (SubjectNotFoundException e) {
			fail("the subjects or sub-subjects adding method don't work properly!");		
		} catch (UserAlreadyExistsException e) {
			fail("the user register method doesn't work properly!");
		} catch (NotRegisteredException e) {
			fail("the user register method doesn't work properly!");
		} catch (MessageNotFoundException e) {
			fail("one of the new messages wasn't found, it seems that the addNewMessage method" +
			"doesn't work properly!");
		}
	}
*/
	/**
	 * Test method for {@link forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler#updateMessage(long, java.lang.String, java.lang.String)}.
	 * 
	 * @pre: addNewMessage method works properly
	 */
/*	@Test
	public void testUpdateMessage() {
		try {
			this.pipe.addNewSubject(1, "subject1", "description1");
			this.pipe.addNewSubSubject(1, 3, "subject3", "description3");
			this.pipe.registerToForum("user1", "123", "test1", "test1", "hello@hello");
			this.pipe.addNewMessage(0, 1, "user1", "title1", "content1"); // adds a new message to subject1
			this.pipe.addNewMessage(1, 3, "user1", "title2", "content2"); // adds a new message to subject3
			this.pipe.updateMessage(0, "title1-updated", "content1");
			this.pipe.updateMessage(1, "title1", "content2-updated");
			this.pipe.updateMessage(0, "new-title", "content1");
			try {
				this.pipe.updateMessage(2, "title1", "content2-updated");
				fail("subject2 updating succeeded, but subject2 doesn't exist in the database");
			}
			catch (MessageNotFoundException e) {
				ForumType tFt = this.pipe.getForumFromDatabase();
				SubjectType tSubject1 = null;
				SubjectType tSubject3 = null;
				for (SubjectType tSubject : tFt.getForumSubjects()) {
					if (tSubject.getSubjectID() == 1) {
						tSubject1 = tSubject;
						break;
					}
				}
				for (SubjectType tSubSubject : tSubject1.getSubSubjects()) {
					if (tSubSubject.getSubjectID() == 3) {
						tSubject3 = tSubSubject;
						break;
					}
				}
				MessageType tMessage0 = tSubject1.getSubThreads().get(0).getStartMessage();
				MessageType tMessage1 = tSubject3.getSubThreads().get(0).getStartMessage();
				// checks that message0 was updated properly
				assertTrue(tMessage0.getTitle().equals("new-title") && tMessage0.getContent().equals("content1"));
				// checks that message1 was updated properly
				assertTrue(tMessage1.getTitle().equals("title1") && tMessage1.getContent().equals("content2-updated"));
			}
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		} catch (SubjectAlreadyExistsException e) {
			fail("the subjects or sub-subjects adding method don't work properly!");
		} catch (SubjectNotFoundException e) {
			fail("the subjects or sub-subjects adding method don't work properly!");		
		} catch (UserAlreadyExistsException e) {
			fail("the user register method doesn't work properly!");
		} catch (NotRegisteredException e) {
			fail("the user register method doesn't work properly!");
		} catch (MessageNotFoundException e) {
			fail("one of the new messages wasn't found, it seems that the addNewMessage method" +
			"doesn't work properly!");
		}
	}
}
*/