package server.security;

import com.auth0.jwt.JWT;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import server.database.ApplicationUser;
import server.database.ApplicationUserRepository;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.mockito.Mockito.when;
import static server.security.SecurityConstants.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class JWTAuthorizationFilterTest {

    private final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    private final MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
    private final MockFilterChain filterChain = new MockFilterChain();

    @Mock
    private AuthenticationManager authenticationManager;

    @SuppressWarnings("unused")
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @SuppressWarnings("unused")
    @MockBean
    private ApplicationUserRepository applicationUserRepository;

    private static final String USER_EMAIL = "email@email.email";
    private static final String USER_PASSWORD = "password";

    private JWTAuthorizationFilter filter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ApplicationUser testUser = new ApplicationUser();
        testUser.setEmail(USER_EMAIL);
        testUser.setPassword(USER_PASSWORD);

        //Mocking the user data repository
        when(applicationUserRepository.findByEmail(USER_EMAIL)).thenReturn(testUser);

        filter = new JWTAuthorizationFilter(authenticationManager, userDetailsService);
    }

    @Test
    public void doFilterInternalTokenValid() throws Exception {
        String accessToken = JWT.create()
                .withSubject(USER_EMAIL)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));

        httpServletRequest.addHeader(HEADER_STRING_ACCESS, TOKEN_PREFIX + accessToken);

        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void doFilterInternalTokenWithoutTokenHeader() throws Exception {
        filter.doFilterInternal(new MockHttpServletRequest(), httpServletResponse, filterChain);

        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void doFilterInternalTokenWithoutPrefix() throws Exception {
        String accessToken = JWT.create()
                .withSubject(USER_EMAIL)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));

        httpServletRequest.addHeader(HEADER_STRING_ACCESS, accessToken);

        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void doFilterInternalTokenExpired() throws Exception {
        String accessToken = JWT.create()
                .withSubject(USER_EMAIL)
                .withExpiresAt(new Date(System.currentTimeMillis() - ACCESS_EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));

        httpServletRequest.addHeader(HEADER_STRING_ACCESS, TOKEN_PREFIX + accessToken);

        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void doFilterInternalTokenUserDoesNotExist() throws Exception {
        String accessToken = JWT.create()
                .withSubject("doesNotExist@email.email")
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));

        httpServletRequest.addHeader(HEADER_STRING_ACCESS, TOKEN_PREFIX + accessToken);

        filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}