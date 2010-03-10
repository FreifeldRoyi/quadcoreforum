/**
 * 
 */
package forum.server.domainlayer.impl;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.SchemaFactory;

import forum.server.domainlayer.interfaces.persistenceDataHandler;
import forum.server.exceptions.message.MessageNotFoundException;
import forum.server.exceptions.subject.SubjectAlreadyExistsException;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.server.exceptions.user.AlreadyConnectedException;
import forum.server.exceptions.user.NotConnectedException;
import forum.server.exceptions.user.NotRegisteredException;
import forum.server.exceptions.user.WrongPasswordException;
import forum.server.persistentlayer.Forum;

/**
 * @author sepetnit
 *
 */
public class JAXBpersistenceDataHandler implements persistenceDataHandler {

	private static String DB_FILES_LOCATION = 
		"src" + System.getProperty("file.separator") +
		"forum" + System.getProperty("file.separator") +
		"server" + System.getProperty("file.separator");
	private static String DB_FILE_NAME = "QuadCoreForumDB";

	private static String SCHEMA_FILE_FULL_LOCATION = DB_FILES_LOCATION + DB_FILE_NAME + ".xsd";
	private static String DB_FILE_FULL_LOCATION 	= DB_FILES_LOCATION + DB_FILE_NAME + ".xml";



	private Forum forum;
	private JAXBContext jaxbContent;
	private Unmarshaller unmarshaller;
	private Marshaller marshaller;


	public JAXBpersistenceDataHandler() {
		try {
			this.jaxbContent = JAXBContext.newInstance("forum.server.persistentlayer");
			this.unmarshaller = this.jaxbContent.createUnmarshaller();
			this.unmarshaller.setSchema(SchemaFactory.newInstance(
					XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(
							SCHEMA_FILE_FULL_LOCATION)));
			this.marshaller = this.jaxbContent.createMarshaller();
			this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			this.forum = getForumFromDatabase();
		}
		catch (Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private Forum getForumFromDatabase() throws JAXBException {
		return this.unmarshalDatabase();
	}

	private Forum unmarshalDatabase() throws JAXBException {
		Forum tForum = (Forum)unmarshaller.unmarshal(new File(
				DB_FILE_FULL_LOCATION));
		return tForum;
	}

	private void marshalDatabase() {
		try {
			marshaller.marshal(this.forum, new File(DB_FILE_FULL_LOCATION));
		} catch (JAXBException e) {
			System.out.println("For some reason the database can't be updated!!!");
			System.exit(-1);
		}
	}


	/* (non-Javadoc)
	 * @see forum.server.domainlayer.interfaces.persistenceDataHandler#registerToForum(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void registerToForum(String username, String password,
			String lastName, String firstName, String email)
	throws JAXBException, IOException {
		// TODO Auto-generated method stub

	}

	public void addNewMessage(String subjectName, String authorUsername,
			String msgTitle, String msgContent) throws JAXBException,
			IOException, SubjectNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addNewSubject(String father, String subjectName)
			throws JAXBException, IOException, SubjectAlreadyExistsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void login(String username, String password)
			throws AlreadyConnectedException, NotRegisteredException,
			WrongPasswordException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logoutUser(String username) throws NotConnectedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replyToMessage(int fatherID, String authorUsername,
			String replyTitle, String replyContent) throws JAXBException,
			IOException, MessageNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMessageContent(int messageID, String newContent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMessageTitle(int messageID, String newTitle) {
		// TODO Auto-generated method stub
		
	}

}
