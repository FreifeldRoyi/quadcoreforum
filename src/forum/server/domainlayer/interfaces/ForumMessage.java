package forum.server.domainlayer.interfaces;

import java.text.SimpleDateFormat;

public interface ForumMessage extends NamedComponentType
{
	/*Setters*/
	public void setMessageTitle (String t);
	public void setMessageContent (String body);
	
	/*Getters*/
	public String getMessageTitle();
	public String getMessageContent();
	public SimpleDateFormat getTime(); //will be changed!!!
	public SimpleDateFormat getDate(); //will be changed!!!
	
	public RegisteredUser getAuthor();
	
	
	
}

/**
 * TODO don't forget to change time/date return type.
 * TODO write proper JavaDoc
 */
