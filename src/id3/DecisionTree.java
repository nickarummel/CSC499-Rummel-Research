package id3;

/**
 * The class for the DecisionTree containing code to traverse and manipulate the
 * decision tree. The decision tree will be used as part of the ID3 algorithm
 * for the research project.
 * @author Nick Rummel
 *
 */
public class DecisionTree
{
	/**
	 * The instance variable that stores the root node to the decision tree.
	 */
	protected TreeNode root;

	/**
	 * The instance variable that stores the value of log base 2 to convert
	 * Java's Math log() method from base 10.
	 */
	private final double logBase2 = Math.log(2.0);

	/**
	 * Constructor of the decision tree class.
	 * @param rNode The root node for the tree
	 */
	public DecisionTree(TreeNode rNode)
	{
		root = rNode;
	}

	/**
	 * Getter method for retrieving the root node of the decision tree.
	 * @return the root node as a TreeNode object.
	 */
	public TreeNode getRoot()
	{
		return root;
	}

	/**
	 * Setter method for the root node of the decision tree.
	 * @param node the TreeNode to be set as the root.
	 */
	public void setRoot(TreeNode node)
	{
		root = node;
	}

	/**
	 * Retrieves a node from the decision tree using the node's ID number. This
	 * method is called recursively.
	 * @param curNode the current TreeNode object to check.
	 * @param id the node's identification number.
	 * @return the TreeNode object if found, otherwise null if not found.
	 */
	public TreeNode getNodeById(TreeNode curNode, int id)
	{
		TreeNode found = null;
		// check the current node itself
		if (curNode.getNodeId() == id)
		{
			found = curNode;
		}
		// check Yes branch first
		if (found == null && curNode.yesBranch != null)
		{
			if (curNode.yesBranch.getNodeId() == id)
			{
				found = curNode.yesBranch;
			}
			else
			{
				found = getNodeById(curNode.yesBranch, id);
			}
		}
		// check No branch second
		if (found == null && curNode.noBranch != null)
		{
			if (curNode.noBranch.getNodeId() == id)
			{
				found = curNode.noBranch;
			}
			else
			{
				found = getNodeById(curNode.noBranch, id);
			}
		}
		return found;
	}

	/**
	 * Calculates information entropy for a particular attribute.
	 * @param attrCount the number of times an attribute was found
	 * @param totalCount the total number of elements
	 * @return the entropy as a double
	 */
	public double entropy(int attrCount, int totalCount)
	{
		double yesRatio = ((double) attrCount) / totalCount;
		double noRatio = ((double) (totalCount - attrCount)) / totalCount;
		double result;
		if (yesRatio == 0 && noRatio == 0)
		{
			result = 0.0;
		}
		else if (yesRatio == 0)
		{
			result = -1.0 * (noRatio * (Math.log(noRatio) / logBase2));
		}
		else if (noRatio == 0)
		{
			result = -1.0 * (yesRatio * (Math.log(yesRatio) / logBase2));
		}
		else
		{
			result = -1.0 * ((yesRatio * (Math.log(yesRatio) / logBase2)) + (noRatio * (Math.log(noRatio) / logBase2)));
		}
		
		System.out.println("Entropy: " + result);
		
		return result;
	}

	/**
	 * Calculates the information gain for each article detection feature.
	 * @param actual the original data that the document is an article or not.
	 * @param results 2D array containing the data of each feature being found.
	 * @return the index of feature with the largest gain
	 * @throws Exception if the sum of yes and no counts does not equal the
	 *             length of the array.
	 */
	public int calculateInfoGain(boolean[] actual, boolean[][] results) throws Exception
	{
		int actualYesCount = 0;
		int actualNoCount = 0;
		int actualLen = actual.length;
		for (int i = 0; i < actualLen; i++)
		{
			if (actual[i] == true)
			{
				actualYesCount++;
			}
			else if (actual[i] == false)
			{
				actualNoCount++;
			}
		}
		if (actualYesCount + actualNoCount != actualLen)
		{
			throw new Exception("Sum of yes/no counts does not equal length of array.");
		}

		double[] gain = new double[results.length];
		for (int i = 0; i < results.length; i++)
		{
			int yesCount = 0;
			int noCount = 0;
			int total = 0;
			for (int j = 0; j < results[i].length; j++)
			{
				if (results[i][j] == true)
				{
					yesCount++;
					total++;
				}
				else if (results[i][j] == false)
				{
					noCount++;
					total++;
				}
			}
			gain[i] = entropy(actualYesCount, actualLen) - ((yesCount / total) * entropy(yesCount, total))
					- ((noCount / total) * entropy(noCount, total));
			
			System.out.println("Calculated Gain of " + i + ": " + gain[i]);
		}

		int largest = 0;
		for (int i = 1; i < gain[i]; i++)
		{
			if (gain[i] > gain[largest])
			{
				largest = i;
			}
		}

		return largest;
	}
}
