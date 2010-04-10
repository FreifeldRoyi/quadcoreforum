package testing.domain.search;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sun.security.jca.GetInstance;

import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.domainlayer.message.ForumMessage;
import forum.server.domainlayer.search.SearchHit;
import forum.server.domainlayer.search.SearchIndex;

public class SearchIndexTest {

	SearchIndex se;
	@Before
	public void setUp() throws Exception {
		this.se = SearchIndex.getInstance();
	}

	/*@Test
	public void testGetDataByAuthor() {
		UIMessage tMsg = new ForumMessage(0, 0, "woohoo", "bummrt", new GregorianCalendar(), null);
		this.se.addMessage(tMsg);
		
		UIMessage tMsg2 = new ForumMessage(1, 1, "asd", "asdfasdf");
		Vector<SearchHit> tMessages = (Vector<SearchHit>)this.se.getDataByAuthor(new Long(0));
		assertEquals(tMessages.elementAt(0).getMessage(), tMsg);
		
		tMessages = (Vector<SearchHit>)this.se.getDataByAuthor(new Long(1));
		assertEquals(tMessages.elementAt(0).getMessage(), tMsg2);
	}*/
	
	@Test
	public void testAddAndGetMessage() {
		UIMessage tMsg1 = new ForumMessage(0,0,"msg1 bla","content1");
		UIMessage tMsg2 = new ForumMessage(1,0,"msg2 bla","content2");
		UIMessage tMsg3 = new ForumMessage(2,0,"msg3","content3 bla");
		UIMessage tMsg4 = new ForumMessage(3,1,"msg4","content4");
		
		this.se.addMessage(tMsg4);
		this.se.addMessage(tMsg3);
		this.se.addMessage(tMsg2);
		this.se.addMessage(tMsg1);
		
		String[] words1 = {"content1"};
		String[] words2 = {"content2"};
		String[] words3 = {"content3"};
		String[] words4 = {"content4"};
		String[] words5 = {"msg1"};
		String[] words6 = {"bla"};
		
		/* Test get by content */
		Vector<SearchHit> tSHCol1 = (Vector<SearchHit>) this.se.getDataByContent(words1); //should hold tMsg1
		Vector<SearchHit> tSHCol2 = (Vector<SearchHit>) this.se.getDataByContent(words2); //should hold tMsg2
		Vector<SearchHit> tSHCol3 = (Vector<SearchHit>) this.se.getDataByContent(words3); //should hold tMsg3
		Vector<SearchHit> tSHCol4 = (Vector<SearchHit>) this.se.getDataByContent(words4); //should hold tMsg4
		Vector<SearchHit> tSHCol5 = (Vector<SearchHit>) this.se.getDataByContent(words5); //should hold tMsg1
		Vector<SearchHit> tSHCol6 = (Vector<SearchHit>) this.se.getDataByContent(words6); //should hold tMsg1 tMsg2 tMsg3
		
		Vector<UIMessage> tMSGCol1 = new Vector<UIMessage>();
		Vector<UIMessage> tMSGCol2 = new Vector<UIMessage>();
		Vector<UIMessage> tMSGCol3 = new Vector<UIMessage>();
		Vector<UIMessage> tMSGCol4 = new Vector<UIMessage>();
		Vector<UIMessage> tMSGCol5 = new Vector<UIMessage>();
		Vector<UIMessage> tMSGCol6 = new Vector<UIMessage>();
		
		for (SearchHit tSh : tSHCol1)
			tMSGCol1.add(tSh.getMessage());
		for (SearchHit tSh : tSHCol2)
			tMSGCol2.add(tSh.getMessage());
		for (SearchHit tSh : tSHCol3)
			tMSGCol3.add(tSh.getMessage());
		for (SearchHit tSh : tSHCol4)
			tMSGCol4.add(tSh.getMessage());
		for (SearchHit tSh : tSHCol5)
			tMSGCol5.add(tSh.getMessage());
		for (SearchHit tSh : tSHCol6)
			tMSGCol6.add(tSh.getMessage());
		
		assertTrue(tMSGCol1.contains(tMsg1));
		assertTrue(tMSGCol2.contains(tMsg2));
		assertTrue(tMSGCol3.contains(tMsg3));
		assertTrue(tMSGCol4.contains(tMsg4));
		assertTrue(tMSGCol5.contains(tMsg1));
		assertTrue(tMSGCol6.contains(tMsg1) && tMSGCol6.contains(tMsg2) && tMSGCol6.contains(tMsg3));
		
		/* Test get by author */
		Vector<SearchHit> tSHCol7 = (Vector<SearchHit>) this.se.getDataByAuthor(new Long(0)); //should contain tMsg1 tMsg2 tMsg3
		Vector<SearchHit> tSHCol8 = (Vector<SearchHit>) this.se.getDataByAuthor(new Long(1)); //should contain tMsg4
		
		Vector<UIMessage> tMSGCol7 = new Vector<UIMessage>();
		Vector<UIMessage> tMSGCol8 = new Vector<UIMessage>();
		
		for (SearchHit tSh : tSHCol7)
			tMSGCol7.add(tSh.getMessage());
		for (SearchHit tSh : tSHCol8)
			tMSGCol8.add(tSh.getMessage());
		
		assertTrue(tMSGCol7.contains(tMsg1) && tMSGCol7.contains(tMsg2) && tMSGCol7.contains(tMsg3));
		assertTrue(tMSGCol8.contains(tMsg4));
	}

}
