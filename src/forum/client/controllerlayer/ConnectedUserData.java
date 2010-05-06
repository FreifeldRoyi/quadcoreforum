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
	public enum UserType {
		GUEST, MEMBER, MODERATOR, ADMIN
	}
	
	private long userID;
	private String username;
	private String firstName;
	private String lastName;
	private Collection<Permission> permissions;
	private UserType type;
	
	public ConnectedUserData(long userID, String username, String lastName, String firstName,
			String type, Collection<Permission> permissions) {
		this.userID = userID;
		this.username = username;
		this.lastName = lastName;
		this.firstName = firstName;
		this.permissions = permissions;
		this.type = UserType.valueOf(type);
	}
	
	public ConnectedUserData(long userID, Collection<Permission> permissions) {
		this(userID, null, null, null, "GUEST", permissions);
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
	
	public String getUsername() {
		return this.username;
	}
	
	public UserType getType() {
		return this.type;
	}
}
