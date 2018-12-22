package server.database;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @see Portfolio
 */
public class PortfolioTest {

    /**
     * @see Portfolio#getCompanies
     * @see Portfolio#setCompanies
     */
    @Test
    public void Companies() {
        Portfolio portfolio = new Portfolio();
        ArrayList<PortfolioCompany> companies = new ArrayList<>();

        portfolio.setCompanies(companies);
        Assert.assertSame(companies, portfolio.getCompanies());
    }
}