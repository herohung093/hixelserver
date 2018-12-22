package server.database;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of companies that a user is currently tracking
 */
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Portfolio {
    /**
     * The unique ID that identifies this <code>Portfolio</code> in the database
     */
    @SuppressWarnings("unused")
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The list of companies that the user is tracking
     */
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "portfolio_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<PortfolioCompany> companies = new ArrayList<>();

    /**
     * Sets the portfolio companies value
     * @param companies The portfolio's new companies
     */
    public void setCompanies(List<PortfolioCompany> companies) {
        this.companies = companies;
    }

    /**
     * @return Returns the list of companies that the user is tracking
     */
    public List<PortfolioCompany> getCompanies() {
        return companies;
    }
}
