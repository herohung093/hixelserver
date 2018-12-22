package server.company;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

/**
 * A class which contains financial ratio calculation logic
 */
class RatioCalculator {

    /**
     * Represents metadata about a ratio and how to calculate it
     */
    static class Ratio {
        /**
         * The name of the ratio
         */
        private final String name;
        /**
         * A function which takes a set of <code>XbrlElements</code> and returns the calculated ratio from those elements
         */
        private final Function<XbrlElements, Double> lambda; //See calculators below.

        /**
         * Constructs a <code>Ratio</code> object
         * @param name The name of the ratio
         * @param lambda A function which returns the calculated ratio
         */
        Ratio(String name, Function<XbrlElements, Double> lambda) {
            this.name = name;
            this.lambda = lambda;
        }

        /**
         * @return Returns the ratio's name
         */
        String getName() {
            return name;
        }

        /**
         * @param xbrlElements The set of <code>XbrlElements</code> to calculate the ratio from
         * @return A Double representing the calculated financial ratio
         */
        Double calculate(XbrlElements xbrlElements)
        {
            return lambda.apply(xbrlElements);
        }
    }

    /**
     * A list of <code>Ratio</code> objects which is used to determine which ratios should be calculated,
     * and how to calculate them.
     */
    private static final List<Ratio> calculators = new ArrayList<Ratio>(){{

        //Current Ratio = Current Assets / Current Liabilities
        add(new Ratio("Current Ratio",
           xbrl -> xbrl.get("AssetsCurrent")
           .divide(xbrl.get("LiabilitiesCurrent"), 10, RoundingMode.HALF_UP)
           .doubleValue()));

        //Debt-To-Equity Ratio = Liabilities / Equity
        add(new Ratio("Debt-to-Equity Ratio",
            xbrl -> xbrl.get("Liabilities")
            .divide(xbrl.getEquity(), 10, RoundingMode.HALF_UP)
            .doubleValue()));

        //Current Debt-To-Equity Ratio = LiabilitiesCurrent / Equity
        add(new Ratio("Current Debt-to-Equity Ratio",
                xbrl -> xbrl.get("LiabilitiesCurrent")
                        .divide(xbrl.getEquity(), 10, RoundingMode.HALF_UP)
                        .doubleValue()));

        //Return-on-Equity Ratio = NetIncomeLoss / Equity
        add(new Ratio("Return-on-Equity Ratio",
            xbrl -> xbrl.get("NetIncomeLoss")
            .divide(xbrl.getEquity(), 10, RoundingMode.HALF_UP)
            .doubleValue()));

        //Return-on-Assets Ratio = NetIncomeLoss / Assets
        add(new Ratio("Return-on-Assets Ratio",
            xbrl -> xbrl.get("NetIncomeLoss")
            .divide( xbrl.get("Assets"), 10, RoundingMode.HALF_UP)
            .doubleValue()));

        //Profit-Margin Ratio = NetIncomeLoss / Revenues
        add(new Ratio("Profit-Margin Ratio",
            xbrl -> xbrl.get("NetIncomeLoss")
            .divide(xbrl.get("Revenues"), 10, RoundingMode.HALF_UP)
            .doubleValue()));

        //Dividend Yield = CommonStockDividendsPerShareDeclared / SaleOfStockPricePerShare
        add(new Ratio("Dividend Yield",
                xbrl -> xbrl.get("CommonStockDividendsPerShareDeclared")
                        .divide(xbrl.get("SaleOfStockPricePerShare"), 10, RoundingMode.HALF_UP)
                        .doubleValue()));

        //Interest Coverage = (EBIT -> OperatingIncomeLoss) / InterestExpense
        add(new Ratio("Interest Coverage",
                xbrl -> xbrl.get("OperatingIncomeLoss")
                        .divide(xbrl.get("InterestExpense"), 10, RoundingMode.HALF_UP)
                        .doubleValue()));
    }};

    /**
     * Calculates all ratios found in the <code>calculators</code> list
     * @param xbrl The XBRL Elements which will be used when calculating the ratios
     * @return A <code>LinkedHashMap</code> containing ratio names and calculated values
     */
    static LinkedHashMap<String, Double> Calculate(XbrlElements xbrl)
    {
        LinkedHashMap<String, Double> ratios = new LinkedHashMap<>();

        for (Ratio ratio : calculators) {
            try {
                ratios.put(ratio.getName(),
                           ratio.calculate(xbrl));
            }
            catch (Exception ignored) {
                //Sue me.
            }
        }

        return ratios;
    }
}
