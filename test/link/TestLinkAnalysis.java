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
				"theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
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
				"theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
		assertTrue(la.linkHasFourSlashes());

		// case 2: 3 slashes
		la.setURL(
				"cbsnews.com/news/trump-mulls-inviting-saudi-crown-prince-mohammad-bin-salman-to-un-nuclear-meeting/");
		assertFalse(la.linkHasFourSlashes());

		// case3: 4 slashes
		la.setURL(
				"abcnews.go.com/GMA/Living/viral-post-volunteer-napping-shelter-cats-brings-funds/story?id=57965675&cid=clicksource_19216223_4_article%20roll_articleroll_hed");
		assertTrue(la.linkHasFourSlashes());
	}

	/**
	 * Tests various cases as to whether the link has an identification number.
	 */
	@Test
	public void testLinkHasIDNumber()
	{
		// case 1: ID number after slashes
		la.setURL(
				"theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
		assertTrue(la.linkHasIDNumber());

		// case 2: ID number in title
		la.setURL("yahoo.com/news/cruz-orourke-face-off-testy-texas-senate-debate-023824069.html");
		assertTrue(la.linkHasIDNumber());

		// case 3: ID number as URL parameter
		la.setURL(
				"abcnews.go.com/GMA/Living/viral-post-volunteer-napping-shelter-cats-brings-funds/story?id=57965675&cid=clicksource_19216223_4_article%20roll_articleroll_hed");
		assertTrue(la.linkHasIDNumber());

		// case 4: ID number is located after an underscore
		la.setURL(
				"huffingtonpost.com/entry/tammie-hedges-hurricane-florence-charges-animals_us_5ba6acabe4b0375f8f9d93d6");
		assertTrue(la.linkHasIDNumber());

		// case 5: no ID number
		la.setURL("bbc.com/news");
		assertFalse(la.linkHasIDNumber());
	}

	/**
	 * Tests that a link contains a date in the URL String.
	 */
	@Test
	public void testLinkHasDate()
	{
		// case 1: date in slashes
		la.setURL(
				"theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
		assertTrue(la.linkHasDate());

		// case 2: no date
		la.setURL("yahoo.com/news/cruz-orourke-face-off-testy-texas-senate-debate-023824069.html");
		assertFalse(la.linkHasDate());
	}

	/**
	 * Tests that a link's length is at least 50 characters in length.
	 */
	@Test
	public void testLinkHasLongerLength()
	{
		// case 1: link has over 50 characters
		la.setURL(
				"theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
		assertTrue(la.linkHasLongerLength());
		// case 2: link is not over 50 characters
		la.setURL("nytimes.com/section/technology");
		assertFalse(la.linkHasLongerLength());
	}

	/**
	 * Tests whether a link contains a reserved word.
	 */
	@Test
	public void testLinkDoesNotContainReservedWord()
	{
		// case 1: link does not any contain reserve words
		la.setURL(
				"theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
		assertTrue(la.linkDoesNotContainReservedWord());

		// case 2: link contains "video" reserve word
		la.setURL("cnn.com/videos/tech/2015/12/04/exp-cnn-films-steve-jobs-man-in-the-machine-lost-my-wife.cnn");
		assertFalse(la.linkDoesNotContainReservedWord());

		// case 3: link contains "photo" reserve word
		la.setURL("yahoo.com/news/photos-week-9-14-9-220000561.html");
		assertFalse(la.linkDoesNotContainReservedWord());
	}

	/**
	 * Tests whether the URL ends with a slash "/".
	 */
	@Test
	public void testLinkDoesNotEndWithSlash()
	{
		// case 1: link does not end with a slash
		la.setURL(
				"theverge.com/2018/9/20/17883242/amazon-alexa-event-2018-news-recap-echo-auto-dot-sub-link-auto-microwave");
		assertTrue(la.linkDoesNotEndWithSlash());

		// case 2: link does end with a slash
		la.setURL("yahoo.com/news/science/");
		assertFalse(la.linkDoesNotEndWithSlash());
	}

}
