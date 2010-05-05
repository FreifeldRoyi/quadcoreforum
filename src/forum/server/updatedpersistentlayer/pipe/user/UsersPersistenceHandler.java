package forum.server.updatedpersistentlayer.pipe.user;

import java.util.*;

import org.apache.commons.collections.Factory;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import forum.server.learning.Message;
import forum.server.learning.SessionFactoryUtil;
import forum.server.updatedpersistentlayer.*;
import forum.server.domainlayer.user.*;
import forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.*;

/**
 * This class is responsible of performing the operations of reading from and writing to the database
 * all the data which refers to the forum members
 */

/**
 * @author Sepetnitsky Vitali
 */
public class UsersPersistenceHandler {

	private void addMember(Session session, MemberType toAdd) throws DatabaseUpdateException {
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(toAdd);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					// add logging
				}
				throw new DatabaseUpdateException();
			}
		}
	}

	private void updateMember(Session session, MemberType toUpdate) 
	throws DatabaseUpdateException {
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.update(toUpdate);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					// add logging
				}
				throw new DatabaseUpdateException();
			}
		}
	}

	private void deleteMember(Session session, MemberType toDelete) throws DatabaseUpdateException {
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(toDelete);
			tx.commit();
		} 
		catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					// add logging
				}
				throw new DatabaseUpdateException();
			}
		}
	}

	/*	private static List executeQuery(Session session, String query) {
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List tResult = session.createQuery(query).list();
			tx.commit();
			return tResult;			
		} 
		catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
				// add logging
				}
				// throw again the first exception
				throw e;
			}
		}
		return null;
	}
	 */

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
		toReturn = tResult.get(0).longValue();
		try {
			this.commitTransaction(session);
		} 
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return ++toReturn;
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
		String query = "from MemberType";
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

	/**
	 * @param data
	 * 		The forum data from which the required user should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getUserByID(long)
	 */
	public ForumUser getUserByID(SessionFactory ssFactory, long userID) throws NotRegisteredException, 
	DatabaseRetrievalException {
		Session session = this.getSessionAndBeginTransaction(ssFactory);		
		MemberType tResult = (MemberType)session.load(MemberType.class, userID);
		if (tResult == null) {
			try {
				this.commitTransaction(session);
			}
			catch (DatabaseUpdateException e) {
				throw new DatabaseRetrievalException();
			}
			throw new NotRegisteredException(userID);
		}
		else {
			ForumUser toReturn = PersistentToDomainConverter.convertMemberTypeToForumMember(tResult);
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
	private ForumMember getMemberByField(final SessionFactory ssFactory, final String field, 
			final String value) throws NotRegisteredException, DatabaseRetrievalException {
		Session session = this.getSessionAndBeginTransaction(ssFactory);
		String query = "from MemberType where " + field  + " = " + value;
		List tResult = session.createQuery(query).list();
		if (tResult.size() != 1)
			throw new DatabaseRetrievalException();
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
			final String lastName, final String firstName, final String email, final Collection<Permission> permissions) {
		this.getSessionAndBeginTransaction(ssFactory);
		
		try {
			tx = session.beginTransaction();
			session.save(toAdd);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					// Second try catch as the rollback could fail as well
					tx.rollback();
				} catch (HibernateException e1) {
					// add logging
				}
				throw new DatabaseUpdateException();
			}
		}
		
		
		
		
		
		
		
		MemberType tNewMemberType = ExtendedObjectFactory.createMemberType(id, username, password, lastName, 
				firstName, email, permissions);
		data.getMembers().add(tNewMemberType);
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
	public void updateUser(final SessionFactory data, final long userID, final Collection<Permission> permissions)
	throws NotRegisteredException {
		MemberType tMemberToUpdate = this.getMemberTypeByID(data, userID);
		tMemberToUpdate.getPrivileges().clear();
		tMemberToUpdate.getPrivileges().addAll(this.parsePermissionsToString(permissions));
	}
}