package server.company;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Objects;

/**
 * Represents the identifying information of a company.
 * Companies are identified by their ticker, name, and CIK
 */
@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CompanyIdentifiers {
    /**
     * Represents the ticker symbol of a company
     */
    private final String ticker;

    /**
     * Represents the name of a company
     */
    private final String name;

    /**
     * Represents the SEC central index key of a company
     */
    private final String cik;

    /**
     * Constructs a <code>CompanyIdentifiers</code> object
     * @param ticker The company's ticker symbol
     * @param name The company's name
     * @param cik The company's Central Index Key
     */
    public CompanyIdentifiers(String ticker, String name, String cik) {
        this.ticker = ticker;
        this.name = name;
        this.cik = cik;
    }

    /**
     * @return Returns a company's cik
     */
    String getCik() {
        return cik;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CompanyIdentifiers that = (CompanyIdentifiers) o;
        return Objects.equals(ticker, that.ticker) &&
                Objects.equals(name, that.name) &&
                Objects.equals(cik, that.cik);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, name, cik);
    }
}
