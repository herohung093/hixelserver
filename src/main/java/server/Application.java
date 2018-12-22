package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The Application class owns the server instance and performs startup actions
 */
@SuppressWarnings("WeakerAccess")
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
public class Application {
    /**
     * The main method, which starts the CorpReport API server
     * @param args An array of command-line arguments for the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}