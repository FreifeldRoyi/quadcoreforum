package forum.server.domainlayer.interfaces;

public interface RegisteredUser 
{	
	/* Getters */
	
	/**
	 * 
	 * @return
	 * 		This user's username
	 */
	public String getUsername();
	
	/**
	 * 
	 * @return
	 * 		This user's password
	 */
	public String getPassword();
	
	/**
	 * 
	 * @return
	 * 		This user's private name (first name)
	 */
	public String getPrivateName();
	
	/**
	 * 
	 * @return
	 * 		This user's last name
	 */
	public String getLastName();
	
	/**
	 * 
	 * @return
	 * 		This user's e-mail address
	 */
	public String getEMail();
	
	/**
	 * 
	 * @return
	 * 		The total number of messages, posted by this user
	 */
	public int getPostedMsgNumber();

	/* Setters */	

	/**
	 * Updates the user password, to be the given one
	 * 
	 * @param pass
	 * 		A new password which should be given to this user
	 */
	public void setPassword(String pass);
	
	/**
	 * 
	 * Updates the user first name, to be the given one
	 * 
	 * @param prvName
	 * 		The updated first name to which the user first name should be changed
	 */
	public void setPrivateName(String prvName);

	/**
	 * 
	 * Updates the user last name, to be the given one
	 * 
	 * @param lastName
	 * 		The updated last name to which the user last name should be changed
	 */
	public void setLastName(String lastName);
	
	/**
	 * Sets the posted messages number (maybe we'll want to nullify the field, in case of server rollback)
	 * 
	 * @param num
	 * 		The new posted messages number
	 */
	public void setPostedMsgNumber(int num);
	
	/* Methods */

	/**
	 * Increases the posted messages number of this user, by one
	 */
	public void incPostedMsgNum();
}