package server.database;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a single company that a user is tracking as part of a portfolio
 */
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PortfolioCompany {
    /**
     * The unique ID which identifies this <code>PortfolioCompany</code> in the database
     */
    @SuppressWarnings("unused")
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * A ticker symbol which identifies the company being tracked
     */
    private String ticker;

    /**
     * @return Returns the ticker
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * Sets the company's ticker symbol
     * @param ticker The company's new ticker symbol
     */
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
