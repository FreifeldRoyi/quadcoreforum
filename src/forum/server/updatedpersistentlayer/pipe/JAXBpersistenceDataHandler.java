/**
 * This class implements the persistenceDataHandler interface, it contains methods which allows database updating.
 * 
 * The interface is intended to the programmers who write the higher layers
 */
package forum.server.updatedpersistentlayer.pipe;

import java.util.*;
import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.SchemaFactory;

import org.hibernate.SessionFactory;
import org.xml.sax.SAXException;
import forum.server.Settings;

import forum.server.domainlayer.message.*;
import forum.server.domainlayer.user.*;
import forum.server.learning.SessionFactoryUtil;
import forum.server.persistentlayer.*;

import forum.server.persistentlayer.pipe.user.*;
import forum.server.persistentlayer.pipe.message.*;

import forum.server.persistentlayer.pipe.user.exceptions.*;
import forum.server.persistentlayer.pipe.message.exceptions.*;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class JAXBpersistenceDataHandler implements PersistenceDataHandler {

	private SessionFactory factory;
	
	// handles the physical operations of writing and reading of the database, related to the users
	private UsersPersistenceHandler usersHandler;
	// handles the physical operations of writing and reading of the database, related to the forum content (subjects, messages and threads)
	private MessagesPersistenceHandler messagesHandler;

	/**
	 * This method initializes a single instance of the class as being implemented using the Singleton design
	 * pattern
	 * 
	 * @return
	 * 		A single instance of the class
	 * @throws JAXBException
	 * 		In case a JAXB xml database retrieval error has occurred because of a database file which isn't
	 * 		compatible with the schema
	 * @throws SAXException
	 * 		In case a JAXB xml database retrieval error has occurred
	 */
	public static JAXBpersistenceDataHandler getInstance() throws JAXBException, SAXException {
		JAXBpersistenceDataHandler toReturn = new JAXBpersistenceDataHandler();
		toReturn.setInternalHandlers(toReturn);
		return toReturn;
	}

	/**
	 * 
	 * The class constructor.
	 * 
	 */
	private JAXBpersistenceDataHandler() {
		factory = SessionFactoryUtil.getInstance();	
	}

	private void setInternalHandlers(JAXBpersistenceDataHandler util) {
		this.usersHandler = new UsersPersistenceHandler();
		this.messagesHandler = new MessagesPersistenceHandler();
	}

	// User related methods

	/**
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeMessageID()
	 */

	public long getFirstFreeMemberID() throws DatabaseRetrievalException {
		return this.usersHandler.getFirstFreeMemberID(factory);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getAllMembers()
	 */
	public Collection<ForumMember> getAllMembers() throws DatabaseRetrievalException {
		return this.usersHandler.getAllMembers(factory);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getUserByID(long)
	 */
	public ForumUser getUserByID(final long memberID) throws NotRegisteredException, DatabaseRetrievalException {
		return this.usersHandler.getUserByID(factory, memberID);
	}

	/**
	 * @see	
	 * 		PersistenceDataHandler#getMemberByUsername(String)
	 */
	public ForumMember getMemberByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException {
		return this.usersHandler.getMemberByUsername(factory, username);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getMemberByEmail(String)
	 */
	public ForumMember getMemberByEmail(final String email) throws NotRegisteredException, DatabaseRetrievalException {
		return this.usersHandler.getMemberByEmail(factory, email);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#addNewMember(long, String, String, String, String, String, Collection)
	 */
	public void addNewMember(final long id, final String username, final String password,
			final String lastName, final String firstName, final String email,
			final Collection<Permission> permissions) throws DatabaseUpdateException {
		try {
			this.usersHandler.addNewMember(factory, id, username, password, lastName, firstName, email, permissions);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#updateUser(long, Collection)
	 */
	public void updateUser(final long userID, final Collection<Permission> permissions) throws
	NotRegisteredException, DatabaseUpdateException {
		try {
			this.usersHandler.updateUser(factory, userID, permissions);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Subject related methods

	/**
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeSubjectID()
	 */
	public long getFirstFreeSubjectID() throws DatabaseRetrievalException {
		return this.messagesHandler.getFirstFreeSubjectID(factory);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getTopLevelSubjects()
	 */
	public Collection<ForumSubject> getTopLevelSubjects() throws DatabaseRetrievalException {
		return this.messagesHandler.getTopLevelSubjects(factory);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getSubjectByID(long)
	 */
	public ForumSubject getSubjectByID(long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		return this.messagesHandler.getSubjectByID(factory, subjectID);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#addNewSubject(long, String, String, boolean)
	 */
	public void addNewSubject(long subjectID, String name, String description, boolean isTopLevel) throws DatabaseUpdateException {
		try {
			this.messagesHandler.addNewSubject(factory, subjectID, name, description, isTopLevel);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}

	}

	/**
	 * @see
	 * 		PersistenceDataHandler#updateSubject(long, Collection, Collection)
	 */
	public void updateSubject(long id, Collection<Long> subSubjects,
			Collection<Long> threads) throws SubjectNotFoundException, DatabaseUpdateException {
		try {
			this.messagesHandler.updateSubject(factory, id, subSubjects, threads);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Thread related methods

	/**
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeThreadID()
	 */
	public long getFirstFreeThreadID() throws DatabaseRetrievalException {
		return this.messagesHandler.getFirstFreeThreadID(factory);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getThreadByID(long)
	 */
	public ForumThread getThreadByID(long threadID) throws ThreadNotFoundException, DatabaseRetrievalException {
		return this.messagesHandler.getThreadByID(factory, threadID);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#openNewThread(long, String, long)
	 */
	public void openNewThread(long threadID, String topic, long rootID) throws DatabaseUpdateException {
		try {
			this.messagesHandler.openNewThread(factory, threadID, topic, rootID);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#deleteAThread(long)
	 */
	public Collection<Long> deleteAThread(final long threadID) throws ThreadNotFoundException, DatabaseUpdateException {
		try {
			Collection<Long> toReturn = this.messagesHandler.deleteAThread(factory, threadID);
			return toReturn;
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	// Message related methods	

	/**
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeMessageID()
	 */
	public long getFirstFreeMessageID() throws DatabaseRetrievalException {
		return this.messagesHandler.getFirstFreeMessageID(factory);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getAllMessages()
	 */
	public Collection<ForumMessage> getAllMessages() throws DatabaseRetrievalException {
		return this.messagesHandler.getAllMessages(factory);
	}
	
	/**
	 * @see
	 * 		PersistenceDataHandler#getMessageByID(long)
	 */
	public ForumMessage getMessageByID(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		return this.messagesHandler.getMessageByID(factory, messageID);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#addNewMessage(long, long, String, String)
	 */
	public void addNewMessage(final long messageID, final long userID, final String title, final String content) 
	throws DatabaseUpdateException {
		try {
			this.messagesHandler.addNewMessage(factory, messageID, userID, title, content);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#updateMessage(long, String, String)
	 */
	public void updateMessage(final long messageID, final String newTitle, final String newContent, final Collection<Long> replies) throws MessageNotFoundException, 
	DatabaseUpdateException {
		try {
			this.messagesHandler.updateMessage(factory, messageID, newTitle, newContent, replies);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#deleteAMessage(long)
	 */
	public Collection<Long> deleteAMessage(final long messageID) throws MessageNotFoundException, DatabaseUpdateException {
		try {
			Collection<Long> toReturn = this.messagesHandler.deleteAMessage(factory, messageID);
			return toReturn;
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
}