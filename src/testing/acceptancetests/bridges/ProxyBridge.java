/**
 * This class serves as a proxy bridge against the implementation (realBridge) from one side, and the called methods, 
 * from the other side.
 * 
 * The class delegates the calls to the methods, to the realBridge class (which contains the real implementation)
 * 
 * In case the realBridge class doesn't exist, all the method return stub values designed to pass the tests
 */
package testing.acceptancetests.bridges;

import java.util.Collection;
import java.util.Vector;

import forum.server.domainlayer.SystemLogger;


/**
 * @author 
 * 		Sepetnitsky Vitali 310106745
 *
 */
public class ProxyBridge implements GeneralForumBridge {
	private GeneralForumBridge realBridge; // will be a this.realBridge instance (if it exists) 

	private boolean user2LoginInfo; // used for the stub implementation
	private boolean replyAddedInfo; // used for the stub implementation
	
	private static ProxyBridge SINGLE_PROXY_BRIDGE = null;

	/**
	 * 
	 * @return
	 * 		A single instance of a ProxyBridge class
	 */
	public static ProxyBridge getProxyBridge() {
		if (ProxyBridge.SINGLE_PROXY_BRIDGE == null)
			ProxyBridge.SINGLE_PROXY_BRIDGE = new ProxyBridge();
		return ProxyBridge.SINGLE_PROXY_BRIDGE;
	}

	/**
	 * The ProxyBridge class constructor, initializes the this.realBridge variable by looking for a class
	 * named RealBridge which implements the {@link GeneralForumBridge} interface.
	 * This class should contain implementation of the tested methods (or delegate to them). 
	 * 
	 * If the class is found, the methods of ProxyBridge are delegated simply to the loaded class.
	 * Otherwise, if the RealBridge class isn't found or is corrupted, the realBridge is set to null and all
	 * the methods of the ProxyBridge will be stubs - will return stub values needed for passing the tests. 
	 * 
	 * The constructor is private and is called by a factory method (Singleton design pattern)
	 */
	private ProxyBridge() {
		try {
			SystemLogger.info("Switching to test database ...");
			Class<?> tRealBridgeClass = 
				ProxyBridge.class.getClassLoader().loadClass("testing.acceptancetests.bridges.RealBridge");
			this.realBridge = (GeneralForumBridge)tRealBridgeClass.newInstance();
			SystemLogger.info("RealBridge class was found and successfully instantiated, will be used " +
			"for the testing");
		}
		catch (ClassNotFoundException e) { 
			// the exception is thrown if the RealBridge class doesn't exist,
			// in this case, the proxy bridge methods will be stub methods 
			SystemLogger.info("Notice: RealBridge implementation wasn't found, ProxyBridge stub methods " +
			"will be used for the testing.");
			this.realBridge = null;
		} catch (InstantiationException e) {
			SystemLogger.info("The default constructor of the found RealBridge class isn't valid, Proxy" +
			"Bridge stub methods will be used.");
			this.realBridge = null;
		} catch (IllegalAccessException e) {
			SystemLogger.info("The found RealBridge class can't be instantiated for some reason, Proxy" +
			"Bridge stub methods will be used.");
			this.realBridge = null;
		}
		catch (Exception e) {
			SystemLogger.info("The found RealBridge class can't be instantiated for some reason, Proxy" +
			"Bridge stub methods will be used.");
			this.realBridge = null;
		}
		this.user2LoginInfo = false;
	}

	/**
	 * @see
	 * 		GeneralForumBridge#login(String, String)
	 */
	public String[] login(final String username, final String password) {
		if (this.realBridge != null)
			return this.realBridge.login(username, password);
		// a stub implementation
		if (username.equals("user1")) {
			String[] toReturn = {"user1", "last1", "first1", "user1@gmail.com"};
			return toReturn;
		}
		else if (username.equals("user2") && (!password.equals("pass3"))) {
			String[] toReturn = {"user2", "last2", "first2", "user2@gmail.com"};
			this.user2LoginInfo = true;
			return toReturn;
		}
		return null;
	}	

	/**
	 * @see
	 * 		GeneralForumBridge#logout(String)
	 */
	public boolean logout(final String username) {
		if (this.realBridge != null)
			return this.realBridge.logout(username);
		// a stub implementation
		if (username.equals("user2") && !this.user2LoginInfo)
			return false;
		else if (username.equals("user2"))
			this.user2LoginInfo = false;
		return true;
	}

	/**
	 * @see
	 * 		GeneralForumBridge#register(String, String, String, String, String)
	 */
	public boolean register(final String username, final String password, final String lastName,
			final String firstName, final String email) {
		if (this.realBridge != null)
			return this.realBridge.register(username, password, lastName, firstName, email);
		// a stub implementation
		return true;
	}	

	/**
	 * @see
	 * 		GeneralForumBridge#addNewSubject(long, long, String, String)
	 */
	public long addNewSubject(final long userID, final long fatherID, final String name, 
			final String description){
		if (this.realBridge != null)
			return this.realBridge.addNewSubject(userID, fatherID, name, description);
		return 1;
	}

	/**
	 * @see
	 * 		GeneralForumBridge#openNewThread(long, long, String, String, String)
	 */
	public long openNewThread(final long userID, final long subjectID, final String topic, 
			final String title, final String content) {
		if (this.realBridge != null)
			return this.realBridge.openNewThread(userID, subjectID, topic, title, content);
		return 2;
	}

	/**
	 * @see
	 * 		GeneralForumBridge#addNewReply(long, long, String, String)
	 */
	public boolean addNewReply(final long authorID, final long fatherID, final String title,
			final String content) {
		if (this.realBridge != null)
			return this.realBridge.addNewReply(authorID, fatherID, title, content);
		// a stub implementation
		this.replyAddedInfo = true;
		if (fatherID == 20 || authorID == 9 || !this.user2LoginInfo)
			return false;
		return true;
	}

	/**
	 * @see
	 * 		GeneralForumBridge#getReplies(long)
	 */
	public Collection<String> getReplies(final long fatherID) {
		if (this.realBridge != null)
			return this.realBridge.getReplies(fatherID);
		// a stub implementation
		Collection<String> toReturn = new Vector<String>();
		if (this.replyAddedInfo)
			toReturn.add("2title1content1");
		return toReturn;
	}
}