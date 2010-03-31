package forum.server.domainlayer.impl.user;


import java.util.*;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.PersistenceDataHandler;
import forum.server.persistentlayer.pipe.PersistenceFactory;
import forum.server.persistentlayer.pipe.user.exceptions.*;

/**
 * This class serves as a cache memory of users, it holds users repository and manages the addition and removal of users.
 * 
 */

/**
 * @author sepetnit
 *
 */
public class UsersCache {

	private long nextFreeUserID;
	private final Map<Long, User> idsToUsersMapping;
	private final PersistenceDataHandler pipe;
	

	/**
	 * The class controller. 
	 * 
	 * Creates and initializes a new users cache.
	 */
	public UsersCache() {
		this.nextFreeUserID = 0;
		this.idsToUsersMapping = new HashMap<Long, User>();
		this.pipe = PersistenceFactory.getPipe();
	}

	/**
	 * 
	 * @return
	 * 		The next id number that can be assigned to a new user of the forum
	 */
	private long getNextFreeID() {
		// TODO: Make this to be synchronized in order to prevent two users with the same id
		this.nextFreeUserID++;
		System.out.println("FreeID");
		return nextFreeUserID - 1;
	}

	public boolean containsUser(final long id) {
		return this.idsToUsersMapping.containsKey(id);
	}

	public Member createNewMember(final String username, final String password, final String lastName,
			final String firstName, final String email, final Set<Permission> permissions) throws 
			MemberAlreadyExistsException, DatabaseUpdateException {
		try {
		if ((this.getMemberByUsername(username) != null) || (this.getMemberByEmail(email) != null))
			throw new MemberAlreadyExistsException(username);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}		
		final long id = this.getNextFreeID();
		final Member newMember = new Member(id, username, password, lastName, firstName, email, permissions);
		this.pipe.addNewMember(newMember.getId(), username, password, lastName, firstName, email, permissions);	
		this.idsToUsersMapping.put(id, newMember);
		return newMember;
	}

	public User createNewGuest(final Set<Permission> permissions) {
		System.out.println("dssssssssssssssssssssssssssasdsadasd");
		final long tId = this.getNextFreeID();
		final User tNewUser = new User(tId, permissions);
		this.idsToUsersMapping.put(tId, tNewUser);
		System.out.println(this.nextFreeUserID);
		return tNewUser;
	}

	public void removeGuest(final long id) throws NotRegisteredException {
		if (this.idsToUsersMapping.containsKey(id))			
			this.idsToUsersMapping.remove(id);
		else throw new NotRegisteredException(id);
	}

	public Set<User> getAllUsers() throws DatabaseRetrievalException {
		final Set<User> toReturn = new HashSet<User>();
		toReturn.addAll(this.idsToUsersMapping.values());
		toReturn.addAll(this.pipe.getAllMembers());
		return toReturn;
	}

	public Member getMemberByUsername(final String username) throws DatabaseRetrievalException {
		try {
			Member toReturn = this.pipe.getMemberByUsername(username);
			return toReturn;
		}
		catch (NotRegisteredException e) {
			return null;
		}
	}

	public Member getMemberByEmail(final String email) throws DatabaseRetrievalException {
		try {
			Member toReturn = this.pipe.getMemberByEmail(email);
			return toReturn;
		}
		catch (NotRegisteredException e) {
			return null;
		}
	}

	// used for permissions only
	public User getUserByID(final long id) throws NotRegisteredException, DatabaseRetrievalException {
		if (this.idsToUsersMapping.containsKey(id))
			return this.idsToUsersMapping.get(id);
		else
			return this.pipe.getMemberByID(id);
	}	
	
}