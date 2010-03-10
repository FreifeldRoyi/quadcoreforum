/**
 * 
 */
package forum.server.dummygui;


/**
 * @author sepetnit
 *
 */
public class ForumLogin {

	private enum LoggedUserOperations { 
		LOGGED_ADD_NEW, LOGGED_VIEW, LOGGED_LOGOFF, LOGGED_EXIT, LOGGED_ERROR, LOGGED_HELP; 

		/** 
		 * This method is used instead the standard ordinal() method, used with enums. 
		 * It allows to get the enum order starting from 1 in order to display these numbers 
		 * on the forum menu. 
		 *  
		 * @return 
		 * 		ordinal() + 1 
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
		System.out.println("\t" + LoggedUserOperations.LOGGED_VIEW.ordinalPlus1()		+ ": view forum messages as a guest user"); 
		System.out.println("\t" + LoggedUserOperations.LOGGED_ADD_NEW.ordinalPlus1() 	+ ": add a new message"); 
		System.out.println("\t" + LoggedUserOperations.LOGGED_LOGOFF.ordinalPlus1()    	+ ": logoff the forum\n"); 
		System.out.println("\t" + LoggedUserOperations.LOGGED_EXIT.ordinalPlus1()       + ": exit the program\n"); 
		System.out.println("The system waits for your choose ..."); 
	}

	/** 
	 * Gets the user choise from the available operation list, parses it and returns the suitable enum 
	 * element or an element which indicates that an error occured. 
	 *  
	 * @return 
	 *              The desired operation typed by user as an enum element 
	 */ 
	public LoggedUserOperations getStartScreenOperation() { 
		String tReadText = ""; 
		try { 
			tReadText = ForumPromt.USER_CHOICE_SCANNER.next();
			for (LoggedUserOperations val : LoggedUserOperations.values())
				if (tReadText.equals(val.ordinalPlus1() + ""))
					return val;
				else if (tReadText.equals("?"))
					return LoggedUserOperations.LOGGED_HELP;
			return LoggedUserOperations.LOGGED_ERROR;
		} 
		catch (Exception tIOException) { 
			System.out.println("An error has occured for some reason, can't read" + 
			"the required operation from the keyboard, please restart the program"); 
			System.exit(-1); 
			return LoggedUserOperations.LOGGED_ERROR; // just to calm the compiler (this code is unreachable) 
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
			System.out.println("A"); 
			return true; 
		case START_REGISTER: 
			System.out.println("B"); 
			return true; 
		case START_VIEW_MESSAGES: 
			System.out.println("C"); 
			return true; 
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











	public void manageLogin() { 
		System.out.println("Please type your username");
		String tUsername = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("Please type your password");
		String tPassword = ForumPromt.USER_CHOICE_SCANNER.next();

		// here the login should be called


		System.out.println("Wellcome"); // here the login should be handled






		System.out.println("Please choose one of the following operations:\n"); 
	}
}
