package server.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import server.database.*;
import server.email.MailSenderService;

import org.springframework.security.test.context.support.WithMockUser;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.security.SecurityConstants.*;

/**
 * @see UserController
 */
@SuppressWarnings("unused")
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @MockBean
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private MailSenderService mailSenderService;

    private ApplicationUser testUser;

    @Mock
    private RefreshToken testRefreshToken;

    private static final String USER_EMAIL = "email@email.email";
    private static final String USER_PASSWORD = "password";
    private static final String USER_REFRESH = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjaXJyYXNidUBob3RtYWlsLm" +
                                               "NvbU4yUFRObm9qd2YwNE1TVGNlNnY5Nmt6SzI5czVoV0J0a3g2SUY2Q0giLCJleHAiOjE1N" +
                                               "DIwMjkzNTR9.ja3NN6oJIoKZEenVSQL-wcUggsZLlodPkN-k6mqSoDO5UQZoT_-9XpYoBLA" +
                                               "6slvkPNIiOw0vsT1IjQMKdEurtA";
    private static final String USER_RESET = "ABCD";

    private static final String validSignupData = "{\"email\":\"someOtherEmail@email.email\"," +
                                                   "\"password\":\"password\"," +
                                                   "\"firstName\":\"User\"," +
                                                   "\"lastName\":\"McUser\"}";

    private static final String alreadyExistsSignupData = "{\"email\":\"email@email.email\"," +
                                                           "\"password\":\"password\"," +
                                                           "\"firstName\":\"User\"," +
                                                           "\"lastName\":\"McUser\"}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUser = new ApplicationUser();
        testUser.setEmail(USER_EMAIL);
        testUser.setPassword(bCryptPasswordEncoder.encode(USER_PASSWORD));
        testUser.setFirstName("Bob");
        testUser.setLastName("Poloo");
        testUser.setRefreshToken(testRefreshToken);

        // Mocking the user data repository
        when(applicationUserRepository.findByEmail(USER_EMAIL)).thenReturn(testUser);
        when(applicationUserRepository.findByRefreshTokenTokenValue(USER_REFRESH)).thenReturn(testUser);
    }

    /**
     * Tests that the method will return a success response with the user information if the user exists
     * @see UserController#info
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void infoValid() throws Exception {
        mockMvc.perform(get("/users/profile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(USER_EMAIL)));
    }

    /**
     * Tests that the method will return a 401 response if the user doesn't exist.
     * @see UserController#info
     */
    @Test
    @WithMockUser(username = "NONEXISTENT_USER")
    public void infoDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/profile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method will return a success response if the new user information is valid
     * @see UserController#signUp
     */
    @Test
    public void signUpValid() throws Exception {
        mockMvc.perform(post("/users/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validSignupData))
                .andExpect(status().isOk());
    }

    /**
     * Tests that the method will return a conflict response (409) if the new user's email already exists in the database
     * @see UserController#signUp
     */
    @Test
    public void signUpAlreadyExists() throws Exception {
        mockMvc.perform(post("/users/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(alreadyExistsSignupData))
                .andExpect(status().isConflict());
    }

    /**
     * Tests that the method will return a success response with the access/refresh token in the headers
     * if the refresh token is valid.
     * @see UserController#refresh
     */
    @Test
    public void refreshValid() throws Exception {
        // Mocked token will return a future expiry date
        when(testRefreshToken.getExpiryDate()).thenReturn(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME));

        mockMvc.perform(get("/users/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Refresh", USER_REFRESH))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_STRING_ACCESS))
                .andExpect(header().exists(HEADER_STRING_REFRESH));
    }

    /**
     * Tests that the method will return a 401 response if the refresh token is expired
     * @see UserController#refresh
     */
    @Test
    public void refreshExpired() throws Exception {
        // Mocked token will return a past expiry date
        when(testRefreshToken.getExpiryDate()).thenReturn(new Date(System.currentTimeMillis() - REFRESH_EXPIRATION_TIME));

        mockMvc.perform(get("/users/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Refresh", USER_REFRESH))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HEADER_STRING_ACCESS))
                .andExpect(header().doesNotExist(HEADER_STRING_REFRESH));
    }

    /**
     * Tests that the method returns a success response when the provided email is valid
     * @see UserController#resetEmail
     */
    @Test
    public void resetEmailValidUser() throws Exception {
        mockMvc.perform(get("/users/reset-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("email", USER_EMAIL))
                .andExpect(status().isOk());

    }

    /**
     * Tests that the method returns a success response when the provided email is <i>invalid</i> as well.
     * We don't want to indicate whether the email was valid or not.
     * @see UserController#resetEmail
     */
    @Test
    public void resetEmailInvalidUser() throws Exception {
        mockMvc.perform(get("/users/reset-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("email", "NOT_AN_EXISTING_USER_EMAIL"))
                .andExpect(status().isOk());

    }

    /**
     * Tests that the method returns a success response when the email and reset code is valid
     * @see UserController#resetCode
     */
    @Test
    public void resetCodeValid() throws Exception {
        ResetCode resetCode = new ResetCode();
        resetCode.setCodeValue(USER_RESET);
        resetCode.setExpiryDate(new Date(System.currentTimeMillis() + RESET_CODE_EXPIRATION_TIME));
        testUser.setResetCode(resetCode);

        mockMvc.perform(get("/users/reset-code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("email", USER_EMAIL)
                    .param("code", USER_RESET))
                .andExpect(status().isOk());
    }

    /**
     * Tests that the method returns an unauthorized response when the user doesn't exist
     * @see UserController#resetCode
     */
    @Test
    public void resetCodeUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/reset-code")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "NOT_AN_EXISTING_USER_EMAIL")
                .param("code", USER_RESET))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method returns an unauthorized response when the user doesn't have a reset code
     * @see UserController#resetCode
     */
    @Test
    public void resetCodeDoesNotExist() throws Exception {
        testUser.setRefreshToken(null);

        mockMvc.perform(get("/users/reset-code")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", USER_EMAIL)
                .param("code", USER_RESET))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method returns an unauthorized response when the reset code is expired
     * @see UserController#resetCode
     */
    @Test
    public void resetCodeIsExpired() throws Exception {
        ResetCode resetCode = new ResetCode();
        resetCode.setCodeValue(USER_RESET);
        resetCode.setExpiryDate(new Date(System.currentTimeMillis() - RESET_CODE_EXPIRATION_TIME));
        testUser.setResetCode(resetCode);

        mockMvc.perform(get("/users/reset-code")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", USER_EMAIL)
                .param("code", USER_RESET))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method returns a success response when the user and reset code is valid
     * @see UserController#resetPassword
     */
    @Test
    public void resetPasswordValid() throws Exception {
        ResetCode resetCode = new ResetCode();
        resetCode.setCodeValue(USER_RESET);
        resetCode.setExpiryDate(new Date(System.currentTimeMillis() + RESET_CODE_EXPIRATION_TIME));
        testUser.setResetCode(resetCode);

        mockMvc.perform(get("/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", USER_EMAIL)
                .param("code", USER_RESET)
                .param("password", "newPassword"))
                .andExpect(status().isOk());
  }

    /**
     * Tests that the method returns an unauthorized response when the user does not exist
     * @see UserController#resetPassword
     */
    @Test
    public void resetPasswordUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "NOT_AN_EXISTING_USER_EMAIL")
                .param("code", USER_RESET)
                .param("password", "newPassword"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method returns an unauthorized response when the user does not have a reset code
     * @see UserController#resetPassword
     */
    @Test
    public void resetPasswordCodeDoesNotExist() throws Exception {
        testUser.setRefreshToken(null);

        mockMvc.perform(get("/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", USER_EMAIL)
                .param("code", USER_RESET)
                .param("password", "newPassword"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method returns an unauthorized response when the reset code is expired
     * @see UserController#resetPassword
     */
    @Test
    public void resetPasswordCodeIsExpired() throws Exception {
        ResetCode resetCode = new ResetCode();
        resetCode.setCodeValue(USER_RESET);
        resetCode.setExpiryDate(new Date(System.currentTimeMillis() - RESET_CODE_EXPIRATION_TIME));
        testUser.setResetCode(resetCode);

        mockMvc.perform(get("/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", USER_EMAIL)
                .param("code", USER_RESET)
                .param("password", "newPassword"))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Tests that the method returns a success response when <code>oldPassword</code> matches the user's current password
     * and the user is valid
     * @see UserController#changePassword
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void changePasswordValid() throws Exception {
        testUser.setPassword(bCryptPasswordEncoder.encode(USER_PASSWORD));

        mockMvc.perform(post("/users/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .param("oldPassword", USER_PASSWORD)
                .param("newPassword", "newPassword"))
                .andExpect(status().isOk());
    }

    /**
     * Tests that the method returns an unauthorized response when the user is invalid
     * @see UserController#changePassword
     */
    @Test
    @WithMockUser(username = "NOT_A_REAL_USER")
    public void changePasswordInvalidUser() throws Exception {
        testUser.setPassword(bCryptPasswordEncoder.encode(USER_PASSWORD));

        mockMvc.perform(post("/users/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .param("oldPassword", USER_PASSWORD)
                .param("newPassword", "newPassword"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method returns an unauthorized response when the <code>oldPassword</code> provided is wrong
     * @see UserController#changePassword
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void changePasswordOldPasswordWrong() throws Exception {
        testUser.setPassword(bCryptPasswordEncoder.encode(USER_PASSWORD));

        mockMvc.perform(post("/users/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .param("oldPassword", "THE_WRONG_PASSWORD")
                .param("newPassword", "newPassword"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method returns a success response and adds the company when the user is valid
     * @see UserController#addCompany
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void addCompanyValid() throws Exception {
        testUser.setPortfolio(new Portfolio());

        mockMvc.perform(post("/users/portfolio/company")
                .contentType(MediaType.APPLICATION_JSON)
                .param("ticker", "AAPL"))
                .andExpect(status().isOk());

        boolean assertCheck = !testUser.getPortfolio().getCompanies().isEmpty();
        Assert.assertTrue("The portfolio should contain a company.", assertCheck);
    }

    /**
     * Tests that the method returns a success response and does <i>not</i> add the company if it is a duplicate
     * @see UserController#addCompany
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void addCompanyDuplicate() throws Exception {
        Portfolio portfolio = new Portfolio();
        PortfolioCompany company = new PortfolioCompany();

        company.setTicker("AAPL");
        portfolio.getCompanies().add(company);
        testUser.setPortfolio(portfolio);

        mockMvc.perform(post("/users/portfolio/company")
                .contentType(MediaType.APPLICATION_JSON)
                .param("ticker", "AAPL"))
                .andExpect(status().isOk());

        boolean assertCheck = testUser.getPortfolio().getCompanies().size() == 1;
        Assert.assertTrue("The portfolio should only contain 1 company (No duplicates!)", assertCheck);
    }

    /**
     * Tests that the method returns an unauthorized response if the user is invalid
     * @see UserController#addCompany
     */
    @Test
    @WithMockUser(username = "NOT_A_REAL_USER")
    public void addCompanyInvalidUser() throws Exception {
        testUser.setPortfolio(new Portfolio());

        mockMvc.perform(post("/users/portfolio/company")
                .contentType(MediaType.APPLICATION_JSON)
                .param("ticker", "AAPL"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that the method returns a success response and removes the company when the user is valid
     * @see UserController#removeCompany
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void removeCompanyValid() throws Exception {
        Portfolio portfolio = new Portfolio();
        PortfolioCompany company = new PortfolioCompany();

        company.setTicker("AAPL");
        portfolio.getCompanies().add(company);
        testUser.setPortfolio(portfolio);

        mockMvc.perform(delete("/users/portfolio/company")
                .contentType(MediaType.APPLICATION_JSON)
                .param("ticker", "AAPL"))
                .andExpect(status().isOk());

        boolean assertCheck = testUser.getPortfolio().getCompanies().isEmpty();
        Assert.assertTrue("The portfolio should be empty.", assertCheck);
    }

    /**
     * Tests that the method returns an unauthorized response if the user is invalid
     * @see UserController#removeCompany
     */
    @Test
    @WithMockUser(username = "NOT_A_REAL_USER")
    public void removeCompanyInvalidUser() throws Exception {
        testUser.setPortfolio(new Portfolio());

        mockMvc.perform(delete("/users/portfolio/company")
                .contentType(MediaType.APPLICATION_JSON)
                .param("ticker", "AAPL"))
                .andExpect(status().isUnauthorized());
    }
}