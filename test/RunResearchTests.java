import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import content.TestVisualFeatureDetection;
import link.TestLinkAnalysis;

/**
 * Run all JUnit tests as a suite for project
 * @author Nick Rummel
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{ 
	TestVisualFeatureDetection.class,
	TestLinkAnalysis.class

})

public class RunResearchTests
{

}
