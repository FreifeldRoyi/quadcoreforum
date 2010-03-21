package forum.server.controllerlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import forum.server.domainlayer.SystemLogger;

/**
 * This class runs the server (currently set to port 1234).
 * 
 * @author Tomer Heber
 */
public class ServerConnectionController extends Thread {
	
	
	public void run() {
		short port = 1234;
		
		try {
			ServerSocket ss = new ServerSocket(port);
			SystemLogger.info("Server has started running on port "+port+".");
			while (true) {
				Socket s = ss.accept();
				SystemLogger.info("A connection was accepted from: "+s.getInetAddress()+".");
				ServerSingleConnectionController.startConnection(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
			SystemLogger.severe("An error has accoured in the server (IOException).");
		}
		
		SystemLogger.info("Server has closed.");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		
		Thread t = new ServerConnectionController();
		/* Start the thread */
		t.start();
		try {
			/* Wait for the thread to finish running */
			t.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
			
		}
				
	}

}