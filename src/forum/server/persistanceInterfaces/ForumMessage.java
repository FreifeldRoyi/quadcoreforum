package forum.server.persistanceInterfaces;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public interface ForumMessage 
{
	/*Setters*/
	public void setMessageTitle (String t);
	public void setMessageContent (String body);
	
	/*Getters*/
	public String getMessageTitle();
	public String getMessageContent();
	public SimpleDateFormat getTime(); //will be changed!!!
	public SimpleDateFormat getDate(); //will be changed!!!
	
	public String getAuthor();
}

/**
 * TODO don't forget to change time/date return type.
 * TODO write proper JavaDoc
 */
