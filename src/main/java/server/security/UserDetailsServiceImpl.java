package server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import server.database.ApplicationUser;
import server.database.ApplicationUserRepository;

import static java.util.Collections.emptyList;

/**
 * An implementation of the <code>UserDetailsService</code> interface.
 * Provides an interface to the user repository, which in turn interfaces with the database.
 * This is used by the Spring Security authentication and authorization filters.
 * @see JWTAuthenticationFilter
 * @see JWTAuthorizationFilter
 * @see UserDetailsService
 */
@Service
class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * A data repository object which interfaces with the database to retrieve and/or modify user data
     */
    private final ApplicationUserRepository applicationUserRepository;

    /**
     * Creates a <code>UserDetailsServiceImpl</code> object
     * @param applicationUserRepository User data repository - Interfaces with the database for user management
     */
    public UserDetailsServiceImpl(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    /**
     * @param email A user's email address
     * @return Returns the user details (email and password), or null if the user is not found
     * @throws UsernameNotFoundException Thrown if the user is not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ApplicationUser applicationApplicationUser = applicationUserRepository.findByEmail(email);

        if (applicationApplicationUser == null) {
            throw new UsernameNotFoundException(email);
        }

        return new org.springframework.security.core.userdetails.User(applicationApplicationUser.getEmail(), applicationApplicationUser.getPassword(), emptyList());
    }
}
