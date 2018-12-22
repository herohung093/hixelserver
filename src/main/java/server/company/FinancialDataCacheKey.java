package server.company;

import java.util.Objects;

/**
 * Used by the financial data cache in the <code>CompanyDataFactory</code> to identify financial data with a combination
 * of a company's identifiers and the year which the data is from.
 */
class FinancialDataCacheKey {
    /**
     * Identifies the company which the financial data is for
     */
    private final CompanyIdentifiers identifiers;

    /**
     * Identifies the year which the financial data is from
     */
    private final Integer year;

    /**
     * Constructs a <code>FinancialDataCacheKey</code>
     * @param identifiers The identity of the company which the financial data is for
     * @param year The year which the financial data is from
     */
    FinancialDataCacheKey(CompanyIdentifiers identifiers, Integer year) {
        this.identifiers = identifiers;
        this.year = year;
    }

    /**
     * @return Returns the company identifiers
     */
    CompanyIdentifiers getIdentifiers() {
        return identifiers;
    }

    /**
     * @return Returns the year
     */
    Integer getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        FinancialDataCacheKey that = (FinancialDataCacheKey) o;
        return Objects.equals(identifiers, that.identifiers) &&
                Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifiers, year);
    }
}
