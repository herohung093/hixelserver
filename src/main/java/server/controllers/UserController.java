package server.controllers;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import server.database.*;
import server.email.MailSenderService;
import server.util;

import java.util.Date;
import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static server.security.SecurityConstants.*;

/**
 * A controller class which handles requests to the server related to User data
 */

@SuppressWarnings({"WeakerAccess", "unused"})
@RestController
@RequestMapping("/users")
public class UserController {
    /**
     * A data repository object which interfaces with the database to retrieve and/or modify user data
     */
    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    /**
     * Encrypts a string using the bCrypt hashing algorithm - Used for securing passwords
     */
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * A service used for password recovery emails.
     */
    @Autowired
    private MailSenderService mailSenderService;

    /**
     * This method handles a request for a user profile. It determines the user's identity based on their Auth token claim
     * @param authentication Represents the token for an authenticated principal once the request
     * has been processed by the Spring Authentication Manager
     * @return Returns a <code>ResponseEntity</code> with the <code>ApplicationUser</code> data stored as JSON in the
     * body if the request is authorized, or status code 401 if the request is unauthorized.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApplicationUser> info(Authentication authentication) {
        ApplicationUser applicationUser = applicationUserRepository.findByEmail(authentication.getName());

        if (applicationUser != null) {
            return ResponseEntity.ok(applicationUser);
        }

        return ResponseEntity.status(401).build();
    }

    /**
     * This method handles a request to create an account
     * @param applicationUser Represents user data which is checked for validity and used to create an account
     * @return Returns a <code>ResponseEntity</code> with a body of "Account created successfully!" if the request was
     * valid, or status code 409 if the email is already in use
     */
    @PostMapping("/sign-up")
    public ResponseEntity signUp(@RequestBody ApplicationUser applicationUser) {
        ApplicationUser existing = applicationUserRepository.findByEmail(applicationUser.getEmail());

        if (existing != null) {
            return ResponseEntity.status(409).body("That email is already in use.");
        }

        //TODO: Add serverside validation for the other fields

        applicationUser.setPassword(bCryptPasswordEncoder.encode(applicationUser.getPassword()));
        applicationUserRepository.save(applicationUser);

        return ResponseEntity.ok().body("Account created successfully!");
    }

