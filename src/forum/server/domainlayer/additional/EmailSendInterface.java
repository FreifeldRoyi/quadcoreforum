/**
 * 
 */
package forum.server.domainlayer.additional;

/**
 * @author sepetnit
 *
 */
public interface EmailSendInterface {	
	public void sendAnEmail(String address, String title, String content);
}
