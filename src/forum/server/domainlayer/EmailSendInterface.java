/**
 * 
 */
package forum.server.domainlayer;

/**
 * @author sepetnit
 *
 */
public interface EmailSendInterface {
	public boolean sendAnEmail(String address, String title, String content);
}
