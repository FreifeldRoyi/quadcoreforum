package forum.server.domainlayer.interfaces;

public interface NamedComponent 
{
	/* Setters */
	
	public void setName(String name);
	public void setDescription (String desc);
	public void incMessagesNumber();
	public void decMessagesNumber();
	
	/* Getters */
	
	public String getName();	
	public String getDescription();	
	public int getNumOfMessages();
}

/**
 * TODO  write proper JavaDoc
 */