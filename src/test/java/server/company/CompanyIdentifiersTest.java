package server.company;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @see CompanyIdentifiers
 */
@SuppressWarnings("FieldCanBeLocal")
public class CompanyIdentifiersTest {

    private static final String TICKER_VALUE = "AAPL";
    private static final String NAME_VALUE = "Apple";
    private static final String CIK_VALUE = "0000320193";

    private CompanyIdentifiers companyIdentifiers;
    private CompanyIdentifiers identical;
    private CompanyIdentifiers notIdentical;

    @Before
    public void setUp() {
        companyIdentifiers = new CompanyIdentifiers(TICKER_VALUE, NAME_VALUE, CIK_VALUE);
        identical = new CompanyIdentifiers(TICKER_VALUE, NAME_VALUE, CIK_VALUE);
        notIdentical = new CompanyIdentifiers("ABC", "Easy as 123", "ABC123");
    }

    /**
     * @see CompanyIdentifiers#getCik
     */
    @Test
    public void getCik() {
        Assert.assertSame(CIK_VALUE, companyIdentifiers.getCik());
    }

    /**
     * @see CompanyIdentifiers#equals
     */
    @Test
    public void equalsShouldPass() {
        Assert.assertEquals(companyIdentifiers, identical);
    }

    /**
     * @see CompanyIdentifiers#equals
     */
    @Test
    public void equalsShouldFail() {
        Assert.assertNotEquals(companyIdentifiers, notIdentical);
    }

    /**
     * @see CompanyIdentifiers#hashCode
     */
    @Test
    public void hashCodeShouldPass() {
        Assert.assertEquals(companyIdentifiers.hashCode(), identical.hashCode());
    }

    /**
     * @see CompanyIdentifiers#hashCode
     */
    @Test
    public void hashCodeShouldFail() {
        Assert.assertNotEquals(companyIdentifiers.hashCode(), notIdentical.hashCode());
    }
}