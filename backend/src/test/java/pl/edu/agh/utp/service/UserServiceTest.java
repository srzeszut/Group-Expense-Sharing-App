package pl.edu.agh.utp.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.edu.agh.utp.exceptions.InvalidPasswordException;
import pl.edu.agh.utp.model.nodes.User;
import pl.edu.agh.utp.records.request.LoginRequest;
import pl.edu.agh.utp.records.request.RegisterRequest;
import pl.edu.agh.utp.records.simple.SimpleGroup;
import pl.edu.agh.utp.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  private User testUser;
  private LoginRequest loginRequest;
  private RegisterRequest registerRequest;

  @BeforeEach
  void setUp() {
    testUser = new User("John Doe", "john@example.com", "password123");
    loginRequest = new LoginRequest("john@example.com", "password123");
    registerRequest = new RegisterRequest("Jane Doe", "jane@example.com", "password123");
  }

  @Nested
  class AuthenticateUser {
    @Test
    public void shouldReturnForOKRequest() {
      // given
      when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(testUser));

      // when
      var result = userService.authenticateUser(loginRequest);

      // then
      assertThat(result).isPresent().get().isEqualTo(testUser);
    }

    @Test
    public void shouldThrowForInvalidPassword() {
      // given
      when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(testUser));

      // when & then
      assertThatThrownBy(
              () ->
                  userService.authenticateUser(
                      new LoginRequest("john@example.com", "wrongpassword")))
          .isExactlyInstanceOf(InvalidPasswordException.class)
          .hasMessage("Invalid password");
    }

    @Test
    public void shouldReturnEmptyForNonExistingUser() {
      // given
      when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

      // when
      var result = userService.authenticateUser(loginRequest);

      // then
      assertThat(result).isEmpty();
    }
  }

  @Nested
  class FindGroupsByUserId {
    @Test
    public void shouldFindForOkData() {

      // given
      var expectedGroups = Collections.singletonList(new SimpleGroup(UUID.randomUUID(), "group"));
      var userId = UUID.randomUUID();
      when(userRepository.findGroupsByUserId(userId)).thenReturn(expectedGroups);

      // when
      var result = userService.findGroupsByUserId(userId);

      // then
      assertThat(result).isEqualTo(expectedGroups);
    }
  }

  @Nested
  class CreateUser {
    @Test
    public void shouldCreateForOkData() {
      // given
      var user =
          new User(registerRequest.name(), registerRequest.email(), registerRequest.password());
      when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
      when(userRepository.save(user)).thenReturn(user);

      // when
      var result = userService.createUser(registerRequest);

      // then
      assertThat(result).isPresent().get().isEqualTo(user);
    }

    @Test
    public void shouldReturnEmptyForExistingEmail() {

      // given
      when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(testUser));

      // when
      var result = userService.createUser(registerRequest);

      // then
      assertThat(result).isEmpty();
    }
  }
}
