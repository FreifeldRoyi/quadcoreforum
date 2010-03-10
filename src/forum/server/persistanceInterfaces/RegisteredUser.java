package forum.server.persistanceInterfaces;

public interface RegisteredUser 
{
	/* Getters */
	
	public String getUsername();
	public String getPassword();
	public String getPrivateName();
	public String getLastName();
	public String getEMail();
	public int getPostedMsgNumber();
	
	/* Setters */
	
	//public void setUsername(String un); I don't think it is needed 
	
	public void setPassword(String pass);
	public void setPrivateName(String prvName);
	public void setLastName(String lastName);
	public void setPostedMsgNumber(int num); //in case of server rollback, we'll want a way to nullify the field
}

/**
 * TODO write proper JavaDoc for RegisteredUser
 * TODO add methods
 */
