package server.company;

import org.junit.Assert;
import org.junit.Test;

/**
 * @see FinancialDataCacheKey
 */
public class FinancialDataCacheKeyTest {

    private final CompanyIdentifiers companyIdentifiers = new CompanyIdentifiers("AAPL", "Apple","0000320193");
    private final Integer year = 2017;
    private final CompanyIdentifiers otherCompanyIdentifiers = new CompanyIdentifiers("ABC", "Easy as 123","ABC123");
    private final Integer otherYear = 2016;

    private final FinancialDataCacheKey key = new FinancialDataCacheKey(companyIdentifiers, year);
    private final FinancialDataCacheKey otherKeyIdentical = new FinancialDataCacheKey(companyIdentifiers, year);
    private final FinancialDataCacheKey otherKeyNotIdentical = new FinancialDataCacheKey(otherCompanyIdentifiers, otherYear);

    /**
     * @see FinancialDataCacheKey#getIdentifiers
     */
    @Test
    public void getIdentifiers() {
        Assert.assertEquals(companyIdentifiers, key.getIdentifiers());
    }

    /**
     * @see FinancialDataCacheKey#getYear
     */
    @Test
    public void getYear() {
        Assert.assertEquals(year, key.getYear());
    }

    /**
     * @see FinancialDataCacheKey#equals
     */
    @Test
    public void equalsShouldPass() {
        Assert.assertEquals(key, otherKeyIdentical);
    }

    /**
     * @see FinancialDataCacheKey#equals
     */
    @Test
    public void equalsShouldFail() {
        Assert.assertNotEquals(key, otherKeyNotIdentical);
    }

    /**
     * @see FinancialDataCacheKey#hashCode
     */
    @Test
    public void hashCodeShouldPass() {
        Assert.assertEquals(key.hashCode(), otherKeyIdentical.hashCode());
    }

    /**
     * @see FinancialDataCacheKey#hashCode
     */
    @Test
    public void hashCodeShouldFail() {
        Assert.assertNotEquals(key.hashCode(), otherKeyNotIdentical.hashCode());
    }
}