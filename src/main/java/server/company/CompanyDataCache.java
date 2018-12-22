package server.company;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.config.CompanyDataProperties;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Encapsulates all the caching logic used to create a new <code>CompanyData</code> instance
 */
@SuppressWarnings("unused")
@Component
class CompanyDataCache {
    /**
     * Properties which determine how the cache operates
     */
    @Autowired
    private CompanyDataProperties properties;

    /**
     * Service which fetches company identifier information from the XBRL API if it doesn't exist in the cache
     */
    @Autowired
    private CompanyIdentifiersFetchService companyIdentifiersFetchService;

    /**
     * Service which fetches company XBRL Element data from the XBRL API if it doesn't exist in the cache
     */
    @Autowired
    private XbrlElementsFetchService xbrlElementsFetchService;

    /**
     * A cache which stores financial data
     */
    private AsyncLoadingCache<FinancialDataCacheKey, FinancialData> financialDataCache;

    /**
     * A cache which stores company identifier data
     */
    private AsyncLoadingCache<String, CompanyIdentifiers> companyIdentifiersCache;

    /**
     * Initializes the <code>financialDataCache</code> and <code>companyIdentifiersCache</code>.
     * This method runs automatically after this object's dependencies are injected
     * The caches are configured with values from `properties`.
     */
    @PostConstruct
    void cacheInit() {
        financialDataCache = Caffeine.newBuilder()
                .expireAfterWrite(properties.getCache().getExpire(), TimeUnit.MILLISECONDS)
                .refreshAfterWrite(properties.getCache().getRefresh(), TimeUnit.MILLISECONDS)
                .buildAsync(this::makeFinancialData);

        companyIdentifiersCache = Caffeine.newBuilder()
                .expireAfterWrite(properties.getCache().getExpire(), TimeUnit.MILLISECONDS)
                .buildAsync(companyIdentifiersFetchService::fetch);
    }

    CompletableFuture<CompanyIdentifiers> getIdentifiers(String ticker) {
        return companyIdentifiersCache.get(ticker);
    }

    CompletableFuture<FinancialData> getFinancialData(FinancialDataCacheKey key) {
        return financialDataCache.get(key);
    }

    /**
     * Constructs a <code>FinancialData</code> with XBRL Elements fetched from the XBRL API.
     * @param cacheKey An object containing a reference to a <code>CompanyIdentifiers</code> object and the year which this data is from
     * @throws XbrlElementsFetchService.XbrlElementsLoadException Thrown when the XBRL Elements fail to load from the XBRL API
     */
    private FinancialData makeFinancialData(FinancialDataCacheKey cacheKey) throws XbrlElementsFetchService.XbrlElementsLoadException {
        String cik = cacheKey.getIdentifiers().getCik();
        int year = cacheKey.getYear();

        return new FinancialData(year, xbrlElementsFetchService.fetch(cik, year));
    }
}
