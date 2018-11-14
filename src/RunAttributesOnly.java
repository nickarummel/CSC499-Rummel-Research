import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import content.VisualFeatureDetection;
import link.LinkAnalysis;

/**
 * A runner class that will run once. It will check each candidate for the
 * visual features and link analysis. The results of each candidate will be
 * printed out.
 * @author Nick Rummel
 *
 */
public class RunAttributesOnly
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

		for (int i = 0; i <= 1; i++)
		{
			int articleNum = 280 + i;
			System.out.println("Article #" + articleNum + "\n");

			// Washington Post articles 280 (article) and 281 (not an article),
			// but offset by 1 in array
			VisualFeatureDetection vfd = new VisualFeatureDetection(DATASETPATH + htmlFilePaths[articleNum - 1]);
			vfd.showArticleAuthorData();
			vfd.showArticleCategoryData();
			vfd.showArticleCommentLinkData();
			vfd.showArticleContentData();
			vfd.showArticlePublicationDateData();
			vfd.showArticleRelatedNewsLinksData();
			vfd.showArticleSourceData();
			vfd.showArticleTitleData();

			LinkAnalysis la = new LinkAnalysis(htmlURL[articleNum - 1]);
			la.showLinkData();
			
			System.out.println("\nWHAT VF IS FOUND??");
			System.out.println("Article author exists - " + vfd.articleAuthorExists());
			System.out.println("Article category exists - " + vfd.articleCategoryExists());
			System.out.println("Article comment link exists - " + vfd.articleCommentLinkExists());
			System.out.println("Article content exists - " + vfd.articleContentExists());
			System.out.println("Article publication date exists - " + vfd.articlePublicationDateExists());
			System.out.println("Article related news links exists - " + vfd.articleRelatedNewsLinksExists());
			System.out.println("Article source exists - " + vfd.articleSourceExists());
			System.out.println("Article title exists - " + vfd.articleTitleExists());
		}

	}

}
