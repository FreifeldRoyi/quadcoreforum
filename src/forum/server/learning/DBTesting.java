package forum.server.learning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Tomer Heber
 *
 */
public class DBTesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			/* Load the hsqldb driver which implements the jdbc interface */
			Class.forName("com.mysql.jdbc.Driver" );
		} catch (Exception e) {
			return;
		}

		try {
			/* 			 
			 * Make sure that you've started the SQL server:
			 * 1. Open the command prompt.
			 * 2. Go to the bin directory ./hsqldb/bin.
			 * 3. java -cp ../lib/hsqldb.jar org.hsqldb.Server -database.0 file:mydb -dbname.0 xdb
			 * 
			 * mydb = the filenames that will be created.
			 * xdb = the data base name.
			 * Connect to the database called xdb with the username sa and an empty password
			 */
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/quadcoreforumdb",
					"root", "1234");
			/* Create a sql statement. With the sql statement we do operations on the database */
			Statement stmt = c.createStatement();
			
			try {
				/* Create a table called Student, with two columns: id (as primary key) and name */
				stmt.executeUpdate("CREATE TABLE Student " +
								  	"(" +
									"id varchar(20) not null," +
									"name varchar(20) not null," +
									"primary key(id)" +
									")"
						);					
			}
			catch (SQLException e) {
				/* Exception is thrown in case the table is already created */
			}
			
			try {
				stmt.executeUpdate("DELETE FROM Student");
				/* Insert to the table Student the row 1,Moshe Cohen */
				stmt.executeUpdate("INSERT INTO Student " +
									"VALUES (1,'Moshe Cohen')"					
				);
			}
			catch (SQLException e) {			
				/* 
				 * Exception is thrown if a row with id 1 is already in the table.
				 * The id must be unique since it's a primary key.
				 */
			}
			
			/* Receive the table Student by quering with the SELECT operation */
			ResultSet set = stmt.executeQuery("SELECT * FROM Student");
			while(set.next()) {
				/* Go over each of the returned rows and print to the screen the name column of each row */
			}

			/* Add some random row to the Student table */
			stmt.executeUpdate("INSERT INTO Student " +
								"VALUES ("+(int)(Math.random()*100.0)+",'Moshe Cohen"+(int)(Math.random()*100.0)+"')"					
			);			
			
			/* Close the statement and the connection */
			stmt.close();
			c.close();
		} catch (SQLException e) {	
		}
	}

}
