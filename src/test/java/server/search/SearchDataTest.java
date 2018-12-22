package server.search;

import org.junit.Assert;
import org.junit.Test;

/**
 * @see SearchData
 */
public class SearchDataTest {

    /**
     * Tests that the company ticker will be matched to queries
     * @see SearchData#matches
     */
    @Test
    public void matchesTicker() {
        SearchData searchData = new SearchData("ABCD", "CompanyName", "NASDAQ");

        Assert.assertTrue(searchData.matches("A"));
        Assert.assertTrue(searchData.matches("a"));
    }

    /**
     * Tests that the company name will be matched to queries
     * @see SearchData#matches
     */
    @Test
    public void matchesName() {
        SearchData searchData = new SearchData("ABCD", "CompanyName", "NASDAQ");

        Assert.assertTrue(searchData.matches("C"));
        Assert.assertTrue(searchData.matches("c"));
    }

    /**
     * Tests that matches will only occur from the start of the name or ticker
     * @see SearchData#matches
     */
    @Test
    public void matchesFromStart() {
        SearchData searchData = new SearchData("ABCD", "CompanyName", "NASDAQ");

        Assert.assertFalse(searchData.matches("BCD"));
        Assert.assertFalse(searchData.matches("ompanyName"));
    }
}