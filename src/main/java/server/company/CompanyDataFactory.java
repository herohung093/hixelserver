package server.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.*;

/**
 * A factory class responsible for creating and caching <code>CompanyData</code> objects.
 * It is primarily used by the <code>CompanyDataController</code>.
 */
@SuppressWarnings("unused")
@Component
public class CompanyDataFactory {
    /**
     * Encapsulates the caching logic used for constructing a <code>CompanyData</code> instance
     */
    @Autowired
    private CompanyDataCache cache;

    /**
     * Constructs a new <code>CompanyData</code> object, retrieving necessary identifiers and financial data from the caches.
     *
     * @param ticker A string representing a company's ticker
     * @param years The number of years of financial data to retrieve
     * @return This method returns a new <code>CompanyData</code> object
     */
    public CompanyData newCompanyData(String ticker, int years) {

        CompletableFuture<CompanyData> dataFuture = cache.getIdentifiers(ticker).thenApply( identifiers -> {

            ArrayList<CompletableFuture<FinancialData>> futures = new ArrayList<>();
            ArrayList<FinancialData> financialDataEntries = new ArrayList<>();

            int last_year = Calendar.getInstance().get(Calendar.YEAR) - 1;

            for (int y = last_year; y > last_year - years; --y) {
                futures.add(cache.getFinancialData(new FinancialDataCacheKey(identifiers, y)));
            }

            util.allOf(futures).join();

            for (CompletableFuture<FinancialData> f : futures) {
                try {
                    financialDataEntries.add(f.get());
                }
                catch (ExecutionException | InterruptedException e){
                    financialDataEntries.add(null);
                }
            }

            return new CompanyData(identifiers, financialDataEntries);
        });

        try {
            dataFuture.join();
            return dataFuture.get();
        }
        catch (ExecutionException | InterruptedException e){
            return null;
        }
    }
}
