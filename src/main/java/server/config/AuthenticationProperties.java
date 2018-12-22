package server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Contains properties that control the way the server handled authentication.
 * Mostly empty at the moment - But exists because auth will surely be developed further after Hixel.
 */
@SuppressWarnings("unused")
@Configuration
class AuthenticationProperties {
    /**
     * Returns a BCryptPasswordEncoder instance, which is then injected into components by the Spring framework.
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
