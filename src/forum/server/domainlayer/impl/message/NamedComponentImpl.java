package forum.server.domainlayer.impl.message;

public class NamedComponentImpl
{

	private int messageNum;
	private String description;
	private String name;
	
	/**
	 * 
	 * @param desc - the description
	 * @param nm - the name
	 */
	public NamedComponentImpl(String desc, String nm)
	{
		this.messageNum = 0;
		this.description = desc;
		this.name = nm;
	}
	
	public NamedComponentImpl()
	{
		this.messageNum = 0;
	}

	public void decMessagesNumber() 
	{
		if (this.messageNum > 0)
			--this.messageNum;		
	}

	public String getDescription() 
	{
		return this.description;
	}

	public String getName() 
	{
		return this.name;
	}

	public int getNumOfMessages() 
	{
		return this.messageNum;
	}


	public void incMessagesNumber() 
	{
		++this.messageNum;
	}

	public void setDescription(String desc) 
	{
		this.description = desc;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
}
