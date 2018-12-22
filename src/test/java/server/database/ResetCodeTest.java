package server.database;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * @see ResetCode
 */
public class ResetCodeTest {

    private final ResetCode resetCode = new ResetCode();

    /**
     * @see ResetCode#setCodeValue
     * @see ResetCode#getCodeValue
     */
    @Test
    public void CodeValue() {
        String codeValue = "1234";
        resetCode.setCodeValue(codeValue);
        Assert.assertSame(codeValue, resetCode.getCodeValue());
    }

    /**
     * @see ResetCode#setExpiryDate
     * @see ResetCode#getExpiryDate
     */
    @Test
    public void ExpiryDate() {
        Date expiryDate = new Date();

        resetCode.setExpiryDate(expiryDate);
        Assert.assertSame(expiryDate, resetCode.getExpiryDate());
    }
}