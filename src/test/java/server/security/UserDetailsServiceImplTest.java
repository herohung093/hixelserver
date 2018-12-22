package server.security;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import server.database.ApplicationUser;
import server.database.ApplicationUserRepository;

import static org.mockito.Mockito.when;

/**
 * @see UserDetailsServiceImpl
 */
public class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    private static final String USER_EMAIL = "email@email.email";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ApplicationUser testUser = new ApplicationUser();
        testUser.setEmail(USER_EMAIL);
        testUser.setPassword("ABC123");

        when(applicationUserRepository.findByEmail(USER_EMAIL)).thenReturn(testUser);

        userDetailsService = new UserDetailsServiceImpl(applicationUserRepository);
    }

    /**
     * Tests that the user details service will return details for a user that exists in the user repository
     * @see UserDetailsServiceImpl#loadUserByUsername
     */
    @Test
    public void loadUserByUsernameUserExists() {
        Assert.assertNotNull(userDetailsService.loadUserByUsername(USER_EMAIL));
    }

    /**
     * Tests that the user details service will not return details if the user does not exist in the user repository.
     * Instead, an exception will be thrown.
     * @see UserDetailsServiceImpl#loadUserByUsername
     */
    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameUserDoesNotExist() {
        Assert.assertNull(userDetailsService.loadUserByUsername("personWhoDoesNotExist@hotmail.com"));
    }
}