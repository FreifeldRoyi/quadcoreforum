package forum.server.learning.compass.example;

import org.compass.annotations.*;

/**
 * http://www.compass-project.org/docs/2.2.0/reference/html/core-osem.html
 * @author Tomer Heber
 */
@Searchable
public class Message {

   @SearchableId
   private Long number; // identifier, each searchable object must have a unique ID.

   @SearchableProperty (name = "title")   
   private String title; // The name of the user who created this message

   @SearchableProperty (name = "content")
   private String body;

   public void setId(long id) {
       number = id;
   }

   public long getId() {
       return number;
   }

   public void setName(String name) {
       title = name;
   }

   public String getName() {
       return title;
   }

   public void setContents(String contents) {
       body = contents;
   }
   
   public String getContents() {
       return body;
   }

}
