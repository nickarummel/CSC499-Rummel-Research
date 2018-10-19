import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import content.VisualFeatureDetection;
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

	private final static String[] DESCRPTIONS =
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
			System.out.print(DESCRPTIONS[i] + ",");
			for(int j = 0; j < resultingData[0].length; j++)
			{
				System.out.print(resultingData[i][j] + ",");
			}
			System.out.print("\n");
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
