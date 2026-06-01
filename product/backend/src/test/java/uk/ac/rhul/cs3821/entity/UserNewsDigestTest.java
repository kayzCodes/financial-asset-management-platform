package uk.ac.rhul.cs3821.entity;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class UserNewsDigestTest {

  @Test
  void gettersAndSetters_workCorrectly() {

    UserNewsDigest digest = new UserNewsDigest();
    User user = new User();

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime later = now.plusHours(6);

    digest.setId(1L);
    digest.setUser(user);
    digest.setDigestJson("{\"data\":[]}");
    digest.setGeneratedAt(now);
    digest.setExpiresAt(later);
    digest.setLastRefreshAt(now.plusMinutes(5));
    digest.setModelVersion("v1");
    digest.setLastSymbolIndex(3);

    assertEquals(1L, digest.getId());
    assertEquals(user, digest.getUser());
    assertEquals("{\"data\":[]}", digest.getDigestJson());
    assertEquals(now, digest.getGeneratedAt());
    assertEquals(later, digest.getExpiresAt());
    assertEquals(now.plusMinutes(5), digest.getLastRefreshAt());
    assertEquals("v1", digest.getModelVersion());
    assertEquals(3, digest.getLastSymbolIndex());
  }

  @Test
  void noArgsConstructor_initializesWithNulls() {

    UserNewsDigest digest = new UserNewsDigest();

    assertNull(digest.getId());
    assertNull(digest.getUser());
    assertNull(digest.getDigestJson());
    assertNull(digest.getGeneratedAt());
    assertNull(digest.getExpiresAt());
    assertNull(digest.getLastRefreshAt());
    assertNull(digest.getModelVersion());
    assertNull(digest.getLastSymbolIndex());
  }
}