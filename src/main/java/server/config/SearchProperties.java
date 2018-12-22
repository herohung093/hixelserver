package server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * Configuration options for all things related to the Company Search feature
 */
@Validated
@Configuration
@ConfigurationProperties(prefix="search")
public class SearchProperties {

    /**
     * The max size of the response to the Search endpoint. See <code>server.controllers.SearchController</code>
     * <b>Minimum:</b> 0
     */
    @Min(0)
    private int responseSize;

    /**
     * Length of time between data ingestions from the NASDAQ/NYSE to refresh search data
     * <b>Minimum:</b> 1000
     */
    @Min(1000)
    private long ingestionInterval;

    /**
     * @return Returns the <code>responseSize</code> property
     */
    public int getResponseSize() {
        return responseSize;
    }

    /**
     * Sets the <code>responseSize</code> property
     */
    @SuppressWarnings("unused")
    public void setResponseSize(int responseSize) {
        this.responseSize = responseSize;
    }

    /**
     * @return Returns the <code>ingestionInterval</code> property
     */
    @SuppressWarnings("unused")
    public long getIngestionInterval() {
        return ingestionInterval;
    }

    /**
     * Sets the <code>ingestionInterval</code> property
     */
    @SuppressWarnings("unused")
    public void setIngestionInterval(long ingestionInterval) {
        this.ingestionInterval = ingestionInterval;
    }
}