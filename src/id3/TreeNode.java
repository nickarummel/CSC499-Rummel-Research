package id3;

/**
 * The class for a Tree Node object to create the decision tree. The decision
 * tree will be used as part of the ID3 algorithm for the research project.
 * @author Nick Rummel
 *
 */
public class TreeNode
{
	/**
	 * Instance variable to uniquely identify the node.
	 */
	protected int nodeId;

	/**
	 * Instance variable that will contain a description of what the node
	 * represents, such as whether an attribute exists.
	 */
	protected String description;

	/**
	 * Instance variable that will contain a reference to the next tree node
	 * that is yes/true.
	 */
	protected TreeNode yesBranch;

	/**
	 * Instance variable that will contain a reference to the next tree node
	 * that is no/false.
	 */
	protected TreeNode noBranch;

	/**
	 * Constructor of tree nodes.
	 * @param id The node ID.
	 * @param desc The description of the node.
	 */
	public TreeNode(int id, String desc)
	{
		nodeId = id;
		description = desc;
		yesBranch = null;
		noBranch = null;
	}

	/**
	 * Getter method for node ID instance variable.
	 * @return the node ID as an int.
	 */
	public int getNodeId()
	{
		return nodeId;
	}

	/**
	 * Setter method for the node ID instance variable.
	 * @param id the new node ID.
	 */
	public void setNodeId(int id)
	{
		nodeId = id;
	}

	/**
	 * Getter method for node description instance variable.
	 * @return the node description as a String.
	 */
	public String getNodeDescription()
	{
		return description;
	}

	/**
	 * Setter method for node description instance variable.
	 * @param desc the new node description.
	 */
	public void setNodeDescription(String desc)
	{
		description = desc;
	}

	/**
	 * Getter method for the node's yes branch instance variable.
	 * @return the yes branch as a TreeNode.
	 */
	public TreeNode getYesBranch()
	{
		return yesBranch;
	}

	/**
	 * Setter method for the node's yes branch instance variable.
	 * @param node the new TreeNode yes branch.
	 */
	public void setYesBranch(TreeNode node)
	{
		yesBranch = node;
	}

	/**
	 * Getter method for the node's no branch instance variable.
	 * @return the no branch as a TreeNode.
	 */
	public TreeNode getNoBranch()
	{
		return noBranch;
	}

	/**
	 * Setter method for the node's no branch instance variable.
	 * @param node the new TreeNode no branch.
	 */
	public void setNoBranch(TreeNode node)
	{
		noBranch = node;
	}

}
