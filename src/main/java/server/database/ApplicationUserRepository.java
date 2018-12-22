package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * A data repository which interfaces with the database for the management of users
 * @see ApplicationUser
 */
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    /**
     * @param email An email address
     * @see ApplicationUser#email
     * @return Returns a user that has the specified email address, or null if a user does not exist.
     */
    ApplicationUser findByEmail(String email);

    /**
     * @param refreshTokenString The representation of a refresh token as a <code>String</code>
     * @see RefreshToken#tokenValue
     * @return Returns a user that has a <code>RefreshToken</code> with a <code>tokenValue</code>
     * matching the specified <code>refreshTokenString</code>, or null if a user does not exist.
     */
    ApplicationUser findByRefreshTokenTokenValue(@Param("tokenValue") String refreshTokenString);
}
