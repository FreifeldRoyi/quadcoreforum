package forum.server.learning;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import forum.server.domainlayer.user.ForumMember;
import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter;


public class TestMessage {
	public static void main(String[] args) {
		/*Message message1 = new Message();
		Message message2 = new Message();

		message1.setTitle("First message title");
		message2.setTitle("Second message title");

		message1.setBody("First message body");
		message2.setBody("Second message body");

		createMessage(message1);
		createMessage(message2);

		deleteMessage(message1);

		message2.setTitle("New Title of second message");
		updateMessage(message2);
		*/
		
		
		//getFirstFreeMemberID(SessionFactoryUtil.getInstance());
		try {
			System.out.println(getFirstFreeMemberID(SessionFactoryUtil.getInstance()));
			System.out.println(getAllMembers(SessionFactoryUtil.getInstance()));
		} catch (DatabaseRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@SuppressWarnings("unchecked")
	public static Collection<ForumMember> getAllMembers(SessionFactory ssFactory) throws DatabaseRetrievalException {
		Collection<ForumMember> toReturn = new Vector<ForumMember>();
		Session session = getSessionAndBeginTransaction(ssFactory);
		String query = "from MemberType";
		List tResult = session.createQuery(query).list();
		for (MemberType tCurrentMemberType : (List<MemberType>)tResult) 
			toReturn.add(PersistentToDomainConverter.convertMemberTypeToForumMember(tCurrentMemberType));
		try {
			commitTransaction(session);
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return toReturn;
	}

	private static Session getSessionAndBeginTransaction(SessionFactory ssFactory) throws DatabaseRetrievalException {
		try {
			Session toReturn = ssFactory.getCurrentSession();
			toReturn.beginTransaction();
			return toReturn;
		}
		catch (RuntimeException e) {
			throw new DatabaseRetrievalException();
		}
	}

	private static void commitTransaction(Session session) throws DatabaseUpdateException {
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
	public static long getFirstFreeMemberID(SessionFactory ssFactory) throws DatabaseRetrievalException {
		long toReturn = -1;
		Session session = getSessionAndBeginTransaction(ssFactory);
		String query = "select max(userID) from MemberType";
		List<Long> tResult = (List<Long>)session.createQuery(query).list();
		toReturn = tResult.get(0).longValue();
		try {
			commitTransaction(session);
		} 
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
		return ++toReturn;
	}
	
	
	@SuppressWarnings("unchecked")
	private static List executeQuery(Session session, String query) {
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
	
	private static void deleteMessage(Message msg) {
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		try {
			tx = session.beginTransaction();
			session.delete(msg);
			session.createQuery("kjdshf dskjfh").list();
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
				// throw again the first exception
				throw e;
			}
		}
	}

	private static void createMessage(Message msg) {
		Transaction tx = null;

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		try {
			tx = session.beginTransaction();
			session.save(msg);
			tx.commit();
		} catch (RuntimeException e) {
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
	}

	private static void updateMessage(Message msg) {
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		try {
			tx = session.beginTransaction();
			session.update(msg);
			tx.commit();
		} catch (RuntimeException e) {
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
	}

}
