package server.database;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

import server.controllers.UserController;

/**
 * Represents a user's reset code - which can be used to reset the user's password
 * @see UserController#resetEmail
 * @see UserController#resetCode
 * @see UserController#resetPassword
 */
@Entity
public class ResetCode {
    /**
     * The unique ID which identifies the <code>ResetCode</code> in the database
     */
    @SuppressWarnings("unused")
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * An alphanumeric <code>String</code> which is always 4 characters long, which is the code's value
     */
    private String codeValue;

    /**
     * The reset code's expiration date
     */
    private Date expiryDate;

    /**
     * @return Returns the reset code's value
     */
    public String getCodeValue() {
        return codeValue;
    }

    /**
     * Set the reset code's value
     * @param codeValue The new reset code value
     */
    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    /**
     * @return Returns the reset code's expiration date
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the reset code's expiration date
     * @param expiryDate The new expiration date
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
