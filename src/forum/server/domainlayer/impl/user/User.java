/**
 * 
 */
package forum.server.domainlayer.impl.user;

import java.util.*;

import forum.server.domainlayer.impl.interfaces.UIUser;

/**
 * @author sepetnit
 *
 */
public class User implements UIUser {

	private long id;
	private Collection<Permission> permissions;
	
		
	public User(final long id, final Collection<Permission> permissions) {
		this.id = id;
		this.permissions = permissions;
	}
	
	public User(final long id) {
		this.id = id;
		this.permissions = new HashSet<Permission>();
	}
	
	public void addPermission(final Permission permissionToAdd) {
		this.permissions.add(permissionToAdd);
	}
	
	public void removePermission(final Permission permissionToRemove) {
		this.permissions.remove(permissionToRemove);
	}

	public Collection<Permission> getPermissions() {
		return this.permissions;
	}
	
	public long getId() {
		return this.id;
	}

	public boolean isAllowed(final Permission permissionToCheck) {
		return this.permissions.contains(permissionToCheck);
	}
}
