/**
 * 
 */
package forum.server.domainlayer.testing;

import static org.junit.Assert.*;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.impl.*;
import forum.server.domainlayer.interfaces.*;
import forum.server.exceptions.subject.*;
import forum.server.exceptions.user.*;
import forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler;
import forum.server.persistentlayer.pipe.PersistenceFactory;

/**
 * @author sepetnit
 *
 */
public class ForumSubjectImplTest {

	private RegisteredUser tUser;
	private ForumMessage tMessage1;
	private ForumMessage tMessage2;
	private ForumMessage tMessage3;
	private ForumSubject tForumSubject;
	private ForumSubject tForumSubject2;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		JAXBpersistenceDataHandler.testMode();
		 tUser = new RegisteredUserImpl("user1", "pass1", "a", "b", "mail@mymail.com");			
		 tMessage1 = new ForumMessageImpl(tUser, "title1", "content1");
		 tMessage2 = new ForumMessageImpl(tUser, "title2", "content2");
		 tMessage3 = new ForumMessageImpl(tUser, "title3", "content3");
		 tForumSubject = new ForumSubjectImpl("my description1" , "name1");
		 tForumSubject2 = new ForumSubjectImpl("my description2" , "name2");
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		JAXBpersistenceDataHandler.regularMode();
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumSubjectImpl#addSubSubject(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddSubSubject() {	
		int tNumOfSub = tForumSubject.getSubSubjects().size();
		ForumSubjectImpl tFssub = new ForumSubjectImpl ("my description2", "name2");
		try {
			new ForumImpl().addForumSubject(tForumSubject);
			tForumSubject.addSubSubject(tFssub);
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		} catch (SubjectAlreadyExistsException e) {
			fail("for some reason, a subject with name2, already exists in the domain layer");
		}
		assertTrue(tNumOfSub + 1 == tForumSubject.getSubSubjects().size());		
		assertFalse(tForumSubject.getSubSubjects().indexOf(tFssub) == -1);	
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumSubjectImpl#getNumOfThreads()}.
	 */
	@Test
	public void testGetNumOfThreads() {	
		int tNumOfThreads = this.tForumSubject.getNumOfThreads();
		try {
			Forum tForum = new ForumImpl();
			tForum.registerUser(tUser);
			tForum.addForumSubject(tForumSubject);
			
			tForumSubject.openNewThread(tMessage1);
			tForumSubject.addSubSubject(tForumSubject2);
			
			tForumSubject2.openNewThread(tMessage2);
			
		} catch (JAXBException e1) {
			fail("database error!");
		} catch (IOException e1) {
			fail("database error!");
		} catch (NotRegisteredException e) {
			fail("for some reason user1 isn't registered to the forum");
		} catch (SubjectNotFoundException e) {
			fail("for some reason a one of the subjects wasn't found");
		} catch (SubjectAlreadyExistsException e) {
			fail("for some reason name1 has already contain a sub-subject name2");
		} catch (UserAlreadyExistsException e) {
			fail("for some reason user1 can't be registered to the forum");
		}
		assertTrue(tNumOfThreads + 2 == this.tForumSubject.getNumOfThreads());
	}
	
	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumSubjectImpl#openNewThread(forum.server.domainlayer.interfaces.RegisteredUser, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testOpenNewThread() {
		int tNumOfThreads = this.tForumSubject.getThreads().size();
		try {
			Forum tForumImpl = new ForumImpl();
			tForumImpl.registerUser(tUser);
			tForumImpl.addForumSubject(tForumSubject);
			this.tForumSubject.openNewThread(tMessage1);
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		} catch (NotRegisteredException e) {
			fail("for some reason user1 isn't registered to the forum");
		} catch (SubjectNotFoundException e) {
			fail("for some reason a one of the subjects wasn't found");
		} catch (UserAlreadyExistsException e) {
			fail("for some reason user1 is already registered to the forum");
		} catch (SubjectAlreadyExistsException e) {
			fail("for some reason one of the subjects already exist in the forum");
		}		
		
		assertTrue(tNumOfThreads + 1 == tForumSubject.getThreads().size());
		
		for (ForumThread tForumThread : tForumSubject.getThreads()) {
			if (tForumThread.getRootMessageID()  == tMessage1.getMessageID())
				return;
		}
		fail("message1 wasn't found");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumSubjectImpl#subjToString()}.
	 */
	@Test
	public void testSubjToString() {
		String tSubjToString = "name1" + " " + "my description1";
		try {
			Forum tForumImpl = new ForumImpl();
			tForumImpl.registerUser(tUser);
			tForumImpl.addForumSubject(tForumSubject);
			
			this.tForumSubject.openNewThread(tMessage1);
			this.tForumSubject.addSubSubject(tForumSubject2);
			tForumSubject2.openNewThread(tMessage2);
			tMessage2.addReplyToMe(tMessage3);
			
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		} catch (NotRegisteredException e) {
			fail("for some reason user1 isn't registered to the forum");
		} catch (SubjectNotFoundException e) {
			fail("for some reason one of the required subjects wasn't found");
		} catch (SubjectAlreadyExistsException e) {
			fail("for some reason subject name1 already contains a sub-subject named name2");
		} catch (UserAlreadyExistsException e) {
			fail("for some reason user1 is already registered to the forum");
		}		
		tSubjToString = tForumSubject.getName() + " " + tForumSubject.getDescription() + "\n\n"  +
			"user1" + "\n" + tMessage1.getDate() + "\n" + tMessage1.getTime() + "\n" +
			"title1" + "\n" + "content1" + "\nreplys\n" +
			"\n" + "subSubjects {" + "\n\n" + tForumSubject2.getName() + " " + tForumSubject2.getDescription() +
			"\n\n"  + "user1" + "\n" + tMessage2.getDate() + "\n" + tMessage2.getTime() + "\n" +
			"title2" + "\n" + "content2" + "\nreplys\n" +
			"user1" + "\n" + tMessage3.getDate() + "\n" + tMessage3.getTime() + "\n" +
			"title3" + "\n" + "content3" + "\nreplys\n" + "\n" + "subSubjects {" + "}}";
		assertTrue(this.tForumSubject.subjToString().equals(tSubjToString));	
	}
}
