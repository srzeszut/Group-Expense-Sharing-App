package pl.edu.agh.utp.model.nodes;

import java.util.UUID;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Data
@RequiredArgsConstructor
public class Category {
  @Id @GeneratedValue private UUID id;
  private String name;

  public Category(String name) {
    this.name = name;
  }
}
