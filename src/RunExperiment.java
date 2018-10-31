import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import content.VisualFeatureDetection;
import id3.DecisionTree;
import id3.TreeNode;
import link.LinkAnalysis;

/**
 * Main class that will execute all of the code for the research project.
 * @author Nick Rummel
 *
 */
public class RunExperiment
{

	/**
	 * Stores each HTML file's path.
	 */
	private static String[] htmlFilePaths;

	/**
	 * Stores whether each HTML file is an article or not.
	 */
	private static boolean[] htmlIsArticle;

	/**
	 * Stores the URL to each HTML
	 */
	private static String[] htmlURL;

	/**
	 * Stores the folder path to the HTML files.
	 */
	private final static String DATASETPATH = "dataset\\";

	/**
	 * The number of HTML files being used overall.
	 */
	private final static int ENTRIES = 330;

	/**
	 * The number of HTML files being used for training.
	 */
	private final static int TRAININGSETSIZE = 300;

	/**
	 * The number of HTML files being used for testing.
	 */
	private final static int TESTSETSIZE = ENTRIES - TRAININGSETSIZE;

	/**
	 * The number of visual features attributes being tested.
	 */
	private final static int VFCOUNT = 8;

	/**
	 * The number of link analysis attributes being tested.
	 */
	private final static int LACOUNT = 6;

	/**
	 * An array of all visual feature and link analysis descriptions for the
	 * decision tree.
	 */
	private final static String[] ALLDESCRIPTIONS =
	{ "Article Author Exists?", "Article Category Exists?", "Article Comment Link Exists?", "Article Content Exists?",
			"Article Publication Date Exists?", "Article Related News Link Exists?", "Article Source Exists?",
			"Article Title Exists?", "Link Does Not Contain Reserve Word?", "Link Does Not End With Slash?",
			"Link Has Date?", "Link Has Four Slashes?", "Link Has ID Number?", "Link Has Longer Length?" };

	/**
	 * An array of visual feature descriptions only for the decision tree.
	 */
	private final static String[] VFDESCRIPTIONS =
	{ "Article Author Exists?", "Article Category Exists?", "Article Comment Link Exists?", "Article Content Exists?",
			"Article Publication Date Exists?", "Article Related News Link Exists?", "Article Source Exists?",
			"Article Title Exists?" };

