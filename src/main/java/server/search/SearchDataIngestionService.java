package server.search;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.csv.QuoteMode.ALL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ingests data from the NASDAQ and NYSE stock exchanges in the form of CSV files (released daily)
 */
@Component
public class SearchDataIngestionService {
    private static final Logger log = LoggerFactory.getLogger("SearchDataIngestionService");

    /**
     * An exception which indicates the data ingestion was unsuccessful
     */
    public class IngestionFailureException extends Exception {
        public IngestionFailureException (String error) {
            super(error);
        }
    }

    private static final Set<String> exchanges = ImmutableSet.of(
        "NASDAQ",
        "NYSE"
    );

    /**
     *  Ingests data from the NASDAQ and NYSE stock exchanges
     * @return Returns a <code>SearchDataRepository</code> populated with the ingested search data entries
     * @throws IngestionFailureException Thrown when the data fails to download, or fails to parse
     * @see SearchData
     */
    public SearchDataRepository ingestData() throws IngestionFailureException {
        ArrayList<SearchData> ingested = new ArrayList<>();

        for (String exchange : exchanges) {
            ArrayList<CSVRecord> records = getExchangeData(exchange);

            try {
                for (CSVRecord record : records) {
                    ingested.add(new SearchData(record.get("Symbol"),
                                                StringEscapeUtils.unescapeHtml4(record.get("Name")),
                                                exchange));
                }
            }
            catch (Exception e) {
                throw new IngestionFailureException(String.format("%s data was malformed: %s",
                        exchange,
                        e.getMessage()));
            }
        }

        return new SearchDataRepository(ingested);
    }

    private ArrayList<CSVRecord> getExchangeData(String exchange) throws IngestionFailureException {
        String ingestUrl = "https://www.nasdaq.com/screening/companies-by-name.aspx"
                         + "?letter=0"
                         + "&exchange=%s"
                         + "&render=download";

        try {
            URL url = new URL(String.format(ingestUrl, exchange));
            URLConnection urlConnection = url.openConnection();

            Reader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));

            ArrayList<CSVRecord> records = newArrayList(CSVFormat
                    .EXCEL
                    .withHeader()
                    .parse(in));

            log.info("Ingested data from {} successfully.",
                    exchange);

            cacheExchangeData(exchange, records); //Important data if the download fails.
            return records;
        }
        catch (IOException e) {
            log.warn("Failed to ingest {} data: {}.", exchange, e.getMessage());
            log.info("Using cached data.");

            return getCachedExchangeData(exchange);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void cacheExchangeData(String exchange, ArrayList<CSVRecord> records) {
        File file = new File(String.format("csv_cache/%s.csv", exchange));
        file.getParentFile().mkdirs();

        try(Writer out = new FileWriter(file)) {
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.EXCEL.withQuoteMode(ALL));
            printer.printRecords(records);
        }
        catch (IOException e) {
            log.warn("Failed to cache {} data: {}",
                            exchange,
                            e.getMessage());
        }
    }

    private ArrayList<CSVRecord> getCachedExchangeData(String exchange) throws IngestionFailureException {
        File f = new File(String.format("csv_cache/%s.csv", exchange));

        try (Reader in = new FileReader(f)) {
            return newArrayList(CSVFormat
                    .EXCEL
                    .withHeader("Symbol","Name","LastSale","MarketCap","IPOyear","Sector","industry","Summary Quote")
                    .parse(in));
        }
        catch (FileNotFoundException e) {
            throw new IngestionFailureException(
                    String.format("%s Cache data did not exist in the csv_cache folder.",
                            exchange));
        }
        catch (IOException e) {
            throw new IngestionFailureException(
                    String.format("Failed to read cached %s data: %s",
                            exchange,
                            e.getMessage()));
        }
    }

}
