/**
 * 
 */
package forum.server.domainlayer.pipe;

import forum.server.domainlayer.impl.ForumImpl;
import forum.server.domainlayer.impl.ForumMessageImpl;
import forum.server.domainlayer.interfaces.Forum;
import forum.server.persistentlayer.pipe.persistenceDataHandler;

/**
 * @author sepetnit
 *
 */
public class Controller implements domainDataHandler {

	
	private Forum forum;

	public Controller() {
		forum = new ForumImpl();
		
	}
	
	public void addNewMessage(String subjectName, String userName,
			String title, String content) {
		ForumMessage tMsg = new ForumMessageImpl()
	}

	@Override
	public void addNewSubSubject(String fatherName, String name,
			String description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addNewSubject(String name, String description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void login(String username, String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerToForum(String username, String password,
			String lastName, String firstName, String email) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replyToMessage(int fatherID, String userName, String title,
			String content) {
		// TODO Auto-generated method stub
		
	}

}
