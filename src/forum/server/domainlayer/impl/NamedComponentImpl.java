package forum.server.domainlayer.impl;

import forum.server.domainlayer.interfaces.NamedComponent;

public class NamedComponentImpl implements NamedComponent 
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

	
	@Override
	public void decMessagesNumber() 
	{
		if (this.messageNum > 0)
			--this.messageNum;		
	}

	@Override
	public String getDescription() 
	{
		return this.description;
	}

	@Override
	public String getName() 
	{
		return this.name;
	}

	@Override
	public int getNumOfMessages() 
	{
		return this.messageNum;
	}

	@Override
	public void incMessagesNumber() 
	{
		++this.messageNum;
	}

	@Override
	public void setDescription(String desc) 
	{
		this.description = desc;
	}

	@Override
	public void setName(String name) 
	{
		this.name = name;
	}
}
