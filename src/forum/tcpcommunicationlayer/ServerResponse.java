package forum.tcpcommunicationlayer;

import java.io.Serializable;

/**
 * This is class represents a server response message sent to the client.<br>
 * (A response is sent after a message is received from the client).
 * 
 * @author Tomer Heber
 */
public class ServerResponse implements Serializable {
	
	private static final long serialVersionUID = -736273246091582824L;
	
	private String m_response;
	private boolean m_hasExecuted;
	private Object responseObj;
	
	
	public ServerResponse(String response, boolean hasExecuted) {
		m_response = response;
		m_hasExecuted = hasExecuted;
		this.responseObj = null;
	}
	
	/**
	 * @return Returns the response message sent by the server.
	 */
	public String getResponse() {
		return m_response;
	}
	
	/**
	 * The response also tells us if a message that we sent to the server was indeed successfully
	 * executed by the server.
	 * 
	 * @return True if a command message sent by the client was executed successfully by the server.	 
	 */
	public boolean hasExecuted() {
		return m_hasExecuted;
	}
	/**
	 * setter to the m_hasExecuted class member
	 * @param execflag - value for the HasExecuted class member. 
	 */
	public void setHasExecuted(boolean execflag){
		m_hasExecuted = execflag;
	}
	
	/**
	 * setter to the m_response class member
	 * @param response -value for the response class member.
	 */
	public void setResponse(String response){
		m_response = response;
	}
	
/*	public Object getResponseObject() {
		return this.responseObj;
	}
	
	public void setResponseObject(Object obj) {
		responseObj = obj;
	}
*/
}
