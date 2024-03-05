package pl.edu.agh.utp.records.dto;

import java.util.UUID;
import pl.edu.agh.utp.model.nodes.User;

public record UserDTO(UUID userId, String name, String email) {
  public static UserDTO fromUser(User user) {
    return new UserDTO(user.getId(), user.getName(), user.getEmail());
  }
}
