package forum.server.domainlayer.testing;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.impl.*;
import forum.server.domainlayer.interfaces.*;
import forum.server.exceptions.message.*;
import forum.server.exceptions.subject.*;
import forum.server.exceptions.user.*;
import forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler;

public class ForumThreadImplTest extends TestCase
{
	private RegisteredUser tUser;
	private Forum tForum;
	private ForumMessage tMessage1;
	private ForumMessage tMessage2;
	private ForumSubject tForumSubject;
	
	@Before
	public void setUp()
	{
		try {
			JAXBpersistenceDataHandler.testMode();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.tForum = new ForumImpl();
		this.tUser = new RegisteredUserImpl("e", "ee", "eee", "eee", "eee@ma.com");
		this.tMessage1 = new ForumMessageImpl(this.tUser, "hello", "world");
		this.tForumSubject = new ForumSubjectImpl("a", "b");
		
		
		try
		{
			this.tForum.addForumSubject(this.tForumSubject);
			this.tForum.registerUser(this.tUser);
			this.tForum.login("e", "ee");
		}
		catch (NotRegisteredException e)
		{
			//sadasd
		}
		catch (AlreadyConnectedException e)
		{
			//sad
		} catch (UserAlreadyExistsException e) {
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} catch (WrongPasswordException e) {
			// TODO Auto-generated catch block
		} catch (SubjectAlreadyExistsException e) {
			// TODO Auto-generated catch block
		}
		
	}

	@After
	public void tearDown() throws Exception 
	{
		JAXBpersistenceDataHandler.regularMode();
	}

	@Test
	public void testAddMessage() 
	{
		int numOfMessages = this.tForumSubject.getNumOfThreads();
		try {
			this.tForumSubject.openNewThread(this.tMessage1);
		} catch (JAXBException e) {
			System.out.println("jaxb error");
		} catch (IOException e) {
			System.out.println("io error");
		} catch (NotRegisteredException e) {
			System.out.println("not reg error");
		} catch (SubjectNotFoundException e) {
			//System.out.println("not found error");		
		}
		assertTrue(this.tForumSubject.getNumOfThreads() == numOfMessages + 1);
	}

	@Test
	public void testDecNumOfResponses() 
	{
		int numOfMessages = this.tForumSubject.getNumOfThreads();
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		int location = 0;
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		
		assertTrue(numOfMessages + 1 == this.tForumSubject.getNumOfThreads()); // good adding

		this.tForumSubject.getThreads().elementAt(location).decNumOfResponses();
		
		assertTrue(numOfMessages == this.tForumSubject.getThreads().elementAt(location).getNumOfResponese());		
	}

	@Test
	public void testGetAuthor() 
	{
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		int location = 0;
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		
		assertTrue(this.tForumSubject.getThreads().elementAt(location).getAuthor().equals(this.tUser.getUsername()));
	}

	@Test
	public void testGetThreadSubject()
	{
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		int location = 0;
		
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		assertTrue(this.tForumSubject.getThreads().elementAt(location).getThreadSubject().equals(tMsg.getMessageTitle()));
	}

	@Test
	public void testIncNumOfResponses() 
	{
		int numOfMessages = this.tForumSubject.getNumOfThreads();
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		int location = 0;
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		
		assertTrue(numOfMessages + 1 == this.tForumSubject.getNumOfThreads()); // good adding

		this.tForumSubject.getThreads().elementAt(location).incNumOfResponses();
		
		assertTrue(numOfMessages + 1 == this.tForumSubject.getThreads().elementAt(location).getNumOfResponese());
	}

/*	@Test
	public void testIncNumOfViews()
	{
		fail("Not yet implemented");
	}*/

	@Test
	public void testGetLatestPostAuthor() 
	{
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		int location = 0;
		
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		assertTrue(this.tForumSubject.getThreads().elementAt(location).getLatestPostAuthor().equals( 
			tMsg.getAuthor().getUsername()));
	}

/*	@Test SAME HERE AS TODO ABOVE
	public void testGetLatestPostDate() 
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetLatestPostTime()
	{
		fail("Not yet implemented");
	}*/

	@Test
	public void testGetPostingDate()
	{
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		int location = 0;
		
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		
		assertTrue (tMsg.getDate().equals(this.tForumSubject.getThreads().elementAt(location).getPostingDate()));
	}

	@Test
	public void testGetPostingTime() 
	{
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		int location = 0;
		
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		
		assertTrue (tMsg.getTime().equals(this.tForumSubject.getThreads().elementAt(location).getPostingTime()));
	}

	@Test
	public void testSetLatestPost()
	{
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		ForumMessage tMsg2 = new ForumMessageImpl(this.tUser, "ttl2", "1243124");
		int location = 0;
		
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		
		
		ForumMessage temp = null;
		try {
			this.tForumSubject.getThreads().elementAt(location).addMessage(tMsg, tMsg2); //TODO I think there is a problem with adding
			temp = tMsg.findMessage(tMsg2.getMessageID());
		} catch (MessageNotFoundException e) {
		}
		assertTrue(temp == tMsg2);
	}

	@Test
	public void testGetRootMessageID() 
	{
		ForumMessage tMsg = new ForumMessageImpl(this.tUser, "ttl", "");
		int location = 0;
		
		try 
		{
			this.tForumSubject.openNewThread(tMsg);
			location = this.tForumSubject.getThreads().size() - 1;
		} 
		catch (JAXBException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (NotRegisteredException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SubjectNotFoundException e) 
		{
			// TODO Auto-generated catch block
		}		
		
		assertTrue (tMsg.getMessageID() == this.tForumSubject.getThreads().elementAt(location).getRootMessageID());
	}
}
