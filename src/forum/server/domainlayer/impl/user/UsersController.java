package forum.server.domainlayer.impl.user ;

import java.util.*;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.impl.ForumDataHandler;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;

import forum.server.domainlayer.impl.interfaces.*;

import forum.server.persistentlayer.pipe.user.exceptions.*;

/**
 * The Class UserController is responsible for managing users authorization. Also responsible for login and
 * registration.
 */
public class UsersController {
	private ForumDataHandler dataHandler;
	
	private long activeGuestsCounter;
	private Set<String> activeMembersUsernames;
	
	
	/**
	 * The class constructor.
	 * 
	 * Creates and initializes a new users' controller.
	 */
	public UsersController(ForumDataHandler dataHandler) {
		this.dataHandler = dataHandler;
		this.activeMembersUsernames = new HashSet<String>();
	}
	
	public long registerNewMember(final String username, final String password, final String lastName,
			final String firstName, final String email) throws MemberAlreadyExistsException, DatabaseUpdateException {
		SystemLogger.info("A User requests to register with username " + username);
		final Set<Permission> tPermissions = this.getDefaultMemberPermissions();
		final String tEncryptedPassword = this.encryptOrDecryptPassword(password);
		final Member newMember = this.dataHandler.getUsersCache().createNewMember(username, tEncryptedPassword, lastName, 
				firstName, email, tPermissions);
		SystemLogger.info("New member with username " + username + " has successfuly been registered");
		return newMember.getId();
	}

	private String encryptOrDecryptPassword(final String password) {
		return PasswordEnDecryptor.encryptMD5(password);
	}
	
	/**
	 * 
	 * @param enteredPassword
	 * @param realPassword
	 * 
	 * @inv 
	 * 		enteredPassword != null && realPassword != null
	 * @return
	 */
	private boolean checkPasswordValidity(final String enteredPassword, final String realPassword) {
		return realPassword.equals(this.encryptOrDecryptPassword(enteredPassword));
	}

	public UIMember login(final String username, final String password) throws NotRegisteredException, 
	WrongPasswordException, DatabaseRetrievalException {
		SystemLogger.fine("A member with username " + username + " tries to log-in");
		final Member tMemberToLogIn = this.dataHandler.getUsersCache().getMemberByUsername(username);
		if (tMemberToLogIn == null) {
			SystemLogger.fine("A member with username " + username + " doesn't exist, can't log-in");
			throw new NotRegisteredException(username);
		}
		if (!this.checkPasswordValidity(password, tMemberToLogIn.getPassword())) {
			SystemLogger.fine("Can't log-in a member with username " + username + " because a wrong password was given");
			throw new WrongPasswordException();
		}
		this.addActiveMemberUsername(username);
		return tMemberToLogIn;
	}

	public void logout(final String username) throws NotConnectedException {
		SystemLogger.fine("A user with username " + username + " requests to log-out the forum");
		if (this.activeMembersUsernames.contains(username))
			this.removeActiveMemberUsername(username);
		else throw new NotConnectedException(username);		
	}

	
	public long getMemberIdByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException {
		Member tMember = this.dataHandler.getUsersCache().getMemberByUsername(username);
		if (tMember == null)
			throw new NotRegisteredException(username);
		else
			return tMember.getId();
	}
	
	private void removeActiveMemberUsername(String usernameToRemove) {
		this.activeMembersUsernames.remove(usernameToRemove);
	}
	
	private void addActiveMemberUsername(String usernameToAdd) {
		this.activeMembersUsernames.add(usernameToAdd);
	}
	
	public Set<String> getActiveMemberNames() {
		return this.activeMembersUsernames;
	}
	
	// Guest related methods
	
	public UIUser addGuest() {
		SystemLogger.fine("A new guest has connected to the forum");
		this.incActiveGuestsCounter();
		System.out.println(this.activeGuestsCounter + " sa");
		final Set<Permission> permissions = this.getDefaultGuestPermissions();
		return this.dataHandler.getUsersCache().createNewGuest(permissions);
	}
	
	public void removeGuest(final long guestId) {
		SystemLogger.fine("The guest with id " + guestId + " tries to exit from the forum.");
		try {
			this.dataHandler.getUsersCache().removeGuest(guestId);
			this.decActiveGuestsCounter();
			SystemLogger.fine("The guest with id " + guestId + " has successfuly been removed from the forum.");
		}
		catch (NotRegisteredException e) {
			SystemLogger.fine("Error occured: a guest with an id " + guestId + " wasn't found in the system.");
		}
	}

	private void incActiveGuestsCounter() {
		this.activeGuestsCounter++;
	}

	private void decActiveGuestsCounter() {
		this.activeGuestsCounter--;
	}
	
	public long getActiveGuestsNumber() {
		return this.activeGuestsCounter;
	}
	
	// Default permissions methods
	
	/**
	 * 
	 * @return
	 * 		A default permissions for the guests of the forum.
	 */
	private Set<Permission> getDefaultGuestPermissions() {
		final Set<Permission> toReturn = new HashSet<Permission>();
		toReturn.add(Permission.VIEW_ALL);
		return toReturn;
	}
	
	/**
	 * 
	 * @return
	 * 		A default permission set for a forum-member user, the set contains all guest permissions
	 * 		and additional permissions specified for the registered users of the forum.
	 */
	private Set<Permission> getDefaultMemberPermissions() {
		final Set<Permission> toReturn = this.getDefaultGuestPermissions();
		toReturn.add(Permission.OPEN_THREAD);
		toReturn.add(Permission.REPLY_TO_MESSAGE);
		toReturn.add(Permission.EDIT_MESSAGE);
		return toReturn;
	}
}
