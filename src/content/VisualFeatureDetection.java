package content;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The class containing all of the code relevant to the content-based visual
 * feature detection for the research project.
 * @author Nick Rummel
 *
 */
public class VisualFeatureDetection
{
	/**
	 * A constant String that contains the character set code for all files
	 * being opened.
	 */
	protected final String CHARSET = "UTF-8";

	/**
	 * A File instance variable represent the file that will be opened and
	 * worked on.
	 */
	protected File file;

	/**
	 * An instance variable representing the parsed HTML from the File object.
	 * The Document object is from JSoup's library.
	 */
	protected Document doc;

	/**
	 * An instance variable that contains the default pixel size for the body of
	 * HTML files.
	 */
	protected final double PIXELSIZE = 16.0;

	/**
	 * An instance variable that contains an array of the default font sizes for
	 * paragraphs and headings 1-6. The paragraph tag's size is located in index
	 * 0. Heading tags 1-6 are locate in indices 1-6, respectively.
	 */
	protected final double[] DEFAULTBODYEMSIZE =
	{ 1.0, 2.0, 1.5, 1.17, 1.0, 0.83, 0.67 };

	/**
	 * An instance variable that contains an array of tag names that contain
	 * text. Each entry corresponds to the tag's size in the DEFAULTBODYEMSIZE
	 * array.
	 */
	protected final String[] TEXTTAGS =
	{ "p", "h1", "h2", "h3", "h4", "h5", "h6" };

	/**
	 * Constructor for class that will immediately update the DOM tree from file
	 * path parameter.
	 * @param path The file path to be stored into the File object
	 */
	public VisualFeatureDetection(String path)
	{
		setFilePath(path);
		updateDOMTree();
	}

	/**
	 * Getter method for file path instance variable
	 * @return value of file path variable
	 */
	public String getFilePath()
	{
		return file.getPath();
	}

	/**
	 * Setter method for file path instance variable. It will immediately pass
	 * it into the File instance variable, followed by updating the DOM tree.
	 * @param path The new file path
	 */
	public void setFilePath(String path)
	{
		file = new File(path);
		updateDOMTree();
	}

