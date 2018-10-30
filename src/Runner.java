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
public class Runner
{

	private static String[] htmlFilePaths;
	private static boolean[] htmlIsArticle;
	private static String[] htmlURL;

	private final static String DATASETPATH = "dataset\\";

	private final static int ENTRIES = 330;
	private final static int TESTSETSIZE = 30;

	private final static int VFCOUNT = 8;
	private final static int LACOUNT = 6;

	private final static String[] DESCRIPTIONS =
	{ "Article Author Exists?", "Article Category Exists?", "Article Comment Link Exists?", "Article Content Exists?",
			"Article Publication Date Exists?", "Article Related News Link Exists?", "Article Source Exists?",
			"Article Title Exists?", "Link Does Not Contain Reserve Word?", "Link Does Not End With Slash?",
			"Link Has Date?", "Link Has Four Slashes?", "Link Has ID Number?", "Link Has Longer Length?" };


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

		// create training set
		System.out.println("Training Set:");
		ArrayList<Integer> trainingSet = chooseRandomTrainingSet(ENTRIES - TESTSETSIZE);
		for (int i = 0; i < trainingSet.size(); i++)
		{
			System.out.println(trainingSet.get(i));
		}

		// create testing set
		System.out.println("Testing Set: ");
		ArrayList<Integer> testingSet = createTestSetList(trainingSet);
		for (int i = 0; i < testingSet.size(); i++)
		{
			System.out.println(testingSet.get(i));
		}

		
		boolean[][] resultingData = new boolean[VFCOUNT + LACOUNT][ENTRIES - 300]; // adjusted for baseline

		// run each article through link analysis and visual feature detection
		for (int i = 0; i < ENTRIES - 300; i++) // adjusted for baseline
		{
			int j = testingSet.get(i);

			System.out.println("Testing article #" + j);

			VisualFeatureDetection vfd = new VisualFeatureDetection(DATASETPATH + htmlFilePaths[j]);
			LinkAnalysis la = new LinkAnalysis(htmlURL[j]);
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

		// print out data results for link analysis and visual feature detection
		for (int i = 0; i < resultingData.length; i++)
		{
			System.out.print(DESCRIPTIONS[i] + ",");
			for (int j = 0; j < resultingData[0].length; j++)
			{
				System.out.print(resultingData[i][j] + ",");
			}
			System.out.print("\n");
		}

		// perform ID3 information gain calculations for root node
		DecisionTree tree = new DecisionTree(null);
		ArrayList<Integer> usedDataLoc = new ArrayList<Integer>();
		boolean[] actualData = htmlIsArticle.clone();
		boolean[][] randomData = resultingData.clone();
		int index = tree.getIndexOfLargestInfoGain(actualData, randomData);

		boolean[][] resized;
		boolean[] resizedActual;
		boolean[][] resizeReady;
		int nextIndex = index;
		ArrayList<String> usedDesc = new ArrayList<String>();

		// perform information gain calculations for child nodes
		int i = 0;
		while (usedDataLoc.size() < randomData.length && i < randomData.length)
		{
			int descIndex;

			// add root node
			if (i == 0)
			{
				usedDataLoc.add(nextIndex);
				tree.setRoot(new TreeNode(0, DESCRIPTIONS[nextIndex]));
				usedDesc.add(DESCRIPTIONS[nextIndex]);
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
					descIndex = getDescriptionIndexFromRemaining(usedDesc, DESCRIPTIONS, nextIndex);
					usedDataLoc.add(descIndex);
					tree.addNodeToBranch(new TreeNode(i, DESCRIPTIONS[descIndex]), findPrevNodeForOddCount(i), true);
					usedDesc.add(DESCRIPTIONS[descIndex]);
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
					descIndex = getDescriptionIndexFromRemaining(usedDesc, DESCRIPTIONS, nextIndex);
					usedDataLoc.add(descIndex);
					tree.addNodeToBranch(new TreeNode(i, DESCRIPTIONS[descIndex]), findPrevNodeForEvenCount(i), false);
					usedDesc.add(DESCRIPTIONS[descIndex]);
				}
				// keep track of from calculation
				index = nextIndex;
			}

			System.out.print("\n");
			tree.printTree();

			i++;
		}

		// check if any nodes with "always yes" or "always no" has children
		ArrayList<Integer> removeList = new ArrayList<Integer>();
		for (int j = 0; j < 14; j++)
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

		System.out.print("\n");
		tree.printTree();
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
