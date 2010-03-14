/**
 * 
 */
package forum.server.domainlayer.testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.impl.ForumMessageImpl;
import forum.server.domainlayer.impl.ForumSubjectImpl;
import forum.server.domainlayer.impl.ForumThreadImpl;
import forum.server.domainlayer.impl.RegisteredUserImpl;
import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.ForumSubject;
import forum.server.domainlayer.interfaces.ForumThread;
import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.exceptions.subject.SubjectNotFoundException;

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
		 tUser = new RegisteredUserImpl("user1", "pass1", "a", "b", "mail@mymail.com");			
		 tMessage1 = new ForumMessageImpl(tUser, "title1", "content1");

		 tMessage2 = new ForumMessageImpl(tUser, "title2", "content2");

		 tMessage3 = new ForumMessageImpl(tUser, "title3", "content3");

		 tForumSubject = new ForumSubjectImpl("my description1" , "name1");
		 
		 tForumSubject2 = new ForumSubjectImpl("my description2" , "name2");

		 
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumSubjectImpl#addSubSubject(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddSubSubject() {	
		int tNumOfSub = tForumSubject.getSubSubjects().size();
		ForumSubjectImpl tFssub = new ForumSubjectImpl ("my description2", "name2");
		
		try {
			tForumSubject.addSubSubject(tFssub);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SubjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(tNumOfSub+1==tForumSubject.getSubSubjects().size());
		
		assertFalse(tForumSubject.getSubSubjects().indexOf(tFssub) == -1);
		
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumSubjectImpl#getNumOfThreads()}.
	 */
	@Test
	public void testGetNumOfThreads() {
		
		int tNumOfThreads = this.tForumSubject.getNumOfThreads();
		
		try {
			tForumSubject.openNewThread(tMessage1);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			tForumSubject2.openNewThread(tMessage2);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			tForumSubject.addSubSubject(tForumSubject2);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SubjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			this.tForumSubject.openNewThread(tMessage1);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		assertTrue(tNumOfThreads + 1 == tForumSubject.getThreads().size());
		
		
		
		for (ForumThread tForumThread : tForumSubject.getThreads()) {
			if (tForumThread.getRootMessage().equals(tMessage1))
				return;
		}
		assertTrue(false);
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumSubjectImpl#subjToString()}.
	 */
	@Test
	public void testSubjToString() {
		String tSubjToString = "name1" + " " + "my description1";
//		assertTrue(this.tForumSubject.subjToString().equals(tSubjToString));
		
		try {
			this.tForumSubject.openNewThread(tMessage1);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.tForumSubject.addSubSubject(tForumSubject2);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SubjectNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			tForumSubject2.openNewThread(tMessage2);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tMessage2.replyToMessage(tMessage3);
		
		tSubjToString = tForumSubject.getName() + " " + tForumSubject.getDescription() + "\n\n"  +
			"user1" + "\n" + tMessage1.getDate() + "\n" + tMessage1.getTime() + "\n" +
			"title1" + "\n" + "content1" + "\nreplys\n" +
			"\n" + "subSubjects {" + "\n\n" + tForumSubject2.getName() + " " + tForumSubject2.getDescription() +
			"\n\n"  + "user1" + "\n" + tMessage2.getDate() + "\n" + tMessage2.getTime() + "\n" +
			"title2" + "\n" + "content2" + "\nreplys\n" +
			"user1" + "\n" + tMessage3.getDate() + "\n" + tMessage3.getTime() + "\n" +
			"title3" + "\n" + "content3" + "\nreplys\n" + "\n" + "subSubjects {" + "}}";

		System.out.println(this.tForumSubject.subjToString());

		assertTrue(this.tForumSubject.subjToString().equals(tSubjToString));
		
	}

}
