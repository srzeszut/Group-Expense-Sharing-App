package pl.edu.agh.utp.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.utp.exceptions.InvalidPasswordException;
import pl.edu.agh.utp.model.nodes.User;
import pl.edu.agh.utp.records.request.LoginRequest;
import pl.edu.agh.utp.records.request.RegisterRequest;
import pl.edu.agh.utp.records.simple.SimpleGroup;
import pl.edu.agh.utp.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public Optional<User> authenticateUser(LoginRequest request) throws InvalidPasswordException {
    return userRepository
        .findByEmail(request.email())
        .map(
            user -> {
              if (!user.getPassword().equals(request.password())) {
                throw new InvalidPasswordException("Invalid password");
              }
              return user;
            });
  }

  public List<SimpleGroup> findGroupsByUserId(UUID userId) {
    return userRepository.findGroupsByUserId(userId);
  }

  @Transactional
  public Optional<User> createUser(RegisterRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      return Optional.empty();
    }
    return Optional.of(new User(request.name(), request.email(), request.password()))
        .map(userRepository::save);
  }
}
