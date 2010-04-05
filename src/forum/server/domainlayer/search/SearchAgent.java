/**
 * 
 */
package forum.server.domainlayer.search;

import java.util.Collection;
import java.util.Vector;

import forum.server.domainlayer.ForumFacade;
import forum.server.domainlayer.MainForumLogic;
import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * @author Royi Freifeld <br></br>
 * This class is the one responsible for searched data mining.
 * Will search on volatile memory and data base.
 */
public class SearchAgent implements SearchEngine
{
	private SearchIndex indexer;
	private ForumFacade facade;
	
	public SearchAgent() throws DatabaseRetrievalException, DatabaseUpdateException
	{
		this.indexer = SearchIndex.getInstance();
		this.facade = MainForumLogic.getInstance();
	}

	
	/**
	 * @see SearchEngine#addData(UIMessage)
	 */
	@Override
	public void addData(UIMessage msg) 
	{
		this.indexer.addMessage(msg);
	}

	/**
	 * @see SearchEngine#searchByAuthor(String, int, int)
	 */
	@Override
	public SearchHit[] searchByAuthor(String username, int from, int to) 
	{
		SearchHit[] toReturn = null;
		Vector<SearchHit> tHits = new Vector<SearchHit>();
		Collection<UIMessage> tDBMsg = null;
		Long tUsrID = null;
		long tPrimUsrID;
		
		try 
		{
			tPrimUsrID = this.facade.getMemberIdByUsername(username);
			tUsrID = new Long(tPrimUsrID);
			tDBMsg = this.facade.getMessagesByUserID(tPrimUsrID);
		} 
		catch (NotRegisteredException e) 
		{
			toReturn = new SearchHit[0];
		} 
		catch (DatabaseRetrievalException e) 
		{
			toReturn = new SearchHit[0];
		}
		
		if (toReturn == null & tUsrID != null)
		{
			Collection<SearchHit> tVolatileHits = this.indexer.getDataByAuthor(tUsrID);

			for (UIMessage tMsg : tDBMsg)
			{
				SearchHit tSe = new SearchHit(tMsg,1);
				if (!tVolatileHits.contains(tSe))
				{
					tVolatileHits.add(tSe);
				}
			}
			
			// TODO sort messages by date - needs a certain parser to the string
			
			toReturn = tVolatileHits.toArray(new SearchHit[0]);
		}
		
		return toReturn;
	}

	/**
	 * @see SearchEngine#searchByContent(String, int, int)
	 */
	@Override
	public SearchHit[] searchByContent(String phrase, int from, int to) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private Vector<String> ReservedWordsRemoval(String[] words)
	{
		Vector<String> toReturn = new Vector<String>();
		Vector<String> tReservedWords = this.indexer.getReservedWords();
		
		for (int tIndex = 0; tIndex < words.length; ++tIndex)
		{
			if (!tReservedWords.contains(words[tIndex]))
				toReturn.add(words[tIndex]);
		}
		
		return toReturn;				
	}
	
	private Collection<UIMessage> getMessagesByContentFromDB(String[] words)
	{
		
	}
	
}
