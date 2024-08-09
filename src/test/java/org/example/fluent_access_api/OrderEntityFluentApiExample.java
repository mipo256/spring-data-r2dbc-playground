package org.example.fluent_access_api;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import java.util.Objects;
import org.example.AbstractIntegrationTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class OrderEntityFluentApiExample extends AbstractIntegrationTest {

  @Autowired
  private R2dbcEntityTemplate r2dbcEntityTemplate;

  @Autowired
  private OrderRepository orderRepository;

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
        .create(insertOneOrder())
        .expectNextMatches(order -> order.getId() != null && order.getCreatedAt() != null)
        .verifyComplete();
  }

  @Test
  void testSelecting() {
    insertOneOrder().block();
    StepVerifier.create(
        r2dbcEntityTemplate
            .select(
                Order.class
            )
            .from("orders")
            .as(Order.class)
            .matching(
                Query
                    .query(
                        Criteria.where("type").is(OrderType.OFFLINE).and("status").is(OrderStatus.DELIVERED)
                    )
                    .limit(1)
                    .sort(Sort.unsorted())
            )
            .one()
    )
        .expectNextMatches(order -> order.getType() == OrderType.OFFLINE && order.getStatus() == OrderStatus.DELIVERED)
        .verifyComplete();
  }

  @Test
  void testSwitchOnEmpty_NoValue() {
    long nonExistentID = 432L;
    StepVerifier.create(orderRepository
        .findById(((long) Integer.MAX_VALUE))
        .switchIfEmpty(
            Mono.justOrEmpty(new Order().setId(nonExistentID))
        )
    ).expectNextMatches(order -> order.getId() == nonExistentID).verifyComplete();
  }

  @Test
  void testSwitchOnEmpty_RecordFound() {
    long nonExistentID = 432L;
    Order savedOrder = insertOneOrder().block();
    StepVerifier.create(orderRepository
        .findById(savedOrder.getId())
        .switchIfEmpty(
            Mono.justOrEmpty(new Order().setId(nonExistentID))
        )
    ).expectNextMatches(order -> Objects.equals(order.getId(), savedOrder.getId())).verifyComplete();
  }

  @NotNull
  private Mono<Order> insertOneOrder() {
    return r2dbcEntityTemplate
        .insert(Order.class)
        .into(SqlIdentifier.quoted("orders"))
        .using(new Order().setType(OrderType.OFFLINE).setStatus(OrderStatus.DELIVERED));
  }
}
