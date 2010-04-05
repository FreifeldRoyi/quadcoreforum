/**
 * 
 */
package forum.server.domainlayer.testing;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.impl.ForumImpl;
import forum.server.domainlayer.impl.ForumSubjectImpl;
import forum.server.domainlayer.impl.RegisteredUserImpl;
import forum.server.domainlayer.interfaces.Forum;
import forum.server.domainlayer.interfaces.ForumSubject;
import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.exceptions.subject.SubjectAlreadyExistsException;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.exceptions.user.AlreadyConnectedException;
import forum.server.exceptions.user.NotConnectedException;
import forum.server.exceptions.user.NotRegisteredException;
import forum.server.exceptions.user.UserAlreadyExistsException;
import forum.server.exceptions.user.WrongPasswordException;
import forum.server.persistentlayer.pipe.JAXBpersistenceDataHandler;

/**
 * @author sepetnit
 *
 */
public class ForumImplTest extends TestCase {

	private Forum forum;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		JAXBpersistenceDataHandler.testMode();
		forum = new ForumImpl();
		
		SystemLogger.switchToOnlyFileLogMode();
		SystemLogger.logAMessage("setup", Level.FINE);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		JAXBpersistenceDataHandler.regularMode();
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#addForumSubject(forum.server.domainlayer.interfaces.ForumSubject)}.
	 */
	@Test
	public void testAddForumSubject() {
		ForumSubject tSubj = new ForumSubjectImpl("subj1", "test1");
		ForumSubject tSubj2 = new ForumSubjectImpl("subj2", "test2");
		ForumSubject tSubj3 = new ForumSubjectImpl("subj3", "test2");
		try {
			forum.addForumSubject(tSubj);
			assertTrue(forum.getForumSubjectByID(tSubj.getSubjectID()) != null);
			forum.addForumSubject(tSubj2);
			assertTrue(forum.getForumSubjectByID(tSubj2.getSubjectID()) != null);
			assertTrue(forum.getForumSubjectByID(tSubj.getSubjectID()) != null);
			try {
				assertTrue(forum.getForumSubjectByID(tSubj3.getSubjectID()) != null);
				fail("the subj3 subject found, but it wasn't added");
			}
			catch (SubjectNotFoundException e) {
				return;
			}
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		} catch (SubjectAlreadyExistsException e) {
			fail("for some reason one of the subjects already exists");
		} catch (SubjectNotFoundException e) {
			fail("one of the subjects wasn't added properly!");
		}		
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#login(java.lang.String, java.lang.String)}.
	 * 
	 * @pre: the registerUser method works properly
	 */
	@Test
	public void testLogin() {
		try {
			this.forum.registerUser(new RegisteredUserImpl("a", "123", "first", "last", "a@a"));
			this.forum.registerUser(new RegisteredUserImpl("c", "123", "first", "last", "c@a"));
			try {
				this.forum.login("a", "123");
				this.forum.registerUser(new RegisteredUserImpl("b", "123", "first", "last", "b@a"));
				this.forum.login("b", "123");
				try {
					this.forum.login("c", "124");						
					fail("login succeedded although the password of c was wrong");
				} 
				catch (WrongPasswordException e) {
					this.forum.login("c", "123");
					try {
						this.forum.login("c", "123");					
						fail("login succeedded although c is already connected to the forum");
					}
					catch (AlreadyConnectedException e1) {
						assertTrue(this.forum.getNumOfConnectedUsers() == 3);
						boolean[] tCheck = new boolean[3];
						for (int i = 0; i < 2; i++) // initialize the array
							tCheck[i] = false;
						for (RegisteredUser tUser : this.forum.getConnectedUsers()) {
							if (tUser.getUsername().equals("a"))
								tCheck[0] = true;
							else if (tUser.getUsername().equals("b"))
								tCheck[1] = true;
							else if (tUser.getUsername().equals("c"))
								tCheck[2] = true;
						}
						assertTrue(tCheck[0] &  tCheck[1] &  tCheck[2]);
					}
				}
			} catch (AlreadyConnectedException e) {
				fail("for some reason user a is already connected to the forum");
			} catch (NotRegisteredException e) {
				fail("the registerUser method failed");
			}
		} catch (UserAlreadyExistsException e) {
			fail("for some reason the new users already exist in the forum");
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		}
		catch (WrongPasswordException e) {
			fail("one of the given passwords is wrong");
		}
	}


	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#logout(java.lang.String)}.
	 * 
	 * @pre: the login method works properly
	 */
	@Test
	public void testLogout() {
		try {
			this.forum.registerUser(new RegisteredUserImpl("a", "123", "first", "last", "a@a"));
			this.forum.registerUser(new RegisteredUserImpl("c", "123", "first", "last", "c@a"));
			this.forum.login("a", "123");
			this.forum.logout("a");

			try {
				this.forum.logout("c");
				fail("c hasn't been connected, but the logout method was performed");
			}
			catch (NotConnectedException e) {
				for (RegisteredUser tUser : this.forum.getConnectedUsers()) {
					if (tUser.getUsername().equals("a") ||
							tUser.getUsername().equals("c"))
						fail("one of the users a or c has been stayed logged-in");
				}
				try {
					this.forum.logout("d");
					fail("d isn't ether connected or even registered, but the logout method was performed");
				}
				catch (NotConnectedException e1) {
					return;
				}
			}
		} catch (UserAlreadyExistsException e) {
			fail("for some reason the new users already exist in the forum");
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");

		} catch (AlreadyConnectedException e) {
			fail("for some reason user a is already connected to the forum");
		}
		catch (NotRegisteredException e) {
			fail("the registerUser method failed");
		}

		catch (WrongPasswordException e) {
			fail("one of the given passwords is wrong");
		} catch (NotConnectedException e) {
			fail("the login method failed, during a logout testing");
		}
	}


	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumImpl#registerUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRegisterUser() {
		try {
			this.forum.registerUser(new RegisteredUserImpl("a", "123", "first", "last", "aa@bb"));

			try {
				assertTrue(this.forum.getUserByUsername("a") != null);
				try {
					this.forum.registerUser(new RegisteredUserImpl("a", "124", "first1", "last1", "bb@bb"));
					fail("duplicate registration of the same user has succeeded");
				}
				catch (UserAlreadyExistsException e) {
					try {
						this.forum.registerUser(new RegisteredUserImpl("b", "123", "first", "last", "aa@bb"));
						fail("duplicate registration of a user with the same email has succeeded");
					}
					catch (UserAlreadyExistsException e1) {
						this.forum.registerUser(new RegisteredUserImpl("c", "123", "first", "last", "dd@ef"));
						assertTrue(this.forum.getUserByUsername("b") != null);
					}
				}
			}
			catch (NotRegisteredException e) {
				fail("the registration of a or c failed");
			}
		} catch (UserAlreadyExistsException e) {
			fail("for some reason, the desired user already exists in the system");
		} catch (JAXBException e) {
			fail("database error!");
		} catch (IOException e) {
			fail("database error!");
		}
	}
}
