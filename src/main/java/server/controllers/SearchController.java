package server.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import server.config.SearchProperties;
import server.search.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * A controller class which handles requests to the server related to Company Search
 */
@SuppressWarnings("WeakerAccess")
@RestController
public class SearchController {
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    /**
     * Properties which determine how the <code>SearchController</code> functions
     */
    @SuppressWarnings("unused")
    @Autowired
    private SearchProperties properties;

    /**
     * Service used to ingest search data from the NASDAQ and NYSE.
     * The data is populated into the <code>searchDataRepository</code>
     */
    @SuppressWarnings("unused")
    @Autowired
    private SearchDataIngestionService searchDataIngestionService;

    /**
     * A data repository which stores all of the ingested search data from the NASDAQ/NYSE exchanges
     */
    private SearchDataRepository searchDataRepository;

    /**
     * This method provides the server with fresh search data at server startup (When the controller bean is instantiated.)
     * If an <code>IngestionFailureException</code> bubbles up to this point, the application will terminate.
     * This can only happen if the download of the CSV files fails, and there are no cached CSVs in the csv_cache folder.
     * (CSVs are cached every time they're ingested, so a failure at this point in the startup is unlikely.)
     */
    @SuppressWarnings("unused")
    @PostConstruct
    private void startupDataIngestion() {
        try {
            searchDataRepository = searchDataIngestionService.ingestData();
        }
        catch (SearchDataIngestionService.IngestionFailureException e) {
            log.error("Server startup failed: {}", e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Ingests data using <code>SearchDataIngestionService.ingestData</code> and populates the <code>searchDataRepository.</code>
     * <b>Scheduled:</b> Runs on a fixed interval based on the search.ingestion-interval property in <i>application.properties.</i>
     * <b>NOTE:</b> This method is (obviously) coupled tightly with the search controller and could be migrated there.
     * @see SearchController
     */
    @Scheduled(initialDelayString = "${search.ingestion-interval}", fixedDelayString = "${search.ingestion-interval}")
    public void fixedIntervalIngestion() {
        try {
            searchDataRepository = searchDataIngestionService.ingestData();
        }
        catch (SearchDataIngestionService.IngestionFailureException e) {
            log.warn("Search Data ingestion failed: {}", e.getMessage());
        }
    }

    /**
     * This method handles a request for search data based on a query string
     * @param query The query string to be matched to company names/tickers in the <code>searchDataRepository</code>
     * @return Returns a list of <code>SearchData</code> objects which matched the query
     */
    @SuppressWarnings("unused")
    @RequestMapping("/search")
    public ArrayList<SearchData> searchQuery(@RequestParam(value="query") String query) {
        return searchDataRepository.query(query, properties.getResponseSize());
    }
}
