package forum.server.updatedpersistentlayer.pipe.user;


import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

import forum.server.domainlayer.user.ForumMember;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.DatabaseRetrievalException;
import forum.server.updatedpersistentlayer.DatabaseUpdateException;
import forum.server.updatedpersistentlayer.ExtendedObjectFactory;
import forum.server.updatedpersistentlayer.MemberType;
import forum.server.updatedpersistentlayer.pipe.PersistenceDataHandler;
import forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotConnectedException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;

/**
 * This class is responsible of performing the operations of reading from and writing to the database
 * all the data which refers to the forum members
 */

/**
 * @author Sepetnitsky Vitali
 */
public class UsersPersistenceHandler {
	private Session getSessionAndBeginTransaction(SessionFactory ssFactory) throws DatabaseRetrievalException {
		try {
			Session toReturn = ssFactory.getCurrentSession();
			toReturn.beginTransaction();
			return toReturn;
		}
		catch (RuntimeException e) {
			throw new DatabaseRetrievalException();
		}
	}

	private void commitTransaction(Session session) throws DatabaseUpdateException {
		try {
			session.getTransaction().commit();
		}
		catch (RuntimeException e) {
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				try {
					// Second try catch as the roll-back could fail as well
					session.getTransaction().rollback();
				}
				catch (HibernateException e1) {
					// add logging
				}
			}
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getGuestsNumber()
	 */
	@SuppressWarnings("unchecked")
	public long getGuestsNumber(SessionFactory ssFactory) throws DatabaseRetrievalException {
		long toReturn = -1;
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "select count(UserID) from ConnectedUsers where UserID < -1";
		List<BigInteger> tResult = (List<BigInteger>)session.createSQLQuery(query).list();
		if (tResult.get(0) != null)
			toReturn = tResult.get(0).longValue();
		try {
			this.commitTransaction(session);
			return toReturn;
		} 
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getNextFreeGuestID()
	 */	
	@SuppressWarnings("unchecked")
	public long getNextFreeGuestID(SessionFactory ssFactory) throws DatabaseRetrievalException {
		long toReturn = -1;
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "select Min(UserID) from ConnectedUsers";
		List<BigInteger> tResult = (List<BigInteger>)session.createSQLQuery(query).list();
		if (tResult.get(0) != null) {
			toReturn = tResult.get(0).longValue();
			toReturn = (toReturn >= -1)? -2 : (toReturn - 1);
		}
		else
			toReturn = -2;
		session.createSQLQuery("insert into ConnectedUsers values(" + toReturn + ", 1)").executeUpdate();
		try {
			this.commitTransaction(session);
			return toReturn;
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#removeGuest(long)
	 */	
	public void removeGuest(SessionFactory ssFactory, final long guestID) throws DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			session.createSQLQuery("delete from ConnectedUsers where UserID = " + guestID).executeUpdate();
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeMemberID()
	 */
	@SuppressWarnings("unchecked")
	public long getFirstFreeMemberID(SessionFactory ssFactory) throws DatabaseRetrievalException {
		long toReturn = -1;
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "select max(userID) from MemberType";
		List<Long> tResult = (List<Long>)session.createQuery(query).list();

		if (tResult.get(0) != null) {
			toReturn = Math.max(tResult.get(0).longValue() + 1, 0);
		}
		else {
			toReturn = 1;
		}
		// add a dummy row in order to promise that the calculate id won't be taken by another server
		session.createSQLQuery("insert into members(UserID, Username, UserPassword, Email) values (" + toReturn + ", \"username" + 
				toReturn + "\", \"password\", \"email\")").executeUpdate();

		try {
			this.commitTransaction(session);
		} 
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getActiveMemberUserNames(SessionFactory ssFactory) throws DatabaseRetrievalException {
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "select Username from ConnectedUsers as Con, Members " +
		"as Mem where Con.UserID = Mem.UserID and Con.UserID != -1";
		List<String> tResult = (List<String>)session.createSQLQuery(query).list();
		Collection<String> toReturn = new HashSet(tResult);
		try {
			this.commitTransaction(session);
			return toReturn;
		} 
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	public void addActiveMemberID(SessionFactory ssFactory, long memberIDToAdd) throws DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			int tUpdatedRowsNum = 
				session.createSQLQuery("update ConnectedUsers set " + 
						"ConnectionsNum = ConnectionsNum + 1 where UserID = " + memberIDToAdd).executeUpdate();
			if (tUpdatedRowsNum == 0) // the user doesn't exist in the table
				session.createSQLQuery("insert into ConnectedUsers values(" + memberIDToAdd + ", 1)").executeUpdate();			
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#removeGuest(long)
	 */	
	@SuppressWarnings("unchecked")
	public void removeActiveMemberID(SessionFactory ssFactory, final long memberID) throws NotConnectedException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			int tUpdatedRowsNum = 
				session.createSQLQuery("update ConnectedUsers set " + 
						"ConnectionsNum = ConnectionsNum - 1 where UserID = " + memberID).executeUpdate();
			if (tUpdatedRowsNum == 0)
				throw new NotConnectedException(memberID);			
			String query = "select ConnectionsNum from ConnectedUsers where UserID = " + memberID;
			List<BigInteger> tResult = (List<BigInteger>)session.createSQLQuery(query).list();
			if (tResult.get(0) != null) {
				long tCurrent = tResult.get(0).longValue();
				if (tCurrent == 0)
					session.createSQLQuery("delete from ConnectedUsers where UserID = " + memberID).executeUpdate(); 
			}
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the forum members should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getAllMembers()
	 */
	@SuppressWarnings("unchecked")
	public Collection<ForumMember> getAllMembers(SessionFactory ssFactory) throws DatabaseRetrievalException {
		Collection<ForumMember> toReturn = new Vector<ForumMember>();
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "from MemberType where UserID != -1";
		List tResult = session.createQuery(query).list();
		for (MemberType tCurrentMemberType : (List<MemberType>)tResult) 
			toReturn.add(PersistentToDomainConverter.convertMemberTypeToForumMember(tCurrentMemberType));
		try {
			this.commitTransaction(session);
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return toReturn;
	}

	private MemberType getMemberTypeByID(final Session session, final long userID) throws 
	DatabaseRetrievalException {
		try {
			return (MemberType)session.get(MemberType.class, userID);
		}
		catch (HibernateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the required user should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getUserByID(long)
	 */
	public ForumMember getMemberByID(SessionFactory ssFactory, long userID) throws NotRegisteredException, 
	DatabaseRetrievalException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);		
			MemberType tMemberType = this.getMemberTypeByID(session, userID);
			if (tMemberType == null) {
				this.commitTransaction(session);
				throw new NotRegisteredException(userID);
			}
			else {
				ForumMember toReturn = PersistentToDomainConverter.convertMemberTypeToForumMember(tMemberType);
				this.commitTransaction(session);
				return toReturn;
			}
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the required member should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getMemberByUsername(String)
	 */
	@SuppressWarnings("unchecked")
	private ForumMember getMemberByField(final SessionFactory ssFactory, final String field, 
			final String value) throws NotRegisteredException, DatabaseRetrievalException {
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "from MemberType where " + field  + " like '" + value + "'";
		List tResult = session.createQuery(query).list();
		if (tResult.size() != 1)
			throw new NotRegisteredException(field);
		else {
			ForumMember toReturn = 
				PersistentToDomainConverter.convertMemberTypeToForumMember((MemberType)tResult.get(0));
			try {
				this.commitTransaction(session);
			}
			catch (DatabaseUpdateException e) {
				throw new DatabaseRetrievalException();
			}
			return toReturn;
		}
	}

	/**
	 * @param data
	 * 		The forum data from which the required member should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getMemberByUsername(String)
	 */
	public ForumMember getMemberByUsername(final SessionFactory ssFactory, final String username) throws 
	NotRegisteredException, DatabaseRetrievalException {
		return this.getMemberByField(ssFactory, "Username", username);
	}

	/**
	 * @param data
	 * 		The forum data from which the required member should be retrieved
	 * @throws DatabaseRetrievalException 
	 * 
	 * @see
	 * 		PersistenceDataHandler#getMemberByEmail(String)
	 */
	public ForumMember getMemberByEmail(final SessionFactory ssFactory, final String email) throws 
	NotRegisteredException, DatabaseRetrievalException {
		return this.getMemberByField(ssFactory, "Email", email);
	}

	/**
	 * @param data
	 * 		The forum data to which the new member should be added
	 * 
	 * @see
	 * 		PersistenceDataHandler#addNewMember(long, String, String, String, String, String, Collection)
	 */
	public void addNewMember(final SessionFactory ssFactory, final long id, final String username, final String password,
			final String lastName, final String firstName, final String email, 
			final Collection<Permission> permissions) throws DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			MemberType tNewMemberType = ExtendedObjectFactory.createMemberType(id, username, password, lastName, firstName, email, permissions);
			// now we can delete the id - in order to add the new member instead

			session.createSQLQuery("delete from Members where UserID = " + id).executeUpdate();
			session.save(tNewMemberType);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * Parses and converts a collection of permissions of type {@link Permission} to a collection of strings
	 * 
	 * @param permissions
	 * 		The collection of permissions which should be parsed and converted to collection of strings
	 * 
	 * @return
	 * 		The created collection of {@link String} objects
	 */
	private Collection<String> parsePermissionsToString(Collection<Permission> permissions) {
		Collection<String> toReturn = new HashSet<String>();
		for (Permission tCurrentPermission : permissions)
			toReturn.add(tCurrentPermission.toString());
		return toReturn;
	}

	/**
	 * @param data
	 * 		The forum data where the user details should be updated
	 * 
	 * @see
	 * 		PersistenceDataHandler#updateUser(long, Collection)
	 */
	public void updateUser(final SessionFactory ssFactory, final long userID, final Collection<Permission> permissions)
	throws NotRegisteredException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			MemberType tMemberToUpdate = this.getMemberTypeByID(session, userID);
			tMemberToUpdate.getPermissions().clear();
			tMemberToUpdate.getPermissions().addAll(this.parsePermissionsToString(permissions));
			session.update(tMemberToUpdate);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @param data
	 * 		The forum data where the user details should be updated

	 * @see
	 * 		PersistenceDataHandler#updateUser(long, String, String, String, String, boolean)
	 */
	public void updateUser(final SessionFactory ssFactory, final long userID, final String password,
			final String lastName, final String firstName, final String email, 
			final boolean shouldAskChangePassword) throws NotRegisteredException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			MemberType tMemberToUpdate = this.getMemberTypeByID(session, userID);
			if (tMemberToUpdate == null)
				throw new NotRegisteredException(userID);
			tMemberToUpdate.setPassword(password);
			tMemberToUpdate.setLastName(lastName);
			tMemberToUpdate.setFirstName(firstName);
			tMemberToUpdate.setEmail(email);
			tMemberToUpdate.setShouldAskChangePassword(shouldAskChangePassword);
			session.update(tMemberToUpdate);
			this.commitTransaction(session);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
}