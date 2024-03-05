package pl.edu.agh.utp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.agh.utp.model.nodes.User;
import pl.edu.agh.utp.records.simple.SimpleGroup;

public interface UserRepository extends Neo4jRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  @Query(
      "MATCH (g:Group)-[:CONTAINS_USER]->(u:User) WHERE u.id = $userId RETURN g.id AS groupId, g.name AS name")
  List<SimpleGroup> findGroupsByUserId(@Param("userId") UUID userId);

  @Query("MATCH (u:User) WHERE u.email IN $emails RETURN u")
  List<User> findAllByEmail(@Param("emails") List<String> emails);
}
