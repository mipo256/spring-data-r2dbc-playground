package org.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest
public class AbstractIntegrationTest {

  public static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.2")
      .withDatabaseName("local")
      .withPassword("local")
      .withUsername("local")
      .withExposedPorts(5432);

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    registry.add("r2dbc.host", postgreSQLContainer::getHost);
    registry.add("r2dbc.port", postgreSQLContainer::getFirstMappedPort);
    registry.add("r2dbc.db", () -> "local");
    registry.add("r2dbc.password", () -> "local");
    registry.add("r2dbc.username", () -> "local");
  }

  @BeforeAll
  static void beforeAll() {
    postgreSQLContainer.start();
  }

  @AfterAll
  static void afterAll() {
    postgreSQLContainer.close();
  }
}
