package forum.swingclient.controllerlayer;

import java.awt.Component;
import java.io.IOException;

import forum.swingclient.ui.events.GUIEvent;
import forum.server.domainlayer.SystemLogger;

/**
 * This abstract class is in charge of the communication between the UI layer and the Controller layer.
 * 
 * @author Tomer Heber
 */
public abstract class ControllerHandler extends GUIObservable {
	
	protected ClientConnectionController connectionController;
	
	
	public synchronized void notifyObservers(GUIEvent event) {
		setChanged();
		super.notifyObservers(event);
	}
	
	public void closeConnection() {
		try {
			this.connectionController.closeConnection();
		} 
		catch (IOException e) {
			SystemLogger.severe("An I/O error occurred while closing.");
		}
	}
	
	public abstract void getMemberDetails(final long memberID, final Component comp);
	
	public abstract void recoverPassword(final String username, final String email, final String password, final Component comp);

	public abstract void changePassword(final long memberID, final String prevPassword, final String newPassword, 
			final boolean shouldAskNewPassword, final Component comp);

	public abstract void updateMemberDetails(final Component comp, final long memberID, final String username,
			final String firstName, final String lastName, final String email);
	
	public abstract void registerToForum(final Component comp, String username, String password, 
			String email, String firstName, String lastName);
	
	public abstract boolean registerAsNewGuest(Component comp);
	
	
//	public abstract void removeMeAsGuest(long guestID);

	/**
	 * 
	 * @return an encoded view of the forum.<br>
	 * An example of such an encoding can be some sort of XML string.<br>
	 * For example:<br>
	 * <message> id: user: sdfsdf content: fsdfsd
	 * 		<message> 
	 *			...
	 *		</message>
	 *		<message>
	 *			...
  	 *		</message>
	 * </message>
	 */
	public abstract String getForumView();

	public abstract boolean logout(final Component comp, String username);

	
	public abstract void getNestedMessages(long rootID, boolean shouldUpdateViews, final Component comp);
	
	
	public abstract void getSubjects(long fatherID, final Component comp);
	
	public abstract void getThreads(long subjectID, final Component comp);
	
	public abstract boolean login(long guestID, String username, String password, final Component comp);
	
	/**
	 * Deletes recursively the subject with the given id and all its content.
	 * 
	 * @param id The id of the subject to delete.
	 */
	public abstract void deleteSubject(long userID, final long fatherID, long subjectID, Component comp);

	
	
	/**
	 * Tries to modify a message.
	 * 
	 * @param id The id of the message to be modified.
	 * @param newContent The new content of the message.
	 */
	public abstract void modifyMessage(final long authorID, long messageID, String newTitle, String newContent, Component comp);

	public abstract void modifyThread(final long authorID, long threadID, String newTopic, Component comp);

	public abstract void modifySubject(final long authorID, long subjectID, String newName, String newDescription, Component comp);

	/**
	 * Adds a reply message.
	 * */
	public abstract void addReplyToMessage(final long author, final long replyTo, 
			final String title, final String content, final Component comp);
	/**
	 * Deletes recursively the message id and all its sons.
	 * 
	 * @param id The id of the message to delete.
	 */
	public abstract void deleteMessage(long userID, long fatherID, long messageID, Component comp);

	/**
	 * Adds a new message to the forum.
	 */
	public abstract void addNewMessage(Component comp);
	
	public abstract void addNewSubject(final long userID, final long fatherID, final String name, final String description, final Component comp);
	
	public abstract void addNewThread (final long userID, final long subjectID, final String topic, final String title,
			final String content, final Component comp);
	public abstract void getActiveUsersNumber();
	
	public abstract void searchByAuthor(Component comp, String username);
	
	public abstract void searchByContent(Component comp, String phrase);	
	
	public abstract void getAllMembers(Component comp);
	
	public abstract void promoteToModerator(Component comp, String username);

	public abstract void demoteToMember(Component comp, String username);

	
	// according to the prev message id we know if the number of view of the thread should be updated - 
	// if the ids are the same then no otherwise yes
	public abstract void getPath(Component comp, long prevFatherMessageID, long messageID);
}
