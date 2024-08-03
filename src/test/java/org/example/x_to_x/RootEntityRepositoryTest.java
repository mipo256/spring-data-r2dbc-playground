package org.example.x_to_x;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.example.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * {@link Sql} annotation cannot be used here, as it requires a {@link javax.sql.DataSource} bean, which spring data r2bdc
 * does not provide (obviously, since it is a non-JDBC API). As such,
 * <a href="https://github.com/spring-projects/spring-framework/issues/27485">there is a ticket for this</a>, but for now we
 * can stick with {@link org.springframework.r2dbc.connection.init.ScriptUtils}
 */
public class RootEntityRepositoryTest extends AbstractIntegrationTest {

  @Autowired
  private ReactiveCrudRepository<RootEntity, Long> repository;

  @Autowired
  private ConnectionFactory connectionFactory;

  @BeforeEach
  void setUp() {
    Connection connection = Mono.from(connectionFactory.create()).block();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("org/example/x_to_x/RootEntityRepositoryTest.sql")).block();
  }

  @AfterEach
  void tearDown() {
    Connection connection = Mono.from(connectionFactory.create()).block();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("cleanup.sql")).block();
  }

  @Test
  void testLoadingWithTwoSelectStatements() {
    // given.
    StepVerifier
        .create(repository.save(new RootEntity().setType("SomeValue")))
        .expectErrorMatches(throwable ->
            throwable instanceof IllegalArgumentException && throwable.getMessage().contains("Unsupported array type"))
        .verify();
  }
}
