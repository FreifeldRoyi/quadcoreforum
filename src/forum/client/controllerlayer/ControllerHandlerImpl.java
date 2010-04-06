package forum.client.controllerlayer;

import java.awt.Component;

import forum.client.ui.events.*;

/**
 * You need to delete all the code in here and implement it yourself.<br>
 * This code is just for you to understand how to work with the Observer/Observed and GUI.
 * 
 * @author Tomer Heber
 */
public class ControllerHandlerImpl extends ControllerHandler {

	/* (non-Javadoc)
	 * @see forumtree.contol.ControllerHandler#getForumView()
	 */
	@Override
	public String getForumView() {
		return "";
	}

	@Override
	public void modifyMessage(long id, String newContent, Component comp) {		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		notifyObservers(new ForumTreeRefreshEvent(comp,getForumView()));
		if (Math.random() > 0.5) {	
			notifyObservers(new ForumTreeErrorEvent("Failed to modify a message"));
		}
	}

	@Override
	public void addReplyToMessage(long id, String string, Component comp) {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		notifyObservers(new ForumTreeRefreshEvent(comp,getForumView()));
		if (Math.random() > 0.5) {	
			notifyObservers(new ForumTreeErrorEvent("Failed to reply to a message"));
		}
	}

	@Override
	public void deleteMessage(long id, Component comp) {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		notifyObservers(new ForumTreeRefreshEvent(comp,getForumView()));
		if (Math.random() > 0.5) {	
			notifyObservers(new ForumTreeErrorEvent("Failed to delete message"));
		}
		
	}

	@Override
	public void addNewMessage(Component comp) {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		notifyObservers(new ForumTreeRefreshEvent(comp,getForumView()));
		if (Math.random() > 0.5) {	
			notifyObservers(new ForumTreeErrorEvent("Failed to add a new message"));
		}
		
	}

}
