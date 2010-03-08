/**
 * 
 */
package forum.server.domainlayer;

/**
 * @author sepetnit
 *
 */
public interface EmailSendInterface {	
	public void sendAnEmail(String address, String title, String content);
}
