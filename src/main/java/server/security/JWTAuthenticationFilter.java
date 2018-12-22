package server.security;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import server.database.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import server.database.ApplicationUserRepository;
import server.database.RefreshToken;
import server.util;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static server.security.SecurityConstants.*;

/**
 * A filter used by Spring Security to handle the user authentication process with JWT tokens.
 * @see WebSecurity#configure
 */
class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final ApplicationUserRepository applicationUserRepository;

    /**
     * Creates a <code>JWTAuthenticationFilter</code> object
     * @param authenticationManager The Spring Security Authentication Manager.
     * @param userRepository Data repository which interfaces with the database for user management.
     */
    @Autowired
    JWTAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationUserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.applicationUserRepository = userRepository;
    }

    /**
     * Performs actual authentication
     * @param request The server request, from which to extract parameters and perform the authentication
     * @param response Unused, but may be needed if the implementation changes and has to do a
     * redirect as part of a multi-stage authentication process (such as OpenID).
     * @return Returns the authenticated user token, or null if authentication is incomplete.
     * @throws AuthenticationException Thrown if authentication fails.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        ApplicationUser creds;

        try {
            creds = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
        }
        catch (IOException e) {
            return null;
        }

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getEmail(),
                        creds.getPassword(),
                        new ArrayList<>())
        );
    }

    /**
     * Performs the following actions for successful authentication:
     * <ol>
     * <li>Generates a new Access Token and Refresh token for the user</li>
     * <li>Saves the Refresh Token to the database</li>
     * <li>Appends the Access/Refresh tokens to the response headers</li>
     * </ol>
     * @param request The user's request
     * @param response The response to the user's request
     * @param chain The filter chain
     * @param authResult The object returned from <code>attemptAuthentication</code>
     * method.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        String username = ((User) authResult.getPrincipal()).getUsername();
        Date accessExpiryDate = new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME);
        Date refreshExpiryDate = new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME);

        String authToken = JWT.create()
                .withSubject(username)
                .withExpiresAt(accessExpiryDate)
                .sign(HMAC512(SECRET.getBytes()));

        String refreshToken = JWT.create()
                .withSubject(username + util.randomAlphanumericString(40))
                .withExpiresAt(refreshExpiryDate)
                .sign(HMAC512(SECRET.getBytes()));

        RefreshToken dbToken = new RefreshToken();
        dbToken.setTokenValue(refreshToken);
        dbToken.setExpiryDate(refreshExpiryDate);

        ApplicationUser user = applicationUserRepository.findByEmail(username);
        user.setRefreshToken(dbToken);
        applicationUserRepository.save(user);

        response.addHeader(HEADER_STRING_ACCESS, TOKEN_PREFIX + authToken);
        response.addHeader(HEADER_STRING_REFRESH, refreshToken);
    }
}