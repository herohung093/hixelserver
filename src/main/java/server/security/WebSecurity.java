package server.security;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;

import server.database.ApplicationUserRepository;

import static server.security.SecurityConstants.*;

/**
 * This class configures how Spring Security functions
 */
@SuppressWarnings("unused")
@EnableWebSecurity
class WebSecurity extends WebSecurityConfigurerAdapter {
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ApplicationUserRepository applicationUserRepository;

    /**
     * Creates a <code>WebSecurity</code> object
     * @param userDetailsService A service which can be used to request user details
     * @param bCryptPasswordEncoder An encoder used to hash passwords
     * @param applicationUserRepository A user data repository which interfaces with the database for user management
     */
    public WebSecurity(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder,
                       ApplicationUserRepository applicationUserRepository) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.applicationUserRepository = applicationUserRepository;
    }

    /**
     * Configures how Spring Security functions, including:
     * <ol>
     * <li>Which URLs to authenticate</li>
     * <li>Unauthorized status code response</li>
     * <li>Which filters to use</li>
     * <li>Whether to use sessions</li>
     * </ol>
     * @param http Object that configures web-based security
     * @throws Exception
     */
    @SuppressWarnings("JavaDoc")
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.GET, REFRESH_URL).permitAll()
                .antMatchers(HttpMethod.GET, RESET_EMAIL_URL).permitAll()
                .antMatchers(HttpMethod.GET, RESET_CODE_URL).permitAll()
                .antMatchers(HttpMethod.GET, RESET_PASS_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), applicationUserRepository))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Configures the Spring Security Authentication Manager
     * In this implementation, all we do is set the password encoder to the <code>bCryptPasswordEncoder</code>
     * @param auth The Authentication Manager Builder
     * @throws Exception
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}