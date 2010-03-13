/**
 * 
 */
package forum.server.domainlayer.impl;

import java.util.Vector;

import forum.server.domainlayer.interfaces.*;
import forum.server.exceptions.user.*;
import forum.server.persistentlayer.SubjectType;

/**
 * @author Royi Friefeld
 *
 */
public class CentralForumImpl implements Forum {

	@Override
	public void addForumSubject(ForumSubject fs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector<RegisteredUser> getConnectedUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<SubjectType> getForumSubjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumOfConnectedUsers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RegisteredUser login(String username, String password)
			throws AlreadyConnectedException, NotRegisteredException {
		// TODO Auto-generated method stub
				
		throw new AlreadyConnectedException(username);
	}

	@Override
	public void logout(String username) throws NotConnectedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerUser(String username, String password, String prvName,
			String lastName, String email) {
		// TODO Auto-generated method stub
		
	} 

}
