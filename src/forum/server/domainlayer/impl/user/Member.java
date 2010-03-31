/**
 * 
 */
package forum.server.domainlayer.impl.user;

import java.util.*;

import forum.server.domainlayer.impl.interfaces.UIMember;

/**
 * @author Freifeld Royi
 *
 */
public class Member extends User implements UIMember
{
	private String userName;
	private String password;
	private String privateName;
	private String lastName;
	private String email;
	private int postedMsgNum;
	
	/*Constructor*/
	public Member(long userID, String username, String password, String lastName, String firstName, String email,
			Collection<Permission> permissions)
	{
		super(userID, permissions);
		this.userName = username;
		this.password = password;
		this.privateName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.postedMsgNum = 0;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getLastName() {
		return this.lastName;
	}

	public String getUsername() {
		return this.userName;
	}

	public String getPassword() {
		return this.password;
	}

	public int getPostedMsgNumber() {
		return this.postedMsgNum;
	}

	public String getFirstName() {
		return this.privateName;
	}


	public void setLastName(String lstNm) {
		this.lastName = lstNm;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public void setPostedMsgNumber(int num) {
		this.postedMsgNum = num;
	}

	public void setPrivateName(String prvName) {
		this.privateName = prvName;
	}

	public void incPostedMsgNum() {
		++this.postedMsgNum;
	}
	
	public String toString()
	{
		return this.userName;
	}

}
