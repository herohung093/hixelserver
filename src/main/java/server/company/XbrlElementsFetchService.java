package server.company;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import server.config.CompanyDataProperties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Service which fetches company XBRL Element data from the XBRL API
 */
@SuppressWarnings("unused")
@Component
class XbrlElementsFetchService {

    /**
     * Properties that configure how the fetch service operates
     */
    @Autowired
    private
    CompanyDataProperties properties;

    private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    private static final XPathFactory xPathfactory = XPathFactory.newInstance();

    /**
     * An exception which indicates the XBRL Element data could not be fetched from the XBRL API
     */
    class XbrlElementsLoadException extends Exception {
        XbrlElementsLoadException () {
            super("Failed to load XBRL Elements from XBRL API.");
        }
    }

    /**
     * The elements which will be fetched from the XBRL API
     */
    private static final Set<String> elements = ImmutableSet.of(
            "Assets",
            "AssetsCurrent",
            "InventoryNet",
            "Liabilities",
            "LiabilitiesCurrent",
            "NetIncomeLoss",
            "Revenues",
            "SaleOfStockPricePerShare",
            "CommonStockDividendsPerShareDeclared",
            "OperatingIncomeLoss",
            "InterestExpense"
    );

    private static final String elementString = StringUtils.join(elements, ',');
    private static final String apiUrl = "https://csuite.xbrl.us/php/dispatch.php"
            + "?Task=xbrlValues"
            + "&Element=%s"
            + "&Year=%d"
            + "&Period=Y"
            + "&CIK=%s"
            + "&DimReqd=False"
            + "&Ultimus=True"
            + "&API_Key=%s";

    /**
     * Constructs and returns an <code>XbrlElements</code> object by retrieving and parsing data from the XBRL API
     * @param cik The Central Index Key of the company that the data is for
     * @param year The year that the data is from
     * @throws XbrlElementsLoadException Thrown when the XBRL Element data can't be fetched from the XBRL API
     */
    XbrlElements fetch(String cik, Integer year) throws XbrlElementsLoadException {
        XbrlElements xbrlElements = new XbrlElements();

        try {
            DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            Document doc = builder.parse(String.format(apiUrl, elementString, year, cik, properties.getXbrlApiKey()));
            XPath xpath = xPathfactory.newXPath();

            for (String element : elements) {
                try {
                    String path = "/dataRequest/fact[elementName='%s']/amount/text()";

                    XPathExpression expr = xpath.compile(String.format(path, element));
                    BigDecimal value;

                    try {
                        value = new BigDecimal((String) expr.evaluate(doc, XPathConstants.STRING));
                        //Use the String constructor for BigDecimal
                    }
                    catch (NumberFormatException e) {
                        value = null;
                    }

                    xbrlElements.put(element, value);
                }
                catch (XPathExpressionException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (java.io.IOException | ParserConfigurationException | SAXException e) {
            throw new XbrlElementsLoadException();
        }

        return xbrlElements;
    }
}
