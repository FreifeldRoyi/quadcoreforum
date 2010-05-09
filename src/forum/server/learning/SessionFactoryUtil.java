package forum.server.learning;

import java.io.File;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import forum.server.updatedpersistentlayer.DatabaseRetrievalException;

public class SessionFactoryUtil {

	private static org.hibernate.SessionFactory sessionFactory = null;

	private SessionFactoryUtil() {
		sessionFactory = null;
	}

	public static SessionFactory getInstance() throws DatabaseRetrievalException, com.mysql.jdbc.exceptions.jdbc4.CommunicationsException {
		try {
			if (sessionFactory == null)
				sessionFactory = new Configuration()
			.configure("forum/server/updatedpersistentlayer/hibernate.cfg.xml").buildSessionFactory();
			return sessionFactory;
		}
		catch (Exception e) {
			throw new DatabaseRetrievalException();
		}
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public static void close() {
		if (sessionFactory != null)
			sessionFactory.close();
		sessionFactory = null;
	}

}
