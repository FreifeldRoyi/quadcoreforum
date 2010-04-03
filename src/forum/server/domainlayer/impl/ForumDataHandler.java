/**
 * 
 */
package forum.server.domainlayer.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.impl.message.MessagesCache;
import forum.server.domainlayer.impl.user.Member;
import forum.server.domainlayer.impl.user.PasswordEnDecryptor;
import forum.server.domainlayer.impl.user.Permission;
import forum.server.domainlayer.impl.user.UsersCache;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.server.persistentlayer.pipe.user.exceptions.MemberAlreadyExistsException;

/**
 * @author sepetnit
 *
 */
public class ForumDataHandler {
	private static String ADMIN_INIITIAL_USERNAME = "admin";
	private static String ADMIN_ENCRYPTED_PASSWORD = PasswordEnDecryptor.encryptMD5("1234");
	private static Set<Permission> ADMIN_PERMISSIONS = new HashSet<Permission>(); 
	
	static { // initializes admin permissions
		ForumDataHandler.ADMIN_PERMISSIONS.addAll(Arrays.asList(Permission.values()));
	}	
	
	private final UsersCache usersCache;
	private final MessagesCache messagesCache;
	
	private Member admin;
	
	public ForumDataHandler() throws DatabaseUpdateException {
		SystemLogger.info("Initializes cache memories");
		this.usersCache = new UsersCache();
		this.messagesCache = new MessagesCache();
		SystemLogger.info("Cache memories have been initializes successfuly");	
		this.initializeAdmin();
	}
	
	private void initializeAdmin() throws DatabaseUpdateException {
		SystemLogger.info("Building initial administrator");
		try {
			this.admin = this.getUsersCache().createNewMember(ForumDataHandler.ADMIN_INIITIAL_USERNAME, 
					ForumDataHandler.ADMIN_ENCRYPTED_PASSWORD, ForumDataHandler.ADMIN_INIITIAL_USERNAME,
					ForumDataHandler.ADMIN_INIITIAL_USERNAME,
					"admin@admin",
					ForumDataHandler.ADMIN_PERMISSIONS);
		}
		catch (MemberAlreadyExistsException e) {
			SystemLogger.info("Admin was previously created in the database");
		}
	}	
	
	public UsersCache getUsersCache() {
		return this.usersCache;
	}
	
	public MessagesCache getMessagesCache() {
		return this.messagesCache;
	}
}
