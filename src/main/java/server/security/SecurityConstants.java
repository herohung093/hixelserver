package server.security;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long REFRESH_EXPIRATION_TIME = 1_468_800_000; // 17 days
    public static final long ACCESS_EXPIRATION_TIME = 480_000; // 30 minutes
    public static final long RESET_CODE_EXPIRATION_TIME = 240_000; // 15 minutes
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING_ACCESS = "Authorization";
    public static final String HEADER_STRING_REFRESH = "Refresh";
    static final String SIGN_UP_URL = "/users/sign-up";
    static final String REFRESH_URL = "/users/refresh";
    static final String RESET_EMAIL_URL = "/users/reset-email";
    static final String RESET_CODE_URL = "/users/reset-code";
    static final String RESET_PASS_URL = "/users/reset-password";
}