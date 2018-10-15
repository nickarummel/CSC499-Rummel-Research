package id3;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * A class that contains all of the JUnit tests related to the DecisionTree
 * class.
 * @author Nick Rummel
 *
 */
public class TestDecisionTree
{
	DecisionTree tree;

	/**
	 * Resets the tree's root to null before each test case.
	 */
	@Before
	public void init()
	{
		tree = new DecisionTree(null);
	}

	/**
	 * Tests the constructor of the decision tree.
	 */
	@Test
	public void testConstructor()
	{
		assertNull(tree.root);
		tree = new DecisionTree(new TreeNode(0, "root"));

		assertEquals(tree.root.getNodeId(), 0);
		assertEquals(tree.root.getNodeDescription(), "root");
	}

	/**
	 * Tests the getter and setter methods for the root node instance variable.
	 */
	@Test
	public void testRootGetterAndSetter()
	{
		assertNull(tree.getRoot());

		TreeNode node = new TreeNode(0, "root");
		tree.setRoot(node);
		assertEquals(node, tree.getRoot());
	}

	/**
	 * Tests that a node can be found in the decision tree given the node's ID.
	 */
	@Test
	public void testGetNodeById()
	{
		// create the root node, and 5 other nodes
		TreeNode root = new TreeNode(0, "root");
		TreeNode nodes[] = new TreeNode[5];
		for (int i = 0; i < nodes.length; i++)
		{
			nodes[i] = new TreeNode(i + 1, "node-" + i + 1);
		}
		// root (node 0) has node 1 as yes and node 4 as no
		root.setYesBranch(nodes[0]);
		root.setNoBranch(nodes[3]);
		// node 1 has node 2 as yes and node 3 as no
		nodes[0].setYesBranch(nodes[1]);
		nodes[0].setNoBranch(nodes[2]);
		// node 4 has node 5 as no
		nodes[3].setNoBranch(nodes[4]);
		tree.setRoot(root);

		// check that each node exists (0 - 5)
		assertEquals(tree.getNodeById(tree.root, 0), root);
		assertEquals(tree.getNodeById(tree.root, 1), nodes[0]);
		assertEquals(tree.getNodeById(tree.root, 2), nodes[1]);
		assertEquals(tree.getNodeById(tree.root, 3), nodes[2]);
		assertEquals(tree.getNodeById(tree.root, 4), nodes[3]);
		assertEquals(tree.getNodeById(tree.root, 5), nodes[4]);
		// a node that does not exist will return null
		assertNull(tree.getNodeById(tree.root, 6));
	}

	/**
	 * Tests the information entropy calculation method using different integer
	 * values as parameters.
	 */
	@Test
	public void testEntropyCalculation()
	{
		assertEquals(1.0, tree.entropy(5, 10), 0.0);
		assertEquals(0.0, tree.entropy(1, 1), 0.0);
		assertEquals(0.8113, tree.entropy(3, 4), 0.0001);
		assertEquals(0.9183, tree.entropy(2, 6), 0.0001);
		assertEquals(0.0, tree.entropy(0, 2), 0.0);
		assertEquals(0.9544, tree.entropy(5, 8), 0.0001);
		assertEquals(0.9852, tree.entropy(4, 7), 0.0001);
		assertEquals(0.9183, tree.entropy(1, 3), 0.0001);

	}

	/**
	 * Tests the information gain calculation method using different integer
	 * values as parameters.
	 */
	@Test
	public void testGainCalculation()
	{
		double result = tree.gain(5, 10, 4, 6, 10, 3, 2);
		assertEquals(0.1245, result, 0.0001);

		result = tree.gain(5, 10, 2, 8, 10, 0, 5);
		assertEquals(0.2365, result, 0.0001);

		result = tree.gain(5, 10, 7, 3, 10, 4, 1);
		assertEquals(0.0349, result, 0.0001);

	}

	/**
	 * Checks that information gain and entropy is calculated correctly to find
	 * the largest gain from randomly generated data.
	 * @throws Exception if data is not either true or false.
	 */
	@Test
	public void testGetIndexOfLargestInfoGain()
	{

		boolean[] actualData =
		{ true, true, false, false, true, false, true, false, false, true };
		boolean[][] randomData =
		{
				{ false, true, false, false, false, true, true, false, false, true },
				{ false, false, true, true, false, false, false, false, false, false },
				{ true, true, false, true, false, true, true, false, true, true }

		};

		// should return index 1, meaning the second attribute has the highest
		// information gain
		assertEquals(1, tree.getIndexOfLargestInfoGain(actualData, randomData));
	}

	/**
	 * Tests that a simple tree can be built (root with yes/no branches having
	 * nodes).
	 */
	@Test
	public void testBuildTree()
	{
		String[] descriptions =
		{ "Question 1", "Question 2", "Question 3" };
		boolean[] actualData =
		{ true, true, false, false, true, false, true, false, false, true };
		boolean[][] randomData =
		{
				{ false, true, false, false, false, true, true, false, false, true },
				{ false, false, true, true, false, false, false, false, false, false },
				{ true, true, false, true, false, true, true, false, true, true }

		};

		// root will be question #2
		ArrayList<Integer> usedDataLoc = new ArrayList<Integer>();
		int index = tree.getIndexOfLargestInfoGain(actualData, randomData);
		assertEquals(1, index);
		usedDataLoc.add(index);
		tree.setRoot(new TreeNode(0, descriptions[index]));

		// yes branch will be No because gain for each of remaining attributes
		// is 0
		boolean[][] resized = tree.resizeDataArrayFromUsedIndices(usedDataLoc, randomData);
		ArrayList<Integer> yes = tree.countOfBooleans(randomData[index], true);
		boolean[] resizedActual = tree.resizeActualDataArray(yes, actualData);
		boolean[][] resizeReady = tree.resizeResultDataArray(yes, resized);
		int nextIndex = tree.getIndexOfLargestInfoGain(resizedActual, resizeReady);
		assertEquals(-1, nextIndex);
		tree.addNodeToBranch(new TreeNode(1, "No - all gains are 0"), 0, true);

		// no branch will be question #1
		// (question #3 not used since yes branch is automatic no)
		ArrayList<Integer> no = tree.countOfBooleans(randomData[index], false);
		resizedActual = tree.resizeActualDataArray(no, actualData);
		resizeReady = tree.resizeResultDataArray(no, resized);
		nextIndex = tree.getIndexOfLargestInfoGain(resizedActual, resizeReady);
		// index 0 of resized data
		assertEquals(0, nextIndex);
		// index 0 of original data
		assertEquals(0, no.get(nextIndex).intValue());
		tree.addNodeToBranch(new TreeNode(2, descriptions[no.get(nextIndex)]), 0, false);

		tree.printTree();
	}

}
