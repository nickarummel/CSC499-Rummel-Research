package content;

import static org.junit.Assert.*;

import java.io.File;

import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

/**
 * A class that contains all of the JUnit tests related to the
 * VisualFeatureDetection class.
 * @author Nick Rummel
 *
 */
public class TestVisualFeatureDetection
{
	VisualFeatureDetection vfd;

	/**
	 * Executes before each test case.
	 */
	@Before
	public void init()
	{
		// initialize the VisualFeatureDetection object before each test case
		vfd = new VisualFeatureDetection("testset/testPage1.html");
	}

	/**
	 * Tests getter method for file path instance variable
	 */
	@Test
	public void testGetFilePath()
	{
		// getter should receive original path from initialization
		assertTrue(vfd.getFilePath().equals("testset\\testPage1.html"));
	}

	/**
	 * Tests setter method for file path from File instance variable
	 */
	@Test
	public void testSetFilePath()
	{
		// verify that File object has original path from initialization
		assertTrue(vfd.file.getPath().equals("testset\\testPage1.html"));

		// change path and verify path updated correctly
		vfd.setFilePath("testset/testPage2.html");
		assertTrue(vfd.file.getPath().equals("testset\\testPage2.html"));
	}

	/**
	 * Tests getter method for File object instance variable
	 */
	@Test
	public void testGetFile()
	{
		// make sure the correct File instance is received
		File temp = vfd.getFile();
		assertEquals(temp, vfd.file);
	}

	/**
	 * Tests that the DOM Tree updates when the file path is changed for the
	 * File object.
	 */
	@Test
	public void testUpdateDOMTree()
	{
		// retrieve original Document object
		Document initial = vfd.doc;

		// print tree out to console
		vfd.printDOMTree();

		// change path
		vfd.setFilePath("testset\\testPage2.html");

		// print tree out to console again
		vfd.printDOMTree();

		// verify that original and updated Document objects are not the same
		assertNotEquals(initial, vfd.doc);

	}

}
