/**
 * This class is responsible for managing users authorization, login and registration methods, and all other operations related to
 * users handling
 */

package forum.server.domainlayer.user ;


import java.util.*;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.ForumDataHandler;
import forum.server.domainlayer.ForumFacade;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;

import forum.server.domainlayer.interfaces.*;
import forum.server.domainlayer.message.NotPermittedException;

import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;

public class UsersController {
	// the handler through which the class accesses the cache instances through which the operations
	// of persistence are performed
	private ForumDataHandler dataHandler;
	// stores a set of all the user-names of members currently connected to the forum
	//	private Map<String, Integer> activeMembersUserNames;

	private Collection<Long> guestsIDsToRemove;
	private TimerTask guestIDsRemovalTask;
	private Timer guestsIDsRemovalTimer;

	private Collection<String> membersUsernamesToRemove;
	private TimerTask membersUsernamesToRemoveTask;
	private Timer membersUsernamesRemovalTimer;

	/**
	 * The class constructor.
	 * 
	 * Creates and initializes a new users' controller.
	 */
	public UsersController(ForumDataHandler dataHandler) {
		this.dataHandler = dataHandler;
		//		this.activeMembersUserNames = new HashMap<String, Integer>();
	}

	// Guest related methods

	/**
	 * @throws DatabaseRetrievalException 
	 * @see
	 * 		ForumFacade#getActiveGuestsNumber()
	 */
	public long getActiveGuestsNumber() throws DatabaseRetrievalException {
		return this.dataHandler.getUsersCache().getGuestsNumber();
	}

	/**
	 * @see
	 * 		ForumFacade#addGuest()
	 */
	public UIUser addGuest() throws DatabaseUpdateException {
		SystemLogger.fine("A new guest has connected to the forum");
		final Collection<Permission> permissions = this.getDefaultGuestPermissions();
		return this.dataHandler.getUsersCache().createNewGuest(permissions);
	}

	/**
	 * @see
	 * 		ForumFacade#removeGuest(long)
	 */
	public void removeGuest(final long guestID) {
		SystemLogger.fine("The guest with id " + guestID + " tries to exit from the forum.");
		try {
			this.dataHandler.getUsersCache().removeGuest(guestID);
			//this.decActiveGuestsCounter();
			SystemLogger.fine("The guest with id " + guestID + " has successfuly been removed from the forum.");
		}
		catch (NotRegisteredException e) {
			SystemLogger.fine("Error occured: a guest with an id " + guestID + " wasn't found in the system.");
		}
		catch (DatabaseUpdateException e) {
			SystemLogger.severe("Database connection error occurred, the server will try to remove " +
			" the guest later.");
			guestsIDsToRemove.add(guestID);

			if (guestsIDsRemovalTimer != null) {
				guestsIDsRemovalTimer.cancel();
				guestsIDsRemovalTimer.purge();
			}
			guestsIDsRemovalTimer = new Timer();
			if (guestIDsRemovalTask != null)
				guestIDsRemovalTask.cancel();
			guestIDsRemovalTask = new TimerTask() {
				public void run() {
					try {
						Long[] tIDs = (Long[])guestsIDsToRemove.toArray();
						for (int i = 0; i < tIDs.length; i++) {
							long tGuestID = tIDs[i].longValue();
							try {
								dataHandler.getUsersCache().removeGuest(tGuestID);
							}
							catch (NotRegisteredException e) {
								guestsIDsToRemove.remove(tGuestID);
								continue;
							}
							SystemLogger.fine("The guest with id " + tGuestID + " has successfuly been removed from the forum.");
							guestsIDsToRemove.remove(tGuestID);
						}
					}
					catch (DatabaseUpdateException e) {
						return;
					}
				}
			};
			guestsIDsRemovalTimer.schedule(guestIDsRemovalTask, new Date(), 5000);
		}
	}

	// User related methods

	/**
	 * @see
	 * 		ForumFacade#getMemberByID(long)
	 */
	public UIMember getMemberByID(long memberID) throws NotRegisteredException, DatabaseRetrievalException {
		ForumUser toReturn = this.dataHandler.getUsersCache().getUserByID(memberID);
		// if no exception was thrown before this line, then the user exists in the database and 
		// therefore he is a registered member of the forum
		return (ForumMember)toReturn;
	}

