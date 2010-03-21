package domain.user ;

import java.util.HashSet;
import java.util.Set;

import org.jasypt.util.password.StrongPasswordEncryptor;

import presentation.UIMember;
import presentation.UIUser;
import util.Bug;
import util.DuplicationException;
import util.HierarchicalSet;
import util.Log;
import util.NotFoundException;
import util.Subsystem;
import domain.ForumData;

/**
 * The Class UserController is responsible for managing users authorization. Also responsible for login and
 * registration.
 */
public class UserController
{
	/** The forum main page which have reference to UserServer. */
	private final ForumData forum ;
	
	private int activeGuestsCounter;
	private Set<String> activeMemberNames;


	/**
	 * Instantiates a new user controller.
	 *
	 * @param forum the forum
	 */
	public UserController(final ForumData forum)
	{
		this.forum = forum ;
		this.activeGuestsCounter = 0;
		this.activeMemberNames = new HashSet<String>();
	}

	/**
	 * Adds the administrator privileges to a specific user (must be a member).
	 *
	 * @param adminId the admin id who gives the privileges
	 * @param userName the username of the user how received the privileges
	 *
	 * @throws NotFoundException the not found exception
	 * @throws UnpermittedActionException the unPermitted action exception
	 */
	public void addAdministratorPrivileges(final long adminId, final String userName)
		throws NotFoundException, UnpermittedActionException
	{
		Log.getLogger(Subsystem.SERVICES).info("user " + adminId + " requests to add a privileges of administrator to user " + userName) ;
		final User adminU = this.forum.getUsers().getUser(adminId) ;
		if (adminU.getPrivileges().contains(Privilege.GIVE_PERMISSION))
		{
			Log.getLogger(Subsystem.SERVICES).finest("permission granted") ;
			final Member newAdmin = this.forum.getUsers().getMember(userName);
			if (newAdmin==null)
			{
				throw new NotFoundException(userName);
			}
			newAdmin.addPrivilege(Privilege.DELETE_POST) ;
			newAdmin.addPrivilege(Privilege.DELETE_THREAD) ;
			newAdmin.addPrivilege(Privilege.EDIT_ANY_POST) ;
			newAdmin.addPrivilege(Privilege.ADD_DIRECTORY) ;
			newAdmin.addPrivilege(Privilege.GIVE_PERMISSION) ;
			newAdmin.addPrivilege(Privilege.DENY_PERMISSION) ;
			this.forum.getUsers().flush(newAdmin); //update changes in member to the persistent
		}
		else
		{
			Log.getLogger(Subsystem.SERVICES).info("unpermitted action.") ;
			throw new UnpermittedActionException() ;
		}
	}

	/**
	 * Adds the moderator privileges to a specific user (must be a member).
	 *
	 * @param adminId the admin id who gives the privileges
	 * @param userName the username of the user how received the privileges
	 *
	 * @throws NotFoundException the not found exception
	 * @throws UnpermittedActionException the unPermitted action exception
	 */
	public void addModeratorPrivileges(final long adminId, final String userName)
		throws NotFoundException, UnpermittedActionException
	{
		Log.getLogger(Subsystem.SERVICES).info("user " + adminId + " requests to add a privileges of moderator to user " + userName) ;
		final User adminU = this.forum.getUsers().getUser(adminId) ;
		if (adminU.getPrivileges().contains(Privilege.GIVE_PERMISSION))
		{
			Log.getLogger(Subsystem.SERVICES).finest("permission granted") ;
			final Member newModerator = this.forum.getUsers().getMember(userName);
			if (newModerator==null)
			{
				throw new NotFoundException(userName);
			}
			newModerator.addPrivilege(Privilege.DELETE_POST) ;
			newModerator.addPrivilege(Privilege.DELETE_THREAD) ;
			newModerator.addPrivilege(Privilege.EDIT_ANY_POST) ;
			this.forum.getUsers().flush(newModerator); //update changes in member to the persistent

		}
		else
		{
			Log.getLogger(Subsystem.SERVICES).info("unpermitted action.") ;
			throw new UnpermittedActionException() ;
		}
	}

