package uk.ac.rhul.cs3821.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserSavingsTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldCreateValidUserSavings() {

    User user = new User();
    user.setId(1L);

    UserGoal goal = new UserGoal();
    goal.setId(10L);
    goal.setUser(user);

    UserSavings saving = UserSavings.builder()
            .id(100L)
            .user(user)
            .goal(goal)
            .amount(new BigDecimal("100.00"))
            .createdAt(LocalDateTime.now())
            .build();

    assertNotNull(saving);
    assertEquals(100L, saving.getId());
    assertEquals(user, saving.getUser());
    assertEquals(goal, saving.getGoal());
    assertEquals(0, saving.getAmount().compareTo(new BigDecimal("100.00")));
  }

  @Test
  void shouldThrowWhenAmountIsNull() {

    assertThrows(NullPointerException.class, () -> {
      UserSavings.builder()
              .amount(null)
              .build();
    });
  }

  @Test
  void shouldFailValidationWhenAmountIsZero() {

    UserSavings saving = UserSavings.builder()
            .amount(new BigDecimal("0.00"))
            .build();

    Set<ConstraintViolation<UserSavings>> violations = validator.validate(saving);

    assertFalse(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenAmountIsNegative() {

    UserSavings saving = UserSavings.builder()
            .amount(new BigDecimal("-50.00"))
            .build();

    Set<ConstraintViolation<UserSavings>> violations = validator.validate(saving);

    assertFalse(violations.isEmpty());
  }
}