	/**
	 * 
	 * @return
	 * 		A collection of all the user-name of members which are currently active
	 * 		(logged-in to the forum)
	 * @throws DatabaseRetrievalException 
	 */
	public Collection<String> getActiveMemberNames() throws DatabaseRetrievalException {
		return this.dataHandler.getUsersCache().getActiveMemberUserNames();
	}

	/**
	 * 
	 * @see
	 * 		ForumFacade#getAllMembers()
	 */
	public Collection<UIMember> getAllMembers() throws DatabaseRetrievalException {
		SystemLogger.info("A User requests to view all the forum members.");
		Collection<UIMember> toReturn = new Vector<UIMember>();
		toReturn.addAll(this.dataHandler.getUsersCache().getAllMembers());
		SystemLogger.info("A forum members were retrieved and returned.");
		return toReturn;
	}

	/**
	 * @see
	 * 		ForumFacade#getMemberIdByUsernameAndOrEmail(String, String)
	 */
	public long getMemberIdByUsernameAndOrEmail(final String username, final String email) throws NotRegisteredException, DatabaseRetrievalException {
		ForumMember tMember = null;
		if (username != null) {
			tMember = this.dataHandler.getUsersCache().getMemberByUsername(username);
			if (tMember == null || (email != null && !tMember.getEmail().equals(email)))
				throw new NotRegisteredException(username);
		}
		else {
			tMember = this.dataHandler.getUsersCache().getMemberByEmail(email);
			if (tMember == null)
				throw new NotRegisteredException(username);
		}
		return tMember.getID();
	}

	/**
	 * @throws DatabaseRetrievalException 
	 * @see	ForumFacade#registerNewMember(String, String, String, String, String)
	 */
	public long registerNewMember(final String username, final String password, final String lastName,
			final String firstName, final String email) throws MemberAlreadyExistsException, DatabaseUpdateException {
		SystemLogger.info("A User requests to register with username " + username);
		final Collection<Permission> tPermissions = this.getDefaultMemberPermissions();
		final String tEncryptedPassword = this.encryptPassword(password);
		final ForumMember newMember = this.dataHandler.getUsersCache().createNewMember(username, tEncryptedPassword, lastName, 
				firstName, email, tPermissions);
		SystemLogger.info("New member with username " + username + " has successfuly been registered");
		return newMember.getID();
	}

	/**
	 * 
	 * Encrypts the given password in order to check its validity against the stored one
	 * 
	 * @param password
	 * 		The password which should be encrypted
	 * @return
	 * 		The encrypted password
	 */
	private String encryptPassword(final String password) {
		return PasswordEncryptor.encryptMD5(password);
	}

	/**
	 * 
	 * Encrypts the entered password according to a chosen encryption algorithm and checks
	 * whether the encrypted password is valid, against the given real password 
	 * 
	 * @param enteredPassword
	 * 		The password which was entered by the user
	 * @param realPassword
	 * 		The real password to which the entered password is compared
	 * @inv 
	 * 		enteredPassword != null && realPassword != null
	 * 
	 * @return
	 * 		True if the entered password is valid and false otherwise
	 */
	private boolean checkPasswordValidity(final String enteredPassword, final String realPassword) {
		return realPassword.equals(this.encryptPassword(enteredPassword));
	}

	/**
	 * Adds a new user-name of a member who has been logged-in to the forum
	 *  
	 * @param memberIDToAdd
	 * 		The user-name of the member which should be added to the active members collection
	 * @throws DatabaseUpdateException 
	 */
	private void addActiveMemberID(long memberIDToAdd) throws DatabaseUpdateException {
		this.dataHandler.getUsersCache().addActiveMemberID(memberIDToAdd);

		/*		Integer tConnectedNumbers = this.activeMembersUserNames.get(usernameToAdd);
		if (tConnectedNumbers == null)
			this.activeMembersUserNames.put(usernameToAdd, 1);
		else
			this.activeMembersUserNames.put(usernameToAdd, tConnectedNumbers + 1);
		 */
	}

