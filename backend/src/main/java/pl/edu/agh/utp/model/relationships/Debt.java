package pl.edu.agh.utp.model.relationships;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import pl.edu.agh.utp.model.nodes.User;

@RelationshipProperties
@Data
public class Debt {
  @Id @GeneratedValue private Long id;
  @TargetNode private final User user;
  private final double amount;
}
