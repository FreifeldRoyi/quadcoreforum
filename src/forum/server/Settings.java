package forum.server;

/**
 * @author Vitali Sepetnitsky
 *
 */

/**
 * This class contains static fields which define the general settings of the forum
 * application, like the database file name, logger name etc.
 */
public class Settings {
	
	public static final String LOG_FILE_NAME = "forumQuadCore.log";

	public static String DB_FILES_LOCATION = 
		"src" + System.getProperty("file.separator") +
		"forum" + System.getProperty("file.separator") +
		"server" + System.getProperty("file.separator");
	public static String DB_FILE_NAME = "QuadCoreForumDB";

	public static String SCHEMA_FILE_FULL_LOCATION = DB_FILES_LOCATION + DB_FILE_NAME + ".xsd";
	public static String DB_FILE_FULL_LOCATION 	= DB_FILES_LOCATION + DB_FILE_NAME + ".xml";

	
	
}
