/**
 * 
 */
package forum.server.domainlayer.impl;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.impl.message.MessagesCache;
import forum.server.domainlayer.impl.user.UsersCache;

/**
 * @author sepetnit
 *
 */
public class ForumDataHandler {
	private final UsersCache usersCache;
	private final MessagesCache messagesCache;
	
	public ForumDataHandler() {
		SystemLogger.info("Initializes cache memories");
		this.usersCache = new UsersCache();
		this.messagesCache = new MessagesCache();
		SystemLogger.info("Cache memories have been initializes successfuly");
	}
	
	public UsersCache getUsersCache() {
		return this.usersCache;
	}
	
	public MessagesCache getMessagesCache() {
		return this.messagesCache;
	}
}
