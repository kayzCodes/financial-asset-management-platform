package uk.ac.rhul.cs3821.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import uk.ac.rhul.cs3821.firebase.FirebaseConfig;

class FirebaseConfigTest {

  @Test
  void shouldInitializeFirebaseWhenNoAppsExist() throws Exception {

    FirebaseConfig config = new FirebaseConfig();

    try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class);
         MockedStatic<GoogleCredentials> credentialsMock = mockStatic(GoogleCredentials.class)) {

      // No existing apps
      firebaseAppMock.when(FirebaseApp::getApps)
              .thenReturn(Collections.emptyList());

      // Mock credentials loading
      InputStream fakeStream = new ByteArrayInputStream("{}".getBytes());
      credentialsMock.when(() -> GoogleCredentials.fromStream(any()))
              .thenReturn(mock(GoogleCredentials.class));

      // Run
      config.initFirebase();

      // Verify Firebase initialized
      firebaseAppMock.verify(
              () -> FirebaseApp.initializeApp(any(FirebaseOptions.class)),
              times(1)
      );
    }
  }

  @Test
  void shouldNotInitializeFirebaseWhenAppAlreadyExists() {

    FirebaseConfig config = new FirebaseConfig();

    try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class)) {

      // Simulate existing app
      firebaseAppMock.when(FirebaseApp::getApps)
              .thenReturn(Collections.singletonList(mock(FirebaseApp.class)));

      config.initFirebase();

      // Should NOT initialize again
      firebaseAppMock.verify(
              () -> FirebaseApp.initializeApp(any(FirebaseOptions.class)),
              never()
      );
    }
  }

  @Test
  void shouldThrowRuntimeExceptionWhenIOExceptionOccurs() {

    FirebaseConfig config = new FirebaseConfig();

    try (MockedStatic<FirebaseApp> firebaseAppMock = mockStatic(FirebaseApp.class);
         MockedStatic<GoogleCredentials> credentialsMock = mockStatic(GoogleCredentials.class)) {

      firebaseAppMock.when(FirebaseApp::getApps)
              .thenReturn(Collections.emptyList());

      // Force exception
      credentialsMock.when(() -> GoogleCredentials.fromStream(any()))
              .thenThrow(new RuntimeException("IO fail"));

      RuntimeException ex = assertThrows(
              RuntimeException.class,
              config::initFirebase
      );

      credentialsMock.when(() -> GoogleCredentials.fromStream(any()))
              .thenThrow(new IOException("IO fail"));
    }
  }
}