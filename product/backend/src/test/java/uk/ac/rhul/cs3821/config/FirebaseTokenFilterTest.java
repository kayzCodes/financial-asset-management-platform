package uk.ac.rhul.cs3821.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FirebaseTokenFilterTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private FirebaseTokenFilter firebaseTokenFilter;

  /**
   * Protected endpoint + valid token → attributes set, chain continues.
   */
  @Test
  void testValidToken_setsAttributesAndContinuesChain()
          throws ServletException, IOException, FirebaseAuthException {

    // Protected URI (shouldNotFilter == false)
    when(request.getRequestURI()).thenReturn("/api/protected/resource");
    String token = "validToken";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

    FirebaseAuth mockAuth = mock(FirebaseAuth.class);
    FirebaseToken mockDecodedToken = mock(FirebaseToken.class);

    try (MockedStatic<FirebaseAuth> firebaseAuthMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthMock.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
      when(mockAuth.verifyIdToken(token)).thenReturn(mockDecodedToken);
      when(mockDecodedToken.getUid()).thenReturn("test-uid-123");

      // Act
      firebaseTokenFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(request).setAttribute("firebaseUid", "test-uid-123");
      // no firebaseToken attribute is set in the current filter implementation
      verify(filterChain).doFilter(request, response);
      verify(response, never()).sendError(anyInt(), anyString());
    }
  }

  /**
   * Protected endpoint + invalid token → 401, chain does NOT continue.
   */
  @Test
  void testInvalidToken_sendsUnauthorizedAndStopsChain()
          throws ServletException, IOException, FirebaseAuthException {

    when(request.getRequestURI()).thenReturn("/api/protected/resource");
    String token = "invalidToken";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

    FirebaseAuth mockAuth = mock(FirebaseAuth.class);

    try (MockedStatic<FirebaseAuth> firebaseAuthMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthMock.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
      when(mockAuth.verifyIdToken(token))
              .thenThrow(new RuntimeException("Token invalid"));


      // Act
      firebaseTokenFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase token");
      verify(filterChain, never()).doFilter(request, response);
      verify(request, never()).setAttribute(eq("firebaseUid"), any());
    }
  }

  /**
   * Public endpoint (whitelisted in shouldNotFilter) → filter skipped,
   * chain continues even without Authorization header.
   */
  @Test
  void testNoAuthorizationHeader_skipsFirebaseAndContinuesChain()
          throws ServletException, IOException {

    // Public URI (shouldNotFilter == true)
    when(request.getRequestURI()).thenReturn("/api/user/registerUser");

    // Act
    firebaseTokenFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(filterChain).doFilter(request, response);
    verify(response, never()).sendError(anyInt(), anyString());
  }

  /**
   * Protected endpoint + missing Authorization header → 401 Missing Authorization header.
   */
  @Test
  void testMissingAuthorizationHeader_sendsUnauthorizedAndStopsChain()
          throws ServletException, IOException {

    // Protected endpoint → shouldNotFilter == false
    when(request.getRequestURI()).thenReturn("/api/protected/resource");

    // No Authorization header sent
    when(request.getHeader("Authorization")).thenReturn(null);

    // Act
    firebaseTokenFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
    verify(filterChain, never()).doFilter(request, response);
  }

  @Test
  void testInvalidAuthorizationFormat_sendsUnauthorizedAndStopsChain()
          throws ServletException, IOException {

    // Protected URI (filter must run)
    when(request.getRequestURI()).thenReturn("/api/protected/resource");

    // Invalid header format (does not start with Bearer )
    when(request.getHeader("Authorization")).thenReturn("Token abc123");

    // Act
    firebaseTokenFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authorization format");
    verify(filterChain, never()).doFilter(request, response);
  }


}
