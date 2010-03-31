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

import forum.server.domainlayer.impl.message.ForumMessage;
import forum.server.domainlayer.impl.message.ForumSubject;
import forum.server.domainlayer.impl.message.ForumThread;
import forum.server.domainlayer.impl.user.Member;
import forum.server.domainlayer.impl.user.Permission;
import forum.server.domainlayer.impl.user.User;
import forum.server.persistentlayer.*;

import forum.server.persistentlayer.pipe.user.*;
import forum.server.persistentlayer.pipe.message.*;

import forum.server.persistentlayer.pipe.user.exceptions.*;
import forum.server.persistentlayer.pipe.message.exceptions.*;

/**
 * @author Sepetnitsky Vitali
 *
 */
public class JAXBpersistenceDataHandler implements PersistenceDataHandler, JAXBInOutUtil
{

	private JAXBContext jaxbContent;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;

	private UsersPersistenceHandler usersHandler;
	private MessagesPersistenceHandler messagesHandler;

	/*	public static void testMode() throws IOException {
		Settings.DB_FILES_LOCATION = "src" + System.getProperty("file.separator") +
		"forum" + System.getProperty("file.separator") +
		"server" + System.getProperty("file.separator") +
		"persistentlayer" + System.getProperty("file.separator") +
		"testing" + System.getProperty("file.separator");

		Settings.SCHEMA_FILE_FULL_LOCATION = Settings.DB_FILES_LOCATION + Settings.DB_FILE_NAME + ".xsd";
		Settings.DB_FILE_FULL_LOCATION 	= Settings.DB_FILES_LOCATION + Settings.DB_FILE_NAME + ".xml";

		Writer output = new BufferedWriter(new FileWriter(new File(Settings.DB_FILE_FULL_LOCATION)));
		output.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n\t" + 
				"<ForumType>\n\t\t" + 
				"<numOfMessages>0</numOfMessages>\n" +
				"</ForumType>"
		);
		output.close();
	}
	 */
	/*	public static void regularMode() {
		Settings.DB_FILES_LOCATION = "src" + System.getProperty("file.separator") +
		"forum" + System.getProperty("file.separator") +
		"server" + System.getProperty("file.separator");

		Settings.SCHEMA_FILE_FULL_LOCATION = Settings.DB_FILES_LOCATION + Settings.DB_FILE_NAME + ".xsd";
		Settings.DB_FILE_FULL_LOCATION 	= Settings.DB_FILES_LOCATION + Settings.DB_FILE_NAME + ".xml";
	}
	 */

	public static JAXBpersistenceDataHandler getInstance() throws JAXBException, SAXException {
		JAXBpersistenceDataHandler toReturn = new JAXBpersistenceDataHandler();
		toReturn.setInternalHandlers(toReturn);
		return toReturn;
	}

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
		this.usersHandler = new UsersPersistenceHandler(util);
		this.messagesHandler = new MessagesPersistenceHandler(util);
	}

	public ForumType unmarshalDatabase() throws DatabaseRetrievalException {
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

	public void marshalDatabase(ForumType forum) throws DatabaseUpdateException {
		try {
			marshaller.marshal(forum, new File(Settings.DB_FILE_FULL_LOCATION));
		}
		catch (JAXBException e) {
			throw new DatabaseUpdateException();
		}
	}

	// User related methods

	public Collection<Member> getAllMembers() throws DatabaseRetrievalException {
		return this.usersHandler.getAllMembers();
	}

	public User getMemberByID(final long memberID) throws NotRegisteredException, DatabaseRetrievalException {
		return this.usersHandler.getMemberByID(memberID);
	}

	public Member getMemberByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException {
		return this.usersHandler.getMemberByUsername(username);
	}

	public Member getMemberByEmail(final String email) throws NotRegisteredException, DatabaseRetrievalException {
		return this.usersHandler.getMemberByEmail(email);
	}

	public void addNewMember(final long id, final String username, final String password,
			final String lastName, final String firstName, final String email,
			final Collection<Permission> permissions) throws DatabaseUpdateException {
		this.usersHandler.addNewMember(id, username, password, lastName, firstName, email, permissions);
	}

	// Subject related methods

	public ForumSubject getSubjectByID(long subjectID) throws SubjectNotFoundException, DatabaseRetrievalException {
		return this.messagesHandler.getSubjectByID(subjectID);
	}

	public void addNewSubject(long subjectID, String name, String description) throws DatabaseUpdateException {
		this.messagesHandler.addNewSubject(subjectID, name, description);
	}

	public void updateSubject(long id, Collection<Long> subSubjects,
			Collection<Long> threads) throws SubjectNotFoundException, DatabaseUpdateException {
		this.messagesHandler.updateSubject(id, subSubjects, threads);
	}

	// Thread related methods

	public ForumThread getThreadByID(long threadID) throws ThreadNotFoundException, DatabaseRetrievalException {
		return this.messagesHandler.getThreadByID(threadID);
	}

	public void openNewThread(long threadID, String topic, long rootID) throws DatabaseUpdateException {
		this.messagesHandler.openNewThread(threadID, topic, rootID);
	}

	public Collection<Long> deleteAThread(final long threadID) throws ThreadNotFoundException, DatabaseUpdateException {
		return this.messagesHandler.deleteAThread(threadID);
	}

	// Message related methods	

	public ForumMessage getMessageByID(final long messageID) throws MessageNotFoundException, DatabaseRetrievalException {
		return this.messagesHandler.getMessageByID(messageID);
	}

	public void addNewMessage(final long messageID, final long userID, final String title, final String content) 
	throws DatabaseUpdateException {
		this.messagesHandler.addNewMessage(messageID, userID, title, content);
	}

	public void updateMessage(final long messageID, final String newTitle, final String newContent) throws MessageNotFoundException, 
	DatabaseUpdateException {
		this.messagesHandler.updateMessage(messageID, newTitle, newContent);
	}

	public Collection<Long> deleteAMessage(final long messageID) throws MessageNotFoundException, DatabaseUpdateException {
		return this.messagesHandler.deleteAMessage(messageID);
	}
}