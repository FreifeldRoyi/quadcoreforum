/**
 * 
 */
package forum.server.domainlayer.interfaces;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import forum.server.persistentlayer.*;

/**
 * @author sepetnit
 *
 */
public interface persistenceDataHandler {
	
	/**
	 * This method updates the database with a new registered user
	 * 
	 * @param username
	 * 		The given username
	 * @param password
	 * 		The given password
	 * @param lastName
	 * 		The given lastName
	 * @param firstName
	 * 		The given firstName
	 * @param email
	 * 		The given e-mail
	 * @throws JAXBException
	 * 		In case of marshal failure
	 * @throws IOException
	 * 		In case there is a problem with the database xml file
	 */
	public void registerToForum(String username, String password, String lastName, String firstName,
			String email) throws JAXBException, IOException;
	
	public void addNewMessage(String subjectName, String authorUsername, String msgTitle, String msgContent) 
		throws JAXBException, IOException, SubjectNotFoundException;
		
	
	//public void replyToMessage(String subjectName, String authorUsername, String msgTitle, String msgContent) 
	//	throws JAXBException, IOException, SubjectNotFoundException;

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
