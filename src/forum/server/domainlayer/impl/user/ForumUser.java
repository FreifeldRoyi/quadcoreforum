/**
 * This class represents a forum user - either a guest or a member
 * 
 * The class is a base class of the forum member, its purpose is to handle a set of the user's permission
 * of operation he is allowed to perform
 */
package forum.server.domainlayer.impl.user;

import java.util.*;

import forum.server.domainlayer.impl.interfaces.UIUser;

/**
 * @author Vitali Sepetnitsky
 *
 */
public class ForumUser implements UIUser {

	private long id;
	private Collection<Permission> permissions;
	
	/**
	 * The class constructor
	 * 
	 * @param id
	 * 		The id of the constructed user
	 * @param permissions
	 * 		The initiali collection of permissions which are given to the user
	 */
	public ForumUser(final long id, final Collection<Permission> permissions) {
		this.id = id;
		this.permissions = permissions;
	}
	
	/**
	 * 
	 * The class constructor
	 * 
	 * Constructs a new user with an empty set of permissions
	 * 
	 * @param id
	 * 		The id of the new user
	 */
	public ForumUser(final long id) {
		this.id = id;
		this.permissions = new HashSet<Permission>();
	}
	
	// getters 
	
	/**
	 * @see
	 * 		UIUser#getID()
	 */
	public long getID() {
		return this.id;
	}

	/**
	 * @see
	 * 		UIUser#isAllowed(Permission)
	 */
	public boolean isAllowed(final Permission permissionToCheck) {
		return this.permissions.contains(permissionToCheck);
	}

	/**
	 * 
	 * @return
	 * 		The collections of permissions which are assigned to this user
	 */
	public Collection<Permission> getPermissions() {
		return this.permissions;
	}

	// methods
	
	/**
	 * Adds a permission to this user existing permissions
	 */
	public void addPermission(final Permission permissionToAdd) {
		this.permissions.add(permissionToAdd);
	}
	
	/**
	 * Removes a permission from this user permissions collection
	 * 
	 * @param permissionToRemove
	 * 		The permission which should be removed from this user
	 */
	public void removePermission(final Permission permissionToRemove) {
		this.permissions.remove(permissionToRemove);
	}
}
