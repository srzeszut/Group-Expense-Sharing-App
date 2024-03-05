package pl.edu.agh.utp.controller;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.edu.agh.utp.exceptions.InvalidPasswordException;
import pl.edu.agh.utp.records.dto.UserDTO;
import pl.edu.agh.utp.records.request.LoginRequest;
import pl.edu.agh.utp.records.request.RegisterRequest;
import pl.edu.agh.utp.records.simple.SimpleGroup;
import pl.edu.agh.utp.service.UserService;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("{id}/groups")
  public List<SimpleGroup> getUserGroups(@PathVariable("id") UUID userId) {
    return userService.findGroupsByUserId(userId);
  }

  @PostMapping
  public UserDTO createUser(@RequestBody RegisterRequest request) {
    return userService
        .createUser(request)
        .map(UserDTO::fromUser)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists"));
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
    try {
      return ResponseEntity.ok(
          userService
              .authenticateUser(loginRequest)
              .map(UserDTO::fromUser)
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    } catch (InvalidPasswordException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
    }
  }
}
