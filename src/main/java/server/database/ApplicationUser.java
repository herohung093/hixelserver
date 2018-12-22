package server.database;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import server.controllers.UserController;

import javax.persistence.*;

/**
 * Represents a particular user and all of its related data
 * @see UserController
 * @see ApplicationUserRepository
 */
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ApplicationUser {
    /**
     * The unique ID which identifies this <code>ApplicationUser</code> in the database
     */
    @SuppressWarnings("unused")
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The user's first name
     */
    private String firstName;

    /**
     * The user's last name
     */
    private String lastName;

    /**
     * The user's email address
     */
    private String email;

    /**
     * The user's password
     * <b>NOTE:</b> This is not sent in JSON responses
     */
    @JsonIgnore
    private String password;

    /**
     * Represents the user's portfolio - All of the companies which the user is tracking
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "portfolio_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Portfolio portfolio = new Portfolio();

    /**
     * A code which can be used to reset the user's password, if it exists and is not expired
     * @see UserController#resetEmail
     * @see UserController#resetCode
     * @see UserController#resetPassword
     */
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "reset_code_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ResetCode resetCode;

    /**
     * A token which was sent to the user when they last reauthorized
     * Can be used to reauthorize again when their current access token expires
     * @see UserController#refresh
     */
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "refresh_token_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RefreshToken refreshToken;

    /**
     * @return Returns the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name
     * @param firstName The user's new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return Returns the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name
     * @param lastName The user's new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return Returns the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address
     * @param email The user's new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's password. (This will be encrypted if it was retrieved from the database)
     * This method is ignored by the JSON serializer, meaning the password will not be sent in JSON responses
     * @return Returns the user's password
     */
    @JsonIgnore //NOTE: This will always be encrypted with bCrypt when the object has been retrieved from the database.
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password
     * @param password The user's new password
     */
    @JsonProperty("password") //Password can be received in JSON - but should never be sent.
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the user's portfolio
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Sets the user's portfolio
     * @param portfolio The user's new portfolio
     */
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    /**
     * @return Returns the user's reset code
     */
    public ResetCode getResetCode() {
        return resetCode;
    }

    /**
     * Sets the user's reset code
     * @param resetCode The user's new reset code
     */
    public void setResetCode(ResetCode resetCode) {
        this.resetCode = resetCode;
    }

    /**
     * @return Returns the user's refresh token
     */
    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets the user's refresh token
     * @param refreshToken The user's new refresh token
     */
    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }
}