    /**
     * This method handles a request to reauthorize with a refresh token
     * @param refreshTokenClaim The refresh token in the form of a String
     * @return Returns a <code>ResponseEntity</code> with a new Access Token and Refresh Token appended to the headers if
     * the request is successful, or a status code 401 if unsuccessful.
     */
    @GetMapping("/refresh")
    public ResponseEntity refresh(@RequestHeader("Refresh") String refreshTokenClaim) {
        ApplicationUser user = applicationUserRepository.findByRefreshTokenTokenValue(refreshTokenClaim);

        if (user != null) {
            String email = user.getEmail();
            RefreshToken r = user.getRefreshToken();

            if (r.getExpiryDate().after(new Date())) {
                Date accessExpiryDate = new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME);
                Date refreshExpiryDate = new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME);

                String authToken = JWT.create()
                        .withSubject(email)
                        .withExpiresAt(accessExpiryDate)
                        .sign(HMAC512(SECRET.getBytes()));

                String refreshToken = JWT.create()
                        .withSubject(email + util.randomAlphanumericString(40))
                        .withExpiresAt(refreshExpiryDate)
                        .sign(HMAC512(SECRET.getBytes()));

                RefreshToken dbToken = new RefreshToken();
                dbToken.setTokenValue(refreshToken);
                dbToken.setExpiryDate(refreshExpiryDate);

                user.setRefreshToken(dbToken);
                applicationUserRepository.save(user);

                return ResponseEntity.ok()
                        .header(HEADER_STRING_ACCESS, TOKEN_PREFIX + authToken)
                        .header(HEADER_STRING_REFRESH, refreshToken)
                        .build();
            }
        }

        return ResponseEntity.status(401).build();
    }

    /**
     * This method handles a request for a password-reset email from the server.
     * Sends an email to the provided address, if the address is matched to a user in the database.
     * @param email The email address to send the password-reset email to
     * @return Returns a <code>ResponseEntity</code> with status code 200
     */
    @GetMapping("/reset-email")
    public ResponseEntity resetEmail(@RequestParam("email") String email) {
        ApplicationUser user = applicationUserRepository.findByEmail(email);

        if (user != null) {
            String code = util.randomNumericString(4);

            ResetCode resetCode = new ResetCode();
            resetCode.setCodeValue(code);
            resetCode.setExpiryDate(new Date(System.currentTimeMillis() + RESET_CODE_EXPIRATION_TIME));
            user.setResetCode(resetCode);

            applicationUserRepository.save(user);

            String subject = "Password Reset Code: Hixel";
            String content = "Hello " + user.getFirstName() + ", \n\n"
                    + "Here is your password reset code: " + code + "\n"
                    + "If you did not request a code, you can safely ignore this email. \n\n"
                    + "Regards,\nHixel Development Team";

            mailSenderService.sendSimpleMail(email, subject, content);
        }

        //NOTE: Always returns a successful response. (Make it harder to bruteforce emails from the server.)
        return ResponseEntity.ok().build();
    }

    /**
     * This method handles a request to check if a reset code is valid
     * @param email The email address that the user claims is associated with the account
     * @param code The reset code that the user claims was received
     * @return Returns a <code>ResponseEntity</code> with status 200 if the request is valid, and 401 otherwise
     */
    @GetMapping("/reset-code")
    public ResponseEntity resetCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        ApplicationUser user = applicationUserRepository.findByEmail(email);

        if (user == null)
            return ResponseEntity.status(401).build();

        ResetCode userCode = user.getResetCode();

        if (userCode != null && userCode.getExpiryDate().after(new Date())) {
            String existing = userCode.getCodeValue().toLowerCase();

            if (existing.equals(code.toLowerCase()))
                return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(401).build();
    }

    /**
     * This method handles a request to reset a password with a valid reset code
     * @param email The email address that the user claims is associated with the account
     * @param code The reset code that the user claims was received
     * @param password The new password for the account
     * @return Returns a <code>ResponseEntity</code> with status 200 if the request is valid, and 401 otherwise
     */
    @GetMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam("email") String email, @RequestParam("code") String code,
                                        @RequestParam("password") String password) {
        ApplicationUser user = applicationUserRepository.findByEmail(email);

        if (user == null)
            return ResponseEntity.status(401).build();

        ResetCode userCode = user.getResetCode();

        if (userCode != null && userCode.getExpiryDate().after(new Date())) {
            String existing = userCode.getCodeValue().toLowerCase();

            if (existing.equals(code.toLowerCase())) {
                user.setPassword(bCryptPasswordEncoder.encode(password));
                applicationUserRepository.save(user);
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.status(401).build();
    }

    /**
     * This method handles a request to change a password
     * @param oldPassword The password that the user claims is currently associated with the account
     * @param newPassword The new password for the account
     * @param authentication Represents the token for an authenticated principal once the request
     * has been processed by the Spring Authentication Manager
     * @return Returns a <code>ResponseEntity</code> with status 200 if the request is valid, and 401 otherwise
     */
    @PostMapping("/change-password")
    public ResponseEntity changePassword(@RequestParam("oldPassword") String oldPassword,
                                         @RequestParam("newPassword") String newPassword,
                                         Authentication authentication) {
        ApplicationUser user = applicationUserRepository.findByEmail(authentication.getName());

        if (user == null || !bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        //TODO: Add serverside validation for the password's validity (password strength, etc.)

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        applicationUserRepository.save(user);

        return ResponseEntity.ok().build();
    }

    /**
     * This method handles a request to add a company to the user's portfolio
     * @param ticker The ticker symbol of the company being added
     * @param authentication Represents the token for an authenticated principal once the request
     * has been processed by the Spring Authentication Manager
     * @return Returns a <code>ResponseEntity</code> with the <code>Portfolio</code> data stored as JSON in the
     * body if the request is authorized, or status code 401 if the request is unauthorized.
     */
    @PostMapping("/portfolio/company")
    public ResponseEntity addCompany(@RequestParam("ticker") String ticker,
                                         Authentication authentication) {
        ApplicationUser user = applicationUserRepository.findByEmail(authentication.getName());

        if (user == null)
            return ResponseEntity.status(401).build();

        List<PortfolioCompany> userCompanies = user.getPortfolio().getCompanies();
        Boolean alreadyExists = userCompanies.stream().anyMatch(c -> c.getTicker().equalsIgnoreCase(ticker));

        if (!alreadyExists) {
            PortfolioCompany newCompany = new PortfolioCompany();
            newCompany.setTicker(ticker.toUpperCase());
            userCompanies.add(newCompany);

            applicationUserRepository.save(user);
        }

        return ResponseEntity.ok(user.getPortfolio());
    }

    /**
     * This method handles a request to remove a company from the user's portfolio
     * @param ticker The ticker symbol of the company being removed
     * @param authentication Represents the token for an authenticated principal once the request
     * has been processed by the Spring Authentication Manager
     * @return Returns a <code>ResponseEntity</code> with the <code>Portfolio</code> data stored as JSON in the
     * body if the request is authorized, or status code 401 if the request is unauthorized.
     */
    @DeleteMapping("/portfolio/company")
    public ResponseEntity removeCompany(@RequestParam("ticker") String ticker,
                                         Authentication authentication) {
        ApplicationUser user = applicationUserRepository.findByEmail(authentication.getName());

        if (user == null)
            return ResponseEntity.status(401).build();

        List<PortfolioCompany> userCompanies = user.getPortfolio().getCompanies();
        userCompanies.removeIf((company) -> company.getTicker().equalsIgnoreCase(ticker));

        applicationUserRepository.save(user);
        return ResponseEntity.ok(user.getPortfolio());
    }
}