/**
 * 
 */
package forum.server.domainlayer.impl;

import forum.server.domainlayer.interfaces.RegisteredUser;

/**
 * @author Freifeld Royi
 *
 */
public class RegisteredUserImpl implements RegisteredUser 
{
	private String userName;
	private String password;
	private String privateName;
	private String lastName;
	private String eMail;
	private int postedMsgNum;
	
	/*Constructor*/
	public RegisteredUserImpl(String usrNm, String pass, String prvNm, String lstNm, String eml)
	{
		this.userName = usrNm;
		this.password = pass;
		this.privateName = prvNm;
		this.lastName = lstNm;
		this.eMail = eml;
		this.postedMsgNum = 0;
	}
	
	@Override
	public String getEMail() 
	{
		return this.eMail;
	}
	
	@Override
	public String getLastName() 
	{
		return this.lastName;
	}

	@Override
	public String getPassword() 
	{
		return this.password;
	}

	@Override
	public int getPostedMsgNumber() 
	{
		return this.postedMsgNum;
	}

	@Override
	public String getPrivateName() 
	{
		return this.privateName;
	}

	@Override
	public String getUsername() 
	{
		return this.userName;
	}

	@Override
	public void setLastName(String lstNm) 
	{
		this.lastName = lstNm;
	}

	@Override
	public void setPassword(String pass) 
	{
		this.password = pass;
	}

	@Override
	public void setPostedMsgNumber(int num) 
	{
		this.postedMsgNum = num;
	}

	@Override
	public void setPrivateName(String prvName) 
	{
		this.privateName = prvName;
	}

	@Override
	public void incPostedMsgNum() 
	{
		++this.postedMsgNum;
	}
	
	public String toString()
	{
		return this.userName;
	}
}