	/**
	 * Getter method for the File object instance variable.
	 * @return The File object
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Updates the Document instance variable by loading the current File
	 * instance variable into JSoup's parse method. This allows for the document
	 * to be opened and the HTML to be parsed immediately by JSoup.
	 */
	protected void updateDOMTree()
	{
		try
		{
			doc = Jsoup.parse(file, CHARSET);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Prints out the Document Object Model (DOM) tree by looping through all
	 * elements. At each element, the node name and element's value will printed
	 * out in the console.
	 */
	public void printDOMTree()
	{
		Elements allElements = doc.getAllElements();
		for (Element element : allElements)
		{
			System.out.println(element.nodeName() + " " + element.ownText());
		}
	}

	/**
	 * Converts the em (print size) to pixels based on the default size.
	 * @param em The font size as an em unit
	 * @return the font size in pixels (as a double)
	 */
	public double calculateEmAsPixels(double em)
	{
		return em * PIXELSIZE;
	}

	/**
	 * Method to determine if the title exists using 6 rules for each eligible
	 * set of data. 1. Font size is between 15 and 45 px 2. Font color is black
	 * or blue 3. The visual location along y-axis is in the upper-half of the
	 * page 4. It is visible without paging down 5. Text length is between 8 and
	 * 50 characters long 6. The text is not a link
	 * @return true if all rules match for an element, false if no elements from
	 *         the set match all rules
	 */
	public boolean articleTitleExists()
	{
		Elements allElements = getAllTextElements();

		// rule 1
		if (articleTitleFontSizeDetection(allElements))
		{
			// TODO: rules 2-6
			return true;
		} else
		{
			return false;
		}

	}

	/**
	 * Retrieves all the text elements (meaning that they have a paragraph or
	 * one of the six heading tags.
	 * @return The Elements array list
	 */
	protected Elements getAllTextElements()
	{
		Elements allElements = new Elements();
		// get all elements with paragraph tag
		allElements.addAll(doc.getElementsByTag("p"));
		// get all elements with heading tags
		for (int i = 1; i <= 6; i++)
		{
			allElements.addAll(doc.getElementsByTag("h" + i));
		}
		return allElements;
	}

	/**
	 * Title detection's rule 1: determine that the font size is between 15 and
	 * 45 pixels. The font size can be found in-line as a style attribute or as
	 * data under the style tag of the HTML's head. Additionally, the font size
	 * can be defined as three different measurement units: percentage, em, or
	 * pixels.
	 * @param textSet The array list of Elements that have paragraph or heading
	 *            tags
	 * @return true if an Element's font size is in the range.
	 */
	protected boolean articleTitleFontSizeDetection(Elements textSet)
	{
		boolean detectFlag = false;
		// loop through each element
		for (int i = 0; i < textSet.size(); i++)
		{
			// get the style's element node form the HTML head
			Elements headNode = doc.select("head");
			// variable will remain null if no CSS style is defined in the head
			Element styleNode = null;
			// get the tags under the head
			for (int j = 0; j < headNode.size(); j++)
			{
				// check if the head node has any children
				if (headNode.get(j).childNodeSize() > 0)
				{
					// check children for style tag
					Elements children = headNode.get(j).children();
					for (int k = 0; k < children.size(); k++)
					{
						if (children.get(k).tagName().equalsIgnoreCase("style"))
						{
							// style tag has been found, so save node for future
							styleNode = children.get(k);
						}
					}
				}
			}

			Element curElement = textSet.get(i);
			System.out.println("Element: " + curElement.toString());
			double size = 0.0;
			// check for in-line style attribute
			if (curElement.hasAttr("style") && curElement.attr("style").contains("font-size"))
			{
				// tokenize to get the font size
				String[] split = curElement.attr("style").split("font-size");
				size = extractFontSizeFromTokens(split);
			}
			// check if style exists in head and if so, check to see if it has a
			// font size attribute
			// and the current text's tag (paragraph or heading) has values in
			// the head's style.
			else if ((styleNode != null) && styleNode.data().contains("font-size")
					&& styleDataContainsElement(curElement, styleNode.data()))
			{
				// tokenize to get the font size
				String[] fontSizeStyle = doc.select("style").first().data().split("font-size");
				size = extractFontSizeFromTokens(fontSizeStyle);
			}
			// no style has been found
			else
			{
				// get default font size for text tag
				String tag = curElement.tagName();
				for (int j = 0; j < TEXTTAGS.length; j++)
				{
					if (tag.equals(TEXTTAGS[j]))
					{
						size = calculateEmAsPixels(DEFAULTBODYEMSIZE[j]);
					}
				}
			}
			// check if font size is in range
			if (size >= 15.0 && size <= 45.0)
			{
				detectFlag = true;
				break;
			}
		}
		return detectFlag;

	}

	/**
	 * Extracts the font size in any measurable unit (em, percent, pixels).
	 * @param split The tokens from a String.split("font-size") call.
	 * @return the font size as a Double
	 */
	private double extractFontSizeFromTokens(String[] split)
	{
		String fontSize = "";
		double size;
		int splitIndex = -1;
		// loop through each token
		for (int k = 0; k < split.length; k++)
		{
			// find the token that contains one of the measurement units for
			// font size
			if (split[k].contains("em") || split[k].contains("%") || split[k].contains("px"))
			{
				splitIndex = k;
				try
				{
					// try to get 3 digits for size
					fontSize = split[k].substring(1, 5);
					// if any other non-number characters remain, try to get 2
					// digits
					if (fontSize.contains("p") || fontSize.contains("e") || fontSize.contains("%"))
					{
						fontSize = split[k].substring(1, 4);
					}
				} catch (Exception e)
				{
					// if getting 3 digits has a null pointer exception,
					// get two digits
					fontSize = split[k].substring(1, 4);
				}
			}
		}
		// trim down to get the correct size as a double if measured in em
		if (splitIndex > -1 && split[splitIndex].contains("em"))
		{
			String trimmed = fontSize.trim();
			// convert to double
			size = calculateEmAsPixels(Double.parseDouble(((trimmed.substring(0, trimmed.length() - 3)))));
		}
		// trim down to get the correct size as a double if measured as
		// percentage
		if (splitIndex > -1 && split[splitIndex].contains("%"))
		{
			String trimmed = fontSize.trim();
			// convert to double
			size = Double.parseDouble(((trimmed.substring(0, trimmed.length() - 2)))) / 100.0;
		} else
		{
			// trim down assuming that it was pixels
			String trimmed = fontSize.trim();
			// if it still contains non-number characters, take the substring
			// again
			if (trimmed.charAt(trimmed.length() - 1) > '9')
			{
				trimmed = trimmed.substring(0, trimmed.length() - 1);
			}
			// convert from string to double
			size = Double.parseDouble(((trimmed)));
		}
		return size;
	}

	/**
	 * Checks for CSS styling inside of the head's style tag. The formatting is
	 * usually tag {attribute: value;} or tag{attribute: value;}
	 * @param current The current Element node
	 * @param data The style data as a String
	 * @return true if the tag exists in the styling, false if the tag does not
	 *         exist
	 */
	private boolean styleDataContainsElement(Element current, String data)
	{
		boolean result = false;
		// get the tag name (p, h1-h6, etc.)
		String curTag = current.tagName();
		// check if it is in the format tag { or tag{
		if (data.contains(curTag + " {") || data.contains(curTag + "{"))
		{
			result = true;
		}

		return result;
	}

}
