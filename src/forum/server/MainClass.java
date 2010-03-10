package forum.server;

import java.util.List;
import java.io.File;
import javax.xml.*;
import javax.xml.bind.*;
import javax.xml.validation.*;

import forum.server.dummygui.ForumPromt;
import forum.server.persistentlayer.*;

public class MainClass {

		
	public static void main(String[] args) {
		MainClass.unmarshalDatabase();
		ForumPromt test = new ForumPromt();
		test.playDummy();		
	}

	
}

