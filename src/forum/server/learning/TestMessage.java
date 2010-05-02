package forum.server.learning;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class TestMessage {
	public static void main(String[] args) {
		Message message1 = new Message();
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
	}

	private static void deleteMessage(Message msg) {
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		try {
			tx = session.beginTransaction();
			session.delete(msg);
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
