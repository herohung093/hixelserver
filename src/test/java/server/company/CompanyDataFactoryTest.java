package server.company;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;

/**
 * @see CompanyDataFactory
 */
@SuppressWarnings("unused")
@SpringBootTest
@RunWith(SpringRunner.class)
public class CompanyDataFactoryTest {
    @SuppressWarnings("unused")
    @MockBean
    private CompanyDataCache cache;

    @SuppressWarnings("unused")
    @Autowired
    private CompanyDataFactory factory;

    private final CompanyIdentifiers identifiers = new CompanyIdentifiers("AAPL", "Apple", "0000320193");

    private final int lastYear = Calendar.getInstance().get(Calendar.YEAR) - 1;

    private final FinancialData financialData1 = new FinancialData(lastYear, new XbrlElements());
    private final FinancialDataCacheKey key1 = new FinancialDataCacheKey(identifiers, lastYear);

    private final FinancialData financialData2 = new FinancialData(lastYear - 1, new XbrlElements());
    private final FinancialDataCacheKey key2 = new FinancialDataCacheKey(identifiers, lastYear - 1);

    @Before
    public void setup() {
        when(cache.getIdentifiers("AAPL")).thenReturn(CompletableFuture.completedFuture(identifiers));
        when(cache.getFinancialData(key1)).thenReturn(CompletableFuture.completedFuture(financialData1));
        when(cache.getFinancialData(key2)).thenReturn(CompletableFuture.completedFuture(financialData2));
    }

    /**
     * Tests that the method returns the expected CompanyData object
     * @see CompanyDataFactory#newCompanyData
     */
    @Test
    public void newCompanyData() {
        CompanyData data = factory.newCompanyData("AAPL", 2);

        Assert.assertNotNull(data);
        Assert.assertEquals(identifiers, data.getIdentifiers());
        Assert.assertEquals(2, data.getFinancialDataEntries().size());
        Assert.assertEquals(financialData1, data.getFinancialDataEntries().get(0));
        Assert.assertEquals(financialData2, data.getFinancialDataEntries().get(1));
    }
}