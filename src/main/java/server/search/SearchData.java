package server.search;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import server.controllers.SearchController;

/**
 * Represents a company which can be searched for
 * Ingested from the NASDAQ/NYSE exchanges by the <code>SearchDataIngestionService</code>
 * @see SearchDataIngestionService
 * @see SearchController#searchQuery
 */
@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SearchData {
    /**
     * The ticker symbol of a company
     */
    private final String ticker;

    /**
     * The name of a company
     */
    private final String name;

    /**
     * The stock exchange of a company
     */
    @SuppressWarnings("unused")
    private final String exchange;

    /**
     * Constructs a <code>SearchData</code> object
     * @param ticker The ticker symbol of a company
     * @param name The name of a company
     * @param exchange The stock exchange of a company
     */
    public SearchData(String ticker, String name, String exchange) {
        this.ticker = ticker;
        this.name = name;
        this.exchange = exchange;
    }

    /**
     * Checks if the ticker or name starts with the query (case-insensitive)
     * @param query A search query which is checked against the name and ticker
     * @return Returns true if the search query matches the name or ticker - Otherwise, returns false.
     */
    boolean matches(String query) {
        String expr = String.format("(?i)%s.*", query);

        return ticker.matches(expr)
              || name.matches(expr);
    }
}
