package forum.client.controllerlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import forum.server.Settings;
import forum.server.exceptions.subject.SubjectNotFoundException;
import forum.tcpcommunicationlayer.*;

/**
 * This class handles the communication between the client and the server.
 * 
 * @author Tomer Heber
 */
public class ClientConnectionController extends Thread {

	private Socket m_socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private static Logger log = Logger.getLogger(Settings.LOG_FILE_NAME);
	
	public ClientConnectionController(String addr, short port) throws IOException {
		InetAddress ia = InetAddress.getByName(addr);
		connect(ia,port);
	}
	
	@Override
	public void run() {
		BufferedReader prompt = new BufferedReader(new InputStreamReader(System.in));  
		try {
			printHelp();
			while (true) {
				/* Receives a command from the user. */
				System.out.print("> ");
				String str = prompt.readLine();
				
				if (str == null) {
					break;
				}
				
				if (str.equals("disconnect")) {
					break;
				}
				
				if (str.equals("help")) {
					printHelp();
					continue;
				}
								
				ClientMessage msg;
				try {
					/* Handles the command. */
					msg = handleCommand(str);						
				} catch (BadCommandException e) {
					log.info("The user has inputed an invalid command.");
					e.printStackTrace();
					System.out.println();
					continue;
				}
				/* send the message to the server. */
				out.writeObject(msg);
				/* receive response from the server. */
				Object o = in.readObject();
				if (o == null) {
					log.severe("Lost connection to server.");
					break;
				}
				if (!(o instanceof ServerResponse)) {
					log.severe("Received an invalid response from server.");
					break;
				}
				
				ServerResponse res = (ServerResponse)o;
				/* Check if the server has done the command. */
				if (res.hasExecuted()) {
					System.out.println("done!");
				}
				else {
					System.out.println("failed!");
				}
				/* Print the response from the server */
				System.out.println(res.getResponse());
				
			}
		} catch (ClassNotFoundException e) {
			log.severe("Received an invalid object from the server.");
			e.printStackTrace();
		} catch (IOException e) {
			log.severe("IOException occured while trying to read/send/write.");
			e.printStackTrace();			
		}
		finally {			
			try {
				/* Closing all the streams. */
				prompt.close();
				closeConnection();
			} catch (IOException e) {
				log.severe("IOException while trying to close streams.");
				e.printStackTrace();
			}			
		}
	}
	
	private void printHelp() {
		System.out.println(
				"help menu:" + "\n" +
		/*done*/		"- help " +  "\n" +
		/*done*/		"- add_message <root subject id> <username> <message title> <message content>" + "\n" +
		/*done*/		"- add_new_subject<root message id> <subject name> <subject description>" + "\n"+				
		/*done*/		"- add_reply <message id to reply to> <username> <message title><message content> " + "\n" +
		/*done*/		"- modify_message <message id to modify>e <new message title> <new message content>" + "\n" +
				//we don't have such function"- view_forum" + "\n" +
		/*done*/		"- logoff <username>" + "\n" +
		/*done*/		"- login <username> <password>" + "\n" +
		/*done*/		"- register <desired user name> <password> <last Name> <first Name> <email> " + "\n" +
		/*done*/		"- view_subjects<root subject id>" + "\n"+
		/*done*/		"- view_subject_content <root subject id>" + "\n"+
		/*done*/		"- view_message_replies <root message id>" + "\n"+
		/*done*/		"- view_Active_Guests" + "\n" +
		/*done*/		"- view_Active_Member_Names" + "\n"+
					
				"//TODO add more operations (Admin, Moderator, Search)"	+ "\n"			
		);								
		
	}

