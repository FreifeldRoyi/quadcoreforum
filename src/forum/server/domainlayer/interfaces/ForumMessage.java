package forum.server.domainlayer.interfaces;

public interface ForumMessage
{
	/*Setters*/
	
	public void setMessageTitle (String t);	
	public void setMessageContent (String body);
	
	/*Getters*/
	public long getMessageID();
	public String getMessageTitle();
	public String getMessageContent();
	public String getTime();
	public String getDate();
	public RegisteredUser getAuthor();
	
	/*Methods*/
	public void addReplyToMe(ForumMessage fm);	
	
	public String msgToString();
}

/**
 * TODO don't forget to change time/date return type.
 * TODO write proper JavaDoc
 * TODO private member timeDate of type GregorianCalendar
 */
