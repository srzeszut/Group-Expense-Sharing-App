package pl.edu.agh.utp.records.simple;

import java.util.UUID;
import pl.edu.agh.utp.model.nodes.Group;

public record SimpleGroup(UUID groupId, String name) {

  public static SimpleGroup fromGroup(Group group) {
    return new SimpleGroup(group.getId(), group.getName());
  }
}