	/**
	 * Removes a user-name of a member who has been logged-out from the forum
	 * 
	 * @param memberIdToRemove
	 * 		The id of the member which should be removed from the active
	 * 		members collection
	 * @throws DatabaseUpdateException 
	 * @throws NotConnectedException 
	 */
	private void removeActiveMemberID(long memberIdToRemove) throws NotConnectedException, DatabaseUpdateException {
		this.dataHandler.getUsersCache().removeActiveMemberID(memberIdToRemove);

		/*Integer toRemove = this.activeMembersUserNames.get(usernameToRemove);
		this.activeMembersUserNames.put(usernameToRemove, toRemove - 1);
		if (toRemove.intValue() - 1 == 0)
			this.activeMembersUserNames.remove(usernameToRemove);
		 */
	}	

	/**
	 * @see
	 * 		ForumFacade#login(String, String)
	 */
	public UIMember login(final String username, final String password) throws NotRegisteredException, 
	WrongPasswordException, DatabaseRetrievalException {
		SystemLogger.fine("A member with username " + username + " tries to log-in");
		final ForumMember tMemberToLogIn = this.dataHandler.getUsersCache().getMemberByUsername(username);
		if (tMemberToLogIn == null || !tMemberToLogIn.getUsername().equals(username)) {
			SystemLogger.fine("A member with username " + username + " doesn't exist, can't log-in");
			throw new NotRegisteredException(username);
		}
		if (!this.checkPasswordValidity(password, tMemberToLogIn.getPassword())) {
			SystemLogger.fine("Can't log-in a member with username " + username + " because a wrong password was given");
			throw new WrongPasswordException();
		}
		try {
			this.addActiveMemberID(tMemberToLogIn.getID());
			return tMemberToLogIn;
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @see
	 * 		ForumFacade#logout(String)
	 */
	public void logout(final String username) throws NotConnectedException {
		SystemLogger.fine("A user with username " + username + " requests to log-out the forum");
		try {
			long tMemberID = this.dataHandler.getUsersCache().getMemberByUsername(username).getID();
			this.removeActiveMemberID(tMemberID);
		}
		catch (DatabaseUpdateException e) {
			SystemLogger.severe("Database connection error occurred, the server will try to remove " +
			" the username later.");
			membersUsernamesToRemove.add(username);
			removeUsernamesFromDatabase();
		}
		catch (DatabaseRetrievalException e) {
			SystemLogger.severe("Database connection error occurred, the server will try to remove " +
			" the username later.");
			membersUsernamesToRemove.add(username);
			removeUsernamesFromDatabase();
		}

		SystemLogger.info("The member with username " + username + " has logged-out from the forum");
		//}
	}

	private void removeUsernamesFromDatabase() {
		if (membersUsernamesRemovalTimer != null) {
			membersUsernamesRemovalTimer.cancel();
			membersUsernamesRemovalTimer.purge();
		}
		membersUsernamesRemovalTimer = new Timer();
		if (membersUsernamesToRemoveTask != null)
			membersUsernamesToRemoveTask.cancel();
		membersUsernamesToRemoveTask = new TimerTask() {
			public void run() {
				try {
					String[] tUsernames = (String[])membersUsernamesToRemove.toArray();
					for (int i = 0; i < tUsernames.length; i++) {
						try {
							long tMemberID = dataHandler.getUsersCache().getMemberByUsername(tUsernames[i]).getID();
							dataHandler.getUsersCache().removeActiveMemberID(tMemberID);
						}
						catch (NotConnectedException e) {
							membersUsernamesToRemove.remove(tUsernames[i]);
							continue;
						}
						SystemLogger.fine("The member with username " + tUsernames[i] + " has successfuly been removed from the forum.");
						membersUsernamesToRemove.remove(tUsernames[i]);
					}
				}
				catch (DatabaseUpdateException e) {
					return;
				}
				catch (DatabaseRetrievalException e) {
					return;
				}
			}
		};
		membersUsernamesRemovalTimer.schedule(membersUsernamesToRemoveTask, new Date(), 5000);
	}

	/**
	 * 
	 * Checks whether the given user is a guest of the forum
	 * 
	 * @param userToCheck
	 *		The user which should be checked to be a forum guest 
	 * 
	 * @return
	 * 		True if the given user is forum guest and false otherwise
	 */
	/*private boolean isGuest(ForumUser userToCheck) {
		return (userToCheck != null) && (userToCheck.getID() < 0);
	}*/

	/**
	 * @see
	 * 		ForumFacade#updateMemberProfile(long, String, String, String, String, String, boolean)
	 */
	public UIMember updateMemberProfile(final long memberID, final String username, final String password, final String lastname,
			final String firstname, final String email, boolean shouldAskPassword) throws
			NotRegisteredException, MemberAlreadyExistsException, DatabaseUpdateException {
		SystemLogger.info("A member " + username + " with id " + memberID + " requests to change his profile details.");

		ForumMember tForumMember = null;
		try {				
			tForumMember = this.dataHandler.getUsersCache().getMemberByUsername(username);
			if (tForumMember == null) {
				SystemLogger.info("A member with id " + memberID + " doesn't exist in the database.");
				throw new NotRegisteredException(memberID);
			}
		}
		catch (DatabaseRetrievalException e) {
			SystemLogger.severe("Can't update profile because of database retrieval error.");
			throw new DatabaseUpdateException();
		}

		// used for recovery
		String tPassword = null;
		String tLastName = null;
		String tFirstName = null;
		String tEmail = null;
		boolean tShouldAskPassword = false;


		// check if the given email is already assigned to a different member
		if (email != null) {

			ForumMember tOtherMember = null;
			try {
				tOtherMember = this.dataHandler.getUsersCache().getMemberByEmail(email);
			}
			catch (DatabaseRetrievalException e) {
				SystemLogger.severe("Can't update profile because of database retrieval error.");
				throw new DatabaseUpdateException();
			}
			if (tOtherMember != null && !tOtherMember.getUsername().equals(username))
				throw new MemberAlreadyExistsException(email);
			tEmail = tForumMember.getEmail();
			tForumMember.setEmail(email);
		}

		if (password != null) {
			tPassword = tForumMember.getPassword();
			tForumMember.setPassword(this.encryptPassword(password));
		}

		if (lastname != null) {
			tLastName = tForumMember.getLastName();
			tForumMember.setLastName(lastname);
		}

		if (firstname != null) {
			tFirstName = tForumMember.getFirstName();
			tForumMember.setFirstName(firstname);
		}
		tShouldAskPassword = tForumMember.askChangePassword();
		tForumMember.setAskChangePassword(shouldAskPassword);

		try {
			this.dataHandler.getUsersCache().updateInDatabase(tForumMember);
			SystemLogger.info("The profile of " + username + " has been successfully changed.");
		}
		catch (NotRegisteredException e) {
			tForumMember.setPassword(tPassword);
			tForumMember.setLastName(tLastName);
			tForumMember.setFirstName(tFirstName);
			tForumMember.setEmail(tEmail);
			tForumMember.setAskChangePassword(tShouldAskPassword);
			throw e;
		}
		catch (DatabaseUpdateException e) {
			tForumMember.setPassword(tPassword);
			tForumMember.setLastName(tLastName);
			tForumMember.setFirstName(tFirstName);
			tForumMember.setEmail(tEmail);
			tForumMember.setAskChangePassword(tShouldAskPassword);
			throw e;
		}
		return tForumMember;
	}


	/**
	 * @see	
	 * 		ForumFacade#updateMemberPassword(long, String, String, boolean)
	 */
	public UIMember updateMemberPassword(final long memberID, final String prevPassword, 
			final String newPassword, 
			final boolean askChangePassword) throws NotRegisteredException, DatabaseUpdateException, WrongPasswordException {
		SystemLogger.info("A member with id " + memberID + " requests to change his password after recovery.");

		ForumMember tForumMember = null;
		try {				
			tForumMember = (ForumMember)this.getMemberByID(memberID);

			if (prevPassword == null || newPassword == null) {
				tForumMember.setAskChangePassword(askChangePassword);
				this.dataHandler.getUsersCache().updateInDatabase(tForumMember);
			}
			else {
				if (!this.checkPasswordValidity(prevPassword, tForumMember.getPassword())) {
					SystemLogger.info("The given previous password of member " + memberID + " is wrong");
					throw new WrongPasswordException();
				}
				else {
					tForumMember.setPassword(this.encryptPassword(newPassword));
					tForumMember.setAskChangePassword(false);
					this.dataHandler.getUsersCache().updateInDatabase(tForumMember);
				}
			}
			return tForumMember;
		}
		catch (NotRegisteredException e) {
			SystemLogger.info("A member with id " + memberID + " doesn't exist in the database.");
			throw e;
		}

		catch (DatabaseRetrievalException e) {
			SystemLogger.severe("Can't update password of member " + memberID + " because of database retrieval error.");
			throw new DatabaseUpdateException();
		}
	}


	/**
	 * @see
	 * 		ForumFacade#promoteToBeModerator(long, long)
	 */
	public void promoteToBeModerator(final long applicantID, final String username) throws NotPermittedException, 
	NotRegisteredException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + applicantID + " requests to promote a user " +
					username + " to be forum moderator.");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(applicantID);
			if (tApplicant.isAllowed(Permission.SET_MODERATOR)) {
				SystemLogger.info("Permission granted for user " + applicantID + ".");
				final ForumUser tForumUser = this.dataHandler.getUsersCache().getMemberByUsername(username);
				tForumUser.setPermissions(this.getDefaultModeratorPermissions());
				this.dataHandler.getUsersCache().updateInDatabase(tForumUser);
				SystemLogger.info("The user with " + username + " has been successfully promoted to be a " +
				"moderator of the forum.");
			}
			else {
				SystemLogger.info("unpermitted operation for user " + applicantID + ".");
				throw new NotPermittedException(applicantID, Permission.SET_MODERATOR);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		ForumFacade#demoteToBeMember(long, String)
	 */
	public void demoteToBeMember(final long applicantID, final String username) throws NotPermittedException, 
	NotRegisteredException, DatabaseUpdateException {
		try {
			SystemLogger.info("A user with id " + applicantID + " requests to demote a user " +
					username + " to be a regular forum member.");
			final ForumUser tApplicant = this.dataHandler.getUsersCache().getUserByID(applicantID);
			if (tApplicant.isAllowed(Permission.SET_MODERATOR)) {
				SystemLogger.info("Permission granted for user " + applicantID + ".");
				final ForumUser tForumUser = this.dataHandler.getUsersCache().getMemberByUsername(username);
				tForumUser.setPermissions(this.getDefaultMemberPermissions());
				this.dataHandler.getUsersCache().updateInDatabase(tForumUser);
				SystemLogger.info("The user with " + username + " has been successfully demoted to be a " +
				"regular member of the forum.");
			}
			else {
				SystemLogger.info("unpermitted operation for user " + applicantID + ".");
				throw new NotPermittedException(applicantID, Permission.SET_MODERATOR);
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Default permissions methods

	/**
	 * 
	 * @return
	 * 		A default permissions for the guests of the forum.
	 */
	private Collection<Permission> getDefaultGuestPermissions() {
		final Collection<Permission> toReturn = new HashSet<Permission>();
		toReturn.add(Permission.VIEW_ALL);
		return toReturn;
	}

	/**
	 * 
	 * @return
	 * 		A default permission set for a forum-member user, the set contains all guest permissions
	 * 		and additional permissions specified for the registered users of the forum.
	 */
	private Collection<Permission> getDefaultMemberPermissions() {
		final Collection<Permission> toReturn = this.getDefaultGuestPermissions();
		toReturn.add(Permission.OPEN_THREAD);
		toReturn.add(Permission.REPLY_TO_MESSAGE);
		toReturn.add(Permission.EDIT_MESSAGE);
		return toReturn;
	}

	/**
	 * 
	 * @return
	 * 		A default permission set for a forum-moderator user, the set contains all member permissions
	 * 		and additional permissions specified for the moderators of the forum.
	 */
	private Collection<Permission> getDefaultModeratorPermissions() {
		final Collection<Permission> toReturn = this.getDefaultMemberPermissions();
		toReturn.add(Permission.DELETE_THREAD);
		toReturn.add(Permission.DELETE_MESSAGE);
		toReturn.add(Permission.ADD_SUBJECT);
		toReturn.add(Permission.DELETE_SUBJECT);
		toReturn.add(Permission.ADD_SUB_SUBJECT);
		toReturn.add(Permission.EDIT_SUBJECT);
		return toReturn;
	}
}
