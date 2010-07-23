/** 
 *  
 */ 
package testing.domain.search; 

import java.io.File;
import java.util.Vector; 

import junit.framework.TestCase;

import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;
import org.junit.*;


import forum.server.Settings;
import forum.server.domainlayer.interfaces.UIMessage; 
import forum.server.domainlayer.message.ForumMessage; 
import forum.server.domainlayer.search.SearchHit; 
import forum.server.domainlayer.search.cmpssearch.CompassAdapter;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;

/** 
 * @author Freifeld Royi 
 * 
 */ 
public class SearchAgentTest extends TestCase 
{ 
	private class MessagesVector extends Vector<UIMessage> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -113058883922451417L;

		@Override
		public boolean contains(Object o) {
			if (o != null && o instanceof ForumMessage)
				for (UIMessage tCurrentMessage : this) {
					if (tCurrentMessage.equals((ForumMessage)o))
						return true;
				}
			return false;			
		}
	}
	
	private CompassAdapter sa; 

	/** 
	 * @throws java.lang.Exception 
	 */ 
	@Before 
	public void setUp() throws Exception { 
		File tFile = new File("src/testing/domain/search/compassSettings.xml");
		Settings.switchToTestMode();
		CompassConfiguration tConf = CompassConfigurationFactory.newConfiguration().configure(tFile);               
	
		Compass compass = tConf.buildCompass();
	
		this.sa = new CompassAdapter(compass);
	} 
	
	/** 
	 * @throws java.lang.Exception 
	 */ 
	@After 
	public void tearDown() throws Exception  
	{ 
//		Settings.switchToRegularMode();
	} 

	@Test 
	public void testSearchByAuthorAndContent() throws MemberAlreadyExistsException, DatabaseUpdateException  
	{  
		UIMessage tMsg1 = new ForumMessage(0,0,"msg1 bla","content1", -1); 
		UIMessage tMsg2 = new ForumMessage(1,0,"msg2 bla","content2", -1); 
		UIMessage tMsg3 = new ForumMessage(2,0,"msg3","content3 bla", -1); 
		UIMessage tMsg4 = new ForumMessage(3,1,"msg4","content4", -1); 

		this.sa.addData(tMsg1); 
		this.sa.addData(tMsg2); 
		this.sa.addData(tMsg3); 
		this.sa.addData(tMsg4); 

		//Search by author 
		MessagesVector tMSGCol1 = new MessagesVector(); 
		MessagesVector tMSGCol2 = new MessagesVector(); 
		MessagesVector tMSGCol3 = new MessagesVector(); 
		MessagesVector tMSGCol4 = new MessagesVector(); 

		SearchHit[] tSHAuthor1 = this.sa.searchByAuthor(0, 0, 10); //should get all messages 
		SearchHit[] tSHAuthor2 = this.sa.searchByAuthor(0, 0, 1); //should get only one message 
		SearchHit[] tSHAuthor3 = this.sa.searchByAuthor(1, 0, 1); //should get the only message 
		SearchHit[] tSHAuthor4 = this.sa.searchByAuthor(1, 4, 10); //should get no messages 

		
		for (SearchHit tSh : tSHAuthor1) 
			tMSGCol1.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHAuthor2) 
			tMSGCol2.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHAuthor3) 
			tMSGCol3.add(tSh.getMessage()); 
		for (SearchHit tSh : tSHAuthor4) 
			tMSGCol4.add(tSh.getMessage()); 

	
		assertTrue(tMSGCol1.contains(tMsg1) && tMSGCol1.contains(tMsg2) && tMSGCol1.contains(tMsg3) && tMSGCol1.size() == 3); 
		assertTrue(tMSGCol2.size() == 1 && (tMSGCol2.contains(tMsg1) || tMSGCol2.contains(tMsg2) || tMSGCol2.contains(tMsg3))); 
		assertTrue(tMSGCol3.size() == 1 && tMSGCol3.contains(tMsg4)); 
		assertTrue(tMSGCol4.size() == 0);


		/* 
		 * Search by content 
		 *  
		 * I will not test the logic again, it was already tested in SearchIndexTest.java 
		 * Instead I'll validate that the returned array is really sorted 
		 */ 
		String words1 = "content1"; //should return tMsg1 
		String words2 = "content2"; //should return tMsg2 
		String words3 = "content3"; //should return tMsg3 
		String words4 = "content4"; //should return tMsg4 
		String words5 = "msg1"; //should return tMsg1 
		String words6 = "bla"; //should return tMsg1 tMsg2 tMsg3 with no difference in score 

		//test logic operation 
		String words7 = "(content3 AND bla)"; //should return tMsg1 tMsg2 tMsg3 where tMsg3 has the higher score 
		String words8 = "(content3 OR content4)"; //should return tMsg3 tMsg4 with no difference in score 
		String words9 = "(content3 AND bla OR content4)"; //should return tMsg1 tMsg2 tMsg3 tMsg4 where tMsg3 has a higher score thus being the first in the array 

		SearchHit[] tSHContent1 = this.sa.searchByContent(words1, 0, 10); 
		SearchHit[] tSHContent2 = this.sa.searchByContent(words2, 0, 10); 
		SearchHit[] tSHContent3 = this.sa.searchByContent(words3, 0, 10); 
		SearchHit[] tSHContent4 = this.sa.searchByContent(words4, 0, 10); 
		SearchHit[] tSHContent5 = this.sa.searchByContent(words5, 0, 10); 
		SearchHit[] tSHContent6 = this.sa.searchByContent(words6, 0, 10); 
		SearchHit[] tSHContent7 = this.sa.searchByContent(words7, 0, 10); 
		SearchHit[] tSHContent8 = this.sa.searchByContent(words8, 0, 10); 
		SearchHit[] tSHContent9 = this.sa.searchByContent(words9, 0, 10); 


		
		assertTrue(tSHContent1.length == 1 && tSHContent1[0].getMessage().equals(tMsg1)); 

		assertTrue(tSHContent2.length == 1 && tSHContent2[0].getMessage().equals(tMsg2)); 

		assertTrue(tSHContent3.length == 1 && tSHContent3[0].getMessage().equals(tMsg3)); 
		assertTrue(tSHContent4.length == 1 && tSHContent4[0].getMessage().equals(tMsg4)); 
		assertTrue(tSHContent5.length == 1 && tSHContent5[0].getMessage().equals(tMsg1)); 

		MessagesVector tMSGByContHelpVec1 = new MessagesVector(); //help for tSHContent6 
		MessagesVector tMSGByContHelpVec2 = new MessagesVector(); //help for tSHContent7 
		MessagesVector tMSGByContHelpVec3 = new MessagesVector(); //help for tSHContent8 
		MessagesVector tMSGByContHelpVec4 = new MessagesVector(); //help for tSHContent9 


		for (SearchHit tSH : tSHContent6) 
			tMSGByContHelpVec1.add(tSH.getMessage()); 
		for (SearchHit tSH : tSHContent7) 
			tMSGByContHelpVec2.add(tSH.getMessage()); 
		for (SearchHit tSH : tSHContent8) 
			tMSGByContHelpVec3.add(tSH.getMessage()); 
		for (SearchHit tSH : tSHContent9) 
			tMSGByContHelpVec4.add(tSH.getMessage()); 

		assertTrue(tMSGByContHelpVec1.size() == 3 &&  
				tMSGByContHelpVec1.contains(tMsg1) &&  
				tMSGByContHelpVec1.contains(tMsg2) &&  
				tMSGByContHelpVec1.contains(tMsg3)); 


		assertTrue(tMSGByContHelpVec2.size() == 1 && 
				tSHContent7[0].getMessage().equals(tMsg3)); 
//				tMSGByContHelpVec2.contains(tMsg1) && 
//				tMSGByContHelpVec2.contains(tMsg2)); 

		
		assertTrue(tMSGByContHelpVec3.size() == 2 &&  
				tMSGByContHelpVec3.contains(tMsg3) &&  
				tMSGByContHelpVec3.contains(tMsg4));  


		System.out.println(tMSGByContHelpVec4.size());
		assertTrue(tMSGByContHelpVec4.size() == 1 && 
				tMSGByContHelpVec4.elementAt(0).equals(tMsg3)); 
/*				tSHContent9[0].getMessage().equals(tMsg3) && 
				tMSGByContHelpVec4.contains(tMsg1) && 
				tMSGByContHelpVec4.contains(tMsg2) && 
				tMSGByContHelpVec4.contains(tMsg4)); 
*/		
	} 
} 