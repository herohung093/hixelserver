package server.database;

import org.junit.Assert;
import org.junit.Test;

public class ApplicationUserTest {

    private final ApplicationUser user = new ApplicationUser();

    /**
     * @see ApplicationUser#getFirstName
     * @see ApplicationUser#setFirstName
     */
    @Test
    public void FirstName() {
        String firstName = "Bob";
        user.setFirstName(firstName);

        Assert.assertSame(firstName, user.getFirstName());
    }

    /**
     * @see ApplicationUser#getLastName
     * @see ApplicationUser#setLastName
     */
    @Test
    public void LastName() {
        String lastName = "Poloo";
        user.setLastName(lastName);

        Assert.assertSame(lastName, user.getLastName());
    }

    /**
     * @see ApplicationUser#getEmail
     * @see ApplicationUser#setEmail
     */
    @Test
    public void Email() {
        String email = "email@email.email";
        user.setEmail(email);

        Assert.assertSame(email, user.getEmail());
    }

    /**
     * @see ApplicationUser#getPassword
     * @see ApplicationUser#setPassword
     */
    @Test
    public void Password() {
        String password = "verySecurePassword";
        user.setPassword(password);

        Assert.assertSame(password, user.getPassword());
    }

    /**
     * @see ApplicationUser#getPortfolio
     * @see ApplicationUser#setPortfolio
     */
    @Test
    public void Portfolio() {
        Portfolio portfolio = new Portfolio();
        user.setPortfolio(portfolio);

        Assert.assertSame(portfolio, user.getPortfolio());
    }

    /**
     * @see ApplicationUser#getResetCode
     * @see ApplicationUser#setResetCode
     */
    @Test
    public void ResetCode() {
        ResetCode resetCode = new ResetCode();
        user.setResetCode(resetCode);

        Assert.assertSame(resetCode, user.getResetCode());
    }

    /**
     * @see ApplicationUser#getRefreshToken
     * @see ApplicationUser#setRefreshToken
     */
    @Test
    public void RefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        user.setRefreshToken(refreshToken);

        Assert.assertSame(refreshToken, user.getRefreshToken());
    }
}