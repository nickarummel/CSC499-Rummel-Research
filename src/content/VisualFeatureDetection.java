package content;

import java.io.File;

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
	 * A File instance variable represent the file that will be opened and worked on.
	 */
	protected File file;

	/**
	 * Constructor for class
	 * @param path The file path to be stored into the File object
	 */
	public VisualFeatureDetection(String path)
	{
		file = new File(path);
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
	 * Setter method for file path instance variable.
	 * It will immediately pass it into the File instance variable.
	 * @param path The new file path
	 */
	public void setFilePath(String path)
	{
		file = new File(path);
	}
	
	/**
	 * Getter method for the File object instance variable.
	 * @return The File object
	 */
	public File getFile()
	{
		return file;
	}

}
