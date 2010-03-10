package forum.server.domainlayer;

import java.util.Vector;

public class Forum {
	public Vector<Subject> subjects;
	
	public Forum() { // dummy
		subjects = new Vector<Subject>();
		subjects.add(new Subject());
		subjects.add(new Subject());
		subjects.add(new Subject());
	}
	
}
