package pl.edu.agh.utp.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.agh.utp.model.nodes.Category;
import pl.edu.agh.utp.model.nodes.Group;
import pl.edu.agh.utp.model.nodes.Transaction;
import pl.edu.agh.utp.model.nodes.User;
import pl.edu.agh.utp.records.UserBalance;
import pl.edu.agh.utp.records.simple.SimpleTransaction;

public interface GroupRepository extends Neo4jRepository<Group, UUID> {
  @Query(
      "MATCH (g:Group)-[:CONTAINS_TRANSACTION]->(t:Transaction) WHERE g.id = $groupId RETURN t.id AS transactionId, t.description AS description, t.date AS date")
  List<SimpleTransaction> findAllTransactionsByGroupId(@Param("groupId") UUID groupId);

  @Query(
      """
                   MATCH (g:Group)-[:CONTAINS_USER]->(u:User)
                   MATCH (g)-[:CONTAINS_TRANSACTION]->(t:Transaction)
                        WHERE g.id = $groupId
                        OPTIONAL MATCH (t)-[o:OWES]->(u)
                        OPTIONAL MATCH (t)-[p:MADE_PAYMENT]->(u)
                        WITH u, COALESCE(SUM(o.amount), 0) AS owesAmount, COALESCE(SUM(p.amount), 0) AS paidAmount
                        RETURN u as user, owesAmount - paidAmount AS balance
                  """)
  List<UserBalance> findAllBalancesByGroupId(@Param("groupId") UUID groupId);

  @Query(
      """
                    MATCH (g:Group)-[:CONTAINS_USER]->(u:User)
                        MATCH (g)-[:CONTAINS_TRANSACTION]->(t:Transaction)-[:IS_OF_CATEGORY]->(c:Category)
                        WHERE g.id = $groupId AND c.name IN $categories
                        OPTIONAL MATCH (t)-[o:OWES]->(u)
                        OPTIONAL MATCH (t)-[p:MADE_PAYMENT]->(u)
                        WITH u, COALESCE(SUM(o.amount), 0) AS owesAmount, COALESCE(SUM(p.amount), 0) AS paidAmount
                        RETURN u as user, owesAmount - paidAmount AS balance
                  """)
  List<UserBalance> findBalancesByGroupIdAndCategory(
      @Param("groupId") UUID groupId, @Param("categories") List<String> categories);

  @Query(
      "MATCH (g:Group)-[:CONTAINS_USER]->(u:User) WHERE g.id = $groupId RETURN u.id AS id, u.name  AS name, u.email AS email, u.password AS password")
  List<User> findAllUsersByGroupId(@Param("groupId") UUID groupId);

  @Query(
      "MATCH (g:Group)-[:CONTAINS_TRANSACTION]->(t:Transaction)-[:IS_OF_CATEGORY]->(c:Category) WHERE g.id = $groupId RETURN c.id AS id, c.name AS name")
  List<Category> findCategoriesByGroupId(@Param("groupId") UUID groupId);

  @Query(
      """
                   MATCH (g:Group)-[:CONTAINS_USER]->(u:User)
                       MATCH (g)-[:CONTAINS_TRANSACTION]->(t:Transaction)-[:IS_OF_CATEGORY]->(c:Category)
                       WHERE g.id = $groupId AND c.name IN $categories
                       RETURN t.id as transactionId, t.description as description, t.date as date
                 """)
  List<SimpleTransaction> findAllTransactionsByGroupIdAndCategories(
      @Param("groupId") UUID groupId, @Param("categories") List<String> categories);

  @Query(
    """
      MATCH (g:Group)-[:CONTAINS_USER]->(u:User)
      MATCH (g)-[:CONTAINS_TRANSACTION]->(t:Transaction)
      WHERE g.id = $groupId AND u.id IN $userIds
      RETURN t.id AS transactionId, t.description AS description, t.date AS date
    """
  )
  List<SimpleTransaction> findAllTransactionsByGroupIdAndUsers(@Param("groupId") UUID groupId, @Param("userIds") List<UUID> users);
}
