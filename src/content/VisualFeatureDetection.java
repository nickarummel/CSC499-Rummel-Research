package content;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
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
	 * An instance variable that will contain all the HTML color names.
	 */
	protected ArrayList<String> htmlColorNames;

	/**
	 * An instance variable that will contain all the HTML color hex values,
	 * where the index of each hex value corresponds with the color names in the
	 * ArrayList htmlColorNames.
	 */
	protected ArrayList<String> htmlColorHex;

	/**
	 * An instance variable that contains each month of the year as an
	 * abbreviation.
	 */
	protected final String[] MONTHABBR =
	{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	/**
	 * Constructor for class that will immediately update the DOM tree from file
	 * path parameter.
	 * @param path The file path to be stored into the File object
	 */
	public VisualFeatureDetection(String path)
	{
		setFilePath(path);
		updateDOMTree();
		htmlColorNames = new ArrayList<String>();
		htmlColorHex = new ArrayList<String>();
		readHtmlColorNamesAndHex();

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
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Initializes both the htmlColorNames and htmlColorHex ArrayLists with the
	 * values from the colorsHTML.csv file.
	 */
	protected void readHtmlColorNamesAndHex()
	{
		try
		{
			Scanner csvReader = new Scanner(new File("data\\colorsHTML.csv"));
			while (csvReader.hasNextLine())
			{
				String[] tokens = csvReader.nextLine().split(",");
				htmlColorNames.add(tokens[0]);
				htmlColorHex.add(tokens[1]);
			}
			csvReader.close();
		}
		catch (FileNotFoundException e)
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
		boolean titleExists = false;
		for (int i = 0; i < allElements.size(); i++)
		{
			// rule 1
			if (articleTitleFontSizeDetection(allElements.get(i)))
			{
				// rule 2
				if (articleTitleFontColorDetection(allElements.get(i)))
				{
					// rule 3
					if (articleTitleTopHalfOfPageDetection(allElements.get(i)))
					{
						// rule 4
						if (articleTitlePageDownDetection(allElements.get(i)))
						{
							// rule 5
							if (articleTitleTextLengthDetection(allElements.get(i)))
							{
								// rule 6
								if (articleTitleHyperLinkDetection(allElements.get(i)))
								{
									// all six rules were passed, so title
									// exists. set flag to true and break out of
									// loop
									titleExists = true;
									break;
								}
							}
						}

					}
				}
			}
		}

		return titleExists;

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
		// get all elements with a span tag
		allElements.addAll(doc.getElementsByTag("span"));
		// get all elements with a time tag
		allElements.addAll(doc.getElementsByTag("time"));
		// get all elements with heading tags
		for (int i = 1; i <= 6; i++)
		{
			allElements.addAll(doc.getElementsByTag("h" + i));
		}
		return allElements;
	}

	/**
	 * Title detection's rule 1: determine that the font size is between 15 and
	 * 45 pixels.
	 * @param textSet The current element that has a paragraph or heading tag
	 * @return true if an Element's font size is in the range.
	 */
	protected boolean articleTitleFontSizeDetection(Element textSet)
	{
		return fontSizeDetection(textSet, 15.0, 45.0);
	}

	/**
	 * Determines the font size of a given Element node. The font size can be
	 * found in-line as a style attribute or as data under the style tag of the
	 * HTML's head. Additionally, the font size can be defined as three
	 * different measurement units: percentage, em, or pixels.
	 * @param textSet The current element node that has a paragraph or heading
	 *            tag
	 * @param min the minimum font size as a double
	 * @param max the maximum font size as a double
	 * @return true if the element's text is within the font size range,
	 *         otherwise false
	 */
	public boolean fontSizeDetection(Element textSet, double min, double max)
	{
		// get the style's element node form the HTML head
		Elements headNode = doc.select("head");
		// styleNode will be null if no CSS data was found in the HTML's head
		Element styleNode = getHeadStyleSheet(headNode);

		Element curElement = textSet;
		// System.out.println("Element: " + curElement.toString());
		double size = 0.0;
		// check for in-line style attribute
		if (curElement.hasAttr("style") && curElement.attr("style").contains("font-size"))
		{
			// tokenize to get the font size
			String[] split = curElement.attr("style").split("font-size");
			size = extractFontSizeFromTokens(split);
		}
		// check if style exists in head and if so, check to see if it has a
		// font size attribute and the current text's tag (paragraph or heading)
		// has values in the head's style.
		else if ((styleNode != null) && styleNode.data().contains("font-size")
				&& styleDataContainsElement(curElement, styleNode.data()))
		{
			// tokenize to get the font size
			String[] fontSizeStyle = doc.select("style").first().data().split("font-size");
			// find the token with the HTML Tag (p, h1-h6, etc), and the
			// font value is in the next token (i+1)
			String[] onlyToken = new String[1];
			for (int i = 0; i < fontSizeStyle.length; i++)
			{
				// make sure the tag exists and the token is not the last in the
				// array
				if (styleDataContainsElement(curElement, fontSizeStyle[i]) && (i + 1) < fontSizeStyle.length)
				{
					// save token in separate variable
					onlyToken[0] = fontSizeStyle[i + 1];
					break;
				}
			}

			size = extractFontSizeFromTokens(onlyToken);
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
		if (size >= min && size <= max)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Checks the HTML's head for a style tag, which will contain internal CSS
	 * information.
	 * @param headNode the start of the head HTML tag
	 * @return the Element node containing the style information, null if the
	 *         style tag was not found
	 */
	public Element getHeadStyleSheet(Elements headNode)
	{
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
		return styleNode;
	}

	/**
	 * Extracts the font size in any measurable unit (em, percent, pixels).
	 * @param tokens The tokens from a String.split("font-size") call.
	 * @return the font size as a Double
	 */
	private double extractFontSizeFromTokens(String[] tokens)
	{
		String fontSize = "";
		double size;
		int splitIndex = -1;
		// loop through each token
		for (int k = 0; k < tokens.length; k++)
		{
			// find the token that contains one of the measurement units for
			// font size
			if (tokens[k].contains("em") || tokens[k].contains("%") || tokens[k].contains("px"))
			{
				splitIndex = k;
				try
				{
					// try to get 3 digits for size
					fontSize = tokens[k].substring(1, 5);
					// if any other non-number characters remain, try to get 2
					// digits
					if (fontSize.contains("p") || fontSize.contains("e") || fontSize.contains("%"))
					{
						fontSize = tokens[k].substring(1, 4);
					}
				}
				catch (Exception e)
				{
					// if getting 3 digits has a null pointer exception,
					// get two digits
					fontSize = tokens[k].substring(1, 4);
				}
			}
		}
		// trim down to get the correct size as a double if measured in em
		if (splitIndex > -1 && tokens[splitIndex].contains("em"))
		{
			String trimmed = fontSize.trim();
			// convert to double
			size = calculateEmAsPixels(Double.parseDouble(((trimmed.substring(0, trimmed.length() - 3)))));
		}
		// trim down to get the correct size as a double if measured as
		// percentage
		if (splitIndex > -1 && tokens[splitIndex].contains("%"))
		{
			String trimmed = fontSize.trim();
			// convert to double
			size = Double.parseDouble(((trimmed.substring(0, trimmed.length() - 2)))) / 100.0;
		}
		else
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

	/**
	 * Title detection rule #2: check to see if the Element's text is a black or
	 * blue font color. It can be checked using HTML color codes, RGB/RGBA, and
	 * HSL/HSLA. The color can also be found from in-line or style tags.
	 * @param textSet the current Element node that is a paragraph or heading
	 *            tag
	 * @return true if the color is black or blue, false if another color
	 */
	protected boolean articleTitleFontColorDetection(Element textSet)
	{
		Color[] articleColors = new Color[2];
		articleColors[0] = Color.decode("#000000");
		articleColors[1] = Color.decode("#0000FF");
		return fontColorDetection(textSet, articleColors);
	}

	/**
	 * To detect the font color of a given Element, the style information is
	 * pulled from in-line attributes and the head's CSS information.
	 * @param textSet The current Element node
	 * @param colors The list of colors to check for
	 * @return true if the color from the HTML matches one of the colors listed,
	 *         else false
	 */
	public boolean fontColorDetection(Element textSet, Color[] colors)
	{
		boolean detectFlag = false;

		// get the style's element node form the HTML head
		Elements headNode = doc.select("head");

		// styleNode will be null if no CSS data was found in the HTML's head
		Element styleNode = getHeadStyleSheet(headNode);
		Element curElement = textSet;

		Color fontColor = null;
		// check for in-line style attribute
		if (curElement.hasAttr("style") && curElement.attr("style").contains("color"))
		{
			// tokenize to get the font color
			String[] split = curElement.attr("style").split("color");
			fontColor = extractColorFromTokens(split);
		}
		// check if style exists in head and if so, check to see if it has a
		// color attribute and the current text's tag (paragraph or heading)
		// has values in the head's style.
		else if ((styleNode != null) && styleNode.data().contains("color")
				&& styleDataContainsElement(curElement, styleNode.data()))
		{
			// tokenize to get the font size
			String[] split = doc.select("style").first().data().split("color");

			// find the token with the HTML Tag (p, h1-h6, etc), and the
			// font value is in the next token (i+1)
			String[] onlyToken = new String[1];
			for (int i = 0; i < split.length; i++)
			{
				// make sure the tag exists and the token is not the last in the
				// array
				if (styleDataContainsElement(curElement, split[i]) && (i + 1) < split.length)
				{
					// save token in separate variable
					onlyToken[0] = split[i + 1];
					break;
				}
			}
			fontColor = extractColorFromTokens(onlyToken);
		}

		if (fontColor == null)
		{
			// no style has been found so default color is black - rgb(0,0,0)
			fontColor = new Color(0, 0, 0);
		}

		// check if color is black or blue
		for (int i = 0; i < colors.length; i++)
		{
			if (checkColorInRange(colors[i], fontColor, 150))
			{
				detectFlag = true;
				break;
			}
		}

		return detectFlag;
	}

	/**
	 * Extracts the color (as a color code, hex, RGB, etc.) from the HTML.
	 * @param tokens The parsed strings of HTML where the color information is
	 *            stored.
	 * @return the color as a Color object
	 */
	private Color extractColorFromTokens(String[] tokens)
	{
		Color color = null;
		for (int i = 0; i < tokens.length; i++)
		{
			// makes sure that the color attribute was not cut off
			// from another attribute containing the word color
			// (example: background-color)
			if (!tokens[i].endsWith("-"))
			{
				// # character starts the hex color code
				if (tokens[i].contains("#"))
				{
					// get the index of where # is
					int index = tokens[i].indexOf("#");
					String hex = "";
					// run until the the end of the string is reached, the
					// character is ';', ')', or '"'
					while ((index < tokens[i].length()) && tokens[i].charAt(index) != ';'
							&& tokens[i].charAt(index) != ')' && tokens[i].charAt(index) != '\"')
					{
						// concatenate characters together
						hex = hex + tokens[i].charAt(index);
						index++;
					}
					// if the hex code is # and three characters,
					// make it the full length of 7 characters
					if (hex.length() == 4)
					{
						String temp = "#";
						for (int j = 1; j < hex.length(); j++)
						{
							temp = temp + hex.charAt(j) + hex.charAt(j);
						}
						hex = temp;
					}
					// store color from hex value
					color = Color.decode(hex);
				}
				// rgba( defines the start of the rgba values
				else if (tokens[i].contains("rgba"))
				{
					// skip characters 'r', 'g', 'b', 'a', and '('
					int index = tokens[i].indexOf("rgba") + 5;
					int red = 0;
					int green = 0;
					int blue = 0;
					int count = 1;
					String num = "";
					// run until the the end of the string is reached, the count
					// is 3, the character is ';', ')', or '"'
					while ((index < tokens[i].length()) && tokens[i].charAt(index) != ';'
							&& tokens[i].charAt(index) != ')' && tokens[i].charAt(index) != '\"' && count <= 3)
					{
						if (tokens[i].charAt(index) == ',')
						{
							// the entire number has been retrieved if a comma
							// was reached
							if (count == 1)
							{
								// 1 - store in red as int
								red = Integer.parseInt(num.trim());
								num = "";
							}
							if (count == 2)
							{
								// 2 - store in green as int
								green = Integer.parseInt(num.trim());
								num = "";
							}
							if (count == 3)
							{
								// 3 - store in blue as int
								blue = Integer.parseInt(num.trim());
								num = "";
							}
							count++;
						}
						else
						{
							// concatenate numbers into a string
							num = num + tokens[i].charAt(index);
						}
						index++;
					}
					// store color using RGB
					color = new Color(red, green, blue);

				}
				// rgb( defines the start of the rgb values
				else if (tokens[i].contains("rgb"))
				{
					// skip characters 'r', 'g', 'b', and '('
					int index = tokens[i].indexOf("rgb") + 4;
					int red = 0;
					int green = 0;
					int blue = 0;
					int count = 1;
					String num = "";
					// run until the the end of the string is reached, the count
					// is 3, the character is ';', ')', or '"'
					while ((index < tokens[i].length()) && tokens[i].charAt(index) != ';'
							&& tokens[i].charAt(index) != ')' && tokens[i].charAt(index) != '\"' && count <= 3)
					{
						if (tokens[i].charAt(index) == ',' || (count == 3 && tokens[i].charAt(index + 1) <= '9'
								&& tokens[i].charAt(index + 1) >= '0'))
						{
							// a number is read if it a comma is reached or if
							// the next character is not a number
							if (count == 1)
							{
								// store in red as int
								red = Integer.parseInt(num.trim());
								num = "";
							}
							if (count == 2)
							{
								// store in green as int
								green = Integer.parseInt(num.trim());
								num = "";
							}
							if (count == 3)
							{
								// store in blue as int
								blue = Integer.parseInt(num.trim());
								num = "";
							}
							count++;
						}
						else
						{
							// concatenate numbers into a string
							num = num + tokens[i].charAt(index);
						}
						index++;
					}
					// store color as RGB
					color = new Color(red, green, blue);
				}
				// hsla( defines the start of the hsla values
				else if (tokens[i].contains("hsla"))
				{
					// skip characters 'h', 's', 'l', 'a', and '('
					int index = tokens[i].indexOf("hsla") + 5;
					float hue = 0;
					float saturation = 0;
					float lightness = 0;
					int count = 1;
					String num = "";
					// run until the the end of the string is reached, the count
					// is 3, the character is ';', ')', or '"'
					while ((index < tokens[i].length()) && tokens[i].charAt(index) != ';'
							&& tokens[i].charAt(index) != ')' && tokens[i].charAt(index) != '\"' && count <= 3)
					{
						if (tokens[i].charAt(index) == ',' || (count == 3 && (tokens[i].charAt(index + 1) == '%'
								|| (tokens[i].charAt(index + 1) <= '9' && tokens[i].charAt(index + 1) >= '0'))))
						{
							// the entire number has been retrieved if a comma
							// was reached, or if the count is 3 and:
							// 1. next character is '%' or 2. next character is
							// a number
							if (count == 1)
							{
								// store hue as a float
								hue = Float.parseFloat(num.trim());
								num = "";
							}
							if (count == 2)
							{
								// store saturation as a float, removing percent
								// sign and diving it by 100
								saturation = (Float.parseFloat(num.trim().substring(0, num.length() - 1)) / 100);
								num = "";
							}
							if (count == 3)
							{
								// store brightness as a float, removing percent
								// sign and diving it by 100
								lightness = (Float.parseFloat(num.trim().substring(0, num.length() - 1)) / 100);
								num = "";
							}
							count++;
						}
						else
						{
							// concatenate the string of numbers
							num = num + tokens[i].charAt(index);
						}
						index++;
					}
					color = new Color(Color.HSBtoRGB(hue, saturation, lightness));
				}
				// hsl( defines the start of the hsl values
				else if (tokens[i].contains("hsl"))
				{
					// skip characters 'h', 's', 'l', and '('
					int index = tokens[i].indexOf("hsl") + 4;
					float hue = 0;
					float saturation = 0;
					float lightness = 0;
					int count = 1;
					String num = "";
					// run until the the end of the string is reached, the count
					// is 3, the character is ';', ')', or '"'
					while ((index < tokens[i].length()) && tokens[i].charAt(index) != ';'
							&& tokens[i].charAt(index) != ')' && tokens[i].charAt(index) != '\"' && count <= 3)
					{
						if (tokens[i].charAt(index) == ',')
						{
							if (count == 1)
							{
								// store hue as a float
								hue = Float.parseFloat(num.trim());
								num = "";
							}
							if (count == 2)
							{
								// store saturation as a float, removing the %
								// sign and dividing by 100
								saturation = (Float.parseFloat(num.trim().substring(0, num.length() - 1)) / 100);
								num = "";
							}
							if (count == 3)
							{
								// store brightness as a float, removing the %
								// sign and dividing by 100
								lightness = (Float.parseFloat(num.trim().substring(0, num.length() - 1)) / 100);
								num = "";
							}
							count++;
						}
						else
						{
							// concatenate the string of numbers
							num = num + tokens[i].charAt(index);
						}
						index++;
					}
					// store color as HSL
					color = new Color(Color.HSBtoRGB(hue, saturation, lightness));
				}
				// pre-defined color as text i.e. red, blue, black, etc.
				else
				{
					String colorName = "";
					// skip the ':' character
					int index = 1;
					// run until the the end of the string is reached or the
					// character is ';', '}', or '"'
					while ((index < tokens[i].length()) && tokens[i].charAt(index) != ';'
							&& tokens[i].charAt(index) != '}' && tokens[i].charAt(index) != '\"')
					{
						// concatenate each letter of the color
						colorName = colorName + tokens[i].charAt(index);
						index++;
					}
					if (colorName != null && !colorName.isEmpty())
					{
						// search for the color name from the list of HTML
						// colors
						// then retrieve the color's hex code from the second
						// list
						colorName = colorName.trim();
						for (int j = 0; j < htmlColorNames.size(); j++)
						{
							if (htmlColorNames.get(j).equalsIgnoreCase(colorName))
							{
								// store color from hex code
								color = Color.decode(htmlColorHex.get(j));
								break;
							}
						}
					}
				}
				// exist early once color is found
				if (color != null)
				{
					break;
				}
			}
		}
		return color;
	}

	/**
	 * Checks to see if a color is similar to a baseline.
	 * @param baseline The color that determines what the test should be like
	 * @param test The color to be tested against the baseline
	 * @param threshold The level of deviation from the baseline from 0 to 765
	 * @return true if color is in threshold, false if color is outside
	 *         threshold
	 */
	protected boolean checkColorInRange(Color baseline, Color test, int threshold)
	{
		boolean inRangeFlag = false;
		// check if baseline is black
		if (baseline.getRed() == 0 && baseline.getGreen() == 0 && baseline.getBlue() == 0)
		{
			// check test color to see if it is also black
			if (test.getRed() == 0 && test.getGreen() == 0 && test.getBlue() == 0)
			{
				inRangeFlag = true;
			}
		}
		// baseline color is not black
		else
		{
			// make sure threshold is between 0 and 765
			int level;
			if (threshold > 765)
			{
				// threshold > 765, so set to max of 765
				level = 765;
			}
			else if (threshold < 0)
			{
				// threshold is negative, so set to minimum of 0
				level = 0;
			}
			else
			{
				// threshold in range, so keep value
				level = threshold;
			}

			// calculate the difference in each color (RGB)
			int red = Math.abs(baseline.getRed() - test.getRed());
			int green = Math.abs(baseline.getGreen() - test.getGreen());
			int blue = Math.abs(baseline.getBlue() - test.getBlue());
			// check that the difference delta is less than the threshold level
			if ((red + green + blue) <= level)
			{
				// if less than threshold, set to true
				inRangeFlag = true;
			}

		}
		return inRangeFlag;
	}

	/**
	 * A method to determine whether the title is located in the top half of the
	 * web page. To determine if it is in the top half, it finds the location of
	 * the given element node in the list of all element nodes. Then, the given
	 * node's location is divided by the total number of nodes to get a
	 * percentage.
	 * @param testSet The given element node
	 * @return true if the percentage < 50%, false if >= 50%
	 */
	protected boolean articleTitleTopHalfOfPageDetection(Element testSet)
	{
		// select all element nodes under the body
		Elements domTree = doc.body().children().select("*");
		// get the total number of nodes
		int treeSize = domTree.size();
		int nodeIndex = -1;
		int i = 0;
		// loops until the given node's index is found or the end of the tree is
		// reached
		while (nodeIndex == -1 && i < domTree.size())
		{
			// the given node was found in the tree
			if (domTree.get(i).equals(testSet))
			{
				// save the node's index
				nodeIndex = i;
			}
			else
			{
				// increment if the node's location was not found
				i++;
			}
		}

		// calculate percentage
		double result = ((double) nodeIndex) / ((double) treeSize);

		if (result < 0.5)
		{
			// return true if < 50%
			return true;
		}
		else
		{
			// return false if >= 50%
			return false;
		}
	}

	/**
	 * A method to determine if the title is located near the top of the screen.
	 * This is determined by converting tags to line breaks and seeing if the
	 * location of the current text is less than the number of lines that can be
	 * shown on a page.
	 * @param textSet the current Element node
	 * @return true if the title can be seen without paging down, false if page
	 *         down is needed to see title
	 */
	protected boolean articleTitlePageDownDetection(Element textSet)
	{
		boolean pageDownFlag = false;

		// array contains tags to be converted to new lines
		String[] htmlTagsToConvert =
		{ "br", "p", "h1", "h2", "h3", "h4", "h5", "h6", "div", "td", "li" };
		// clone the DOM tree
		Document clone = doc.clone();
		// convert each tag to new line ('\n')
		for (int i = 0; i < htmlTagsToConvert.length; i++)
		{
			clone.select(htmlTagsToConvert[i]).before("\\n");
		}
		String html = clone.body().html().replaceAll("\\\\n", "\n");
		// run Jsoup's clean method to strip out all tags but keep new lines
		String allText = Jsoup.clean(html, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
		// split text into tokens based on new line
		String[] tokens = allText.split("\n");
		// find which token has the current node's text and save that index
		int index = -1;
		for (int i = 0; i < tokens.length; i++)
		{
			if (tokens[i].contains(textSet.text()))
			{
				index = i;
			}
		}
		// check to see if the node is in the first page of web page
		// (by looking at whether there was less than 40 lines)
		if (index > -1 && index <= 40)
		{
			// if true, set flag to false
			pageDownFlag = true;
		}

		return pageDownFlag;
	}

	/**
	 * A method to determine if the text of an element's node (without
	 * tags/attributes) has a text length between 8 and 50 characters.
	 * @param textSet The current Element node to check
	 * @return true if the text falls in the range of 8 and 50 characters, false
	 *         if is outside that range
	 */
	protected boolean articleTitleTextLengthDetection(Element textSet)
	{
		return textLengthDetection(textSet, 8, 50);
	}

	/**
	 * Detects the text length of a given Element node.
	 * @param textSet The current Element node
	 * @param min The minimum value as an integer
	 * @param max The maximum value as an integer
	 * @return true if
	 */
	public boolean textLengthDetection(Element textSet, int min, int max)
	{
		boolean lengthFlag = false;
		// get the node's text without tags and attributes
		String nodeText = textSet.text();
		// check if the text's length falls in range
		if (nodeText.length() > min && nodeText.length() < max)
		{
			// true, so set flag
			lengthFlag = true;
		}
		return lengthFlag;
	}

	/**
	 * A method to determine if the title is a hyper link.
	 * @param textSet The current Element node to check
	 * @return true if the title is not a hyper link based on requirements,
	 *         return false if the title is a hyper link.
	 */
	protected boolean articleTitleHyperLinkDetection(Element textSet)
	{
		return hyperLinkDetection(textSet);
	}

	/**
	 * Determines whether a node's text is hyper linked.
	 * @param textSet The current Element node to check
	 * @return true if the node is not hyper linked based on requirements,
	 *         return false if the node's text is hyper linked.
	 */
	private boolean hyperLinkDetection(Element textSet)
	{
		boolean linkFlag = true;
		// get all nodes that contain a link (a tag)
		Elements hyperlinkNodes = doc.getElementsByTag("a");
		// get all parent nodes up to root from current element to check if any
		// surround nodes are links
		Elements parentNodes = textSet.parents();
		// get all children nodes from current element to check if any children
		// are links
		Elements childNodes = textSet.children();
		// outer loop: loop through all nodes containing links
		for (int i = 0; i < hyperlinkNodes.size(); i++)
		{
			// inner loop #1: loop through all parent nodes
			for (int j = 0; j < parentNodes.size(); j++)
			{
				// checks if any parent nodes match the current node containing
				// a link
				if (parentNodes.get(j).equals(hyperlinkNodes.get(i)))
				{
					// if the nodes match, set flag to true and break from inner
					// loop
					linkFlag = false;
					break;
				}
			}
			// check if flag is true from previous loop
			if (linkFlag == false)
			{
				// if flag is true, break from outer loop
				break;
			}
			// inner loop #2: loop through all children nodes
			for (int j = 0; j < childNodes.size(); j++)
			{
				// checks if any child nodes match the current node containing a
				// link
				if (childNodes.get(j).equals(hyperlinkNodes.get(i)))
				{
					// if the nodes match, set flag to true and break from inner
					// loop
					linkFlag = false;
					break;
				}
			}
			// check if flag is true from previous loop
			if (linkFlag == false)
			{
				// if flag is true, break from outer loop
				break;
			}
		}
		return linkFlag;
	}

	/**
	 * Method to determine if the publication date exists using 5 rules for each
	 * eligible set of data. 1. Font size is no larger than 10 px 2. Font color
	 * is black, blue, or gray 3. Text length is no longer than 18 characters 4.
	 * The text is formatted as a date 5. The text is not hyperlinked
	 * @return true if all rules match for an element, false if no elements from
	 *         the set match all rules
	 */
	public boolean articlePublicationDateExists()
	{
		Elements allElements = getAllTextElements();
		boolean pubDateExists = false;
		for (int i = 0; i < allElements.size(); i++)
		{
			// rule 1
			if (articlePublicationDateFontSizeDetection(allElements.get(i)))
			{
				// rule 2
				if (articlePublicationDateFontColorDetection(allElements.get(i)))
				{
					// rule 3
					if (articlePublicationDateTextLengthDetection(allElements.get(i)))
					{
						// rule 4
						if (articlePublicationDateFormatDetection(allElements.get(i)))
						{
							// rule 5
							if (articlePublicationDateHyperLinkDetection(allElements.get(i)))
							{
								// all five rules were passed, so date exists.
								// set flag to true and break out of loop
								pubDateExists = true;
								break;

							}
						}

					}
				}
			}
		}

		return pubDateExists;

	}

	/**
	 * Date detection rule #1: see if the Element's text font size is less than
	 * or equal to 10 pixels.
	 * @param textSet the current Element containing a paragraph or heading tag
	 * @return true if the font size <= 10 px, otherwise false
	 */
	protected boolean articlePublicationDateFontSizeDetection(Element textSet)
	{
		double min = 0.0;
		double max = 10.0;
		return fontSizeDetection(textSet, min, max);
	}

	/**
	 * Date detection rule #2: see if the Element's text font color is black,
	 * blue, or gray.
	 * @param textSet the current Element containing a paragraph or heading tag
	 * @return true if the font color is black, blue, or gray, else false
	 */
	protected boolean articlePublicationDateFontColorDetection(Element textSet)
	{
		Color[] articleColors = new Color[3];
		articleColors[0] = Color.decode("#000000");
		articleColors[1] = Color.decode("#0000FF");
		articleColors[2] = Color.decode("#808080");
		return fontColorDetection(textSet, articleColors);
	}

	/**
	 * Date detection rule #3: see if the Element's text has a length less than
	 * 18 characters.
	 * @param textSet The current Element containing a paragraph or heading tag
	 * @return true if the text length is less than 18 characters, else false
	 */
	protected boolean articlePublicationDateTextLengthDetection(Element textSet)
	{
		return textLengthDetection(textSet, 0, 19);
	}

	/**
	 * Date detection rule #3: see if the Element's text is formatted as a date.
	 * This method can detect numerical date values (ie. mm/dd/yyyy) and dates
	 * written out as text (ie. month dd, yyyy).
	 * @param textSet The current Element node
	 * @return true if the Element's text matches one of the date formats,
	 *         otherwise false
	 */
	protected boolean articlePublicationDateFormatDetection(Element textSet)
	{
		boolean dateFlag = false;
		// save text from Element node
		String text = textSet.text().trim();
		int loc = -1;
		// see if text contains one of the months as an abbreviation
		for (int i = 0; i < MONTHABBR.length; i++)
		{
			if (text.contains(MONTHABBR[i]))
			{
				// text contains a month, store the index of where the month
				// starts
				loc = text.indexOf(MONTHABBR[i]);
				break;
			}
		}
		// month found (index variable, loc, is set to 0 or above)
		if (loc > -1)
		{
			// the date does not come before the month
			if (checkForDayBeforeMonth(text, loc))
			{
				// format: dd month (may have yyyy)
				dateFlag = true;
			}
			else
			{
				for (int i = loc; i < text.length(); i++)
				{
					if (text.charAt(i) == ' ')
					{
						// month dd, yyyy - check next 8 characters for format
						if (i + 8 < text.length())
						{
							if (text.charAt(i + 1) >= '0' && text.charAt(i + 1) <= '9')
							{
								if (text.charAt(i + 2) >= '0' && text.charAt(i + 2) <= '9')
								{
									if (text.charAt(i + 3) == ',' && text.charAt(i + 4) == ' ')
									{
										int trueCount = 0;
										for (int j = 1; j <= 4; j++)
										{
											if (text.charAt(i + j + 4) >= '0' && text.charAt(i + j + 4) <= '9')
											{
												trueCount++;
											}
										}
										if (trueCount == 4)
										{
											dateFlag = true;
										}
									}
								}
							}
						}
						// month d, yyyy - check next 7 characters for format
						if (dateFlag == false && i + 7 < text.length())
						{
							if (text.charAt(i + 1) >= '0' && text.charAt(i + 1) <= '9')
							{
								if (text.charAt(i + 2) == ',' && text.charAt(i + 3) == ' ')
								{
									int trueCount = 0;
									for (int j = 1; j <= 4; j++)
									{
										if (text.charAt(i + j + 3) >= '0' && text.charAt(i + j + 3) <= '9')
										{
											trueCount++;
										}
									}
									if (trueCount == 4)
									{
										dateFlag = true;
									}

								}
							}
						}
						// month dd - check next 2 characters for format
						if (dateFlag == false && i + 2 < text.length())
						{
							if (text.charAt(i + 1) >= '0' && text.charAt(i + 1) <= '9')
							{
								if (text.charAt(i + 2) >= '0' && text.charAt(i + 2) <= '9')
								{
									dateFlag = true;
								}

							}
						}
						// month d - check next character for format
						if (dateFlag == false && i + 1 < text.length() && text.charAt(i + 1) >= '0'
								&& text.charAt(i + 1) <= '9')
						{
							dateFlag = true;

						}
						// break because next space has been found
						break;
					}
				}
			}
		}
		// no month found (index variable, loc, is null or -1)
		else
		{
			// split the string based on a space
			String[] tokens = text.split(" ");
			// check each token
			for (int i = 0; i < tokens.length; i++)
			{
				// must contain at least 6 characters(m/d/yy)
				// but no more than 10 characters (mm/dd/yyyy)
				if (tokens[i].length() >= 6 && tokens[i].length() <= 10)
				{
					// count the number of '/' or '-' and number of integer
					// characters
					// found in the string token
					int countSlash = 0;
					int countDash = 0;
					int countNum = 0;
					for (int j = 0; j < tokens[i].length(); j++)
					{
						if (tokens[i].charAt(j) == '/')
						{
							countSlash++;
						}
						else if (tokens[i].charAt(j) == '-')
						{
							countDash++;
						}
						else if (tokens[i].charAt(j) >= '0' && tokens[i].charAt(j) <= '9')
						{
							countNum++;
						}
					}
					// if the proper number of slashes/dashes and integers
					// are found in the token, it is a date
					if (((countSlash == 2 && countDash == 0) || (countDash == 2 && countSlash == 0))
							&& countNum == tokens[i].length() - 2)
					{
						dateFlag = true;
						break;
					}
				}
			}
		}

		return dateFlag;
	}

	/**
	 * Checks whether the day is located before the month following the format
	 * like '20 July'. Before
	 * @param data The text containing the date
	 * @param startIndex The index of where the date starts
	 * @return true if the day is found before the month in the text, otherwise
	 *         false
	 */
	private boolean checkForDayBeforeMonth(String data, int startIndex)
	{
		// 1 character back: space
		if (startIndex != 0 && startIndex >= 3 && data.charAt(startIndex - 1) == ' ')
		{
			// 2 characters back: a number between 0 and 9
			if (data.charAt(startIndex - 2) <= '0' && data.charAt(startIndex - 2) <= '9')
			{
				// 3 characters back: can either be a space or a number between
				// 0 and 9
				if (data.charAt(startIndex - 3) == ' '
						|| (data.charAt(startIndex - 3) <= '0' && data.charAt(startIndex - 3) <= '9'))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Date Detection Rule #6: Determine if the publication date is a hyper
	 * link.
	 * @param textSet The current Element node to check
	 * @return true if the date is not a hyper link based on requirements,
	 *         return false if the date is a hyper link.
	 */
	protected boolean articlePublicationDateHyperLinkDetection(Element textSet)
	{
		return hyperLinkDetection(textSet);
	}

	/**
	 * Method to determine if the author exists using 3 rules for each eligible
	 * set of data. 1. Font size is no larger than 12 px 2. Text length is
	 * between 3 and 25 characters 3. The text contains a frequent key word
	 * ("by", "author")
	 * @return true if all rules match for an element, false if no elements from
	 *         the set match all rules
	 */
	public boolean articleAuthorExists()
	{
		Elements allElements = getAllTextElements();
		boolean authorExists = false;
		for (int i = 0; i < allElements.size(); i++)
		{
			// rule 1
			if (articleAuthorFontSizeDetection(allElements.get(i)))
			{
				// rule 2
				if (articleAuthorTextLengthDetection(allElements.get(i)))
				{
					// rule 3
					if (articleAuthorFrequentWordDetection(allElements.get(i)))
					{
						// all three rules were passed, so author exists.
						// set flag to true and break out of loop
						authorExists = true;
						break;
					}
				}
			}
		}
		return authorExists;
	}

	/**
	 * Author Detection Rule #1: Determine if the node's text is less than or
	 * equal to 12 pixels.
	 * @param textSet The current Element node
	 * @return true if the Element's font size is less than or equal to 12
	 *         pixels, otherwise false
	 */
	protected boolean articleAuthorFontSizeDetection(Element textSet)
	{
		return fontSizeDetection(textSet, 0.0, 13.0);
	}

	/**
	 * Author detection rule #2: see if the Element's text has a length between
	 * 3 and 25 characters.
	 * @param textSet The current Element containing a paragraph or heading tag
	 * @return true if the text length is between 3 and 25 characters, else
	 *         false
	 */
	protected boolean articleAuthorTextLengthDetection(Element textSet)
	{
		return textLengthDetection(textSet, 3, 25);
	}

	/**
	 * Author detection rule #3: see if the Element's text contains one of the
	 * frequent words "by" or "author".
	 * @param textSet The current Element containing text
	 * @return true if the Element's text has one of the words, otherwise false
	 */
	protected boolean articleAuthorFrequentWordDetection(Element textSet)
	{
		String[] freqWords =
		{ "by", "author" };
		return frequentWordDetection(textSet, freqWords);
	}

	/**
	 * Determines if an Element node's text contains one of the frequent words.
	 * The code also verifies that the word stands by itself, meaning that
	 * cannot be part of another word (it must be separate from other letter
	 * characters).
	 * @param textSet The current Element node to check
	 * @param freqWordList The list of frequent words to check the node for
	 * @return true if the node contains one of the frequent words, otherwise
	 *         false
	 */
	private boolean frequentWordDetection(Element textSet, String[] freqWordList)
	{
		boolean wordFlag = false;
		// get text from element, make it lower case, and trim off extra white
		// space
		String text = textSet.text().toLowerCase().trim();
		for (int i = 0; i < freqWordList.length; i++)
		{
			// check if the text contains the current frequent word
			if (text.contains(freqWordList[i]))
			{
				// if so, check to make sure the word is "alone"
				int loc = text.indexOf(freqWordList[i]);
				// no characters before the start of the string, so check the
				// next two conditions
				if (loc == 0)
				{
					// check to see if the word doesn't have text after it
					if ((loc + freqWordList[i].length()) == text.length() - 1)
					{
						wordFlag = true;
						break;
					}
					// check to see if word is followed by a non-letter
					// character (ex. space, colon, etc.)
					else if ((loc + freqWordList[i].length()) < text.length()
							&& (text.charAt(loc + freqWordList[i].length()) < 'A'
									|| text.charAt(loc + freqWordList[i].length()) > 'z'))
					{
						wordFlag = true;
						break;
					}
				}
				// check to see if the character before the word is not a letter
				// (ex. space), check next two conditions
				else if (loc > 0 && (text.charAt(loc - 1) < 'A' || text.charAt(loc - 1) > 'z'))
				{
					// check to see if the word doesn't have text after it
					if ((loc + freqWordList[i].length()) == text.length() - 1)
					{
						wordFlag = true;
						break;
					}
					// check to see if word is followed by a non-letter
					// character (ex. space, colon, etc.)
					else if ((loc + freqWordList[i].length()) < text.length()
							&& (text.charAt(loc + freqWordList[i].length()) < 'A'
									|| text.charAt(loc + freqWordList[i].length()) > 'z'))
					{
						wordFlag = true;
						break;
					}
				}
			}
		}
		return wordFlag;
	}
}
