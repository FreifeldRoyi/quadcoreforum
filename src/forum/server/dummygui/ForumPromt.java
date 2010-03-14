package forum.server.dummygui;

import java.util.Scanner;

import forum.server.domainlayer.interfaces.Forum;
import forum.server.domainlayer.pipe.Controller;

/** 
 * @author Sepetnitsky Vitali 
 * 
 */ 
public class ForumPromt { 

	public static Scanner USER_CHOICE_SCANNER = new Scanner(System.in); 
	public static Controller CONT = new Controller();
	
	private enum StartOperation { 
		START_LOGIN, START_REGISTER, START_VIEW_MESSAGES, START_NEW_SUBJECT, START_EXIT, START_ERROR, START_HELP; 

		/** 
		 * This method is used instead the standard ordinal() method, used with enums. 
		 * It allows to get the enum order starting from 1 in order to display these numbers 
		 * on the forum menu. 
		 *  
		 * @return 
		 *              ordinal() + 1 
		 */ 
		public int ordinalPlus1() { 
			return this.ordinal() + 1; 
		} 
	}

	/** 
	 * Prints a guide which shows the user all the available operations he can choose to perform 
	 */ 
	public void printStartScreenHelp() { 
		System.out.println("Please choose one of the following operations:\n"); 
		System.out.println("\t" + StartOperation.START_LOGIN.ordinalPlus1()         + ": login to the forum"); 
		System.out.println("\t" + StartOperation.START_REGISTER.ordinalPlus1()      + ": register to the forum"); 
		System.out.println("\t" + StartOperation.START_VIEW_MESSAGES.ordinalPlus1() + ": view forum messages as a guest user"); 
		System.out.println();
		System.out.println("\t" + StartOperation.START_NEW_SUBJECT.ordinalPlus1()   + ": add new subject to the forum"); 
		System.out.println("\t" + StartOperation.START_EXIT.ordinalPlus1()          + ": exit the program\n"); 
		System.out.println("The system waits for your choose ..."); 
	} 

	/** 
	 * Prints the welcome promt with the available operations 
	 */ 
	private void printStartPromt() { 
		System.out.println("\nWelcome to the QuadCoreForum system!"); 
		printStartScreenHelp(); // print the choice guide 
	} 


	/** 
	 * Gets the user choice from the available operation list, parses it and returns the suitable enum
	 * element or an element which indicates that an error occurred. 
	 *  
	 * @return 
	 *              The desired operation typed by user as an enum element 
	 */ 
	public StartOperation getStartScreenOperation() { 
		String tReadText = ""; 
		try { 
			tReadText = USER_CHOICE_SCANNER.next();  
			if (tReadText.equals(StartOperation.START_LOGIN.ordinalPlus1() + "")) 
				return StartOperation.START_LOGIN; 
			if (tReadText.equals(StartOperation.START_REGISTER.ordinalPlus1() + "")) 
				return StartOperation.START_REGISTER; 
			if (tReadText.equals(StartOperation.START_VIEW_MESSAGES.ordinalPlus1() + "")) 
				return StartOperation.START_VIEW_MESSAGES;
			if (tReadText.equals(StartOperation.START_NEW_SUBJECT.ordinalPlus1() + "")) 
				return StartOperation.START_NEW_SUBJECT;
			if (tReadText.equals(StartOperation.START_EXIT.ordinalPlus1() + "")) 
				return StartOperation.START_EXIT; 
			if (tReadText.equals("?")) 
				return StartOperation.START_HELP; 
			return StartOperation.START_ERROR; 

		} 
		catch (Exception tIOException) { 
			System.out.println("An error has occured for some reason, can't read" + 
			"the required operation from the keyboard, please restart the program"); 
			System.exit(-1); 
			return StartOperation.START_ERROR; // just to calm the compiler (this code is unreachable) 
		} 
	} 

	/** 
	 * Redirects the chosen start operation to the promt methods which are responsible to 
	 * handle this type of operation 
	 *  
	 * @param userChose 
	 *              The operation chosen by user (represented as an enum element)  
	 * @return 
	 *              true if a valid operation has chosen and false otherwise 
	 */ 
	public boolean redirectOperations(StartOperation userChose) { 
		switch (userChose) { 
		case START_LOGIN:
			new ForumLogin().manageLogin();
			return true; 
		case START_REGISTER: 
			new ForumFunctions().playRegister();
			return true; 
		case START_VIEW_MESSAGES:
			new ForumFunctions().viewMessages();
			return true; 
		case START_NEW_SUBJECT:
			new ForumFunctions().addNewSubject();
		case START_EXIT:  
			System.out.println("Exiting ..."); 
			System.exit(0);
			return true; // just to calm the compiler 
		case START_HELP: 
			return true; 
		default: 
			return false; 
		} 
	}

	public void playDummy() { 
		while (true) { 
			printStartPromt(); // print welcome ... 

			// get the user choice and redirect it or exit if exit is pressed 
			while (!redirectOperations(getStartScreenOperation())) { 
				System.out.println("This chose isn't performed, please try again! (press " + 
						StartOperation.START_EXIT.ordinalPlus1() + 
				" to exit and ? to help)"); 
				System.out.println(); 
				System.out.println("The system waits for your choose ..."); 
			} 
		} 
	}
	
	
	public static void main (String[] args) {
		new ForumPromt().playDummy(); // creates a new PromtForum instance which handles the promt
	}
		
} 