	/**
	 * Main method
	 * @param args Arguments
	 */
	public static void main(String[] args)
	{
		htmlFilePaths = new String[ENTRIES];
		htmlIsArticle = new boolean[ENTRIES];
		htmlURL = new String[ENTRIES];

		// read in data set file path, actual article value, and article link
		File dataset = new File("dataset\\ready.csv");
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(dataset));
			for (int i = 0; i < ENTRIES; i++)
			{
				String line = br.readLine();
				String[] tokens = line.split(",");
				htmlFilePaths[i] = tokens[1];

				int boolVal = Integer.parseInt(tokens[2]);
				if (boolVal == 0)
				{
					htmlIsArticle[i] = false;
				}
				else
				{
					htmlIsArticle[i] = true;
				}

				htmlURL[i] = tokens[3];
			}
			br.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		for (int trial = 1; trial <= 20; trial++)
		{
			System.out.println("\nTrial #" + trial + "\n");

			// create training set
			System.out.println("Training Set:");
			ArrayList<Integer> trainingSet = chooseRandomTrainingSet(ENTRIES - TESTSETSIZE);
			for (int i = 0; i < trainingSet.size(); i++)
			{
				System.out.print(trainingSet.get(i));
				if ((i + 1) < trainingSet.size())
				{
					System.out.print(",");
				}
			}
			System.out.print("\n\n");

			// create testing set
			System.out.println("Testing Set: ");
			ArrayList<Integer> testingSet = createTestSetList(trainingSet);
			for (int i = 0; i < testingSet.size(); i++)
			{
				System.out.print(testingSet.get(i));
				if ((i + 1) < testingSet.size())
				{
					System.out.print(",");
				}
			}
			System.out.print("\n\n");

			System.out.println("Testing Training Set...");
			boolean[][] resultingTrainingData = testDataSet(trainingSet, VFCOUNT + LACOUNT, TRAININGSETSIZE);

			System.out.println("Testing Test Set...\n");
			boolean[][] resultingTestingData = testDataSet(testingSet, VFCOUNT + LACOUNT, TESTSETSIZE);

			// print out training data results for link analysis and visual
			// feature detection
			for (int i = 0; i < resultingTrainingData.length; i++)
			{
				System.out.print(ALLDESCRIPTIONS[i] + ",");
				for (int j = 0; j < resultingTrainingData[0].length; j++)
				{
					System.out.print(resultingTrainingData[i][j] + ",");
				}
				System.out.print("\n");
			}

			System.out.print("\n\n");

			// print out data results for link analysis and visual feature
			// detection
			for (int i = 0; i < resultingTestingData.length; i++)
			{
				System.out.print(ALLDESCRIPTIONS[i] + ",");
				for (int j = 0; j < resultingTestingData[0].length; j++)
				{
					System.out.print(resultingTestingData[i][j] + ",");
				}
				System.out.print("\n");
			}

			// perform ID3 information gain calculations for root node of VF+LA
			// attributes
			DecisionTree vfdAndLaTree = new DecisionTree(null);
			ArrayList<Integer> usedDataLoc = new ArrayList<Integer>();
			boolean[] actualData = htmlIsArticle.clone();
			boolean[][] randomData = resultingTrainingData.clone();
			int index = vfdAndLaTree.getIndexOfLargestInfoGain(actualData, randomData);
			int nextIndex = index;
			ArrayList<String> usedDesc = new ArrayList<String>();
			generateDecisionTree(vfdAndLaTree, usedDataLoc, actualData, randomData, index, nextIndex, usedDesc,
					ALLDESCRIPTIONS, VFCOUNT + LACOUNT);
			System.out.println("\nVF & LA Tree");
			vfdAndLaTree.printTree();

			// perform ID3 information gain calculations for root node of VF
			// attributes only
			DecisionTree vfdOnlyTree = new DecisionTree(null);
			usedDataLoc = new ArrayList<Integer>();
			actualData = htmlIsArticle.clone();

			// copy training set result data to a new array of the proper size
			boolean[][] vfOnlyData = new boolean[VFDESCRIPTIONS.length][resultingTrainingData[0].length];
			for (int i = 0; i < VFDESCRIPTIONS.length; i++)
			{
				for (int j = 0; j < resultingTrainingData[0].length; j++)
				{
					vfOnlyData[i][j] = resultingTrainingData[i][j];
				}
			}
			randomData = vfOnlyData.clone();
			index = vfdOnlyTree.getIndexOfLargestInfoGain(actualData, randomData);
			nextIndex = index;
			usedDesc = new ArrayList<String>();
			generateDecisionTree(vfdOnlyTree, usedDataLoc, actualData, randomData, index, nextIndex, usedDesc,
					VFDESCRIPTIONS, VFCOUNT);
			System.out.println("\nVF Only Tree");
			vfdOnlyTree.printTree();

			// run testing set through VF+LA decision tree and print out results
			System.out.println("\nTesting All Attribute Results in Decision Tree...");
			testDataInDecisionTree(testingSet, resultingTestingData, vfdAndLaTree, ALLDESCRIPTIONS);

			// run testing set through VF only decision tree and print out
			// results
			System.out.println("\nTest VF Attribute Results Only in Decision Tree...");
			testDataInDecisionTree(testingSet, resultingTestingData, vfdOnlyTree, VFDESCRIPTIONS);

		}
	}

	/**
	 * Runs the a data set through the given given decision tree and calculates
	 * the accuracy.
	 * @param dataSet the indices of HTML files for the data set
	 * @param dataTestResults the 2D boolean array of results from the attribute
	 *            detection.
	 * @param dTree the decision tree to compare the results against
	 * @param desc an array of descriptions that were used in the decision tree
	 */
	public static void testDataInDecisionTree(ArrayList<Integer> dataSet, boolean[][] dataTestResults,
			DecisionTree dTree, String[] desc)
	{
		boolean[] correctAnswer = new boolean[TESTSETSIZE];
		// run test set through the decision true
		for (int j = 0; j < dataTestResults[0].length; j++)
		{
			TreeNode curNode = dTree.getRoot();
			// get the index of the file and the whether the file is an article
			int realIndex = dataSet.get(j);
			boolean realAns = htmlIsArticle[realIndex - 1];
			boolean result = false;

			/*
			 * Traverses the tree using the description of the node and whether
			 * the the resulting data is true (yes) or false (no). This loop
			 * runs until the one of the yes or no child nodes are null.
			 */
			while (curNode.getYesBranch() != null && curNode.getNoBranch() != null)
			{
				int loc = indexOfDescription(curNode.getNodeDescription(), desc);
				if (dataTestResults[loc][j] == true)
				{
					curNode = curNode.getYesBranch();
				}
				else if (dataTestResults[loc][j] == false)
				{
					curNode = curNode.getNoBranch();
				}
			}

			// always yes means the file is an article
			if (curNode.getNodeDescription().equals("Always Yes"))
			{
				result = true;
			}
			// always no means the file is not an article
			else if (curNode.getNodeDescription().equals("Always No"))
			{
				result = false;
			}
			// makes the last decision before arriving at the yes or no
			// conclusion
			else
			{
				int loc = indexOfDescription(curNode.getNodeDescription(), desc);
				if (dataTestResults[loc][j] == true)
				{
					result = true;
				}
				else if (dataTestResults[loc][j] == false)
				{
					result = false;
				}
			}
			// saves whether the guess from the decision tree matches the actual
			// decision on the file from the CSV
			if (realAns == result)
			{
				correctAnswer[j] = true;
			}
			else
			{
				correctAnswer[j] = false;
			}
		}

		/*
		 * Counts the number of correct/incorrect results then calculates the
		 * accuracy rate (% of correct answers)
		 */
		int correctCount = 0;
		System.out.print("Results,");
		for (int j = 0; j < correctAnswer.length; j++)
		{
			if (correctAnswer[j] == true)
			{
				correctCount++;
			}
			System.out.print(correctAnswer[j] + ",");
		}
		System.out.print("\n\n");

		int incorrectCount = TESTSETSIZE - correctCount;
		double correctPercent = (((double) correctCount) / TESTSETSIZE) * 100.0;
		double incorrectPercent = (((double) incorrectCount) / TESTSETSIZE) * 100.0;

		System.out.println("Correct %: " + correctPercent);
		System.out.println("Incorrect %: " + incorrectPercent);
	}

	/**
	 * Builds the decision tree given a variety of data.
	 * @param tree
	 * @param usedDataLoc the list of description indices used
	 * @param actualData the data used to compare the answers against
	 * @param randomData the data used in the info gain calculations
	 * @param index the current index
	 * @param nextIndex the next index
	 * @param usedDesc the list of descriptions used
	 * @param totalAttr the total of attributes being used for building the tree
	 * @param desc the list of descriptions to use
	 */
	public static void generateDecisionTree(DecisionTree tree, ArrayList<Integer> usedDataLoc, boolean[] actualData,
			boolean[][] randomData, int index, int nextIndex, ArrayList<String> usedDesc, String[] desc, int totalAttr)
	{
		boolean[][] resized;
		boolean[] resizedActual;
		boolean[][] resizeReady;
		// perform information gain calculations for child nodes
		int i = 0;
		while (usedDataLoc.size() < randomData.length && i < randomData.length)
		{
			int descIndex;

			// add root node
			if (i == 0)
			{
				usedDataLoc.add(nextIndex);
				tree.setRoot(new TreeNode(0, desc[nextIndex]));
				usedDesc.add(desc[nextIndex]);
			}
			// check if the count i is odd
			// then run info gain calculation
			else if (i % 2 == 1)
			{
				ArrayList<Integer> yes = tree.countOfBooleans(randomData[nextIndex], true);
				resized = tree.resizeDataArrayFromUsedIndices(usedDataLoc, randomData);
				resizedActual = tree.resizeActualDataArray(yes, actualData);
				resizeReady = tree.resizeResultDataArray(yes, resized);

				nextIndex = tree.getIndexOfLargestInfoGain(resizedActual, resizeReady);
				// always no (info gain = 0)
				if (nextIndex == -1)
				{
					tree.addNodeToBranch(new TreeNode(i, "Always No"), findPrevNodeForOddCount(i), true);
					nextIndex = index;
				}
				// always yes (info gain = 1)
				else if (nextIndex < -1)
				{
					int realIndex = (nextIndex + 2) * -1;
					tree.addNodeToBranch(new TreeNode(i, "Always Yes"), findPrevNodeForOddCount(i), true);
					nextIndex = index;
					usedDataLoc.add(realIndex);
				}
				// info gain is between 0 and 1, add node with description
				else
				{
					descIndex = getDescriptionIndexFromRemaining(usedDesc, desc, nextIndex);
					usedDataLoc.add(descIndex);
					tree.addNodeToBranch(new TreeNode(i, desc[descIndex]), findPrevNodeForOddCount(i), true);
					usedDesc.add(desc[descIndex]);
				}
				index = nextIndex;

			}
			// check if the count i is even
			// then run info gain calculation
			else if (i % 2 == 0)
			{
				ArrayList<Integer> no = tree.countOfBooleans(randomData[nextIndex], false);
				resized = tree.resizeDataArrayFromUsedIndices(usedDataLoc, randomData);
				resizedActual = tree.resizeActualDataArray(no, actualData);
				resizeReady = tree.resizeResultDataArray(no, resized);

				nextIndex = tree.getIndexOfLargestInfoGain(resizedActual, resizeReady);
				// always no (info gain = 0)
				if (nextIndex == -1)
				{
					tree.addNodeToBranch(new TreeNode(i, "Always No"), findPrevNodeForEvenCount(i), false);
					usedDataLoc.add(index);
					nextIndex = index;
				}
				// always yes (info gain = 1)
				else if (nextIndex < -1)
				{
					int realIndex = (nextIndex + 2) * -1;
					tree.addNodeToBranch(new TreeNode(i, "Always Yes"), findPrevNodeForEvenCount(i), false);
					nextIndex = index;
					usedDataLoc.add(realIndex);
				}
				// info gain is between 0 and 1, add node with description
				else
				{
					descIndex = getDescriptionIndexFromRemaining(usedDesc, desc, nextIndex);
					usedDataLoc.add(descIndex);
					tree.addNodeToBranch(new TreeNode(i, desc[descIndex]), findPrevNodeForEvenCount(i), false);
					usedDesc.add(desc[descIndex]);
				}
				// keep track of from calculation
				index = nextIndex;
			}

			i++;
		}

		// check if any nodes with "always yes" or "always no" has children
		ArrayList<Integer> removeList = new ArrayList<Integer>();
		for (int j = 0; j < totalAttr; j++)
		{
			TreeNode root = tree.getRoot();
			if (tree.getNodeById(root, j).getNodeDescription().equals("Always Yes")
					|| tree.getNodeById(root, j).getNodeDescription().equals("Always No"))
			{
				removeList.add(j);
			}
		}

		// remove children of "always yes/no" nodes
		for (int j = 0; j < removeList.size(); j++)
		{
			TreeNode root = tree.getRoot();
			try
			{
				TreeNode node = tree.getNodeById(root, removeList.get(j));
				node.setYesBranch(null);
				node.setNoBranch(null);
			}
			catch (Exception e)
			{
				// just skip
			}
		}
	}

	/**
	 * Retrieves the index of a description given the description as a String.
	 * @param descStr the description as a string
	 * @param descArray the array to look for the description in
	 * @return the array index of the description, or -1 if the description
	 *         isn't found
	 */
	public static int indexOfDescription(String descStr, String[] descArray)
	{
		int index = -1;
		boolean found = false;
		int i = 0;
		while (found == false && i < descArray.length)
		{
			if (descArray[i].equals(descStr))
			{
				index = i;
				found = true;
			}
			i++;
		}

		return index;
	}

	/**
	 * Runs all the articles listed in the data set against the link analysis
	 * and visual feature detection tests.
	 * @param dataSet A list of the articles being tested.
	 * @param attrCount the number of attributes to be tested, used in the 2D
	 *            array dimensions.
	 * @param dataSetSize the amount of data to be tested, used in the 2D array
	 *            dimensions.
	 * @return the results of the tests as a 2D boolean array.
	 */
	public static boolean[][] testDataSet(ArrayList<Integer> dataSet, int attrCount, int dataSetSize)
	{
		boolean[][] resultingData = new boolean[attrCount][dataSetSize];

		// run each article through link analysis and visual feature detection
		for (int i = 0; i < dataSetSize; i++)
		{
			int j = dataSet.get(i);

			VisualFeatureDetection vfd = new VisualFeatureDetection(DATASETPATH + htmlFilePaths[j - 1]);
			LinkAnalysis la = new LinkAnalysis(htmlURL[j - 1]);
			resultingData[0][i] = vfd.articleAuthorExists();
			resultingData[1][i] = vfd.articleCategoryExists();
			resultingData[2][i] = vfd.articleCommentLinkExists();
			resultingData[3][i] = vfd.articleContentExists();
			resultingData[4][i] = vfd.articlePublicationDateExists();
			resultingData[5][i] = vfd.articleRelatedNewsLinksExists();
			resultingData[6][i] = vfd.articleSourceExists();
			resultingData[7][i] = vfd.articleTitleExists();

			resultingData[8][i] = la.linkDoesNotContainReservedWord();
			resultingData[9][i] = la.linkDoesNotEndWithSlash();
			resultingData[10][i] = la.linkHasDate();
			resultingData[11][i] = la.linkHasFourSlashes();
			resultingData[12][i] = la.linkHasIDNumber();
			resultingData[13][i] = la.linkHasLongerLength();
		}
		return resultingData;
	}

	/**
	 * Determines which description string to use given a list of used
	 * descriptions. This verifies that descriptions aren't duplicated on the
	 * tree.
	 * @param used the description strings used
	 * @param desc the array of descriptions
	 * @param index the current index
	 * @return the index of the description to be used.
	 */
	public static int getDescriptionIndexFromRemaining(ArrayList<String> used, String[] desc, int index)
	{
		int chosen = 0;
		boolean flag = false;
		int i = 0;
		if (!used.contains(desc[index]))
		{
			chosen = index;
			flag = true;
		}

		while (flag == false && i < desc.length)
		{
			if (i >= index && !used.contains(desc[i]))
			{
				chosen = i;
				flag = true;
			}
			i++;
		}

		return chosen;
	}

	/**
	 * Calculates the index of the parent node.
	 * @param index the current index (odd number)
	 * @return the index of the parent node.
	 */
	public static int findPrevNodeForOddCount(int index)
	{
		if (index == 0)
		{
			return 0;
		}
		else
		{
			int result = index - 1;
			result = result / 2;
			return result;
		}
	}

	/**
	 * Calculates the index of the parent node.
	 * @param index the current index (even number)
	 * @return the index of the parent node.
	 */
	public static int findPrevNodeForEvenCount(int index)
	{
		if (index == 0)
		{
			return 0;
		}
		else
		{
			int result = index - 2;
			result = result / 2;
			return result;
		}
	}

	/**
	 * Randomly generate a specified number of unique numbers that are less than
	 * the number of total entries. Each of these numbers are stored into an
	 * ArrayList. These numbers are the indices for articles in the data set.
	 * @param numEntries how many numbers need to be generated
	 * @return the ArrayList of integers generated randomly
	 */
	public static ArrayList<Integer> chooseRandomTrainingSet(int numEntries)
	{
		ArrayList<Integer> chosen = new ArrayList<Integer>();
		while (chosen.size() < numEntries)
		{
			int rand = (int) Math.floor(Math.random() * ENTRIES) + 1;
			if (!chosen.contains(rand))
			{
				chosen.add(rand);
			}
		}
		Collections.sort(chosen);
		return chosen;
	}

	/**
	 * From the training set, create a list of the remaining indices that were
	 * not selected for the training set as the test set.
	 * @param trainingSet The list of numbers already being used for training.
	 * @return the ArrayList of integers containing indices for the test set.
	 */
	public static ArrayList<Integer> createTestSetList(ArrayList<Integer> trainingSet)
	{
		ArrayList<Integer> remaining = new ArrayList<Integer>();
		int i = 1;
		boolean fullFlag = false;
		while (fullFlag == false && i <= ENTRIES)
		{
			if (!trainingSet.contains(i))
			{
				remaining.add(i);
			}
			if (remaining.size() == TESTSETSIZE)
			{
				fullFlag = true;
			}
			i++;
		}
		return remaining;
	}

}
