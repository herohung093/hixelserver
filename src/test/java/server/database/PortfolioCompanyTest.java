package server.database;

import org.junit.Assert;
import org.junit.Test;

/**
 * @see PortfolioCompany
 */
public class PortfolioCompanyTest {

    /**
     * @see PortfolioCompany#getTicker
     * @see PortfolioCompany#setTicker
     */
    @Test
    public void Ticker() {
        PortfolioCompany portfolioCompany = new PortfolioCompany();
        String ticker = "AAPL";

        portfolioCompany.setTicker(ticker);
        Assert.assertSame(ticker, portfolioCompany.getTicker());
    }
}