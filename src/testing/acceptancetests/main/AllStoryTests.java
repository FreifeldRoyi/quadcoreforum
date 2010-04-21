/**
 * This class is a test suite which is used in order to run automatically all the acceptance
 * tests contained in this AT unit.
 */
package testing.acceptancetests.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testing.acceptancetests.stories.*;

/**
 * @author 
 * 		Sepetnitsky Vitali 310106745
 *
 */
@RunWith(Suite.class)
// Here the test classes which should be tested, are added
@Suite.SuiteClasses({LoginLogoutTestStory.class, ReplyToMessageTestStory.class})
public class AllStoryTests {}