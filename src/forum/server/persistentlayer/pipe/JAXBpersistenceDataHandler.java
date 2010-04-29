/**
 * This class implements the persistenceDataHandler interface, it contains methods which allows database updating.
 * 
 * The interface is intended to the programmers who write the higher layers
 */
package forum.server.persistentlayer.pipe;

import java.util.*;
import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import forum.server.Settings;

import forum.server.domainlayer.message.*;
import forum.server.domainlayer.user.*;
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

	private JAXBContext jaxbContent;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;

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
	 * initializes the JAXB marshaller and unmarshaller objects to be connected to the database.
	 * 
	 * @throws JAXBException
	 * 		In case a JAXB xml database retrieval error has occurred because of a database file which isn't
	 * 		compatible with the schema
	 * @throws SAXException
	 * 		In case a JAXB xml database retrieval error has occurred
	 */
	private JAXBpersistenceDataHandler() throws JAXBException, SAXException {
		this.jaxbContent = JAXBContext.newInstance("forum.server.persistentlayer");
		this.unmarshaller = this.jaxbContent.createUnmarshaller();
		this.unmarshaller.setSchema(SchemaFactory.newInstance(
				XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(
						Settings.SCHEMA_FILE_FULL_LOCATION)));
		this.marshaller = this.jaxbContent.createMarshaller();
		this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
	}

	public void setInternalHandlers(JAXBpersistenceDataHandler util) {
		this.usersHandler = new UsersPersistenceHandler();
		this.messagesHandler = new MessagesPersistenceHandler();
	}

	/**
	 * Unmarshalles the database file and creates a persistence layer forum type object which
	 * contains all the forum data stored in the database, in an accessible way
	 * 
	 * @return
	 * 		An instance of {@link ForumType} which contains all the forum data in an accessible way
	 * 
	 * @throws DatabaseRetrievalException
	 * 		In case the required data can't be retrieved from the database
	 */
	private ForumType unmarshalDatabase() throws DatabaseRetrievalException {
		try {
			ForumType tForum = (ForumType)this.unmarshaller.unmarshal(new File(
					Settings.DB_FILE_FULL_LOCATION));
			return tForum;
		}
		catch (JAXBException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new DatabaseRetrievalException();
		}
	}

	/**
	 * Marshalles the given {@link ForumType} data to the database file
	 * 
	 * @throws DatabaseUpdateException
	 * 		In case an error occurred while trying to update the database with the given data 
	 */
	private void marshalDatabase(ForumType forum) throws DatabaseUpdateException {
		try {
			marshaller.marshal(forum, new File(Settings.DB_FILE_FULL_LOCATION));
		}
		catch (JAXBException e) {
			throw new DatabaseUpdateException();
		}
	}

	// User related methods

	/**
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeMessageID()
	 */

	public long getFirstFreeMemberID() throws DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.usersHandler.getFirstFreeMemberID(tForum);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getAllMembers()
	 */
	public Collection<ForumMember> getAllMembers() throws DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.usersHandler.getAllMembers(tForum);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getUserByID(long)
	 */
	public ForumUser getUserByID(final long memberID) throws NotRegisteredException, DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.usersHandler.getUserByID(tForum, memberID);
	}

	/**
	 * @see	
	 * 		PersistenceDataHandler#getMemberByUsername(String)
	 */
	public ForumMember getMemberByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.usersHandler.getMemberByUsername(tForum, username);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getMemberByEmail(String)
	 */
	public ForumMember getMemberByEmail(final String email) throws NotRegisteredException, DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.usersHandler.getMemberByEmail(tForum, email);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#addNewMember(long, String, String, String, String, String, Collection)
	 */
	public void addNewMember(final long id, final String username, final String password,
			final String lastName, final String firstName, final String email,
			final Collection<Permission> permissions) throws DatabaseUpdateException {
		try {
			ForumType tForum = this.unmarshalDatabase();
			this.usersHandler.addNewMember(tForum, id, username, password, lastName, firstName, email, permissions);
			this.marshalDatabase(tForum);
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
			ForumType tForum = this.unmarshalDatabase();
			this.usersHandler.updateUser(tForum, userID, permissions);
			this.marshalDatabase(tForum);
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
		ForumType tForum = this.unmarshalDatabase();
		return this.messagesHandler.getFirstFreeSubjectID(tForum);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getTopLevelSubjects()
	 */
	public Collection<ForumSubject> getTopLevelSubjects() throws DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.messagesHandler.getTopLevelSubjects(tForum);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getSubjectByID(long)
	 */
	public ForumSubject getSubjectByID(long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.messagesHandler.getSubjectByID(tForum, subjectID);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#addNewSubject(long, String, String, boolean)
	 */
	public void addNewSubject(long subjectID, String name, String description, boolean isTopLevel) throws DatabaseUpdateException {
		try {
			ForumType tForum = this.unmarshalDatabase();
			this.messagesHandler.addNewSubject(tForum, subjectID, name, description, isTopLevel);
			this.marshalDatabase(tForum);
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
			ForumType tForum = this.unmarshalDatabase();
			this.messagesHandler.updateSubject(tForum, id, subSubjects, threads);
			this.marshalDatabase(tForum);
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
		ForumType tForum = this.unmarshalDatabase();
		return this.messagesHandler.getFirstFreeThreadID(tForum);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getThreadByID(long)
	 */
	public ForumThread getThreadByID(long threadID) throws ThreadNotFoundException, DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.messagesHandler.getThreadByID(tForum, threadID);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#openNewThread(long, String, long)
	 */
	public void openNewThread(long threadID, String topic, long rootID) throws DatabaseUpdateException {
		try {
			ForumType tForum = this.unmarshalDatabase();
			this.messagesHandler.openNewThread(tForum, threadID, topic, rootID);
			this.marshalDatabase(tForum);
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
			ForumType tForum = this.unmarshalDatabase();
			Collection<Long> toReturn = this.messagesHandler.deleteAThread(tForum, threadID);
			this.marshalDatabase(tForum);
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
		ForumType tForum = this.unmarshalDatabase();
		return this.messagesHandler.getFirstFreeMessageID(tForum);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#getAllMessages()
	 */
	public Collection<ForumMessage> getAllMessages() throws DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.messagesHandler.getAllMessages(tForum);
	}
	
	/**
	 * @see
	 * 		PersistenceDataHandler#getMessageByID(long)
	 */
	public ForumMessage getMessageByID(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		ForumType tForum = this.unmarshalDatabase();
		return this.messagesHandler.getMessageByID(tForum, messageID);
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#addNewMessage(long, long, String, String)
	 */
	public void addNewMessage(final long messageID, final long userID, final String title, final String content) 
	throws DatabaseUpdateException {
		try {
			ForumType tForum = this.unmarshalDatabase();
			this.messagesHandler.addNewMessage(tForum, messageID, userID, title, content);
			this.marshalDatabase(tForum);
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}

	/**
	 * @see
	 * 		PersistenceDataHandler#updateMessage(long, String, String)
	 */
	public void updateMessage(final long messageID, final String newTitle, final String newContent) throws MessageNotFoundException, 
	DatabaseUpdateException {
		try {
			ForumType tForum = this.unmarshalDatabase();
			this.messagesHandler.updateMessage(tForum, messageID, newTitle, newContent);
			this.marshalDatabase(tForum);
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
			ForumType tForum = this.unmarshalDatabase();
			Collection<Long> toReturn = this.messagesHandler.deleteAMessage(tForum, messageID);
			this.marshalDatabase(tForum);
			return toReturn;
		}
		catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
}