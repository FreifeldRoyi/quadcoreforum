package forum.client.controllerlayer;

import java.awt.Component;
import java.io.IOException;

import forum.client.ui.events.GUIEvent;
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
	
	public abstract boolean registerAsNewGuest(Component comp);
	
	/**
	 * 
	 * @return and encoded view of the forum.<br>
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

	public abstract void getSubjects(long fatherID, final Component comp);
	
	/**
	 * Tries to modify a message.
	 * 
	 * @param id The id of the message to be modified.
	 * @param newContent The new content of the message.
	 */
	public abstract void modifyMessage(long id, String newContent, Component comp);

	/**
	 * Adds a reply message.
	 * 
	 * @param id The id of the message to which we reply.
	 * @param string The content of the new message.
	 */
	public abstract void addReplyToMessage(long id, String string, Component comp);

	/**
	 * Deletes recursively the message id and all his sons.
	 * 
	 * @param id The id of the message to delete.
	 */
	public abstract void deleteMessage(long id, Component comp);

	/**
	 * Adds a new message to the forum.
	 */
	public abstract void addNewMessage(Component comp);

	
	
}
