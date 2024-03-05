package pl.edu.agh.utp.repository;

import java.util.Objects;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DbTest {

  private static final String NEO4J_CONTAINER_IMAGE = "neo4j:5.2";

  private static final Neo4jContainer<?> NEO4J_CONTAINER;

  static {
    NEO4J_CONTAINER = new Neo4jContainer<>(NEO4J_CONTAINER_IMAGE);
    NEO4J_CONTAINER
        .withLabsPlugins("apoc")
        .withoutAuthentication()
        .withCreateContainerCmdModifier(
            cmd ->
                Objects.requireNonNull(cmd.getHostConfig())
                    .withMemory(gigabytesToBytes(1))
                    .withMemorySwap(gigabytesToBytes(1)));
    NEO4J_CONTAINER.start();
  }

  @DynamicPropertySource
  private static void registerNeo4jProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.neo4j.uri", NEO4J_CONTAINER::getBoltUrl);
  }

  private static long gigabytesToBytes(long gigabytes) {
    return gigabytes * 1024L * 1024L * 1024L;
  }
}
