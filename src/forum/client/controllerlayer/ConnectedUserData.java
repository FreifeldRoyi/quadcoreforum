/**
 * 
 */
package forum.client.controllerlayer;

import java.util.Collection;

import forum.server.domainlayer.user.Permission;

/**
 * @author sepetnit
 *
 */
public class ConnectedUserData {
	private long userID;
	private String username;
	private String firstName;
	private String lastName;
	private Collection<Permission> permissions;
	
	public ConnectedUserData(long userID, String username, String lastName, String firstName,
			Collection<Permission> permissions) {
		this.userID = userID;
		this.username = username;
		this.lastName = lastName;
		this.firstName = firstName;
		this.permissions = permissions;
	}
	
	
	public ConnectedUserData(long userID, Collection<Permission> permissions) {
		this(userID, null, null, null, permissions);
	}

	
	public boolean isAllowed(final Permission permissionToCheck) {
		return this.permissions.contains(permissionToCheck);
	}
	
	public long getID() {
		return this.userID;
	}
	
	public boolean isGuest() {
		return this.userID < 0;
	}
	
	public String getLastAndFirstName() {
		return this.lastName + " " + this.firstName;
	}
}
