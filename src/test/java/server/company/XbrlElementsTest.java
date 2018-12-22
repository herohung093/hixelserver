package server.company;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @see XbrlElements
 */
public class XbrlElementsTest {
    private XbrlElements xbrlElements;
    private BigDecimal assets;
    private BigDecimal liabilities;
    private BigDecimal equity;

    @Before
    public void setUp() {
        assets = new BigDecimal(100);
        liabilities = new BigDecimal(50);
        equity = assets.subtract(liabilities);

        xbrlElements = new XbrlElements();
        xbrlElements.put("Assets", assets);
        xbrlElements.put("Liabilities", liabilities);
    }

    /**
     * @see XbrlElements#get
     */
    @Test
    public void getExistingElement() {
        Assert.assertEquals(assets, xbrlElements.get("Assets"));
        Assert.assertEquals(liabilities, xbrlElements.get("Liabilities"));
    }

    /**
     * @see XbrlElements#get
     */
    @Test
    public void getNonExistingElement() {
        Assert.assertNull(xbrlElements.get("ELEMENT_THAT_DOES_NOT_EXIST"));
    }

    /**
     * @see XbrlElements#getEquity
     */
    @Test
    public void getEquity() {
        Assert.assertEquals(equity, xbrlElements.getEquity());
    }
}