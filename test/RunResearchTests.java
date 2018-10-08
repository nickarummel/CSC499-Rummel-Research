import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import content.TestVisualFeatureDetection;
import id3.TestDecisionTree;
import id3.TestTreeNode;
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
	TestLinkAnalysis.class,
	TestTreeNode.class,
	TestDecisionTree.class

})

public class RunResearchTests
{

}
