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

}
