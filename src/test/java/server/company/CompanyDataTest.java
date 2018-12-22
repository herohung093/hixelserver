package server.company;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @see CompanyData
 */
public class CompanyDataTest {
    private CompanyData testData;
    private CompanyIdentifiers companyIdentifiers;
    private ArrayList<FinancialData> financialDataEntries;

    @Before
    public void setUp() {
        companyIdentifiers = new CompanyIdentifiers("AAPL", "Apple", "0000320193");

        XbrlElements xbrl = new XbrlElements();
        FinancialData financialData = new FinancialData(2017, xbrl);
        financialDataEntries = new ArrayList<>();
        financialDataEntries.add(financialData);

        testData = new CompanyData(companyIdentifiers, financialDataEntries);
    }

    /**
     * @see CompanyData#getIdentifiers
     */
    @Test
    public void getIdentifiers() {
        Assert.assertSame(companyIdentifiers, testData.getIdentifiers());
    }

    /**
     * @see CompanyData#getFinancialDataEntries
     */
    @Test
    public void getFinancialDataEntries() {
        Assert.assertSame(financialDataEntries, testData.getFinancialDataEntries());
    }
}