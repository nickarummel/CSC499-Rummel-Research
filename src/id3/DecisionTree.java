package id3;

import java.util.ArrayList;

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
	protected double entropy(int attrCount, int totalCount)
	{
		double yesRatio = ((double) attrCount) / totalCount;
		double noRatio = ((double) (totalCount - attrCount)) / totalCount;
		double result;
		// both ratios are 0, so return 0
		if (yesRatio == 0 && noRatio == 0)
		{
			result = 0.0;
		}
		// yes ratio is 0, so only calculate no ratio
		else if (yesRatio == 0)
		{
			result = -1.0 * (noRatio * (Math.log(noRatio) / logBase2));
		}
		// no ratio is 0, so only calculate yes ratio
		else if (noRatio == 0)
		{
			result = -1.0 * (yesRatio * (Math.log(yesRatio) / logBase2));
		}
		// otherwise, calculate the yes and no ratios together
		else
		{
			result = -1.0 * ((yesRatio * (Math.log(yesRatio) / logBase2)) + (noRatio * (Math.log(noRatio) / logBase2)));
		}

		System.out.println("Entropy: " + result);

		return result;
	}

	/**
	 * Finds the index containing the largest gain for a list of article
	 * detection features.
	 * @param actual the original data that the document is an article or not.
	 * @param results 2D array containing the data of each feature being found.
	 * @return the index of feature with the largest gain
	 */
	public int getIndexOfLargestInfoGain(boolean[] actual, boolean[][] results)
	{
		// create list of indices that contain a yes (true) or no (false)
		// from the actual data
		ArrayList<Integer> actualYesCount = countOfBooleans(actual, true);
		ArrayList<Integer> actualNoCount = countOfBooleans(actual, false);
		int actualLen = actualYesCount.size() + actualNoCount.size();

		// create a list of indices that contain
		double[] gain = new double[results.length];
		for (int i = 0; i < results.length; i++)
		{
			// get counts of trues and falses from the results list
			ArrayList<Integer> yesCount = countOfBooleans(results[i], true);
			ArrayList<Integer> noCount = countOfBooleans(results[i], false);

			// total is the sum of trues and falses from results list
			int total = yesCount.size() + noCount.size();

			// counts indices that are found in two lists
			int yesIndices = countOfIndicesFound(yesCount, actualYesCount);
			int noIndices = countOfIndicesFound(noCount, actualNoCount);

			// calculate information gain
			gain[i] = gain(actualYesCount.size(), actualLen, yesCount.size(), noCount.size(), total, yesIndices,
					noIndices);
			System.out.println("Calculated Gain of " + i + ": " + gain[i]);
		}

		// find the largest value from the calculate gains list
		int largest = 0;
		for (int i = 1; i < gain.length; i++)
		{
			if (gain[i] > gain[largest])
			{
				largest = i;
			}
		}

		return largest;
	}

	/**
	 * Calculates the information gain from a variety of values. This method
	 * assumes that information entropy is being calculated correctly.
	 * @param actualYesCount the number of trues counted from the actual data
	 * @param actualTotal the sum of trues and falses counted from actual data
	 * @param yesCount the number of trues counted from the experiment data
	 * @param noCount the number of falses counted from the experiment data
	 * @param total the sum of trues and falses counted from the experiment data
	 * @param yesIndices the number of indices with true found in two lists
	 * @param noIndices the number of indices with false found in two lists
	 * @return the information gain calculated
	 */
	protected double gain(int actualYesCount, int actualTotal, int yesCount, int noCount, int total, int yesIndices,
			int noIndices)
	{
		double entS = entropy(actualYesCount, actualTotal);
		double entYes = ((double) yesCount / total) * entropy(yesIndices, yesCount);
		double entNo = ((double) noCount / total) * entropy(noIndices, noCount);
		return (entS - entYes - entNo);
	}

	/**
	 * A method to compare a list of indices to another list. If the index from
	 * the first list is found in the second list, then the count is
	 * incremented.
	 * @param results the resulting data from the experiment.
	 * @param actual the actual data from before the experiment
	 * @return the number of indices found in both lists.
	 */
	protected int countOfIndicesFound(ArrayList<Integer> results, ArrayList<Integer> actual)
	{
		int count = 0;
		for (int i = 0; i < results.size(); i++)
		{
			for (int j = 0; j < actual.size(); j++)
			{
				if (results.get(i) == actual.get(j))
				{
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Stores each index that contains a specific type of boolean in a list.
	 * @param list the array list of booleans to check
	 * @param checkType look for either true or false values
	 * @return the ArrayList of indices where the boolean exists
	 */
	protected ArrayList<Integer> countOfBooleans(boolean[] list, boolean checkType)
	{
		ArrayList<Integer> count = new ArrayList<Integer>();
		for (int i = 0; i < list.length; i++)
		{
			if (list[i] == checkType)
			{
				count.add(i);
			}
		}
		return count;
	}

}
