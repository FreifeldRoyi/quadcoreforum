/**
 * 
 */
package forum.server.domainlayer.search;

import java.util.Arrays;
import java.util.Collection;

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
	//private ForumFacade facade;
	
	public SearchAgent() throws DatabaseRetrievalException, DatabaseUpdateException
	{
		this.indexer = SearchIndex.getInstance();
	//	this.facade = MainForumLogic.getInstance();
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
		Long tUsrID = null;
		
		/*try 
		{
			tUsrID = new Long(this.facade.getMemberIdByUsername(username));
		} 
		catch (NotRegisteredException e) 
		{
			toReturn = new SearchHit[0];
		} 
		catch (DatabaseRetrievalException e) 
		{
			toReturn = new SearchHit[0];
		}*/
		
	
		if (toReturn == null & tUsrID != null) //was used when the code in try-catch block wasn't a comment
		{
			Collection<SearchHit> tVolatileHits = this.indexer.getDataByAuthor(tUsrID);
			if (-1 < from && from < to && from < tVolatileHits.size())
			{
				if (tVolatileHits.size() < to)
					to = tVolatileHits.size();
	
				toReturn = new SearchHit[to];
				SearchHit[] tVolHitsArr = tVolatileHits.toArray(new SearchHit[0]);
				
				int tIndex = from;
				while (tIndex != to)
				{
					toReturn[tIndex - from] = tVolHitsArr[tIndex];
					++tIndex;
				}
				
				// TODO sort messages by date - needs a certain parser for the string
			}
		}
		
		else
		{
			toReturn = new SearchHit[0];
		}
		
		return toReturn;
	}

	/**
	 * @see SearchEngine#searchByContent(String, int, int)
	 */
	@Override
	public SearchHit[] searchByContent(String phrase, int from, int to) 
	{
		SearchHit[] toReturn = null;
		Collection<SearchHit> tSearchHitUnsorted = this.indexer.getDataByContent(phrase.split(" "));
		SearchHit[] tSearchHitSorted = this.sortByHitScore(tSearchHitUnsorted);
		
		if (-1 < from && from < to && from < tSearchHitSorted.length)
		{
			if (tSearchHitSorted.length < to)
				to = tSearchHitSorted.length;
			
			toReturn = new SearchHit[to];
			
			int tIndex = from;
			while (tIndex != to)
			{
				toReturn[tIndex - from] = tSearchHitSorted[tIndex];
				++tIndex;
			}
		}
		else
		{
			toReturn = new SearchHit[0];
		}
		
		return toReturn;
	}
	
	private SearchHit[] sortByHitScore(Collection<SearchHit> hits)
	{
		SearchHit[] toReturn = hits.toArray(new SearchHit[0]);
		
		Arrays.sort(toReturn, new SearchHitComparator());
				
		return toReturn;
	}
}
