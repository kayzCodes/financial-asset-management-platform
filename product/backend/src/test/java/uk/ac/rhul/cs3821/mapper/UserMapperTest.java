package uk.ac.rhul.cs3821.mapper;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs3821.dto.UserDto;
import uk.ac.rhul.cs3821.entity.User;

public class UserMapperTest {

  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();

    // Create a User entity
    user = new User();
    user.setId(1L);
    user.setFirebaseUid("Hn3iuGlnuxVjcgu6Rtb7WEKSncm1");
    user.setFirstName("Alice");
    user.setLastName("Johnson");
    user.setUsername("alicej");
    user.setEmail("alice.johnson@example.com");
    user.setActive(true);
    user.setEmailVerifiedAt(now.minusDays(5));
    user.setLastLoginAt(now.minusDays(1));
    user.setProfilePictureUrl("https://example.com/alice.jpg");
    user.setBio("Software engineer and crypto enthusiast.");
    user.setPhoneNumber("+441234567890");
    user.setCurrency("USD");
    user.setPreferredLanguage("en");
    user.setTimezone("Europe/London");
    user.setRole("standard");
    user.setTheme("system");
    user.setCreatedAt(now.minusDays(10));
    user.setUpdatedAt(now);
    user.setDeletedAt(null);

    // Create a corresponding DTO
    userDto = new UserDto(
            1L,
            "Hn3iuGlnuxVjcgu6Rtb7WEKSncm1",
            "Alice",
            "Johnson",
            "alicej",
            "alice.johnson@example.com",
            true,
            now.minusDays(5),
            now.minusDays(1),
            "https://example.com/alice.jpg",
            "Software engineer and crypto enthusiast.",
            "+441234567890",
            "USD",
            "en",
            "Europe/London",
            "standard",
            "system",
            now.minusDays(10),
            now,
            null
    );
  }

  @Test
  void testConstructorCoverage() {
    // Covers the implicit no-args constructor
    new UserMapper();
  }

  @Test
  void testMapToUserDto() {
    UserDto dto = UserMapper.mapToUserDto(user);

    assertNotNull(dto);
    assertEquals(user.getId(), dto.getId());
    assertEquals(user.getFirebaseUid(), dto.getFirebaseUid());
    assertEquals(user.getFirstName(), dto.getFirstName());
    assertEquals(user.getLastName(), dto.getLastName());
    assertEquals(user.getUsername(), dto.getUsername());
    assertEquals(user.getEmail(), dto.getEmail());
    assertEquals(user.isActive(), dto.isActive());
    assertEquals(user.getCurrency(), dto.getCurrency());
    assertEquals(user.getPreferredLanguage(), dto.getPreferredLanguage());
    assertEquals(user.getRole(), dto.getRole());
    assertEquals(user.getTheme(), dto.getTheme());
    assertEquals(user.getTimezone(), dto.getTimezone());
    assertEquals(user.getCreatedAt(), dto.getCreatedAt());
    assertEquals(user.getUpdatedAt(), dto.getUpdatedAt());
  }

  @Test
  void testMapToUser() {
    User entity = UserMapper.mapToUser(userDto);

    assertNotNull(entity);
    assertEquals(userDto.getId(), entity.getId());
    assertEquals(userDto.getFirebaseUid(), entity.getFirebaseUid());
    assertEquals(userDto.getFirstName(), entity.getFirstName());
    assertEquals(userDto.getLastName(), entity.getLastName());
    assertEquals(userDto.getUsername(), entity.getUsername());
    assertEquals(userDto.getEmail(), entity.getEmail());
    assertEquals(userDto.isActive(), entity.isActive());
    assertEquals(userDto.getTimezone(), entity.getTimezone());
    assertEquals(userDto.getCurrency(), entity.getCurrency());
    assertEquals(userDto.getRole(), entity.getRole());
    assertEquals(userDto.getTheme(), entity.getTheme());
    assertEquals(userDto.getCreatedAt(), entity.getCreatedAt());
    assertEquals(userDto.getUpdatedAt(), entity.getUpdatedAt());
  }

  @Test
  void testNullInputs() {
    assertNull(UserMapper.mapToUser(null));
    assertNull(UserMapper.mapToUserDto(null));
  }
}
