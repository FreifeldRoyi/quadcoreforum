/**
 * 
 */
package forum.server.dummygui;

import forum.server.domainlayer.interfaces.Forum;

/**
 * @author sepetnit
 *
 */
public class ForumFunctions 
{
	public void playRegister() 
	{
		System.out.println("please type your username!");
		String username = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type your password!");
		String password = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type your private name!");
		String firstName = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type your last name!");
		String lastName = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type your email!");
		String email = ForumPromt.USER_CHOICE_SCANNER.next();
		
		
		System.out.println(ForumPromt.CONT.registerToForum(username, password, lastName, firstName, email));
		System.out.println();
	}
	
	public void viewMessages() {
		String[] subjects = ForumPromt.CONT.getForumSubjects();
		for (int i = 0; i < subjects.length; i++)
			System.out.println(subjects[i]);
	}
	
	public void addNewSubject() {
		System.out.println("please type new name!");
		String name = ForumPromt.USER_CHOICE_SCANNER.next();
		System.out.println("please type new description!");
		String description = ForumPromt.USER_CHOICE_SCANNER.next();

		ForumPromt.CONT.addNewSubject(name, description);
	}
}
