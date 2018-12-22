package server.company;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

/**
 * Service which fetches company identifier information from the XBRL API
 */
@SuppressWarnings("unused")
@Component
class CompanyIdentifiersFetchService {

    private static final Logger log = LoggerFactory.getLogger(CompanyIdentifiersFetchService.class);

    private final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

    private final XPathFactory xPathfactory = XPathFactory.newInstance();

    private static final String apiUrl = "https://csuite.xbrl.us/php/dispatch.php?Task=xbrlCIKLookup&Ticker=%s";

    /**
     * An exception which indicates a <code>CompanyIdentifiers</code> object could not be retrieved from the XBRL API
     */
    class CompanyIdentifiersLoadException extends Exception {
        CompanyIdentifiersLoadException () {
            super("Failed to load Company Identifiers from XBRL API.");
        }
    }

    /**
     * Constructs a <code>CompanyIdentifiers</code> object by retrieving data from the XBRL API
     * @param ticker The ticker symbol used as a key to retrieve data from the XBRL API
     * @throws CompanyIdentifiersLoadException Thrown if the <code>CompanyIdentifiers</code> object fails to load from the XBRL API
     */
    @SuppressWarnings("SameParameterValue")
    CompanyIdentifiers fetch(String ticker) throws CompanyIdentifiersLoadException {
        String name;
        String cik;

        try {
            DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            Document doc = builder.parse(String.format(apiUrl, ticker));

            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile("/dataRequest/tickerLookup/cik");
            cik = (String)expr.evaluate(doc, XPathConstants.STRING);

            expr = xpath.compile("/dataRequest/tickerLookup/name");
            name = (String)expr.evaluate(doc, XPathConstants.STRING);
        }
        catch (java.io.IOException | ParserConfigurationException | SAXException | XPathExpressionException e) {
            log.error(e.getMessage());
            throw new CompanyIdentifiersLoadException();
        }

        if (!StringUtils.isNumeric(cik))
            throw new CompanyIdentifiersLoadException();

        return new CompanyIdentifiers(ticker, name, cik);
    }
}
