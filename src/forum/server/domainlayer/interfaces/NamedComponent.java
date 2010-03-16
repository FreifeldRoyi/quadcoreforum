package forum.server.domainlayer.interfaces;

public interface NamedComponent 
{
	/* Setters */

	/**
	 * Sets the name of this component to be the given one
	 * 
	 * @param name
	 * 		The new name of this component
	 */
	public void setName(String name);
	
	/**
	 * Sets the description of this component to be the given one
	 * 
	 * @param desc
	 * 		The new description of this component
	 */
	public void setDescription (String desc);
	
	
	/* Getters */
	
	/**
	 * Returns the name of this component
	 */
	public String getName();	
	
	/**
	 * 
	 * @return
	 * 		The description of this component
	 */
	public String getDescription();	
	
	/**
	 * 
	 * @return
	 * 		The total number of this component's messages (performs a recursive counting)
	 */
	public int getNumOfMessages();
	
	/* Methods */

	/**
	 * Increments the messages number of this component
	 */
	public void incMessagesNumber();
	
	/**
	 * Decrease the messages number of this component
	 */
	public void decMessagesNumber();
	

}