package forum.server.controllerlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import forum.server.ForumFacade;
import forum.server.domainlayer.SystemLogger;
import forum.tcpcommunicationlayer.ClientMessage;
import forum.tcpcommunicationlayer.ServerResponse;

/**
 * This class handles a single TCP connection between a client and the server.
 * 
 * @author Tomer Heber
 */
public class ServerSingleConnectionController implements Runnable {

	private static final ExecutorService pool = Executors.newCachedThreadPool();
	
	private Socket m_socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	// TODO create an instance (maybe singleton?) of the facade.
	private ForumFacade forum = null;
	
	private ServerSingleConnectionController(Socket socket) throws IOException {
		m_socket = socket;		
		out = new ObjectOutputStream(m_socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(m_socket.getInputStream());		
	}
	
	/**
	 * This method is in charge of starting the communication between the server
	 * and the client.
	 * 
	 * @param socket The socket that connects between the client and the server
	 */
	public static void startConnection(Socket socket) {
		ServerSingleConnectionController sscc;
		try {
			sscc = new ServerSingleConnectionController(socket);
		} catch (IOException e) {
			SystemLogger.severe("Failed to start communication with client "+socket.getInetAddress()+".");
			e.printStackTrace();
			return;
		}
		pool.execute(sscc);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {		
		SystemLogger.info("Communication has started between "+m_socket.getInetAddress()+" and the server.");
		
		try {
			while (true) {
				/* Receive a message from the client */
				Object o = in.readObject();
				if (o == null) {
					SystemLogger.info("Client "+m_socket.getInetAddress()+"has disconnected from the server.");
					break;
				}
				if (!(o instanceof ClientMessage)) {
					SystemLogger.warning("Received an invalid message from client"+m_socket.getInetAddress()+".");
					break;
				}	
				
				SystemLogger.info("Received a message from client "+m_socket.getInetAddress()+".");
				ClientMessage message = (ClientMessage)o;
				/* Operate on the message */				
				ServerResponse response = message.doOperation(forum); 
				/* Send response back to the client */
				SystemLogger.info("Sending a response back to client "+m_socket.getInetAddress()+".");
				out.writeObject(response);
			} 
		}
		catch (IOException e) {
			SystemLogger.severe("A readObject operation failed with client "+m_socket.getInetAddress()+".");
			e.printStackTrace();				
		} catch (ClassNotFoundException e) {
			SystemLogger.severe("A bad  operation failed with client "+m_socket.getInetAddress()+".");			
			e.printStackTrace();
		} finally {
			SystemLogger.info("Closing connection with client "+m_socket.getInetAddress()+".");
			try {
				in.close();
				out.close();
				m_socket.close();
			} catch (IOException e) {
				SystemLogger.severe("Failed to close I/O streams with some client.");
				e.printStackTrace();
			}			
		}
	}

}
