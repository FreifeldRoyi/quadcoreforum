/**
 * 
 */
package forum.server.domainlayer.testing;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forum.server.domainlayer.impl.ForumImpl;
import forum.server.domainlayer.impl.ForumMessageImpl;
import forum.server.domainlayer.impl.ForumSubjectImpl;
import forum.server.domainlayer.impl.RegisteredUserImpl;
import forum.server.domainlayer.interfaces.ForumMessage;
import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.domainlayer.pipe.Controller;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.persistentlayer.ForumType;
import forum.server.persistentlayer.pipe.PersistenceFactory;
import forum.server.persistentlayer.pipe.persistenceDataHandler;

/**
 * @author sepetnit
 *
 */
public class ForumMessageImplTest {
	private RegisteredUser tUser; 
	private ForumMessage message1; 
	private ForumMessage message2;
	private ForumMessage message3;
	private ForumMessage message4;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		tUser = new RegisteredUserImpl("user1", "pass1", "a", "b", "mail@mymail.com");

		message1 = new ForumMessageImpl(tUser, "title1", "content1");
		message2 = new ForumMessageImpl(tUser, "title2", "content2");
		message3 = new ForumMessageImpl(tUser, "title3", "content3");
		message4 = new ForumMessageImpl(tUser, "title4", "content4");
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumMessageImpl#addReplyToMe()}.
	 */
	@Test	
	public void addMessageReplyData() {
		this.message1.addMessageReplyData(this.message2);

		try { // the message shouldn't be found in the database

			new ForumImpl().getMessageByID(this.message2.getMessageID());
			fail("The message shouldn't be found");
		}
		catch (MessageNotFoundException e) {
			return; // do nothing - the message should be found
		}

	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumMessageImpl#addReplyToMe()}.
	 */
	@Test	
	public void testAddReplyToMe() {

	
	}

	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumMessageImpl#findMessage()}.
	 */
	@Test		
	public void testFindMessage() {

		try {
			this.message1.findMessage(10);
			fail("test error!");
		} catch (MessageNotFoundException e) { // the message shouldn't be found
			try {
				this.message1.findMessage(3);
				fail("test error!");				
			} catch (MessageNotFoundException e2) {
				try {
					this.message1.findMessage(message2.getMessageID());
					fail("test error!");
				} catch (MessageNotFoundException e1) {
					try {
						this.message1.addMessageReplyData(this.message2);
						this.message2.addMessageReplyData(this.message3);
						this.message1.addMessageReplyData(this.message4);

						this.message1.findMessage(this.message2.getMessageID());
						this.message1.findMessage(this.message3.getMessageID());
						this.message2.findMessage(this.message3.getMessageID());
						this.message1.findMessage(this.message4.getMessageID());
					} catch (MessageNotFoundException e3) {
						fail("The message should be found after adding!");
					}
					finally {
						try {
							message2.findMessage(this.message4.getMessageID());
							fail("The message should be found after adding!");
						} catch (MessageNotFoundException e3) {
							return;
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Test method for {@link forum.server.domainlayer.impl.ForumMessageImpl#msgToString()}.
	 */
	@Test
	public void testMsgToString() {

		String msg1ToString = "user1" + "\n" + this.message1.getDate() + "\n" + this.message1.getTime() + "\n" +
		"title1" + "\n" + "content1" + "\nreplys\n";

		assertTrue(msg1ToString.equals(this.message1.msgToString()));


		this.message1.addMessageReplyData(this.message2);

		String msg2ToString = "user1" + "\n" + this.message1.getDate() + "\n" + this.message1.getTime() + "\n" +
		"title1" + "\n" + "content1" + "\nreplys\n" +

		"user1" + "\n" + this.message2.getDate() + "\n" + this.message2.getTime() + "\n" +
		"title2" + "\n" + "content2" + "\nreplys\n";

		assertTrue(msg2ToString.equals(this.message1.msgToString()));


	}
}
