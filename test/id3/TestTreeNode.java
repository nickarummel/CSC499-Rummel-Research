package id3;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * A class that contains all of the JUnit tests related to the TreeNode class.
 * @author Nick Rummel
 */
public class TestTreeNode
{
	TreeNode root;

	/**
	 * Executes before each test case.
	 */
	@Before
	public void init()
	{
		root = new TreeNode(0, "root node");
	}

	/**
	 * Tests that the constructor is storing values properly.
	 */
	@Test
	public void testConstructor()
	{
		assertEquals(0, root.nodeId);
		assertEquals("root node", root.description);
		assertNull(root.yesBranch);
		assertNull(root.noBranch);
	}

	/**
	 * Tests the getter/setter methods for the node's ID
	 */
	@Test
	public void testIdGetterAndSetter()
	{
		assertEquals(0, root.getNodeId());
		root.setNodeId(1);
		assertEquals(1, root.getNodeId());
	}

	/**
	 * Tests the getter/setter methods for the node's description
	 */
	@Test
	public void testDescriptionGetterAndSetter()
	{
		assertEquals("root node", root.getNodeDescription());
		root.setNodeDescription("new description");
		assertEquals("new description", root.getNodeDescription());
	}

	/**
	 * Tests the getter/setter methods for the node's yes branch
	 */
	@Test
	public void testYesBranchGetterAndSetter()
	{
		assertNull(root.getYesBranch());
		TreeNode yesChild = new TreeNode(1, "yes node");
		root.setYesBranch(yesChild);
		assertEquals(yesChild, root.getYesBranch());
	}

	/**
	 * Tests the getter/setter methods for the node's no branch
	 */
	@Test
	public void testNoBranchGetterAndSetter()
	{
		assertNull(root.getNoBranch());
		TreeNode noChild = new TreeNode(2, "no node");
		root.setNoBranch(noChild);
		assertEquals(noChild, root.getNoBranch());
	}

}
