package forum.server.persistanceInterfaces;

public interface NamedComponentType 
{
	/*Setters*/
	public void setName(String name);
	public void setDescription (String desc);
	public void incNumberOfMessages();
	public void decNumberOfMessages();
	
	/*Getters*/
	public String getName();
	public String getDescription();
	public int getNumOfMessages();
}

/**
 * TODO  write proper JavaDoc
 */