package forum.server.domainlayer.interfaces;

public interface RegisteredUser 
{
	/* Getters */
	
	public String getUsername();
	public String getPassword();
	public String getPrivateName(); // what is private????????? first?
	public String getLastName();
	public String getEMail();
	public int getPostedMsgNumber();
	
	/* Setters */
	
	//public void setUsername(String un); I don't think it is needed 
	
	public void setPassword(String pass);
	public void setPrivateName(String prvName);
	public void setLastName(String lastName);
	public void setPostedMsgNumber(int num); //in case of server rollback, we'll want a way to nullify the field
	
	/*Methods*/
	public void incPostedMsgNum();
}

/**
 * TODO write proper JavaDoc for RegisteredUser
 * TODO add methods
 */
