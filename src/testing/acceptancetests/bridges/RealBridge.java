/**
 * This class is an implementation of the real bridge which serves as a bridge between
 * the acceptance tests and the forum implementation.
 */
package testing.acceptancetests.bridges;

import java.util.Collection;
import java.util.Vector;

import forum.server.domainlayer.*;
import forum.server.domainlayer.interfaces.UIMember;
import forum.server.domainlayer.interfaces.UIMessage;
import forum.server.updatedpersistentlayer.pipe.user.exceptions.NotConnectedException;

public class RealBridge implements GeneralForumBridge {

	public ForumFacade forum;

	/**
	 * The class constructor.
	 * 
	 * Creates and initializes the forum logic class, to which the methods are
	 * delegated.
	 * 
	 * @throws Exception
	 * 		In case of a database connection error 
	 */
	public RealBridge() throws Exception {
		this.forum = MainForumLogic.getInstance();
	}

	/**
	 * @see
	 * 		GeneralForumBridge#login(String, String)
	 */
	public String[] login(final String username, final String password) {
		try {
			UIMember tLoggedIn = this.forum.login(username, password);
			String[] toReturn = new String[5];
			toReturn[0] = tLoggedIn.getUsername();
			toReturn[1] = tLoggedIn.getLastName();
			toReturn[2] = tLoggedIn.getFirstName();
			toReturn[3] = tLoggedIn.getEmail();
			toReturn[4] = tLoggedIn.getID() + "";
			return toReturn;
		} 
		catch (Exception e) {
			return null;
		}
	}	

	/**
	 * @see
	 * 		GeneralForumBridge#logout(String)
	 */
	public boolean logout(final String username) {
		try {
			this.forum.logout(username);
			return true;
		} 
		catch (NotConnectedException e) {
			return false;
		}
	}

	/**
	 * @see
	 * 		GeneralForumBridge#register(String, String, String, String, String)
	 */
	public boolean register(final String username, final String password, final String lastName,
			final String firstName, final String email) {
		try {
			this.forum.registerNewMember(username, password, lastName, firstName, email);
			return true;
		} 
		catch (Exception e) {
			return false;
		}
	}	

	/**
	 * @see
	 * 		GeneralForumBridge#addNewSubject(long, long, String, String)
	 */
	public long addNewSubject(final long userID, final long fatherID, final String name, 
			final String description){
		try {
			return this.forum.addNewSubject(userID, fatherID, name, description).getID();
		} 
		catch (Exception e) {
			return -1;
		}
	}

	/**
	 * @see
	 * 		GeneralForumBridge#openNewThread(long, long, String, String, String)
	 */
	public long openNewThread(final long userID, final long subjectID, final String topic, 
			final String title, final String content) {
		try {
			return this.forum.openNewThread(userID, subjectID, topic, title, content).getRootMessageID();
		}
		catch (Exception e) {
			return -1;
		}

	}

	/**
	 * @see
	 * 		GeneralForumBridge#addNewReply(long, long, String, String)
	 */
	public boolean addNewReply(final long authorID, final long fatherID, final String title,
			final String content) {
		try {
			this.forum.addNewReply(authorID, fatherID, title, content);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * @see
	 * 		GeneralForumBridge#getReplies(long)
	 */
	public Collection<String> getReplies(final long fatherID) {
		try {
			Collection<UIMessage> tReplies = this.forum.getReplies(fatherID, false);
			Collection<String> toReturn = new Vector<String>();
			for (UIMessage tCurrent : tReplies) 
				toReturn.add(tCurrent.getAuthorID() + tCurrent.getTitle() + tCurrent.getContent());
			return toReturn;
		}
		catch (Exception e) {
			return null;
		}		
	}
}