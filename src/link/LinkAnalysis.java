package link;

/**
 * The class containing all of the code relevant to the link-based detection for
 * the research project.
 * @author Nick Rummel
 */
public class LinkAnalysis
{
	/**
	 * Instance variable that contains the URL of the web page.
	 */
	protected String url;

	/**
	 * Constructor of class that saves the URL.
	 * @param url The URL link to the web page as a String
	 */
	public LinkAnalysis(String url)
	{
		this.url = url;
	}

	/**
	 * Getter method for the URL instance variable.
	 * @return the URL as a String
	 */
	public String getURL()
	{
		return url;
	}

	/**
	 * Setter method for the URL instance variable.
	 * @param url The new URL value as a String
	 */
	public void setURL(String url)
	{
		this.url = url;
	}

	/**
	 * Checks if the URL has at least 4 or more slashes to be considered an
	 * article.
	 * @return true if at least 4 slashes exist in the string, otherwise false
	 */
	public boolean linkHasFourSlashes()
	{
		if (countSlashes() >= 4)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Counts the number of slashes '/' in the URL.
	 * @return number of slashes as an int
	 */
	protected int countSlashes()
	{
		int slashCount = 0;
		for (int i = 0; i < url.length(); i++)
		{
			if (url.charAt(i) == '/')
			{
				slashCount++;
			}
		}

		return slashCount;
	}

}
