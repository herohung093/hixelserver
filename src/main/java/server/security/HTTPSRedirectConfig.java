package server.security;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration bean responsible for automatically redirecting HTTP requests to a secure HTTPS connection.
 * See the <i>application.properties</i> file for HTTPS settings.
 */
@SuppressWarnings("unused")
@Configuration
class HTTPSRedirectConfig {
    /**
     * @return Returns a new <code>TomcatServletWebServerFactory</code> with an HTTPS redirect connector attached
     * @see HTTPSRedirectConfig#redirectConnector
     */
    @SuppressWarnings("WeakerAccess")
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    /**
     * Creates and returns an HTTPS redirect connector which can be attached to a <code>TomcatServletWebServerFactor</code>.
     * <b>NOTE:</b> The ports which are being redirected are currently hardcoded (8080 for HTTP and 8443 for HTTPS).
     * Ideally, these would instead be populated from the <i>application.properties</i> file.
     * @return Returns an HTTPS redirect connector which can be attached to a <code>TomcatServletWebServerFactor</code>
     * @see HTTPSRedirectConfig#servletContainer
     */
    private Connector redirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}