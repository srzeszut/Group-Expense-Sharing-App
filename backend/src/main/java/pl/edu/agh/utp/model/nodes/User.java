package pl.edu.agh.utp.model.nodes;

import java.util.UUID;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Data
public class User {
  @Id @GeneratedValue private UUID id;
  private final String name;
  private final String email;
  private final String password;
}
