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
	
	private final static int[] TREELEVELS = {1, 3, 5, 9, 65};

	/**
	 * Main method
	 * @param args Arguments
	 */
	public static void main(String[] args)
	{
		htmlFilePaths = new String[ENTRIES];
		htmlIsArticle = new boolean[ENTRIES];
		htmlURL = new String[ENTRIES];

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

		System.out.println("Training Set:");
		ArrayList<Integer> trainingSet = chooseRandomTrainingSet(ENTRIES - TESTSETSIZE);
		for (int i = 0; i < trainingSet.size(); i++)
		{
			System.out.println(trainingSet.get(i));
		}

		System.out.println("Testing Set: ");
		ArrayList<Integer> testingSet = createTestSetList(trainingSet);
		for (int i = 0; i < testingSet.size(); i++)
		{
			System.out.println(testingSet.get(i));
		}

		boolean[][] resultingData = new boolean[VFCOUNT + LACOUNT][ENTRIES-300];

		for (int i = 0; i < ENTRIES-300; i++)
		{
			int j = testingSet.get(i);
			
			System.out.println("Testing article #" + j);
			
//			VisualFeatureDetection vfd = new VisualFeatureDetection(DATASETPATH + htmlFilePaths[i]);
//			LinkAnalysis la = new LinkAnalysis(htmlURL[i]);
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
		
		for(int i = 0; i < resultingData.length; i++)
		{
			System.out.print(DESCRIPTIONS[i] + ",");
			for(int j = 0; j < resultingData[0].length; j++)
			{
				System.out.print(resultingData[i][j] + ",");
			}
			System.out.print("\n");
		}
		
		DecisionTree tree = new DecisionTree(null);
		ArrayList<Integer> usedDataLoc = new ArrayList<Integer>();
		boolean[] actualData = htmlIsArticle.clone();
		boolean[][] randomData = resultingData.clone();
		int index = tree.getIndexOfLargestInfoGain(actualData, randomData);
		//tree.setRoot(new TreeNode(0, DESCRIPTIONS[index]));
		
		/*
		boolean[][] resized = tree.resizeDataArrayFromUsedIndices(usedDataLoc, randomData);
		ArrayList<Integer> yes = tree.countOfBooleans(randomData[index], true);
		boolean[] resizedActual = tree.resizeActualDataArray(yes, actualData);
		boolean[][] resizeReady = tree.resizeResultDataArray(yes, resized);
		int nextIndex = tree.getIndexOfLargestInfoGain(resizedActual, resizeReady);
		*/
		boolean[][] resized;
		boolean[] resizedActual;
		boolean[][] resizeReady;
		int nextIndex = index;
		int prevNoNode = 0;
		int prevYesNode = 0;
		int levelCount = 0;
		
		int i = 0;
		while(usedDataLoc.size() < randomData.length && i < randomData.length)
		{
			
			
			if(i == 0)
			{
				usedDataLoc.add(nextIndex);
				tree.setRoot(new TreeNode(0, DESCRIPTIONS[nextIndex]));
				levelCount++;
			}
			else if(i%2 == 1)
			{
				ArrayList<Integer> yes = tree.countOfBooleans(randomData[nextIndex], true);
				resized = tree.resizeDataArrayFromUsedIndices(usedDataLoc, randomData);
				resizedActual = tree.resizeActualDataArray(yes, actualData);
				resizeReady = tree.resizeResultDataArray(yes, resized);
				
				nextIndex = tree.getIndexOfLargestInfoGain(resizedActual, resizeReady);
				if(nextIndex == -1)
				{
					tree.addNodeToBranch(new TreeNode(i, "Always No"), prevYesNode, true);
					nextIndex = index;
				}
				else if(nextIndex < -1)
				{
					int realIndex = (nextIndex + 2) * -1;
					tree.addNodeToBranch(new TreeNode(i, "Always Yes"), prevYesNode, true);
					nextIndex = index;
					
				}
				else
				{
					usedDataLoc.add(nextIndex);
					tree.addNodeToBranch(new TreeNode(i, DESCRIPTIONS[nextIndex]), prevYesNode, true);
				}
				index = nextIndex;
				prevYesNode = i;
				
			}
			else if(i%2 == 0)
			{
				ArrayList<Integer> no = tree.countOfBooleans(randomData[nextIndex], false);
				resized = tree.resizeDataArrayFromUsedIndices(usedDataLoc, randomData);
				resizedActual = tree.resizeActualDataArray(no, actualData);
				resizeReady = tree.resizeResultDataArray(no, resized);
				
				nextIndex = tree.getIndexOfLargestInfoGain(resizedActual, resizeReady);
				if(nextIndex == -1)
				{
					tree.addNodeToBranch(new TreeNode(i, "Always No"), prevNoNode, false);
					nextIndex = index;
				}
				else if(nextIndex < -1)
				{
					int realIndex = (nextIndex + 2) * -1;
					tree.addNodeToBranch(new TreeNode(i, "Always Yes"), prevNoNode, false);
					nextIndex = index;
				}
				else
				{
					usedDataLoc.add(nextIndex);
					tree.addNodeToBranch(new TreeNode(i, DESCRIPTIONS[nextIndex]), prevNoNode, false);
				}
				
				index = nextIndex;
				prevNoNode = i;
			}
			
			System.out.print("\n");
			tree.printTree();
			
			i++;
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