	/**
	 * Adds the privilege to a specific user.
	 *
	 * @param privilege the privilege
	 * @param adminId the admin id
	 * @param userId the user id
	 *
	 * @throws NotFoundException the not found exception
	 * @throws UnpermittedActionException the unpermitted action exception
	 */
	public void addPrivilege(final long adminId, final Privilege privilege, final long userId)
		throws NotFoundException, UnpermittedActionException
	{
		Log.getLogger(Subsystem.SERVICES).info("user " + adminId + " requests to add a privilege of " +
			privilege + " to user " + userId) ;
		final User adminU = this.forum.getUsers().getUser(adminId) ;
		if (adminU.getPrivileges().contains(Privilege.GIVE_PERMISSION))
		{
			Log.getLogger(Subsystem.SERVICES).finest("permission granted") ;
			this.forum.getUsers().getUser(userId).addPrivilege(privilege) ;
		}
		else
		{
			Log.getLogger(Subsystem.SERVICES).info("unpermitted action.") ;
			throw new UnpermittedActionException() ;
		}
	}

	/**
	 * handle users for exit the system
	 */
	public void exit()
	{
		this.forum.getUsers().close();
	}

	/**
	 * when user exits the forum: if it is a member - operate logout
	 * else - erase it from the guests set in user server.
	 * @param userId the user that exists the forum
	 */
	public void exitUser(final long userId)
	{
		try
		{
			final User user = this.forum.getUsers().getUser(userId) ;
			if (user instanceof Member)
			{
				this.logout(userId);
			}
			else
			{
				this.forum.getUsers().deleteUser(userId);
				this.decreaseActiveGuests();
			}
		}
		catch(final NotFoundException e)
		{
			Log.getLogger(Subsystem.SERVICES).info("bug occurerd: user that was not found has tried to exit.") ;
			throw new Bug("user that was not found has tried to exit", e);
		}
	}

	/**
	 * @param userId id of the user to find
	 * @return UIMember by its id
	 * @throws NotFoundException if no such member exists
	 */
	public UIMember getMemberById(final long userId) throws NotFoundException
	{
		return this.forum.getUsers().getMember(userId);
	}

	/**
	 * @param username name of the user to find
	 * @return a member id by its username
	 * @throws NotFoundException if no such member exists
	 */
	public long getMemberId(final String username) throws NotFoundException
	{
		final Member member = this.forum.getUsers().getMember(username);
		if(member!= null)
		{
			return member.getId();
		}
		else
		{
			throw new NotFoundException(username);
		}
	}

	/**
	 * Gets the reports.
	 */
	public void getReports()
	{
		// TODO
	}

	/**
	 * Login function is responsible of login an existing member to the forum system .
	 *
	 * @param name the username of the member
	 * @param password the password of the member
	 *
	 * @return the UIUser object of this member
	 *
	 * @throws NotFoundException in case the username does not exist.
	 * @throws BadPasswordException in case the password is wrong
	 */
	public UIUser login(final String name, final String password) throws NotFoundException,
		BadPasswordException
	{
		Log.getLogger(Subsystem.SERVICES).fine("user " + name + "is logging in.") ;
		final Member currentMember = this.forum.getUsers().getMember(name) ;
		if(currentMember == null)
		{
			throw new NotFoundException(name);
		}
		if (!this.checkPassword(password, currentMember.getPassword()))
		{
			throw new BadPasswordException(password) ;
		}
		currentMember.setActive(true);
		this.forum.getUsers().flush(currentMember); //update changes in member to the persistent
		Log.getLogger(Subsystem.SERVICES).fine("success") ;
		this.addActiveMember(name);
		return currentMember;
	}

	/**
	 * logout a member by changing its 'active' flag to false
	 * (the new state will be seen from all places that holds a reference to this member)
	 * @param userId the id of the user that asked to logout
	 * @throws NotFoundException if the user was not found, or it was not found as a member
	 */
	public void logout(final long userId) throws NotFoundException
	{
		Log.getLogger(Subsystem.SERVICES).fine("user " + userId + "is logging out.") ;
		final Member user = this.forum.getUsers().getMember(userId) ;
		if (user!=null)
		{
			user.setActive(false);
			this.forum.getUsers().flush(user); //update changes in member to the persistent
			Log.getLogger(Subsystem.SERVICES).info("success") ;
			this.removeActiveMember(user.getUsername());
		}
		else
		{
			Log.getLogger(Subsystem.SERVICES).info("a non-member is trying to logout") ;
			throw new NotFoundException(userId);
		}
	}

