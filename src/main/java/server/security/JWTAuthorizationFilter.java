package server.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static server.security.SecurityConstants.HEADER_STRING_ACCESS;
import static server.security.SecurityConstants.SECRET;
import static server.security.SecurityConstants.TOKEN_PREFIX;

/**
 * A filter used by Spring Security to handle user authorization.
 */
class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserDetailsService userDetailsService;
    /**
     * Creates a <code>JWTAuthorizationFilter</code> object
     * @param authenticationManager The Spring Security Authentication Manager
     * @param userDetailsService Service which loads user data
     */
    JWTAuthorizationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String accessToken = req.getHeader(HEADER_STRING_ACCESS);

        if (accessToken == null || !accessToken.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(accessToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    /**
     *  Checks the validity of the Access Token sent in the request
     * @param accessToken The JWT access token extracted from the user's request
     * @return Returns the authorized user token, or null if authorization fails
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String accessToken) {
        try {
            // parse the token.
            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(accessToken.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            userDetailsService.loadUserByUsername(user);
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        catch (Exception e) {
            return null;
        }
    }
}