package forum.server.domainlayer.interfaces;

import java.util.Vector;

import forum.server.exceptions.user.*;
import forum.server.persistentlayer.SubjectType;

public interface Forum 
{
	/* Methods */
	
	public void addForumSubject (ForumSubject fs);

	
	public void registerUser (String username, String password, String prvName, String lastName, String email);

	/**
	 * TODO: change to private
	 */
	//public void decConnectedUsers (RegisteredUser ru);
	// public void incConnectedUsers (RegisteredUser ru);

	public void logout(String username) throws NotConnectedException;
	public RegisteredUser login(String username, String password) throws AlreadyConnectedException,
		NotRegisteredException;
	
//	public void addNewThread (ForumSubject fs, ForumMessage fm);
	
//	public void addNewMessage (ForumSubject fs, ForumThread ft, ForumMessage father, ForumMessage self);
	
	/* Getters */
	
	public Vector<RegisteredUser> getConnectedUsers(); // in case we'll want to display the users' names

	public int getNumOfConnectedUsers();

	
	// new methods 
	public Vector<SubjectType> getForumSubjects();
}

/**
 * TODO write proper JavaDoc for Forum
 * TODO add methods
 * explanation: this entire class will have the functionality of thread, subject and message adding.
 * 	it will send the correct input to the public methods of each and every object.
 * 	example: message handling will be synchronized in some point (in thread) but here it will search for
 * 			the correct subject in the forum, and send the message to the subject, while the message
 * 			will be added in ForumSubject. there the add message method will find the correct thread 
 * 			and in the thread, to the correct part of the tree.
 * 			this functionality can be changed in time
 */
