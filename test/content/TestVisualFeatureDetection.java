package content;

import static org.junit.Assert.*;

import java.awt.Color;
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
	 * Tests to see if the text font size in an HTML file can be found for an
	 * article's title.
	 */
	@Test
	public void testArticleTitleFontSizeDetection()
	{
		Elements allElements = vfd.getAllTextElements();
		// paragraph tag is size 9 font so false
		for (int i = 0; i < allElements.size(); i++)
		{
			assertFalse(vfd.articleTitleFontSizeDetection(allElements.get(i)));
		}

		// h2 tag at default size exists so true
		// all three tags will return true (h1, h2, paragraph)
		vfd.setFilePath("testset\\testPage2.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleTitleFontSizeDetection(allElements.get(i)));
		}

		// h1 tag will have a size over 100 px so false
		vfd.setFilePath("testset\\testPage3.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertFalse(vfd.articleTitleFontSizeDetection(allElements.get(i)));
		}

		// pull style information from head of HTML file
		// paragraph is font size 12 so false
		vfd.setFilePath("testset\\testPage4.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertFalse(vfd.articleTitleFontSizeDetection(allElements.get(i)));
		}

	}

	/**
	 * Checks if the color checker can match black as black
	 */
	@Test
	public void testCheckColorInRangeBlack()
	{
		Color black = new Color(0, 0, 0);
		assertTrue(vfd.checkColorInRange(black, new Color(0, 0, 0), 50));
	}

	/**
	 * Checks if the color checker can determine if colors are in the range of
	 * another color.
	 */
	@Test
	public void testCheckColorInRangeBlue()
	{
		Color blue = Color.decode("#0000FF");
		// check black against blue
		assertFalse(vfd.checkColorInRange(blue, new Color(0, 0, 0), 150));
		// check red against blue
		assertFalse(vfd.checkColorInRange(blue, Color.decode("#FF0000"), 150));
		// check green against blue
		assertFalse(vfd.checkColorInRange(blue, Color.decode("#008000"), 150));
		// check yellow against blue
		assertFalse(vfd.checkColorInRange(blue, Color.decode("#FFFF00"), 150));
		// check magenta against blue
		assertFalse(vfd.checkColorInRange(blue, Color.decode("#FF00FF"), 150));
		// check cyan against blue
		assertFalse(vfd.checkColorInRange(blue, Color.decode("#00FFFF"), 150));
		// check navy against blue
		assertTrue(vfd.checkColorInRange(blue, Color.decode("#000080"), 150));
	}

	/**
	 * Tests to see if the text font color in an HTML file can be found for an
	 * article's title.
	 */
	@Test
	public void testArticleTitleFontColorDetection()
	{
		// check paragraph and two heading tags
		vfd.setFilePath("testset\\testPage2.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			boolean flag = vfd.articleTitleFontColorDetection(allElements.get(i));
			if (i == 0)
			{
				// paragraph is red
				assertFalse(flag);
			}
			else
			{
				// indices 1 and 2: h1 is blue (in-line style) and h2 is black
				// by default
				assertTrue(flag);
			}

		}

		// h1 tag has no color set, so it will default to black
		vfd.setFilePath(("testset\\testPage3.html"));
		allElements = vfd.getAllTextElements();
		assertTrue(vfd.articleTitleFontColorDetection(allElements.get(0)));
	}

	/**
	 * Tests whether elements are in the top half of the web page.
	 */
	@Test
	public void testArticleTitleTopHalfOfPageDetection()
	{
		// check h2, paragraph, and h1 elements
		vfd.setFilePath("testset\\testPage2.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			boolean flag = vfd.articleTitleTopHalfOfPageDetection(allElements.get(i));
			if (i == 2)
			{
				// h2 (index 2) element is in the top half
				assertTrue(flag);
			}
			else
			{
				// paragraph (index 0) and h1 (index 1) are not in the top half
				assertFalse(flag);
			}
		}
	}

	/**
	 * Tests that the title is located near the top and the user does not need
	 * to page down to see to see the title.
	 */
	@Test
	public void testArticleTitlePageDownDetection()
	{
		// no need to page down for any text elements in this case,
		// all are near the top
		vfd.setFilePath("testset\\testPage2.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleTitlePageDownDetection(allElements.get(i)));
		}

		// file contains a paragraph at the top that will be true (meaning no
		// page down) then a heading after about 50 line breaks that will be
		// false (meaning page down required)
		vfd.setFilePath("testset\\testPage7.html");
		allElements = vfd.getAllTextElements();
		assertTrue(vfd.articleTitlePageDownDetection(allElements.get(0)));
		assertFalse(vfd.articleTitlePageDownDetection(allElements.get(1)));

	}

	/**
	 * Tests that the length of a title is between 8 and 50 characters.
	 */
	@Test
	public void testArticleTitleTextLengthDetection()
	{
		// check h2, paragraph, and h1 elements in character range
		vfd.setFilePath("testset\\testPage2.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleTitleTextLengthDetection(allElements.get(i)));
		}

		// case: a title longer than 50 characters
		vfd.setFilePath("testset\\testPage5.html");
		allElements = vfd.getAllTextElements();
		assertFalse(vfd.articleTitleTextLengthDetection(allElements.get(0)));

		// case: a title less than 8 characters
		vfd.setFilePath("testset\\testPage6.html");
		allElements = vfd.getAllTextElements();
		assertFalse(vfd.articleTitleTextLengthDetection(allElements.get(0)));
	}

	/**
	 * Tests that the title does not contain a hyper link.
	 */
	@Test
	public void testArticleTitleHyperLinkDetection()
	{
		// all three elements (p, h1, h2) are not linked so it will return true
		vfd.setFilePath("testset\\testPage2.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleTitleHyperLinkDetection(allElements.get(i)));
		}

		// border case: link will be a parent to the current node
		vfd.setFilePath("testset\\testPage5.html");
		allElements = vfd.getAllTextElements();
		assertFalse(vfd.articleTitleHyperLinkDetection(allElements.get(0)));

		// border case: link will be a child node to the current node
		vfd.setFilePath("testset\\testPage6.html");
		allElements = vfd.getAllTextElements();
		assertFalse(vfd.articleTitleHyperLinkDetection(allElements.get(0)));

	}

	/**
	 * Tests all title rules together in nested conditionals.
	 */
	@Test
	public void testArticleTitleExists()
	{
		// testPage1.html - no title
		assertFalse(vfd.articleTitleExists());

		// testPage2.html - has title
		vfd.setFilePath("testset\\testPage2.html");
		assertTrue(vfd.articleTitleExists());

		// testPage3.html - no title
		vfd.setFilePath("testset\\testPage3.html");
		assertFalse(vfd.articleTitleExists());

		// testPage4.html - no title
		vfd.setFilePath("testset\\testPage4.html");
		assertFalse(vfd.articleTitleExists());

		// testPage5.html - no title
		vfd.setFilePath("testset\\testPage5.html");
		assertFalse(vfd.articleTitleExists());

		// testPage6.html - no title
		vfd.setFilePath("testset\\testPage6.html");
		assertFalse(vfd.articleTitleExists());

		// testPage7.html - no title
		vfd.setFilePath("testset\\testPage7.html");
		assertFalse(vfd.articleTitleExists());

	}

}
