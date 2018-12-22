package server.company;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import server.config.CompanyDataProperties;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;

/**
 * @see CompanyDataCache
 */
@SuppressWarnings("FieldCanBeLocal")
@RunWith(MockitoJUnitRunner.class)
public class CompanyDataCacheTest {

    @Mock
    private CompanyDataProperties properties;

    @Mock
    private CompanyDataProperties.Cache cacheProperties;

    @Mock
    private XbrlElementsFetchService xbrlElementsFetchService;

    @Mock
    private CompanyIdentifiersFetchService companyIdentifiersFetchService;

    @InjectMocks
    private CompanyDataCache testCache;

    private static final long CACHE_EXPIRE = 86400000;
    private static final long CACHE_REFRESH = 900000;

    private static final String TICKER_VALUE = "AAPL";
    private static final String CIK_VALUE = "0000320193";
    private static final int YEAR_VALUE = 2017;

    private final CompanyIdentifiers identifiers = new CompanyIdentifiers(TICKER_VALUE, "Apple", CIK_VALUE);
    private final XbrlElements xbrlElements = new XbrlElements();

    @Before
    public void setUp() throws Exception {
        when(cacheProperties.getExpire()).thenReturn(CACHE_EXPIRE);
        when(cacheProperties.getRefresh()).thenReturn(CACHE_REFRESH);
        when(properties.getCache()).thenReturn(cacheProperties);

        when(xbrlElementsFetchService.fetch(CIK_VALUE, YEAR_VALUE)).thenReturn(xbrlElements);
        when(companyIdentifiersFetchService.fetch(TICKER_VALUE)).thenReturn(identifiers);

        testCache.cacheInit();
    }


    @Test
    public void getIdentifiers() throws Exception {
        CompletableFuture<CompanyIdentifiers> retrieved = testCache.getIdentifiers(TICKER_VALUE);
        retrieved.join();

        Assert.assertEquals(identifiers, retrieved.get());

    }

    @Test
    public void getFinancialData() throws Exception {
        FinancialDataCacheKey key = new FinancialDataCacheKey(identifiers, YEAR_VALUE);

        CompletableFuture<FinancialData> retrieved = testCache.getFinancialData(key);
        retrieved.join();

        Assert.assertEquals(xbrlElements, retrieved.get().getXbrlElements());
    }
}