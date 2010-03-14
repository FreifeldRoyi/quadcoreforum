package forum.server.domainlayer.impl;

import java.util.Vector;

import forum.server.domainlayer.interfaces.Forum;
import forum.server.domainlayer.interfaces.ForumSubject;
import forum.server.domainlayer.interfaces.RegisteredUser;
import forum.server.exceptions.user.AlreadyConnectedException;
import forum.server.exceptions.user.NotConnectedException;
import forum.server.exceptions.user.NotRegisteredException;
import forum.server.persistentlayer.SubjectType;

public class ForumImpl extends NamedComponentImpl implements Forum 
{
	private Vector<ForumSubject> subjects;
	
	public ForumImpl(String desc, String nm) {
		super(desc, nm);
		subjects = new Vector<ForumSubject>();
	}

	public void addForumSubject(ForumSubject fs) {
		subjects.add(fs);

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
		return null;
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
