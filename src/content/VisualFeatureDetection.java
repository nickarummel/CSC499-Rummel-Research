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
	 * elements. At each element, the node name and element's value will
	 * printed out in the console.
	 */
	public void printDOMTree()
	{
		Elements allElements = doc.getAllElements();
		for (Element element : allElements)
		{
			System.out.println(element.nodeName() + " " + element.ownText());
		}
	}

}
