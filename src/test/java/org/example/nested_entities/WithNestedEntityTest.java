package org.example.nested_entities;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import java.util.Objects;
import org.example.AbstractIntegrationTest;
import org.example.nested_entities.WithNestedEntity.NestedEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class WithNestedEntityTest extends AbstractIntegrationTest {

  @Autowired
  private WithNestedRepository repository;

  @Autowired
  private ConnectionFactory connectionFactory;

  @BeforeEach
  void setUp() {
    Connection connection = Mono.from(connectionFactory.create()).block();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("org/example/nested_entities/WithNestedEntityTest.sql")).block();
  }

  @AfterEach
  void tearDown() {
    Connection connection = Mono.from(connectionFactory.create()).block();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("cleanup.sql")).block();
  }

  @Test
  void testPaginationForPartTreeQueries() {
    StepVerifier
        .create(repository.save(new WithNestedEntity().setType("ON").setNestedEntity(new NestedEntity().setAnything("anything"))))
        .verifyErrorMatches(throwable -> throwable instanceof InvalidDataAccessApiUsageException);
  }
}
