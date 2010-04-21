package forum.server.controllerlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import forum.server.domainlayer.SystemLogger;
import forum.server.domainlayer.MainForumLogic;
import forum.server.persistentlayer.DatabaseRetrievalException;
import forum.server.persistentlayer.DatabaseUpdateException;

/**
 * This class runs the server (currently set to port 1234).
 * 
 * @author Tomer Heber
 */
public class ServerConnectionController extends Thread {	
	public void run() {
		short port = 1234;

		try {
			ServerSocket tServerSocket = new ServerSocket(port);
			SystemLogger.info("Server has started running on port " + port + ".");
			MainForumLogic.getInstance(); // initialize all the logic and data
			while (true) {
				Socket tSocket = tServerSocket.accept(); // blocking operation
				SystemLogger.info("A connection was accepted from: " + tSocket.getInetAddress() + ".");
				ServerSingleConnectionController.startConnection(tSocket);
			}
		} 
		catch (IOException e) {
			SystemLogger.severe("An error has occurred in the server (IOException).");
		} 
		catch (DatabaseRetrievalException e) {
			SystemLogger.severe("An error has occurred in the server (DatabaseRetrievalException).");	
		}
		catch (DatabaseUpdateException e) {
			SystemLogger.severe("An error has occurred in the server (DatabaseUpdateException).");	
		}
		SystemLogger.info("Server has closed.");
	}

	/**
	 * @param
	 * 		args
	 */
	public static void main(String[] args) {		

		Thread tThread = new ServerConnectionController();
		/* Start the thread */
		tThread.start();
		try {
			/* Wait for the thread to finish running */
			tThread.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();	
		}		
	}

}