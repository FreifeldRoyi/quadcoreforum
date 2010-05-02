package forum.server.learning.compass.example;

import java.io.File;

import org.compass.core.Compass;
import org.compass.core.CompassHit;
import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;

/**
 *
 * @author Tomer Heber
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	File file = new File("src/forum/server/learning/compass/compassSettings.xml");
        /* The genData directory is where the search engine will save its data */
        CompassConfiguration conf = CompassConfigurationFactory
                .newConfiguration().configure(file);               



        Compass compass = conf.buildCompass();

        compass.getSearchEngineIndexManager().deleteIndex();

        /* Open a session (not thread safe!) for this thread */
        CompassSession session = compass.openSession();

        Message m = new Message();
        m.setId(2);
        m.setName("Mickey Mouse");
        m.setContents("Hello how are you today");

        /**
         * Add the message to the Search engine.
         * Since the Message is saved to the DB the message should be saved only once. 
         * After the data is already added to the DB, try removing this line and see what happens. 
         */
        session.save(m);

        /* http://www.compass-project.org/docs/2.2.0/reference/html/core-workingwithobjects.html#Searching */
        /* Search for the words "Bugs" */
        CompassHits hits = session.find("contents:\"Hello how are you today\"");
        CompassHit[] detachedHits = hits.detach(0, 10).getHits();

        for (int i = 0; i < detachedHits.length; i++) {
            System.out.println("score: "+detachedHits[i].getScore());
            Message m2 = (Message)(detachedHits[i].data());
            System.out.println(m2.getName());
            System.out.println(m2.getContents());
        }

        hits = session.find("contents: Hello OR cool OR today");
        detachedHits = hits.detach(0, 10).getHits();

        for (int i = 0; i < detachedHits.length; i++) {
            System.out.println("score: "+detachedHits[i].getScore());
            Message m2 = (Message)(detachedHits[i].data());
            System.out.println(m2.getName());
            System.out.println(m2.getContents());
        }

        hits = session.find("contents: Hello AND today");
        detachedHits = hits.detach(0, 10).getHits();

        for (int i = 0; i < detachedHits.length; i++) {
            System.out.println("score: "+detachedHits[i].getScore());
            Message m2 = (Message)(detachedHits[i].data());
            System.out.println(m2.getName());
            System.out.println(m2.getContents());
        }

        /* After you are done with the session close it */
        session.close();           
    }

}
