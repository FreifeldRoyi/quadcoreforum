/**
 * 
 */
package forum.server.domainlayer.search.basicsearch;

/**
 * @author Freifeld Royi
 *
 */
public class Pair<K,V>
{
	private K first;
	private V second;
	
	public Pair(K first, V second)
	{
		this.first = first;
		this.second = second;
	}
	
	public K getFirst()
	{
		return this.first;
	}
	
	public V getSecond()
	{
		return this.second;
	}
	
	public void setFirst(K newFirst)
	{
		this.first = newFirst;
	}
	
	public void setSecond(V newSecond)
	{
		this.second = newSecond;
	}
	
	public String toString()
	{
		return "(" + this.first.toString() + ", " + this.second.toString() + ")"; 
	}
}