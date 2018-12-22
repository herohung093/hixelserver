package server.company;

import org.junit.Assert;
import org.junit.Test;

/**
 * @see FinancialData
 */
public class FinancialDataTest {

    private final Integer year = 2017;
    private final XbrlElements xbrlElements = new XbrlElements();
    private final FinancialData financialData = new FinancialData(year, xbrlElements);

    /**
     * @see FinancialData#getYear
     */
    @Test
    public void getYear() {
        Assert.assertEquals(year, financialData.getYear());
    }

    /**
     * @see FinancialData#getXbrlElements
     */
    @Test
    public void getXbrlElements() {
        Assert.assertSame(xbrlElements, financialData.getXbrlElements());
    }

    /**
     * @see FinancialData#getRatios
     */
    @Test
    public void getRatios() {
        Assert.assertNotNull(financialData.getRatios());
    }
}