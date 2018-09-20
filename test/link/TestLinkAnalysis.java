package link;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * A class that contains all of the JUnit tests related to the LinkAnalysis
 * class.
 * @author Nick Rummel
 */
public class TestLinkAnalysis
{
	LinkAnalysis la;

	/**
	 * Executes before each test case.
	 */
	@Before
	public void init()
	{
		// initialize the LinkAnalysis object before each test case
		la = new LinkAnalysis("https://www.google.com");
	}

	/**
	 * Tests getter method for URL instance variable
	 */
	@Test
	public void testGetURL()
	{
		// getter should receive original URL from initialization
		assertEquals("https://www.google.com", la.url);
	}

	/**
	 * Tests setter method for URL instance variable
	 */
	@Test
	public void testSetURL()
	{
		// verify the URL from initialization
		assertEquals("https://www.google.com", la.url);

		// change URL and verify URL updated correctly
		la.setURL("https://www.facebook.com");
		assertEquals("https://www.facebook.com", la.url);
	}

	/**
	 * Tests that the number of slashes '/' in the URL can be counted.
	 */
	@Test
	public void testCountSlashes()
	{
		// case 1: 5 slashes
		la.setURL(
				"www.theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
		assertEquals(5, la.countSlashes());

		// case 2: 3 slashes
		la.setURL(
				"cbsnews.com/news/trump-mulls-inviting-saudi-crown-prince-mohammad-bin-salman-to-un-nuclear-meeting/");
		assertEquals(3, la.countSlashes());

		// case3: 7 slashes
		la.setURL("money.cnn.com/2018/09/20/news/companies/wells-fargo-job-cuts/index.html");
		assertEquals(7, la.countSlashes());
	}

	/**
	 * Tests if a link has at least four slashes in the URL.
	 */
	@Test
	public void testLinkHasFourSlashes()
	{
		// case 1: 5 slashes
		la.setURL(
				"www.theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
		assertTrue(la.linkHasFourSlashes());

		// case 2: 3 slashes
		la.setURL(
				"cbsnews.com/news/trump-mulls-inviting-saudi-crown-prince-mohammad-bin-salman-to-un-nuclear-meeting/");
		assertFalse(la.linkHasFourSlashes());

		// case3: 4 slashes
		la.setURL("abcnews.go.com/GMA/Living/viral-post-volunteer-napping-shelter-cats-brings-funds/story?id=57965675&cid=clicksource_19216223_4_article%20roll_articleroll_hed");
		assertTrue(la.linkHasFourSlashes());
	}

}