	/**
	 * Register function is responsible of register the new member to the forum. And also updating the
	 * UserServer for a new member.
	 * The action does not makes the user active!!
	 * which means that the user has to login in order to use the privileges of a registered member!
	 *
	 * @param name the username of the new member
	 * @param password the password of the new member
	 *
	 * @return the userId of the created member
	 *
	 * @throws DuplicationException occur when there are two same username's.
	 */
	public long register(final String name, final String password) throws DuplicationException
	{
		Log.getLogger(Subsystem.SERVICES).info("user " + name + "requests to register.") ;
		final Set<Privilege> privileges = this.setPrivilegesForNewMember() ;
		final String encryptedPassword = this.encryptPassword(password) ;
		final Member newMember = this.forum.getUsers().createMember(name, encryptedPassword, privileges) ;
		Log.getLogger(Subsystem.SERVICES).info("success") ;
		return newMember.getId() ;
	}

	/**
	 * @param username user name.
	 * @param password password of this member.
	 * @param firstName first name of the user.
	 * @param lastName last name of the user.
	 * @param dob date of birth.
	 * @param chosenGender male/female.
	 * @param residence where the user lives (free text).
	 * @return the id of the new member
	 * @throws DuplicationException - if the username already exists
	 */
	public long register(final String username, final String password, final String firstName,
		final String lastName, final String dob, final String chosenGender, final String residence)
		throws DuplicationException
	{
		Log.getLogger(Subsystem.SERVICES).info("user " + username + "requests to register.") ;
		final Set<Privilege> privileges = this.setPrivilegesForNewMember() ;
		final String encryptedPassword = this.encryptPassword(password) ;
		final Member newMember = this.forum.getUsers().createMember(username, encryptedPassword, privileges) ;
		newMember.setFirstName(firstName) ;
		newMember.setLastName(lastName) ;
		newMember.setDateOfBirth(dob) ;
		newMember.setGender(chosenGender) ;
		newMember.setResidence(residence) ;
		Log.getLogger(Subsystem.SERVICES).info("success") ;
		return newMember.getId() ;

	}

	/**
	 * create new guest in the system.
	 * creates a User object and puts it in the guests set in the user serve.
	 * @return the UIUser object of the newly created guest
	 */
	public UIUser registerGuest()
	{
		Log.getLogger(Subsystem.SERVICES).fine("a new guest has connected to the forum") ;
		this.incActiveGuests();
		final Set<Privilege> privileges = this.setPrivilegesForNewUser();
		final User guest = this.forum.getUsers().createUser(privileges);
		return guest;
	}
	
	/**
	 * unregister a guest (used when a user stops using a guest id it was given)
	 * @param userId the guest id
	 */
	public void unregisterGuest(long userId){
		Log.getLogger(Subsystem.SERVICES).fine("a guest with id "+userId+" has unregistered") ;
		try{
			this.forum.getUsers().deleteUser(userId);
			this.decreaseActiveGuests();
		}
		catch( NotFoundException e){
			Log.getLogger(Subsystem.SERVICES).fine("bug occurerd: guest that was not found has tried to exit.") ;
			throw new Bug("user that was not found has tried to exit", e);
		}
	}

	/**
	 * Removes the privilege from a specific user. Start with checking if the privilege exist in the user set
	 * of privileges.
	 *
	 * @param privilege the privilege
	 * @param adminId the admin id
	 * @param userId the user id
	 *
	 * @throws UnpermittedActionException the unpermitted action exception
	 * @throws NotFoundException the not found exception
	 */
	public void removePrivilege(final long adminId, final Privilege privilege, final long userId)
		throws UnpermittedActionException, NotFoundException
	{
		Log.getLogger(Subsystem.SERVICES).info("user " + adminId + " requests to remove a privilege of " +
			privilege + " to user " + userId) ;
		final User adminU = this.forum.getUsers().getUser(adminId) ;
		if (adminU.getPrivileges().contains(Privilege.DENY_PERMISSION))
		{
			Log.getLogger(Subsystem.SERVICES).finest("permission granted") ;
			this.forum.getUsers().getUser(userId).removePrivilege(privilege) ;
		}
		else
		{
			Log.getLogger(Subsystem.SERVICES).finest("unpermitted action.") ;
			throw new UnpermittedActionException() ;
		}
	}


