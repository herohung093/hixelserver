package server.database;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * @see RefreshToken
 */
public class RefreshTokenTest {

    private final RefreshToken refreshToken = new RefreshToken();

    /**
     * @see RefreshToken#getTokenValue
     * @see RefreshToken#setTokenValue
     */
    @Test
    public void TokenValue() {
        String tokenValue = "ABC";
        refreshToken.setTokenValue(tokenValue);

        Assert.assertSame(tokenValue, refreshToken.getTokenValue());
    }

    /**
     * @see RefreshToken#getExpiryDate
     * @see RefreshToken#setExpiryDate
     */
    @Test
    public void ExpiryDate() {
        Date expiryDate = new Date();
        refreshToken.setExpiryDate(expiryDate);

        Assert.assertSame(expiryDate, refreshToken.getExpiryDate());
    }

}