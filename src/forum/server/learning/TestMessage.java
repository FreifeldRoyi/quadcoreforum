package forum.server.learning;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;


import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import forum.server.domainlayer.message.ForumMessage;
import forum.server.domainlayer.message.ForumSubject;
import forum.server.domainlayer.message.ForumThread;
import forum.server.domainlayer.user.ForumMember;
import forum.server.domainlayer.user.Permission;
import forum.server.updatedpersistentlayer.*;
import forum.server.updatedpersistentlayer.pipe.PersistentToDomainConverter;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.MessageNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.SubjectNotFoundException;
import forum.server.updatedpersistentlayer.pipe.message.exceptions.ThreadNotFoundException;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotRegisteredException;


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

		try {
			SessionFactory factory = SessionFactoryUtil.getInstance();
			TestMessage test = new TestMessage();

			try {
				Set<Long> a = new HashSet<Long>();
				a.add(2L);
				//		a.add(5L);

				Set<Long> b = new HashSet<Long>();

				//				b.add(2L);
				b.add(2L);
				b.add(3L);

				
			//	System.out.println(test.getSubjectByID(factory, 1).getThreads());
			//	test.deleteAThread(factory, 1);
			//	System.out.println(test.getSubjectByID(factory, 1).getThreads());
				
				Set<Permission> ac = new HashSet<Permission>();
				ac.add(Permission.ADD_SUB_SUBJECT);
	
				System.out.println(test.getFirstFreeMemberID(factory));
				System.out.println(test.getAllMembers(factory));
				
				test.addNewMember(factory, 10, "abc", "def", "cc", "yy", "S@s", new HashSet<Permission>());
				System.out.println(test.getAllMembers(factory));
				test.updateUser(factory, 10, ac);
				System.out.println(test.getAllMembers(factory));
				
				System.out.println(test.getMemberByEmail(factory, "S@s").getPermissions());
				System.out.println(test.getMemberByUsername(factory, "abc").getPermissions());
				System.out.println(test.getMemberByID(factory, 10).getPassword());
				
				


			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("erro");
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("jjj");

			//			e.printStackTrace();
			return;
		}
	}

	
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
		System.out.println(query);
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
			final String lastName, final String firstName, final String email, 
			final Collection<Permission> permissions) throws DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			MemberType tNewMemberType = ExtendedObjectFactory.createMemberType(id, username, password, lastName, firstName, email, permissions);
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private ThreadType getThreadTypeByID(Session session,
			long threadID) throws DatabaseRetrievalException {
		try {
			return (ThreadType)session.get(ThreadType.class, threadID);
		}
		catch (HibernateException e) {
			throw new DatabaseRetrievalException();
		}
	}
	
	
	/* if the message wasn't found - an empty collection will be returned. */
	private Collection<Long> findMessageAndRepliesIDs(SessionFactory ssFactory, 
			long messageID) throws DatabaseRetrievalException {
		Collection<Long> toReturn = new Vector<Long>();
		MessageType tFatherMessage = this.getMessageTypeByID(ssFactory.getCurrentSession(), messageID);
		if (tFatherMessage != null) {
			for (long tReplyID : tFatherMessage.getRepliesIDs()) {
				toReturn.addAll(this.findMessageAndRepliesIDs(ssFactory, tReplyID));
			}
			toReturn.add(messageID);
		}
		return toReturn;
	}	

	private MessageType getMessageTypeByID(Session session, 
			long messageID) throws DatabaseRetrievalException {
		try {
			return (MessageType)session.get(MessageType.class, messageID);
		}
		catch (HibernateException e) {
			throw new DatabaseRetrievalException();
		}
	}		
	
	public Collection<Long> deleteAThread(SessionFactory ssFactory, 
			long threadID) throws ThreadNotFoundException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);		
			ThreadType tThreadToDelete = this.getThreadTypeByID(session, threadID);
			if (tThreadToDelete == null) {
				this.commitTransaction(session);
				throw new ThreadNotFoundException(threadID);				
			}
			else {
				long tRootMessageID = tThreadToDelete.getStartMessageID();
				Collection<Long> toReturn = this.findMessageAndRepliesIDs(ssFactory, tRootMessageID);
				session.delete(tThreadToDelete);
				try {
					this.deleteAMessage(ssFactory, tRootMessageID);
				}
				catch (MessageNotFoundException e) {
					this.commitTransaction(session);
				}
				return toReturn;
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	public Collection<Long> deleteAMessage(SessionFactory ssFactory, 
			long messageID) throws MessageNotFoundException, DatabaseUpdateException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);
			try {
				Collection<Long> tMessagesIDsToDelete = this.findMessageAndRepliesIDs(ssFactory, messageID);
				MessageType tMessageType = this.getMessageTypeByID(session, messageID);
				if (tMessageType == null)
					throw new MessageNotFoundException(messageID);
				session.delete(tMessageType);
				for (Long tReplyIDToDelete : tMessagesIDsToDelete) {
					if (tReplyIDToDelete != messageID) {
						MessageType tCurrentReply = this.getMessageTypeByID(session, tReplyIDToDelete);
						try {
							session.delete(tCurrentReply);
						}
						catch (HibernateException e) {
							//TODO: add logging
						}
					}
				}
				this.commitTransaction(session);			
				return tMessagesIDsToDelete;
			}
			catch (DatabaseRetrievalException e) {
				this.commitTransaction(session);
				throw new DatabaseUpdateException();
			}
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public SubjectType getSubjectTypeByID(Session session, 
			long subjectID) throws DatabaseRetrievalException {
		try {
			return (SubjectType)session.get(SubjectType.class, subjectID);
		}
		catch (HibernateException e) {
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * @param data
	 * 		The forum type from which the data should be read
	 * 
	 * @see
	 * 		PersistenceDataHandler#getSubjectByID(long)
	 */
	public ForumSubject getSubjectByID(SessionFactory ssFactory,
			long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		try {
			Session session = this.getSessionAndBeginTransaction(ssFactory);		
			SubjectType toConvert = this.getSubjectTypeByID(session, subjectID);
			if (toConvert == null) {
				this.commitTransaction(session);
				throw new SubjectNotFoundException(subjectID);
			}
			else {
				ForumSubject toReturn = PersistentToDomainConverter.convertSubjectTypeToForumSubject(toConvert);
				this.commitTransaction(session);
				return toReturn;
			}
		}
		catch (DatabaseUpdateException e) {
			throw new DatabaseRetrievalException();
		}
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
		catch (HibernateException e) {
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
	/*
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
	 */
}
