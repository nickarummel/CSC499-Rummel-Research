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

	/**
	 * Checks if the URL instance variable has an identification number in the
	 * String. The ID number could come after a slash "/", after a title
	 * following a "-", as a URL parameter following "&id=", or following an
	 * underscore "_".
	 * @return true if an ID number exists, otherwise false
	 */
	public boolean linkHasIDNumber()
	{
		boolean idFlag = false;

		// parse the URL into tokens where there is a slash "/"
		String[] tokens = url.split("/");
		for (int i = 0; i < tokens.length; i++)
		{
			// check if token length > 7
			if (tokens[i].length() > 7)
			{
				int num = 0;
				// try to convert into number
				try
				{
					num = Integer.parseInt(tokens[i]);
					if (num > 1000000)
					{
						idFlag = true;
						break;
					}
				}
				catch (NumberFormatException e)
				{
					// not a number, move on to next check
				}

				// if last check failed, token may be a title
				// so parse based on hyphens "-"
				String[] titleTokens = tokens[i].split("-");
				for (int j = 0; j < titleTokens.length; j++)
				{
					// current title token starts with an int
					if (titleTokens[j].charAt(0) >= '0' && titleTokens[j].charAt(0) <= '9')
					{
						String savedToken = "";
						// case 1: title ends with html, so get rid of it
						if (titleTokens[j].endsWith(".html"))
						{
							String[] numTokens = titleTokens[j].split(".html");
							savedToken = numTokens[0];
						}
						// case 2: title ends with a number
						else if (titleTokens[j].charAt(titleTokens[j].length() - 1) >= '0'
								&& titleTokens[j].charAt(titleTokens[j].length() - 1) <= '9')
						{
							savedToken = titleTokens[j];
						}
						// try to convert into number
						try
						{
							num = Integer.parseInt(savedToken);
							if (num > 1000000)
							{
								idFlag = true;
								break;
							}
						}
						catch (NumberFormatException e)
						{
							// not a number, move on to next check
						}
					}
				}

				// if that fails, check if the number is in a parameter
				if (tokens[i].contains("?id=") || tokens[i].contains("&id="))
				{
					// split into tokens based on "id="
					String[] idTokens = tokens[i].split("id=");
					for (int j = 0; j < idTokens.length; j++)
					{
						// find the string with the id number
						String savedToken = "";
						if (idTokens[j].charAt(idTokens[j].length() - 1) >= '0'
								&& idTokens[j].charAt(idTokens[j].length() - 1) <= '9')
						{
							savedToken = idTokens[j];
						}
						else if (idTokens[j].contains("&"))
						{
							int index = idTokens[j].indexOf("&");
							savedToken = idTokens[j].substring(0, index);
						}
						// try to convert to a number
						try
						{
							num = Integer.parseInt(savedToken);
							if (num > 1000000)
							{
								idFlag = true;
								break;
							}
						}
						catch (NumberFormatException e)
						{
							// not a number, move on to next check
						}
					}
				}
				// previous failed, so see if underscore exists
				else if (tokens[i].contains("_"))
				{
					// create tokens from "_"
					String[] lineTokens = tokens[i].split("_");
					for (int j = 0; j < lineTokens.length; j++)
					{
						// check if token length > 7
						if (lineTokens[j].length() > 7)
						{
							idFlag = true;
							break;
						}
					}
				}
			}
		}

		return idFlag;
	}

}
