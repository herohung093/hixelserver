package server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Configuration options for all things related to Company Data
 */
@SuppressWarnings("unused")
@Validated
@Configuration
@ConfigurationProperties(prefix="company-data")
public class CompanyDataProperties {

    /**
     * The max number of years of financial data that can be requested for a <code>CompanyData</code> object.
     * <b>Minimum:</b> 0
     */
    @Min(0)
    private int yearsMax;

    /**
     * The API Key which is used when retrieving data from the XBRL US API
     */
    private String xbrlApiKey;

    /**
     * Reference to an instance of cache properties
     */
    @Valid
    private final Cache cache = new Cache();

    /**
     * @return Returns the <code>yearsMax</code> property
     */
    public int getYearsMax() {
        return yearsMax;
    }

    /**
     * Sets the <code>yearsMax</code> property
     */
    public void setYearsMax(int yearsMax) {
        this.yearsMax = yearsMax;
    }

    /**
     * @return Returns the <code>xbrlApiKey</code> property
     */
    public String getXbrlApiKey() {
        return xbrlApiKey;
    }

    /**
     * Sets the <code>xbrlApiKey</code> property
     */
    public void setXbrlApiKey(String xbrlApiKey) {
        this.xbrlApiKey = xbrlApiKey;
    }

    /**
     * @return Returns Company Data cache properties
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Configuration options for the Company Data caches
     */
    @Configuration
    public class Cache {
        /**
         * Length of time from last access before entries in the cache expire (in milliseconds)
         * <b>Minimum:</b> 1000
         */
        @Min(1000)
        private long expire;

        /**
         * Length of time from last write before entries in the cache are refreshed (in milliseconds)
         * <b>Minimum:</b> 1000
         */
        @Min(1000)
        private long refresh;

        /**
         * @return Returns the <code>expire</code> property
         */
        public long getExpire() {
            return expire;
        }

        /**
         * Sets the <code>expire</code> property
         */
        public void setExpire(long expire) {
            this.expire = expire;
        }

        /**
         * @return Returns the <code>refresh</code> property
         */
        public long getRefresh() {
            return refresh;
        }

        /**
         * Sets the <code>refresh</code> property
         */
        public void setRefresh(long refresh) {
            this.refresh = refresh;
        }
    }
}