	/**
	 * This method receives commands and creates the proper message to send to the server.
	 * 
	 * @param str The command to do.
	 * @return The message to send back to the server.
	 * @throws BadCommandException This exception is thrown is the command given by the user is invalid.
	 */
	private ClientMessage handleCommand(String str) throws BadCommandException {		
		try {
			String[]splitTokens = str.split("\\ ");
			
			/** adding a new thread with a new first message **/ 
			if (splitTokens[0].equals("add_message")) {
				if (splitTokens.length != 5){
					throw new BadCommandException("usage: add_message <root subject id> <username> <message title> <message content>");
				}
				else{
					try {
					long tid = Long.parseLong(splitTokens[1]);
					return new AddNewThread(tid,splitTokens[2],splitTokens[3],splitTokens[4]);
					}
					catch( NumberFormatException e){
						throw new BadCommandException("The first argument must be a number we got: ," + splitTokens[1]);
					}
				}
			}

			if (splitTokens[0].equals("view_Active_Guests")) {
				return new ViewActiveGuests();
			}
			
			if (splitTokens[0].equals("view_Active_Member_Names")) {
				return new ViewActiveMemberNames();
			}
			if (splitTokens[0].equals("view_subjects")) {
				if (splitTokens.length != 2){
					throw new BadCommandException("usage: view_subjects<root subject id>");
				}
				else{
					try {
					long tid = Long.parseLong(splitTokens[1]);
					return new ViewSubjects(tid);
					}
					catch(NumberFormatException e){
						throw new BadCommandException("The first argument must be a number we got: ," + splitTokens[1]);
					}
				}
				
			}
			if (splitTokens[0].equals("view_subject_content")) {
				if (splitTokens.length != 2){
					throw new BadCommandException("usage: view_subject_content <root subject id>");
				}
				else{
					try {
					long tid = Long.parseLong(splitTokens[1]);
					return new ViewSubjectContent(tid);
					}
					catch(NumberFormatException e){
						throw new BadCommandException("The first argument must be a number we got: ," + splitTokens[1]);
					}
				}
				
			}
			if (splitTokens[0].equals("view_message_replies")) {
				if (splitTokens.length != 2){
					throw new BadCommandException("usage: view_message_replies <root message id>");
				}
				else{
					try {
					long tid = Long.parseLong(splitTokens[1]);
					return new ViewMessageReplies(tid);
					}
					catch(NumberFormatException e){
						throw new BadCommandException("The first argument must be a number we got: ," + splitTokens[1]);
					}
				}
				
			}
			
			if (splitTokens[0].equals("add_new_subject")) {
				if (splitTokens.length != 4){
					throw new BadCommandException("add_new_subject<root message id> <subject name> <subject description>");
				}
				else{
					try {
					long tid = Long.parseLong(splitTokens[1]);
					return new AddNewSubject(tid,splitTokens[2],splitTokens[3]);
					}
					catch(NumberFormatException e){
						throw new BadCommandException("The first argument must be a number we got: ," + splitTokens[1]);
					}			
				}
			}
			if (splitTokens[0].equals("login")) {
				if(splitTokens.length!= 3){
					throw new BadCommandException("login <username> <password>");			
				}
				else{
					return new LoginMessage(splitTokens[1],splitTokens[2]);
				}
			}
					
			if (splitTokens[0].equals("logoff")) {
				if (splitTokens.length != 2){
					throw new BadCommandException("logoff <username>");
				}
				else{
					return new LogoffMessage(splitTokens[1]);			
				}
			}			
			if (splitTokens[0].equals("register")) {
				if (splitTokens.length != 6){
					throw new BadCommandException("register <desired user name> <password> <last Name> <first Name> <email>");
				}
				else{
				return new RegisterMessage(splitTokens[1],splitTokens[2],splitTokens[3],splitTokens[4],splitTokens[5]);
				}
			}
			
			if (splitTokens.equals("add_reply")) {
				if (splitTokens.length != 5){
					throw new BadCommandException("usage: add_reply <message id to reply to> <username> <message title><message content>");
				}
				else{
					try {
					long tid = Long.parseLong(splitTokens[1]);
					return new AddReplyMessage(tid,splitTokens[2],splitTokens[3],splitTokens[4]);
					}
					catch( NumberFormatException e){
						throw new BadCommandException("The first argument must be a number we got: ," + splitTokens[1]);
					}
				}
			}			
			if (splitTokens[0].equals("modify_message")) {
				if (splitTokens.length != 4){
					throw new BadCommandException("usage: modify_message <message id to modify>e <new message title> <new message content>");
				}
				else{
					try {
					long tid = Long.parseLong(splitTokens[1]);
					return new ModifyMessageMessage(tid,splitTokens[2],splitTokens[3]);
					}
					catch( NumberFormatException e){
						throw new BadCommandException("The first argument must be a number we got: ," + splitTokens[1]);
					}
				}
			}
			
			// TODO Add Search messages.
			// TODO Add Admin messages
			// TODO Add Moderator messages.
		}
		catch(Exception e) {
			throw new BadCommandException("The command "+str+" is invalid.");
		}
		
		throw new BadCommandException("The command "+str+" is unknown.");		
	}	

	private void connect(InetAddress addr, short port) throws IOException {
		SocketAddress sa = new InetSocketAddress(addr,port);
		/* Connect to the server */
		m_socket = new Socket();
		m_socket.connect(sa);
		out = new ObjectOutputStream(m_socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(m_socket.getInputStream());
		log.info("The client is connected to the server.");
	}
	
	private void closeConnection() throws IOException {
		in.close();
		out.close();
		m_socket.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String logFileName = "clientLog"+(int)(Math.random()*1000.0)+".log";
		if (args.length > 0) {
			logFileName = args[0];
		}
		try {
			/* Create a logger for the client (to a file...). */
			FileHandler handler = new FileHandler(logFileName);
			log.addHandler(handler);
		} catch (SecurityException e) {		
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		}
				
		try {
			/* Start the client */
			Thread thread = new ClientConnectionController("127.0.0.1",(short)1234);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		} catch (IOException e) {
			log.severe("An IOException was thrown while trying to connect to the server.");
			e.printStackTrace();
		}		
				
		log.info("Exiting...");

	}

}
