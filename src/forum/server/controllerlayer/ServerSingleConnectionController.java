package forum.server.controllerlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.impl.ForumFacade;
import forum.server.domainlayer.impl.MainForumLogic;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;
import forum.tcpcommunicationlayer.ClientMessage;
import forum.tcpcommunicationlayer.ServerResponse;

/**
 * This class handles a single TCP connection between a client and the server.
 * 
 * @author Tomer Heber
 */
public class ServerSingleConnectionController implements Runnable {

	private static final ExecutorService pool = Executors.newCachedThreadPool();
	
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ForumFacade forum;
	
	private ServerSingleConnectionController(Socket socket) throws IOException {
		try {
			this.forum = MainForumLogic.getInstance();
		}
		catch (Exception e) {
			// do nothing, the forum has been already initialized
		}
		this.socket = socket;		
		this.out = new ObjectOutputStream(this.socket.getOutputStream());
		this.out.flush();
		this.in = new ObjectInputStream(this.socket.getInputStream());		
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
	public void run() {		
		SystemLogger.info("Communication has started between " + this.socket.getInetAddress() + " and the server.");
		
		try {
			while (true) {
				/* Receive a message from the client */
				Object tReceivedObject = this.in.readObject();
				if (tReceivedObject == null) {
					SystemLogger.info("Client " + this.socket.getInetAddress()+ " has disconnected from the server.");
					break;
				}
				if (!(tReceivedObject instanceof ClientMessage)) {
					SystemLogger.warning("Received an invalid message from client " + this.socket.getInetAddress() + ".");
					break;
				}	
				SystemLogger.info("Received a message from client " + this.socket.getInetAddress() + ".");
				ClientMessage message = (ClientMessage)tReceivedObject;
			
				/* Operate on the message */				
				ServerResponse response = message.doOperation(this.forum);
				/* Send response back to the client */
				SystemLogger.info("Sending a response back to client " + this.socket.getInetAddress() + ".");
				this.out.writeObject(response);
			} 
		}
		catch (IOException e) {
			SystemLogger.severe("A readObject operation failed with client " + this.socket.getInetAddress() + ".");
		} catch (ClassNotFoundException e) {
			SystemLogger.severe("A bad  operation failed with client " + this.socket.getInetAddress() + ".");			
		} finally {
			SystemLogger.info("Closing connection with client "+ this.socket.getInetAddress() + ".");
			try {
				this.in.close();
				this.out.close();
				this.socket.close();
			} catch (IOException e) {
				SystemLogger.severe("Failed to close I/O streams with some client.");
				e.printStackTrace();
			}			
		}
	}
}