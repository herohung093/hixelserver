package server.controllers;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.company.CompanyData;
import server.company.CompanyDataFactory;
import server.util;

/**
 * A controller class which handles requests to the server related to Company Data retrieval
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@RestController
public class CompanyDataController {
    /**
     * The factory object which creates <code>CompanyData</code> instances when requested
     */
    @Autowired
    private CompanyDataFactory companyDataFactory;

    /**
     * This method handles a request for a particular company's data
     * @param tickers The ticker symbol for the requested company
     * @param years The number of years of financial data to retrieve for the company
     * @return A <code>CompanyData</code> object representing the requested company
     */
    @RequestMapping("/companydata")
    public ArrayList<CompanyData> companyData(@RequestParam("tickers") String tickers,
                                              @RequestParam("years") Integer years) {
        ArrayList<CompletableFuture<CompanyData>> futures = new ArrayList<>();

        ArrayList<String> tickerList = Lists
                        .newArrayList(Splitter.on(',')
                        .trimResults()
                        .omitEmptyStrings()
                        .splitToList(tickers));

        for (String ticker : tickerList) {
            futures.add(CompletableFuture.supplyAsync(() -> companyDataFactory.newCompanyData(ticker, years)));
        }

        util.allOf(futures).join();

        ArrayList<CompanyData> companies = new ArrayList<>();

        for (CompletableFuture<CompanyData> f : futures) {
            try {
                CompanyData c = f.get();

                if (c != null)
                    companies.add(c);
            }
            catch (InterruptedException | ExecutionException e){
                //Oh well - let it go, move to the next company.
            }
        }

        return companies;
    }
}
