package uk.ac.rhul.cs3821.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

/**
 * Loads and initializes the Firebase SDK using the service account file.
 */
@Configuration
@Profile("!ci") // <--- DO NOT load this config when profile = 'ci'
public class FirebaseConfig {

  /**
   * Initializes Firebase when the application starts.
   * Loads the service account JSON and registers the FirebaseApp instance.
   *
   * @throws RuntimeException if the service account file cannot be read
   */
  @PostConstruct
  public void initFirebase() {
    try {
      if (FirebaseApp.getApps().isEmpty()) {

        ClassPathResource resource =
                new ClassPathResource("firebase-service-account.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();

        FirebaseApp.initializeApp(options);
        System.out.println("🔥 Firebase initialized successfully");
      }

    } catch (IOException e) {
      throw new RuntimeException("Failed to initialize Firebase", e);
    }
  }
}
