package forum.server.updatedpersistentlayer;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryUtil {

	private static org.hibernate.SessionFactory sessionFactory = new Configuration()
			.configure("forum/server/updatedpersistentlayer/hibernate.cfg.xml").buildSessionFactory();

	private SessionFactoryUtil() {
	}

	public static SessionFactory getInstance() {
		return sessionFactory;
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
