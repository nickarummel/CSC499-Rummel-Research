import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import content.TestVisualFeatureDetection;

/**
 * Run all JUnit tests as a suite for project
 * @author Nick Rummel
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{
	TestVisualFeatureDetection.class

}
)

public class RunResearchTests
{

}
