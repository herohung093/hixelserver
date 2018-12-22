package server.company;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashMap;

/**
 * Represents a year of a company's financial information.
 * Stores elements retrieved from the XBRL API, along with financial ratios calculated from those elements
 */
@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FinancialData {
    /**
     * The year that this data is from
     */
    private final Integer year;

    /**
     * The elements retrieved from the XBRL API
     */
    private final XbrlElements xbrlElements;

    /**
     * The financial ratios calculated from the <code>XbrlElements</code>
     */
    private final LinkedHashMap<String, Double> ratios;

    @SuppressWarnings("unused")
    @JsonIgnore
    private static final Logger log = LoggerFactory.getLogger(FinancialData.class);

    /**
     * Constructs a <code>FinancialData</code> object
     * @param year The year that this data is from
     * @param xbrlElements Elements which represent "facts" about a company's finances
     */
    public FinancialData(int year, XbrlElements xbrlElements) {
        this.year = year;
        this.xbrlElements = xbrlElements;
        this.ratios = RatioCalculator.Calculate(xbrlElements);
    }

    /**
     * @return Returns the year that this financial data is from
     */
    Integer getYear() {
        return year;
    }

    /**
     * @return Returns the XBRL Elements that were used to calculate the financial ratios for this object
     */
    XbrlElements getXbrlElements() {
        return xbrlElements;
    }

    /**
     * @return Returns the financial ratios calculated from the XBRL Elements the object was constructed with
     */
    LinkedHashMap<String, Double> getRatios() {
        return ratios;
    }
}