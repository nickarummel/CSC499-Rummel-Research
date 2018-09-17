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

	/**
	 * Tests that the publication date's font size can be detected and is found
	 * to be less than or equal to 10 px.
	 */
	@Test
	public void testArticlePublicationDateFontSizeDetection()
	{
		// contains 3 tags: p tag at 12px, h5 tag at 9px
		// and h6 tag at default size (approx. 10.7px)
		vfd.setFilePath("testset\\testPage8.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// p tag at index 0, h6 at index 2
			// will both return false
			if (i == 0 || i == 2)
			{
				assertFalse(vfd.articlePublicationDateFontSizeDetection(allElements.get(i)));
			}
			else
			{
				// h5 tag at index 1 will return true
				assertTrue(vfd.articlePublicationDateFontSizeDetection(allElements.get(i)));
			}
		}

	}

	/**
	 * Tests that the publication date's font color can be detected and it is
	 * either black, blue, or gray.
	 */
	@Test
	public void testArticlePublicationDateFontColorDetection()
	{
		// contains 3 tags: p tag is red, h5 tag is gray
		// and h6 tag at default color (black)
		vfd.setFilePath("testset\\testPage8.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// p tag at index 0 will return false
			if (i == 0)
			{
				assertFalse(vfd.articlePublicationDateFontColorDetection(allElements.get(i)));
			}
			else
			{
				// h5 and h6 tags at index 1 and 2 will return true
				assertTrue(vfd.articlePublicationDateFontColorDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the publication date is less than or equal to 18 characters
	 * long.
	 */
	@Test
	public void testArticlePublicationDateTextLengthDetection()
	{
		// contains 3 tags: p tag is 34 characters, h5 tag is 10 characters and
		// h6 tag is 16 characters
		vfd.setFilePath("testset\\testPage8.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// p tag is > 16 characters, so it will return false
			if (i == 0)
			{
				assertFalse(vfd.articlePublicationDateTextLengthDetection(allElements.get(i)));
			}
			else
			{
				// h5 and h6 tags have <= 18 characters so it will return true
				assertTrue(vfd.articlePublicationDateTextLengthDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the publication date can be detected in a variety of formats.
	 */
	@Test
	public void testArticlePublicationDateFormatDetection()
	{
		// File contains different date formats. Each
		// tag has a valid date, so all should return true.
		vfd.setFilePath("testset\\testPage9.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articlePublicationDateFormatDetection(allElements.get(i)));
		}
	}

	/**
	 * Tests whether the publication date is hyper linked.
	 */
	@Test
	public void testArticlePublicationDateHyperLinkDetection()
	{
		// the span tag and paragraph tag contain links, the other tags do not
		vfd.setFilePath("testset\\testPage9.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// the span and p tags are the first two in the list and will return
			// false (meaning links were detected)
			if (i == 0 || i == 1)
			{
				assertFalse(vfd.articlePublicationDateHyperLinkDetection(allElements.get(i)));
			}
			// all other tags do not have links, so will return true (meaning a
			// link was not detected for the element)
			else
			{
				assertTrue(vfd.articlePublicationDateHyperLinkDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests all publication date rules together in nested conditionals.
	 */
	@Test
	public void testArticlePublicationDateExists()
	{
		// page 2 does not have elements that pass the rules, so false
		vfd.setFilePath("testset\\testPage2.html");
		assertFalse(vfd.articlePublicationDateExists());

		// page 8 has a publication date and will pass, so true
		vfd.setFilePath("testset\\testPage8.html");
		assertTrue(vfd.articlePublicationDateExists());

		// page 9 has many publication dates, but at least one will pass so true
		vfd.setFilePath("testset\\testPage9.html");
		assertTrue(vfd.articlePublicationDateExists());
	}

	/**
	 * Tests that an Element's text font size for an author must be less than or
	 * equal to 12px.
	 */
	@Test
	public void testArticleAuthorFontSizeDetection()
	{
		// only the h6 tag is <= 12 pixels, other tags will fail
		vfd.setFilePath("testset\\testPage10.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			if (i == allElements.size() - 1)
			{
				assertTrue(vfd.articleAuthorFontSizeDetection(allElements.get(i)));
			}
			else
			{
				assertFalse(vfd.articleAuthorFontSizeDetection(allElements.get(i)));
			}
		}

		// all tags have a font size <= 12 pixels, so all will pass
		vfd.setFilePath("testset\\testPage11.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleAuthorFontSizeDetection(allElements.get(i)));
		}
	}

	/**
	 * Tests that Elements with text between 3 and 25 characters long may be the
	 * article's author.
	 */
	@Test
	public void testArticleAuthorTextLengthDetection()
	{
		// all tags have less than 25 characters
		vfd.setFilePath("testset\\testPage10.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleAuthorTextLengthDetection(allElements.get(i)));
		}

		vfd.setFilePath("testset\\testPage11.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// the p tag has more than 25 characters
			if (i == 0)
			{
				assertFalse(vfd.articleAuthorTextLengthDetection(allElements.get(i)));
			}
			// all other tags have less than 25 characters
			else
			{
				assertTrue(vfd.articleAuthorTextLengthDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests if a potential author node has the frequent word "author" or "by".
	 */
	@Test
	public void testArticleAuthorFrequentWordDetection()
	{
		// only the h6 tag contains a frequent word
		vfd.setFilePath("testset\\testPage10.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// h6 tag is last index, contains word "by"
			if (i == allElements.size() - 1)
			{
				assertTrue(vfd.articleAuthorFrequentWordDetection(allElements.get(i)));
			}
			// all other tags do not contain a frequent word
			else
			{
				assertFalse(vfd.articleAuthorFrequentWordDetection(allElements.get(i)));
			}
		}

		// only the last h6 tag contains a frequent word
		vfd.setFilePath("testset\\testPage11.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// the last h6 tag is the last index, contains word "author"
			if (i == allElements.size() - 1)
			{
				assertTrue(vfd.articleAuthorFrequentWordDetection(allElements.get(i)));
			}
			// all other tags do not contain a frequent word
			else
			{
				assertFalse(vfd.articleAuthorFrequentWordDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests all author rules together in nested conditionals.
	 */
	@Test
	public void testArticleAuthorExists()
	{
		// contains author
		vfd.setFilePath("testset\\testPage10.html");
		assertTrue(vfd.articleAuthorExists());

		// contains author
		vfd.setFilePath("testset\\testPage11.html");
		assertTrue(vfd.articleAuthorExists());

		// does not contain author
		vfd.setFilePath("testset\\testPage6.html");
		assertFalse(vfd.articleAuthorExists());
	}

	/**
	 * Tests that it can detect that the font size is less than 12 px for the
	 * comment link.
	 */
	@Test
	public void testArticleCommentLinkFontSizeDetection()
	{
		// the paragraph tag and h6 tag font size is <= 12 px
		vfd.setFilePath("testset\\testPage12.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// second paragraph tag and h6 tag will pass
			if (i == 1 || i == 4)
			{
				assertTrue(vfd.articleCommentLinkFontSizeDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCommentLinkFontSizeDetection(allElements.get(i)));
			}
		}

		// all tags are <= 12 px font size so all will pass
		vfd.setFilePath("testset\\testPage13.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleCommentLinkFontSizeDetection(allElements.get(i)));
		}
	}

	/**
	 * Tests that a text length between 6 and 15 characters can be detected for
	 * the comment link.
	 */
	@Test
	public void testArticleCommentLinkTextLengthDetection()
	{
		// only two of 5 elements are between 6 and 15 characters
		vfd.setFilePath("testset\\testPage12.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// index 1 (p) and 4 (h6) will pass
			if (i == 1 || i == allElements.size() - 1)
			{
				assertTrue(vfd.articleCommentLinkTextLengthDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCommentLinkTextLengthDetection(allElements.get(i)));
			}
		}

		// only two of 5 elements are between 6 and 15 characters
		vfd.setFilePath("testset\\testPage13.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// index 1 (h3) and 2 (h5) will pass
			if (i == 1 || i == 2)
			{
				assertTrue(vfd.articleCommentLinkTextLengthDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCommentLinkTextLengthDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the frequent word "comment" is detected in the comment link.
	 */
	@Test
	public void testArticleCommentLinkFrequentWordDetection()
	{
		// only the paragraph tag contains "comment"
		vfd.setFilePath("testset\\testPage12.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// second paragraph tag will pass
			if (i == 1)
			{
				assertTrue(vfd.articleCommentLinkFrequentWordDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCommentLinkFrequentWordDetection(allElements.get(i)));
			}
		}

		// only the h3 tag contains "comment"
		vfd.setFilePath("testset\\testPage13.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// h3 tag will pass
			if (i == 1)
			{
				assertTrue(vfd.articleCommentLinkFrequentWordDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCommentLinkFrequentWordDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the comment link text from the Element is hyper linked.
	 */
	@Test
	public void testArticleCommentLinkHyperLinkDetection()
	{
		// only the paragraph tag contains the hyper link
		vfd.setFilePath("testset\\testPage12.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// second paragraph tag will pass
			if (i == 1)
			{
				assertTrue(vfd.articleCommentLinkHyperLinkDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCommentLinkHyperLinkDetection(allElements.get(i)));
			}
		}

		// only the h3 tag contains the hyper link
		vfd.setFilePath("testset\\testPage13.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// h3 tag will pass
			if (i == 1)
			{
				assertTrue(vfd.articleCommentLinkHyperLinkDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCommentLinkHyperLinkDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests all comment link rules together in nested conditionals.
	 */
	@Test
	public void testArticleCommentLinkExists()
	{
		// contains comment link
		vfd.setFilePath("testset\\testPage12.html");
		assertTrue(vfd.articleCommentLinkExists());

		// contains comment link
		vfd.setFilePath("testset\\testPage13.html");
		assertTrue(vfd.articleCommentLinkExists());

		// does not contain comment link
		vfd.setFilePath("testset\\testPage11.html");
		assertFalse(vfd.articleCommentLinkExists());
	}

	/**
	 * Tests that the source's font size is no larger than 12 pixels.
	 */
	@Test
	public void testArticleSourceFontSizeDetection()
	{
		// the first paragraph and h6 tags font size is <= 12 px
		vfd.setFilePath("testset\\testPage14.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// first paragraph tag and h6 tag will pass
			if (i == 0 || i == 4)
			{
				assertTrue(vfd.articleSourceFontSizeDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleSourceFontSizeDetection(allElements.get(i)));
			}
		}

		// all tags are <= 12 px font size so all will pass
		vfd.setFilePath("testset\\testPage15.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			assertTrue(vfd.articleSourceFontSizeDetection(allElements.get(i)));
		}
	}

	/**
	 * Tests that the source's font color is black, brown, or gray.
	 */
	@Test
	public void testArticleSourceFontColorDetection()
	{
		// the first paragraph, h2, and h6 tags font color is black or brown
		vfd.setFilePath("testset\\testPage14.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// first p tag, h2, and h6 are black or brown, so pass
			if (i == 0 || i == 3 || i == 4)
			{
				assertTrue(vfd.articleSourceFontColorDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleSourceFontColorDetection(allElements.get(i)));
			}
		}

		// all tags but the p tag are black or gray
		vfd.setFilePath("testset\\testPage15.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// only the first p tag is red, so it will fail
			if (i == 0)
			{
				assertFalse(vfd.articleSourceFontColorDetection(allElements.get(i)));
			}
			// all other tags are gray or black, so they will all pass
			else
			{
				assertTrue(vfd.articleSourceFontColorDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the source's text contains a frequent word "source" or "from".
	 */
	@Test
	public void testArticleSourceFrequentWordDetection()
	{
		// only the first p tag contains a frequent word
		vfd.setFilePath("testset\\testPage14.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// first p has "source" so pass
			if (i == 0)
			{
				assertTrue(vfd.articleSourceFrequentWordDetection(allElements.get(i)));
			}
			// all other tags don't have a frequent word so fail
			else
			{
				assertFalse(vfd.articleSourceFrequentWordDetection(allElements.get(i)));
			}
		}

		// only the last h6 tag contains a frequent word
		vfd.setFilePath("testset\\testPage15.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// last h6 contains "from" so it will pass
			if (i == 4)
			{
				assertTrue(vfd.articleSourceFrequentWordDetection(allElements.get(i)));
			}
			// all other tags don't contain a frequent word so false
			else
			{
				assertFalse(vfd.articleSourceFrequentWordDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the source's text length is between 4 and 25 characters.
	 */
	@Test
	public void testArticleSourceTextLengthDetection()
	{
		// all tags are less than 25 characters
		vfd.setFilePath("testset\\testPage14.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// all tags are less than 25 characters, so all will pass
			assertTrue(vfd.articleSourceTextLengthDetection(allElements.get(i)));

		}

		// all tags except the p tag are less than 25 characters
		vfd.setFilePath("testset\\testPage15.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// p tag is greater than 25 characters, so it will fail
			if (i == 0)
			{
				assertFalse(vfd.articleSourceTextLengthDetection(allElements.get(i)));
			}
			// all other tags are less than 25 characters so true
			else
			{
				assertTrue(vfd.articleSourceTextLengthDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests all source rules together in nested conditionals.
	 */
	@Test
	public void testArticleSourceExists()
	{
		// contains source
		vfd.setFilePath("testset\\testPage14.html");
		assertTrue(vfd.articleSourceExists());

		// contains source
		vfd.setFilePath("testset\\testPage15.html");
		assertTrue(vfd.articleSourceExists());

		// does not contain source
		vfd.setFilePath("testset\\testPage13.html");
		assertFalse(vfd.articleSourceExists());
	}

	/**
	 * Tests that the content's font size is between 6 pixels and 12 pixels.
	 */
	@Test
	public void testArticleContentFontSizeDetection()
	{
		// p tag (index 0) is less than 12 px, h1 is not
		vfd.setFilePath("testset\\testPage16.html");
		Elements allElements = vfd.getAllTextElements();
		assertTrue(vfd.articleContentFontSizeDetection(allElements.get(0)));
		assertFalse(vfd.articleContentFontSizeDetection(allElements.get(1)));
	}

	/**
	 * Tests that the content's font color is black
	 */
	@Test
	public void testArticleContentFontColorDetection()
	{
		// p tag (index 0) is black, h1 is a shade of blue
		vfd.setFilePath("testset\\testPage16.html");
		Elements allElements = vfd.getAllTextElements();
		assertTrue(vfd.articleContentFontColorDetection(allElements.get(0)));
		assertFalse(vfd.articleContentFontColorDetection(allElements.get(1)));
	}

	/**
	 * Tests that the content can be seen without paging down.
	 */
	@Test
	public void testArticleContentPageDownDetection()
	{
		// p and h1 tags can both be seen without paging down
		vfd.setFilePath("testset\\testPage16.html");
		Elements allElements = vfd.getAllTextElements();
		assertTrue(vfd.articleContentPageDownDetection(allElements.get(0)));
		assertTrue(vfd.articleContentPageDownDetection(allElements.get(1)));

		// only h1 can be seen without paging down, p cannot be seen
		vfd.setFilePath("testset\\testPage17.html");
		allElements = vfd.getAllTextElements();
		assertTrue(vfd.articleContentPageDownDetection(allElements.get(1)));
		assertFalse(vfd.articleContentPageDownDetection(allElements.get(0)));
	}

	/**
	 * Tests that the content's text length is greater than 20 characters.
	 */
	@Test
	public void testArticleContentTextLengthDetection()
	{
		// p tag (index 0) is greater than 20 characters, h1 is less than 20
		// characters
		vfd.setFilePath("testset\\testPage16.html");
		Elements allElements = vfd.getAllTextElements();
		assertTrue(vfd.articleContentTextLengthDetection(allElements.get(0)));
		assertFalse(vfd.articleContentTextLengthDetection(allElements.get(1)));
	}

	/**
	 * Tests all content rules together in nested conditionals.
	 */
	@Test
	public void testArticleContentExists()
	{
		// this page has content, so it will pass
		vfd.setFilePath("testset\\testPage16.html");
		assertTrue(vfd.articleContentExists());

		// this page has content that requires the user to scroll so it will
		// fail.
		vfd.setFilePath("testset\\testPage17.html");
		assertFalse(vfd.articleContentExists());
	}

	/**
	 * Tests that the category's font size is no larger than 12 pixels.
	 */
	@Test
	public void testArticleCategoryFontSizeDetection()
	{
		// all tags except the last 2 (h6) tags are larger than 12 px
		vfd.setFilePath("testset\\testPage18.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// h6 tags < 12 px, so will pass
			if (i == allElements.size() - 2 || i == allElements.size() - 1)
			{
				assertTrue(vfd.articleCategoryFontSizeDetection(allElements.get(i)));
			}
			// all other tags > 12 px, so will fail
			else
			{
				assertFalse(vfd.articleCategoryFontSizeDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the category is in the top-half of the web page.
	 */
	@Test
	public void testArticleCategoryTopHalfOfPageDetection()
	{
		// h1 tag, h3 tag, and first h6 tag are in the top half of elements,
		// others are not in the first half
		vfd.setFilePath("testset\\testPage18.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// h1 tag, h3 tag, and first h6 tag will pass
			if (i == 1 || i == 2 || i == 3)
			{
				assertTrue(vfd.articleCategoryTopHalfOfPageDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCategoryTopHalfOfPageDetection(allElements.get(i)));
			}
		}

		// a variety of tags fall in the upper half
		vfd.setFilePath("testset\\testPage19.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// 3 p tags, h1, and h6 tag fall into top half
			if (i == 0 || i == 1 || i == 2 || i == 6 || i == 7)
			{
				assertTrue(vfd.articleCategoryTopHalfOfPageDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCategoryTopHalfOfPageDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the category can be seen without paging down.
	 */
	@Test
	public void testArticleCategoryPageDownDetection()
	{
		// all tags can be seen without paging down
		vfd.setFilePath("testset\\testPage18.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// all tags will pass - no page down needed
			assertTrue(vfd.articleCategoryPageDownDetection(allElements.get(i)));
		}

		// the last p tag and last h6 tags require page down, all other tags
		// don't
		vfd.setFilePath("testset\\testPage19.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// tags at indices 3-5 and 8 will fail
			if (i == 3 || i == 4 || i == 5 || i == 8)
			{
				assertFalse(vfd.articleCategoryPageDownDetection(allElements.get(i)));
			}
			// all other tags will pass
			else
			{
				assertTrue(vfd.articleCategoryPageDownDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the category's text length is between 8 and 30 characters
	 */
	@Test
	public void testArticleCategoryTextLengthDetection()
	{
		// all tags except p tag will pass into the character range
		vfd.setFilePath("testset\\testPage18.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// p tag > 30 characters, so fail
			if (i == 0)
			{
				assertFalse(vfd.articleCategoryTextLengthDetection(allElements.get(i)));
			}
			// all other tags between 8 and 30 characters, so pass
			else
			{
				assertTrue(vfd.articleCategoryTextLengthDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests that the category's text contains a frequent word ">", "->", or
	 * "|".
	 */
	@Test
	public void testArticleCategoryFrequentWordDetection()
	{
		// only first h6 tag has a frequent word
		vfd.setFilePath("testset\\testPage18.html");
		Elements allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// first h6 tag has frequent word "|"
			if (i == 3)
			{
				assertTrue(vfd.articleCategoryFrequentWordDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCategoryFrequentWordDetection(allElements.get(i)));
			}
		}

		// two tags have a frequent word
		vfd.setFilePath("testset\\testPage19.html");
		allElements = vfd.getAllTextElements();
		for (int i = 0; i < allElements.size(); i++)
		{
			// last two h6 tags contain frequent words ">" and "->"
			if (i == 7 || i == 8)
			{
				assertTrue(vfd.articleCategoryFrequentWordDetection(allElements.get(i)));
			}
			// all other tags will fail
			else
			{
				assertFalse(vfd.articleCategoryFrequentWordDetection(allElements.get(i)));
			}
		}
	}

	/**
	 * Tests all category rules together in nested conditionals.
	 */
	@Test
	public void testArticleCategoryExists()
	{
		// contains category
		vfd.setFilePath("testset\\testPage18.html");
		assertTrue(vfd.articleCategoryExists());

		// contains category
		vfd.setFilePath("testset\\testPage19.html");
		assertTrue(vfd.articleCategoryExists());

		// does not contain category
		vfd.setFilePath("testset\\testPage14.html");
		assertFalse(vfd.articleCategoryExists());
	}

}
