package server.company;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Represents a set of XBRL Elements retrieved from the XBRL API for a particular company
 */
public class XbrlElements extends HashMap<String, BigDecimal> {

    /**
     * Constructs an empty <code>XbrlElements</code> object
     */
    public XbrlElements() {

    }

    /**
     * Retrieves an XBRL Element based on the provided key.
     * <b>NOTE:</b> This is where extra processing can be done to handle missing or incorrect XBRL Element data.
     * @param key The name of the XBRL Element
     * @return Returns a <code>BigDecimal</code> representing the requested XBRL Element
     */
    BigDecimal get(String key) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        BigDecimal value = super.get(key);

        //Do extra processing here.
        //We can handle fallback calculation for null XBRL elements here, if that's possible.

        return value;
    }

    /**
     * Calculates and returns equity based on these XBRL values
     * @return Returns a <code>BigDecimal</code> representing equity based on these values
     */
    BigDecimal getEquity()
    {   //Equity = Assets - Liabilities
        return this.get("Assets").subtract(this.get("Liabilities"));
    }

}
