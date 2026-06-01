package uk.ac.rhul.cs3821.entity;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the User entity class.
 */
public class UserTest {

  @Test
  void testNoArgsConstructorAndSettersGetters() {
    User user = new User();

    LocalDateTime now = LocalDateTime.now();

    user.setId(1L);
    user.setFirebaseUid("firebase123");
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setUsername("alicej");
    user.setEmail("alice.johnson@example.com");
    user.setActive(true);
    user.setEmailVerifiedAt(now);
    user.setLastLoginAt(now.minusDays(1));
    user.setProfilePictureUrl("https://example.com/alice.jpg");
    user.setBio("Software engineer.");
    user.setPhoneNumber("+441234567890");
    user.setCurrency("USD");
    user.setPreferredLanguage("en");
    user.setTimezone("Europe/London");
    user.setRole("standard");
    user.setTheme("system");
    user.setCreatedAt(now.minusDays(2));
    user.setUpdatedAt(now);
    user.setDeletedAt(null);

    assertEquals(1L, user.getId());
    assertEquals("firebase123", user.getFirebaseUid());
    assertEquals("Alice", user.getFirstName());
    assertEquals("Johnson", user.getLastName());
    assertEquals("alicej", user.getUsername());
    assertEquals("alice.johnson@example.com", user.getEmail());
    assertTrue(user.isActive());
    assertEquals(now, user.getEmailVerifiedAt());
    assertEquals(now.minusDays(1), user.getLastLoginAt());
    assertEquals("https://example.com/alice.jpg", user.getProfilePictureUrl());
    assertEquals("Software engineer.", user.getBio());
    assertEquals("+441234567890", user.getPhoneNumber());
    assertEquals("USD", user.getCurrency());
    assertEquals("en", user.getPreferredLanguage());
    assertEquals("Europe/London", user.getTimezone());
    assertEquals("standard", user.getRole());
    assertEquals(now.minusDays(2), user.getCreatedAt());
    assertEquals(now, user.getUpdatedAt());
    assertNull(user.getDeletedAt());
  }

  @Test
  void testAllArgsConstructor() {
    LocalDateTime created = LocalDateTime.now().minusDays(3);
    LocalDateTime updated = LocalDateTime.now().minusDays(1);

    User user = new User(
            2L,
            "firebase456",
            "Bob",
            "Smith",
            "bobsmith",
            "bob.smith@example.com",
            true,
            created.minusDays(1),
            updated,
            "https://example.com/bob.jpg",
            "Investor.",
            "+441112223334",
            "USD",
            "en",
            "UTC",
            "admin",
            "system"
            , created,
            updated,
            null
    );

    assertEquals(2L, user.getId());
    assertEquals("firebase456", user.getFirebaseUid());
    assertEquals("Bob", user.getFirstName());
    assertEquals("Smith", user.getLastName());
    assertEquals("bobsmith", user.getUsername());
    assertEquals("bob.smith@example.com", user.getEmail());
    assertTrue(user.isActive());
    assertEquals("Investor.", user.getBio());
    assertEquals("admin", user.getRole());
    assertEquals("system", user.getTheme());
  }
}
