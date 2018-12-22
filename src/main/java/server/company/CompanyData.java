package server.company;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.ArrayList;

/**
 * Represents a particular company. Owns both identity information and financial information
 */
@SuppressWarnings({"FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CompanyData {

    /**
     * Represents the company's identity
     */
    private final CompanyIdentifiers identifiers;

    /**
     * List of <code>FinancialData</code> entries, each of which represents one year of the company's financial data
     */
    private final ArrayList<FinancialData> financialDataEntries;

    /**
     *
     * @param identifiers The company's identity
     * @param financialDataEntries List of <code>FinancialData</code> entries, each of which represents one year of the company's financial data
     */
    public CompanyData(CompanyIdentifiers identifiers, ArrayList<FinancialData> financialDataEntries) {
        this.identifiers = identifiers;
        this.financialDataEntries = financialDataEntries;
    }

    /**
     * @return Returns the company's identifiers
     */
    public CompanyIdentifiers getIdentifiers() {
        return identifiers;
    }

    /**
     * @return Returns the company's financial data
     */
    public ArrayList<FinancialData> getFinancialDataEntries() {
        return financialDataEntries;
    }
}
