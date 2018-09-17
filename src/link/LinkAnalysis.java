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

}
