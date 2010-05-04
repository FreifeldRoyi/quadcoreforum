package forum.server.learning;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import forum.server.domainlayer.user.ForumMember;
import forum.server.domainlayer.user.ForumUser;
import forum.server.persistentlayer.MemberType;
import forum.server.persistentlayer.pipe.PersistentToDomainConverter;
import forum.server.persistentlayer.pipe.user.exceptions.NotRegisteredException;

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
			System.out.println(getUserByID(SessionFactoryUtil.getInstance(), 1));
		} 
		catch (NotRegisteredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getFirstFreeMemberID(SessionFactory data) {
		Collection<ForumMember> toReturn = new Vector<ForumMember>();
		Session session = data.getCurrentSession();
		String query = "from MemberType";
		List tResult = executeQuery(session, query);		
		System.out.println((List<MemberType>)tResult);
	}
	
	public static ForumUser getUserByID(SessionFactory data, long userID) throws NotRegisteredException {
		Collection<ForumMember> toReturn = new Vector<ForumMember>();
		Session session = data.getCurrentSession();
		String query = "from MemberType where userID = " + userID;
		List tResult = executeQuery(session, query);
		if (tResult.isEmpty())
			throw new NotRegisteredException(userID);
		return PersistentToDomainConverter.convertMemberTypeToForumMember((MemberType) tResult.get(0));
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
