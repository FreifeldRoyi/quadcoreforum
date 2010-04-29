package forum.client.controllerlayer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import forum.server.domainlayer.SystemLogger;
import forum.tcpcommunicationlayer.*;

/**
 * This class handles the communication between the client and the server.
 * 
 * @author Tomer Heber
 */
public class ClientConnectionController extends Observable {

	private final ExecutorService pool = Executors.newCachedThreadPool();

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private void connect(InetAddress addr, short port) throws IOException {
		SocketAddress sa = new InetSocketAddress(addr,port);
		/* Connect to the server */
		this.socket = new Socket();
		this.socket.connect(sa);
		out = new ObjectOutputStream(this.socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(this.socket.getInputStream());
		SystemLogger.info("The client is connected to the server.");
	}

	public boolean isConnected() {
		return this.socket.isConnected();		
	}

	public void closeConnection() throws IOException {
		in.close();
		out.close();
		this.socket.close();
		setChanged();
		notifyObservers(null);
	}

	public ClientConnectionController(String addr, short port) throws IOException {
		InetAddress tInetAddress = InetAddress.getByName(addr);
		this.connect(tInetAddress, port);
	}

	public void handleQuery(final ClientMessage messageToSend) {
		Runnable tSenderReceiver = new Runnable() {
			private boolean exceptionThrown = false;
			public void run() {
				try {
					/* sends the message to the server. */
					synchronized (out) {
						out.writeObject(messageToSend);

					}
					System.out.println(messageToSend.getID() + " sended");
					/* receive responses from the server. */
					Object o = null; // the server response
					synchronized (in) {
						o = in.readObject();
					}
					System.out.println("Read");
					if (o == null) {
						SystemLogger.severe("Lost connection to server.");
						setChanged();
						notifyObservers(null);
					}
					if (!(o instanceof ServerResponse))
						SystemLogger.severe("Received an invalid response from server.");
					else {
						ServerResponse res = (ServerResponse)o;
						setChanged();
						notifyObservers(res);
					}
				}
				catch (ClassNotFoundException e) {
					this.exceptionThrown = true;
					SystemLogger.severe("Received an invalid object from the server.");
				}
				catch (IOException e) {
					this.exceptionThrown = true;
					SystemLogger.severe("IOException occured while trying to read/write/send.");
				}
				finally {
					if (this.exceptionThrown) {
						try {
							SystemLogger.info("Closing connections.");
							closeConnection();
						}
						catch (IOException e) {
							SystemLogger.severe("An IOException occurred while trying to close streams.");
						}
						setChanged();
						notifyObservers(null);
					}
				}
			}	
		};
		this.pool.execute(tSenderReceiver);
	}
}
