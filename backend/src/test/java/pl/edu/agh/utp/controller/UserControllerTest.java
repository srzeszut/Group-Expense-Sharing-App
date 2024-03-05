package pl.edu.agh.utp.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.utp.exceptions.InvalidPasswordException;
import pl.edu.agh.utp.model.nodes.User;
import pl.edu.agh.utp.records.dto.UserDTO;
import pl.edu.agh.utp.records.request.LoginRequest;
import pl.edu.agh.utp.records.request.RegisterRequest;
import pl.edu.agh.utp.records.simple.SimpleGroup;
import pl.edu.agh.utp.service.UserService;

public class UserControllerTest {

  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @BeforeEach
  @SneakyThrows
  public void setup() {
    try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
      mockMvc = standaloneSetup(userController).build();
    }
  }

  @Nested
  class FindGroupsByUserId {
    @Test
    @SneakyThrows
    public void shouldReturnOkForCorrectData() {
      var userId = UUID.randomUUID();
      var groups = Collections.singletonList(new SimpleGroup(UUID.randomUUID(), "group"));
      given(userService.findGroupsByUserId(userId)).willReturn(groups);

      mockMvc
          .perform(get("/users/" + userId + "/groups"))
          .andExpect(status().isOk())
          .andExpect(content().json(objectMapper.writeValueAsString(groups)));
    }
  }

  @Nested
  class CreateUser {
    @Test
    @SneakyThrows
    public void shouldReturnOkForCorrectData() {
      var request = new RegisterRequest("name", "email", "password");
      var user = new User("name", "email", "password");
      given(userService.createUser(request)).willReturn(Optional.of(user));

      mockMvc
          .perform(
              post("/users")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(content().json(objectMapper.writeValueAsString(UserDTO.fromUser(user))));
    }

    @Test
    @SneakyThrows
    public void shouldReturnConflictForExistingEmail() {
      var request = new RegisterRequest("name", "email", "password");
      given(userService.createUser(request)).willReturn(Optional.empty());

      mockMvc
          .perform(
              post("/users")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isConflict());
    }
  }

  @Nested
  class LoginUser {
    @Test
    @SneakyThrows
    public void shouldReturnOkForCorrectData() {
      var loginRequest = new LoginRequest("email", "password");
      var user = new User("name", "email", "password");
      given(userService.authenticateUser(loginRequest)).willReturn(Optional.of(user));

      mockMvc
          .perform(
              post("/users/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(loginRequest)))
          .andExpect(status().isOk())
          .andExpect(content().json(objectMapper.writeValueAsString(UserDTO.fromUser(user))));
    }

    @Test
    @SneakyThrows
    public void shouldReturnNotFoundForNonExistingUser() {
      var loginRequest = new LoginRequest("email", "password");
      given(userService.authenticateUser(loginRequest)).willReturn(Optional.empty());

      mockMvc
          .perform(
              post("/users/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(loginRequest)))
          .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void shouldReturnUnauthorizedForInvalidPassword() {
      var loginRequest = new LoginRequest("email", "password");
      var exception = new InvalidPasswordException("Invalid password");
      given(userService.authenticateUser(loginRequest)).willThrow(exception);

      mockMvc
          .perform(
              post("/users/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(loginRequest)))
          .andExpect(status().isUnauthorized())
          .andExpect(content().string(exception.getMessage()));
    }
  }
}
