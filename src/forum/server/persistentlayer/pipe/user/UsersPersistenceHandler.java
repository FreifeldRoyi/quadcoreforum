package forum.server.persistentlayer.pipe.user;

import java.util.*;

import forum.server.persistentlayer.*;
import forum.server.persistentlayer.pipe.*;
import forum.server.domainlayer.impl.user.*;

import forum.server.persistentlayer.pipe.user.exceptions.*;

public class UsersPersistenceHandler {

	private JAXBInOutUtil inOutUtil;

	public UsersPersistenceHandler(JAXBInOutUtil util) {
		this.inOutUtil = util;
	}

	private Collection<Permission> parseStringPermissions(Collection<String> permissions) {
		Collection<Permission> toReturn = new HashSet<Permission>();
		for (String tCurrentPermission : permissions)
			try {
				toReturn.add(Permission.valueOf(tCurrentPermission));
			}
		catch (IllegalArgumentException e) {
			continue; // do nothing
		}
		return toReturn;
	}

	public Collection<Member> getAllMembers() throws DatabaseRetrievalException {
		ForumType tForum = this.inOutUtil.unmarshalDatabase();
		Collection<Member> toReturn = new Vector<Member>();
		for (MemberType tCurrentMemberType : tForum.getMembers()) {
			try {
				toReturn.add(this.getMemberByID(tForum, tCurrentMemberType.getUserID()));
			}
			catch (NotRegisteredException e) {
				continue; // do nothing
			}
		}
		return toReturn;
	}

	public User getMemberByID(long userID) throws NotRegisteredException, DatabaseRetrievalException {
		ForumType tForum = this.inOutUtil.unmarshalDatabase();
		return this.getMemberByID(tForum, userID);
	}

	private Member getMemberByID(ForumType forum, long memberID) throws NotRegisteredException {
		for (MemberType tCurrentMemberType : forum.getMembers())
			if (tCurrentMemberType.getUserID() == memberID)
				return new Member(tCurrentMemberType.getUserID(), tCurrentMemberType.getUsername(), 
						tCurrentMemberType.getPassword(), tCurrentMemberType.getLastName(), tCurrentMemberType.getFirstName(),
						tCurrentMemberType.getEMail(), this.parseStringPermissions(tCurrentMemberType.getPrivileges()));
		throw new NotRegisteredException(memberID);
	}


	public Member getMemberByUsername(final String username) throws NotRegisteredException, DatabaseRetrievalException {
		ForumType tForum = this.inOutUtil.unmarshalDatabase();
		for (MemberType tCurrentMemberType : tForum.getMembers())
			if (tCurrentMemberType.getUsername().equals(username))
				return new Member(tCurrentMemberType.getUserID(), tCurrentMemberType.getUsername(), 
						tCurrentMemberType.getPassword(), tCurrentMemberType.getLastName(), tCurrentMemberType.getFirstName(),
						tCurrentMemberType.getEMail(), this.parseStringPermissions(tCurrentMemberType.getPrivileges()));
		
		throw new NotRegisteredException(username);
	}

	public Member getMemberByEmail(final String email) throws NotRegisteredException, DatabaseRetrievalException {
		ForumType tForum = this.inOutUtil.unmarshalDatabase();
		for (MemberType tCurrentMemberType : tForum.getMembers())
			if (tCurrentMemberType.getEMail().equals(email))
				return new Member(tCurrentMemberType.getUserID(), tCurrentMemberType.getUsername(), 
						tCurrentMemberType.getPassword(), tCurrentMemberType.getLastName(), tCurrentMemberType.getFirstName(),
						tCurrentMemberType.getEMail(), this.parseStringPermissions(tCurrentMemberType.getPrivileges()));
		throw new NotRegisteredException(email);
	}

	public void addNewMember(long id, String username, String password,
			String lastName, String firstName, String email, Collection<Permission> permissions) throws DatabaseUpdateException {
		try {
			ForumType tForum = this.inOutUtil.unmarshalDatabase();
			MemberType tNewMemberType = ExtendedObjectFactory.createMemberType(id, username, password, lastName, 
					firstName, email, permissions);
			tForum.getMembers().add(tNewMemberType);
			this.inOutUtil.marshalDatabase(tForum);
		} catch (DatabaseRetrievalException e) {
			throw new DatabaseUpdateException();
		}
	}
}