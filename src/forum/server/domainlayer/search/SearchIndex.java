/**

 */
package forum.server.domainlayer.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import forum.server.domainlayer.interfaces.UIMessage;

/**
 * @author Royi Freifeld <br></br>
 * This class is responsible for the search engine's data.
 * While searching for an item, it will add it to the data tables
 * managed by this class, if it is not already in it, thus enabling
 * faster access for later search.
 * All data is saved on volatile memory only, which means every search data
 * saved after a computer restart, will be lost.
 */
public class SearchIndex
{
	private Vector<String> reserved_words;
	
	long wordIdNumber; //word indexing count
	private Map<String, Long> words; //word <-> wordID
	private Map<Long, Collection<Long>> relations; //wordID <-> collection of msgIDs
	private Map<Long, UIMessage> items; //msgID <-> msg
	private Map<Long, Collection<Long>> users; //usrID <-> collection of msgIDs
	
	private static SearchIndex INDEXER;
	
	public static SearchIndex getInstance()
	{
		if (SearchIndex.INDEXER == null)
			SearchIndex.INDEXER = new SearchIndex();
		
		return SearchIndex.INDEXER;
	}
	
	private SearchIndex()
	{
		this.reserved_words = new Vector<String>();
		this.words = new HashMap<String, Long>();
		this.relations = new HashMap<Long, Collection<Long>>();
		this.items = new HashMap<Long, UIMessage>();
		this.users = new HashMap<Long, Collection<Long>>();
		
		//special search operators
		this.reserved_words.add("or");
		this.reserved_words.add("and");
		this.reserved_words.add("+");
		this.reserved_words.add("-");	
		
		//nullify the number of word index
		this.wordIdNumber = 0;		
	}
	
	/**
	 * Adds data to the search tables.
	 * 
	 * @param msg - the message item to add
	 */
	public void addMessage(UIMessage msg)
	{
		String[] tContentSplit = msg.getContent().split(" ");
		Vector<String> tNoReservedWords = new Vector<String>();
		
		if (!this.items.containsKey(msg.getID()))
		{
			//removal of reserved words
			for (int tIndex = 0; tIndex < tContentSplit.length; ++tIndex)
			{
				if (!this.reserved_words.contains(tContentSplit[tIndex]))
				{
					tNoReservedWords.add(tContentSplit[tIndex]);
				}
			}
		
			//add words and relation bindings
			for (int tIndex = 0; tIndex < tNoReservedWords.size(); ++tIndex)
			{
				this.addWord(tNoReservedWords.elementAt(tIndex), msg.getID());
			}
			
			//message ID <-> message binding
			this.items.put(new Long (msg.getID()), msg);
			
			//user name <-> message ID binding
			Long tUsrID = new Long(msg.getAuthorID());
			Long tMsgID = new Long(msg.getID());
			if (this.users.containsKey(tUsrID))
			{
				Collection<Long> tCol = this.users.get(tUsrID);
				tCol.add(tMsgID);
			}
			else
			{
				Collection<Long> tCol = new Vector<Long>();
				tCol.add(tMsgID);
				this.users.put(tUsrID, tCol);
			}				
		}
	}
	
	/**
	 * Returns a collection of search hits, containing the messages.
	 * The hit score is measured according to the number
	 * of words appearing in the words array (given as a parameter) 
	 * and in the message's content
	 * 
	 * @param words - the array of words to search
	 * @return a collection of search hits 
	 */
	public Collection<SearchHit> getDataByContent(String[] wordsArr)
	{
		Vector<SearchHit> toReturn = new Vector<SearchHit>();
		HashMap<UIMessage,Double> tHitTimes = new HashMap<UIMessage, Double>();
		
		for (int tIndex = 0; tIndex < wordsArr.length; ++tIndex)
		{
			if (this.words.containsKey(wordsArr[tIndex]))
			{
				//get collection of UI messages
				//for every item in collection do
					//get UIMessage from items hash map
					//if UI message is in tHitTimes then
						//create new Double object with value + 1
					//else
						//create new Double object with value = 1
				Collection<Long> tMsgID = this.relations.get(this.words.get(wordsArr[tIndex]));
				for (Long tUImsgID : tMsgID)
				{
					UIMessage tUIMsg = this.items.get(this.relations.get(tUImsgID));
					Double tValue;
					
					if (tHitTimes.containsKey(tUIMsg))
					{
						tValue = new Double(tHitTimes.get(tUIMsg).doubleValue() + 1);
					}
					else
					{
						tValue = new Double(1);
					}
					
					tHitTimes.put(tUIMsg, tValue);
				}
			}
		}
		
		Set<Map.Entry<UIMessage, Double>> tMapping = tHitTimes.entrySet();
		Iterator<Map.Entry<UIMessage, Double>> tItr = tMapping.iterator();
		
		while (tItr.hasNext())
		{
			Map.Entry<UIMessage, Double> tMe = (Map.Entry<UIMessage, Double>)tItr.next();
			SearchHit sh = new SearchHit(tMe.getKey(), tMe.getValue().doubleValue());
			toReturn.add(sh);
		}
		
		return toReturn;		
	}
	
	/**
	 * Returns a collection of search hits, containing the messages
	 * written by the user who's ID is the same as specified in @param usrID
	 * The score is the same for every hit and is set to 1
	 * 
	 * @param usrID - the user's ID given in a Long object format
	 * @return a collection of search hits
	 */
	public Collection<SearchHit> getDataByAuthor(Long usrID)
	{
		Vector<SearchHit> toReturn = new Vector<SearchHit>();
		Collection<Long> tValues = this.users.get(usrID);
		
		for (Long tVal : tValues)
		{
			toReturn.add(new SearchHit(this.items.get(tVal),1));
		}
		
		return toReturn;
	}
	
	/**
	 * @return a copy of the reserved words
	 */
	public Vector<String> getReservedWords()
	{
		Vector<String> toReturn = new Vector<String>();
		for (String tStr : this.reserved_words)
		{
			toReturn.add(tStr.substring(0));
		}
		return toReturn;
	}
	
	/**
	 * Adding a word to the data tables
	 * creating word <-> word ID binding
	 * and word ID <-> message ID
	 * 
	 * 
	 * @param word - the word to add
	 * @param msgID - the message ID that will be bound to the word
	 */
	private void addWord(String word, Long msgID)
	{
		if (!this.words.containsKey(word))
		{
			++this.wordIdNumber;
			this.words.put(word, this.wordIdNumber);
		}
		
		Long tWordID = this.words.get(word); 
		
		if (this.relations.containsKey(tWordID))
		{
			Collection<Long> tColl = this.relations.get(tWordID);
			tColl.add(msgID);
		}
		else
		{
			Vector<Long> toAdd = new Vector<Long>();
			toAdd.add(msgID);
			this.relations.put(tWordID, toAdd);
		}
	}
}

// TODO delete unneeded comments
// TODO get data by content - support also boolean operators
