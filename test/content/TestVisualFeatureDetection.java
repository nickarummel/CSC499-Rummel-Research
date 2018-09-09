package content;

import static org.junit.Assert.*;

import java.io.File;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
		// vfd.printDOMTree();

		// change path
		vfd.setFilePath("testset\\testPage2.html");

		// print tree out to console again
		// vfd.printDOMTree();

		// verify that original and updated Document objects are not the same
		assertNotEquals(initial, vfd.doc);

	}

	/**
	 * Tests that the font size as em units can be converted to pixels
	 * correctly.
	 */
	@Test
	public void testCalculateEmAsPixel()
	{
		// calculate for 16 pixels
		double result = vfd.calculateEmAsPixels(1.0);
		assertTrue(16.0 == result);

		// calculate for 22 pixels
		result = vfd.calculateEmAsPixels(1.3750);
		assertTrue(22.0 == result);

		// calculate for 9 pixels
		result = vfd.calculateEmAsPixels(0.5625);
		assertTrue(9.0 == result);
	}

	/**
	 * Tests to see if the font size of text in an HTML file can be found for an
	 * article's title.
	 */
	@Test
	public void testArticleTitleFontSizeDetection()
	{
		Elements allElements = vfd.getAllTextElements();
		// paragraph tag is size 9 font so false
		for(int i = 0; i < allElements.size(); i++)
		{
			assertFalse(vfd.articleTitleFontSizeDetection(allElements.get(i)));
		}
		

		// h2 tag at default size exists so true
		// all three tags will return true (h1, h2, paragraph)
		vfd.setFilePath("testset\\testPage2.html");
		allElements = vfd.getAllTextElements();
		for(int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleTitleFontSizeDetection(allElements.get(i)));
		}
		

		// h1 tag will have a size over 100 px so false
		vfd.setFilePath("testset\\testPage3.html");
		allElements = vfd.getAllTextElements();
		for(int i = 0; i < allElements.size(); i++)
		{
			assertFalse(vfd.articleTitleFontSizeDetection(allElements.get(i)));
		}
		

		// pull style information from head of HTML file
		// paragraph is font size 12 so false
		vfd.setFilePath("testset\\testPage4.html");
		allElements = vfd.getAllTextElements();
		for(int i = 0; i < allElements.size(); i++)
		{
			assertFalse(vfd.articleTitleFontSizeDetection(allElements.get(i)));
		}
		
	}
	

}
