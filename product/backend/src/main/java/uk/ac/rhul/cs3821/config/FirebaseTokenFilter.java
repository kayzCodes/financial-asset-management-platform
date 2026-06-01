package uk.ac.rhul.cs3821.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that validates Firebase ID tokens on incoming requests.
 */
@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    boolean skip =
            uri.startsWith("/api/user/registerUser")
                    || uri.startsWith("/api/user/checkUserByFirebaseUid")
                    || uri.startsWith("/api/user/getUserByUid");
    System.out.println("🔍 shouldNotFilter? " + skip + " → URI = " + uri);
    return skip;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
          throws ServletException, IOException {
    String uri = request.getRequestURI();
    System.out.println("======================================");
    System.out.println("FirebaseTokenFilter Triggered");
    System.out.println("Request URI: " + uri);
    // Skip filter when necessary
    if (shouldNotFilter(request)) {
      System.out.println(" Filter skipped for URI: " + uri);
      filterChain.doFilter(request, response);
      return;
    }
    // Log Authorization header
    String header = request.getHeader("Authorization");
    System.out.println(" Incoming Authorization Header: " + header);
    if (header == null) {
      System.out.println("No Authorization header present!");
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
      return;
    }
    if (!header.startsWith("Bearer ")) {
      System.out.println(" Authorization header does not start with 'Bearer '");
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authorization format");
      return;
    }
    String token = header.substring(7);
    System.out.println(" Extracted Token: " + token);

    try {
      FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
      String firebaseUid = decodedToken.getUid();
      System.out.println("✅ Firebase Token Verified — UID = " + firebaseUid);
      request.setAttribute("firebaseUid", firebaseUid);
      // ⭐⭐⭐ IMPORTANT FIX — ADD AUTH TO SPRING CONTEXT ⭐⭐⭐
      UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(firebaseUid, null, List.of());
      SecurityContextHolder.getContext().setAuthentication(auth);
      System.out.println("🔐 Spring Security Authentication SET for UID = " + firebaseUid);
    } catch (Exception e) {
      System.out.println("❌ Firebase token verification failed:");
      System.out.println("   Error: " + e.getMessage());
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase token");
      return;
    }
    // Continue
    System.out.println("✅ Firebase authentication passed → Continuing filter chain");
    System.out.println("======================================");
    filterChain.doFilter(request, response);
  }
}