package server;

public interface ForumMessage 
{
	/*Setters*/
	public void setMessageTitle (String t);
	public void setMessageContent (String body);
	
	/*Getters*/
	public String getMessageTitle();
	public String getMessageContent();
	public int getTime(); //will be changed!!!
	public int getDate(); //will be changed!!!
}

/**
 * TODO don't forget to change time/date return type.
 * TODO write proper JavaDoc
 */
