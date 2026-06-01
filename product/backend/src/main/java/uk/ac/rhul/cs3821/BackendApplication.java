package uk.ac.rhul.cs3821;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This is main file.
 */
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class BackendApplication {

  /**
   * The entry point of the Spring Boot application.
   *
   * @param args command-line arguments
   */

  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }

}
