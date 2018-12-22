package server.database;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import server.controllers.UserController;
import server.security.SecurityConstants;

/**
 * Represents a user's refresh token, which can be used to reauthorize when an access token expires
 * @see UserController#refresh
 */
@Entity
public class RefreshToken {
    /**
     * The unique ID which identifies the <code>RefreshToken</code> in the database
     */
    @SuppressWarnings("unused")
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Represents the refresh token's value as a <code>String</code>
     */
    private String tokenValue;

    /**
     * The refresh token's expiration date
     * @see SecurityConstants#RESET_CODE_EXPIRATION_TIME
     */
    private Date expiryDate;

    /**
     * @return Returns the token's value as a <code>String</code>
     */
    public String getTokenValue() {
        return tokenValue;
    }

    /**
     * Sets the token's value as a <code>String</code>
     * @param tokenValue The new token value
     */
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    /**
     * @return Returns the token's expiration date
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the token's expiration data
     * @param expiryDate The token's new expiration date
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
