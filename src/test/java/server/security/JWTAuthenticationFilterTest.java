package server.security;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import server.database.ApplicationUser;
import server.database.ApplicationUserRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static server.security.SecurityConstants.HEADER_STRING_ACCESS;
import static server.security.SecurityConstants.HEADER_STRING_REFRESH;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class JWTAuthenticationFilterTest {

    private final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    private final MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
    private final MockFilterChain filterChain = new MockFilterChain();

    @Mock
    private AuthenticationManager authenticationManager;
    private Authentication authResult;

    @SuppressWarnings("unused")
    @MockBean
    private ApplicationUserRepository applicationUserRepository;
    private ApplicationUser testUser;

    private static final String USER_EMAIL = "email@email.email";
    private static final String USER_PASSWORD = "password";
    private static final String USER_JSON = "{\"email\":\"email@email.email\",\"password\":\"password\"}";
    private static final String INVALID_USER_JSON = "{\"email\":\"doesNotExist@email.email\",\"password\":\"password\"}";

    private JWTAuthenticationFilter filter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUser = new ApplicationUser();
        testUser.setEmail(USER_EMAIL);
        testUser.setPassword(USER_PASSWORD);

        User principal = new User(USER_EMAIL, USER_PASSWORD, new ArrayList<>());

        Authentication authRequest = new UsernamePasswordAuthenticationToken(USER_EMAIL, USER_PASSWORD, new ArrayList<>());
        authResult = new UsernamePasswordAuthenticationToken(principal, USER_PASSWORD, new ArrayList<>());

        //Mocking the user data repository
        when(applicationUserRepository.findByEmail(USER_EMAIL)).thenReturn(testUser);

        //Mocking the Spring Authentication Manager
        when(authenticationManager.authenticate(authRequest)).thenReturn(authResult);
        when(authenticationManager.authenticate(not(eq(authRequest)))).thenThrow(
                new BadCredentialsException(""));

        filter = new JWTAuthenticationFilter(authenticationManager, applicationUserRepository);
    }

    /**
     * Tests to ensure that authentication will succeed if valid user credentials are sent
     * @see JWTAuthenticationFilter#attemptAuthentication
     */
    @Test
    public void attemptAuthenticationUserExists() {
        httpServletRequest.setContent(USER_JSON.getBytes());
        Assert.assertEquals(authResult, filter.attemptAuthentication(httpServletRequest, httpServletResponse));
    }

    /**
     * Tests to ensure that authentication will fail if invalid user credentials are sent
     * @see JWTAuthenticationFilter#attemptAuthentication
     */
    @Test(expected = BadCredentialsException.class)
    public void attemptAuthenticationUserDoesNotExist() {
        httpServletRequest.setContent(INVALID_USER_JSON.getBytes());
        Assert.assertNotEquals(authResult, filter.attemptAuthentication(httpServletRequest, httpServletResponse));
    }

    /**
     * Tests to ensure that authentication will fail in an exceptional case
     * @see JWTAuthenticationFilter#attemptAuthentication
     */
    @Test
    public void attemptAuthenticationExceptionalRequest() throws Exception{
        HttpServletRequest badRequest = mock(HttpServletRequest.class);
        when(badRequest.getInputStream()).thenThrow(new IOException());

        Assert.assertNotEquals(authResult, filter.attemptAuthentication(badRequest, httpServletResponse));
    }

    /**
     * Tests to ensure that an access token and refresh token is returned in the response headers
     * @see JWTAuthenticationFilter#successfulAuthentication
     */
    @Test
    public void successfulAuthenticationCheckHeadersExist() {
        filter.successfulAuthentication(httpServletRequest, httpServletResponse, filterChain, authResult);

        Assert.assertNotNull(httpServletResponse.getHeader(HEADER_STRING_ACCESS));
        Assert.assertNotNull(httpServletResponse.getHeader(HEADER_STRING_REFRESH));
    }

    /**
     * Tests to ensure that the user is assigned a refresh token in the database when they are authenticated
     * @see JWTAuthenticationFilter#successfulAuthentication
     */
    @Test
    public void successfulAuthenticationCheckRefreshTokenExistsInDatabase() {
        testUser.setRefreshToken(null);
        filter.successfulAuthentication(httpServletRequest, httpServletResponse, filterChain, authResult);

        Assert.assertNotNull(testUser.getRefreshToken());
    }
}