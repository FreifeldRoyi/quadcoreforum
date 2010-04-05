package forum.server.persistentlayer.pipe.user;

import java.util.*;

import forum.server.persistentlayer.*;
import forum.server.persistentlayer.pipe.*;
import forum.server.domainlayer.user.*;
import forum.server.persistentlayer.pipe.user.exceptions.*;

/**
 * This class is responsible of performing the operations of reading from and writing to the database
 * all the data which refers to the forum members
 */

/**
 * @author Sepetnitsky Vitali
 */
public class UsersPersistenceHandler {

	/**
	 * @param data
	 * 		The forum data from required data should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getFirstFreeMemberID()
	 */
	public long getFirstFreeMemberID(ForumType data) {
		long toReturn = -1;
		for (MemberType tCurrentMember : data.getMembers())
			if (tCurrentMember.getUserID() > toReturn)
				toReturn = tCurrentMember.getUserID();
		return toReturn++;
	}

	/**
	 * @param data
	 * 		The forum data from which the forum members should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getAllMembers()
	 */
	public Collection<ForumMember> getAllMembers(ForumType data) {
		Collection<ForumMember> toReturn = new Vector<ForumMember>();
		for (MemberType tCurrentMemberType : data.getMembers()) {
			try {
				toReturn.add(this.getMemberTypeByID(data, tCurrentMemberType.getUserID()));
			}
			catch (NotRegisteredException e) {
				continue; // do nothing
			}
		}
		return toReturn;
	}

	/**
	 * @param data
	 * 		The forum data from which the required user should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getUserByID(long)
	 */
	public ForumUser getUserByID(ForumType data, long userID) throws NotRegisteredException {
		return this.getMemberTypeByID(data, userID);
	}

	/**
	 * Performs a lookup in the database and returns a {@link MemberType} object, whose id equals to the given one
	 * 
	 * @param data
	 * 		The forum data from in which the required member should be found
	 * @param memberID
	 * 		The id of the member which should be found
	 * 
	 * @return
	 * 		The found member
	 * 
	 * @throws NotRegisteredException
	 * 		In case a member with the given id isn't registered to the forum (and therefore hasn't been found in the database)
	 */
	private ForumMember getMemberTypeByID(ForumType forum, long memberID) throws NotRegisteredException {
		for (MemberType tCurrentMemberType : forum.getMembers())
			if (tCurrentMemberType.getUserID() == memberID)
				return PersistentToDomainConverter.convertMemberTypeToForumMember(tCurrentMemberType);
		throw new NotRegisteredException(memberID);
	}


	/**
	 * @param data
	 * 		The forum data from which the required member should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getMemberByUsername(String)
	 */
	public ForumMember getMemberByUsername(final ForumType data, final String username) throws NotRegisteredException {
		for (MemberType tCurrentMemberType : data.getMembers())
			if (tCurrentMemberType.getUsername().equals(username))
				return PersistentToDomainConverter.convertMemberTypeToForumMember(tCurrentMemberType);
		throw new NotRegisteredException(username);
	}

	/**
	 * @param data
	 * 		The forum data from which the required member should be retrieved
	 * 
	 * @see
	 * 		PersistenceDataHandler#getMemberByEmail(String)
	 */
	public ForumMember getMemberByEmail(final ForumType data, final String email) throws NotRegisteredException {
		for (MemberType tCurrentMemberType : data.getMembers())
			if (tCurrentMemberType.getEMail().equals(email))
				return PersistentToDomainConverter.convertMemberTypeToForumMember(tCurrentMemberType);
		throw new NotRegisteredException(email);
	}

	/**
	 * @param data
	 * 		The forum data to which the new member should be added
	 * 
	 * @see
	 * 		PersistenceDataHandler#addNewMember(long, String, String, String, String, String, Collection)
	 */
	public void addNewMember(final ForumType data, final long id, final String username, final String password,
			final String lastName, final String firstName, final String email, final Collection<Permission> permissions) {
			MemberType tNewMemberType = ExtendedObjectFactory.createMemberType(id, username, password, lastName, 
					firstName, email, permissions);
			data.getMembers().add(tNewMemberType);
	}
}