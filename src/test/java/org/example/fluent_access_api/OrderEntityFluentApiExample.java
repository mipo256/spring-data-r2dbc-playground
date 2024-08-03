package org.example.fluent_access_api;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.example.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class OrderEntityFluentApiExample extends AbstractIntegrationTest {

  @Autowired
  private R2dbcEntityTemplate r2dbcEntityTemplate;

  @Autowired
  private ConnectionFactory connectionFactory;

  @BeforeEach
  void setUp() {
    Connection connection = Mono.from(connectionFactory.create()).block();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("org/example/fluent_access_api/OrderEntityFluentApiExample.sql")).block();
  }

  @AfterEach
  void tearDown() {
    Connection connection = Mono.from(connectionFactory.create()).block();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("cleanup.sql")).block();
  }

  @Test
  void testInserting() {
    StepVerifier
        .create(r2dbcEntityTemplate
            .insert(Order.class)
            .into(SqlIdentifier.quoted("orders"))
            .using(new Order().setType(OrderType.OFFLINE).setStatus(OrderStatus.DELIVERED))
        )
        .expectNextMatches(order -> order.getId() != null && order.getCreatedAt() != null)
        .verifyComplete();
  }
}
