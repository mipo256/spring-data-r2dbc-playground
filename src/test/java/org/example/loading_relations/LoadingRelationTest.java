package org.example.loading_relations;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import java.util.List;
import java.util.Objects;
import org.example.AbstractIntegrationTest;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class LoadingRelationTest extends AbstractIntegrationTest {

  @Autowired
  private RootEntityRepository rootEntityRepository;

  @Autowired
  private ChildEntityRepository childEntityRepository;

  @Autowired
  private ConnectionFactory connectionFactory;

  @Autowired
  private TransactionalOperator transactionalOperator;

  @BeforeEach
  void setUp() {
    Connection connection = Mono.from(connectionFactory.create()).block();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("org/example/loading_relations/LoadingRelationTest.sql")).block();
  }

  @AfterEach
  void tearDown() {
    Connection connection = Mono.from(connectionFactory.create()).block();
    ScriptUtils.executeSqlScript(connection, new ClassPathResource("cleanup.sql")).block();
  }

  @Test
  void loadingRootWithRelations() {
    String name = "SomeName";
    ChildEntity lastChildInChain = insertRootWithRelations(name);

    StepVerifier
        .create(
            rootEntityRepository
                .findById(lastChildInChain.getRootEntityId())
                .zipWith(childEntityRepository.findByRootEntityId(lastChildInChain.getRootEntityId()).collectList())
                .map(tuple -> {
                  RootEntity root = tuple.getT1();
                  root.setChildren(tuple.getT2());
                  return root;
                })
                .as(transactionalOperator::transactional)
        )
        .expectNextMatches(re ->
            Objects.equals(re.getId(), lastChildInChain.getRootEntityId()) &&
            Objects.equals(re.getName(), name) &&
            Objects.equals(re.getChildren().size(), 3)
        )
        .verifyComplete();
  }

  @Test
  void testDeletionTheRightWay() {
    ChildEntity child = insertRootWithRelations("SomeName");

    StepVerifier
        .create(childEntityRepository
            .deleteAllByRootEntityId(child.getRootEntityId())
            .flatMap(c -> rootEntityRepository.deleteById(child.getRootEntityId()))
            .as(transactionalOperator::transactional)
        )
        .verifyComplete();
  }

  private @Nullable ChildEntity insertRootWithRelations(String name) {
    ChildEntity lastChildInChain = transactionalOperator.execute(status ->
        rootEntityRepository
            .save(new RootEntity().setName(name))
            .flux()
            .flatMap(saved -> childEntityRepository.saveAll(
                List.of(
                    new ChildEntity().setRootEntityId(saved.getId()),
                    new ChildEntity().setRootEntityId(saved.getId()),
                    new ChildEntity().setRootEntityId(saved.getId())
                )
            ).doOnNext(childEntity -> {
              saved.getChildren().add(childEntity);
            }))
    ).blockLast(); // fine in tests
    return lastChildInChain;
  }
}