	/**
	 * @param userId id of this user.
	 * @param newFirstName first name of the user.
	 * @param newLastName last name of the user.
	 * @param dob date of birth.
	 * @param chosenGender male/female.
	 * @param newResidence where the user lives (free text).
	 * @throws NotFoundException if no such member exists.
	 */
	public void updateMemberDetails(final long userId,final String newFirstName, final String newLastName,
		final String dob, final String chosenGender,final String newResidence) throws NotFoundException
	{
		final Member currentMember = this.forum.getUsers().getMember(userId) ;
		Log.getLogger(Subsystem.SERVICES).fine("user " + currentMember.getUsername() + "is trying to update details.") ;
		if(currentMember == null)
		{
			throw new NotFoundException(userId);
		}
		else
		{
			currentMember.setFirstName(newFirstName);
			currentMember.setLastName(newLastName);
			currentMember.setDateOfBirth(dob);
			currentMember.setGender(chosenGender);
			currentMember.setResidence(newResidence);
		}
		this.forum.getUsers().flush(currentMember); //update changes in member to the persistent
		Log.getLogger(Subsystem.SERVICES).fine("success") ;
	}

	/**
	 *
	 * @param userId id of the user.
	 * @param oldPassword old password.
	 * @param newPassword new password.
	 * @throws NotFoundException if no such user exists.
	 * @throws BadPasswordException if the old password is wrong.
	 */
	public void updatePassword(final long userId, final String oldPassword, final String newPassword)
	throws NotFoundException, BadPasswordException
	{
		final Member currentMember = this.forum.getUsers().getMember(userId) ;
		Log.getLogger(Subsystem.SERVICES).fine("user " + currentMember.getUsername() + "is trying to change password.") ;
		if(currentMember == null)
		{
			throw new NotFoundException(userId);
		}
		if (!this.checkPassword(oldPassword, currentMember.getPassword()))
		{
			throw new BadPasswordException(oldPassword) ;
		}
		else
		{
			String newEncryptedPass = this.encryptPassword(newPassword);
			currentMember.setEncryptedPassword(newEncryptedPass);
		}
		this.forum.getUsers().flush(currentMember); //update changes in member to the persistent
		Log.getLogger(Subsystem.SERVICES).fine("success") ;

	}
	
	/**
	 * @return number of active guests
	 */
	public synchronized int getActiveGuests(){
		return this.activeGuestsCounter;
	}
	
	/**
	 * @return the usernames of active members
	 */
	public synchronized Set<String> getActiveMemberNames(){
		return this.activeMemberNames;
	}
	
	private synchronized void incActiveGuests(){
		this.activeGuestsCounter++;
	}
	
	private synchronized void decreaseActiveGuests(){
		this.activeGuestsCounter--;
	}
	
	private synchronized void addActiveMember(String username){
		this.activeMemberNames.add(username);
	}
	
	private synchronized void removeActiveMember(String username){
		this.activeMemberNames.remove(username);
	}


	/**
	 * Method which checks the users password using Jasypt strong password encryption The algorithm is
	 * SHA-256, the salt size is 16 bytes, and number of hash iterations is 100000
	 *
	 * @param enteredPassword - the password the user entered in login
	 * @param savedPassword - the password saved for the user in the system
	 *
	 * @return - true if passwords matched, false otherwise
	 */
	private boolean checkPassword(final String enteredPassword, final String savedPassword)
	{
		final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor() ;
		return passwordEncryptor.checkPassword(enteredPassword, savedPassword) ;
	}

	/**
	 * Method which encrypts the users password using Jasypt strong password encryption The algorithm is
	 * SHA-256, the salt size is 16 bytes, and number of hash iterations is 100000
	 *
	 * @param plainPassword - the users chosen password
	 *
	 * @return - an encrypted password
	 */
	private String encryptPassword(final String plainPassword)
	{
		final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor() ;
		final String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword) ;
		return encryptedPassword ;
	}

	/**
	 * Sets the privileges for new member.
	 *
	 * @return set< privilege>
	 */
	private Set<Privilege> setPrivilegesForNewMember()
	{
		final Set<Privilege> privileges = new HierarchicalSet<Privilege>(
			Privilege.VIEW_POST,
			Privilege.ADD_POST,
			Privilege.ADD_THREAD,
			Privilege.EDIT_POST,
			Privilege.REPLY_POST
		) ;

		return privileges ;
	}

	private Set<Privilege> setPrivilegesForNewUser()
	{
		final Set<Privilege> privileges = new HierarchicalSet<Privilege>(Privilege.VIEW_POST);

		return privileges;
	}
}
