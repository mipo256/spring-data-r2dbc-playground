package org.example.loading_relations;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import java.util.List;
import java.util.Objects;
import org.example.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class LoadingRelationTest extends AbstractIntegrationTest {

  @Autowired
  private RootEntityRepository rootEntityRepository;

  @Autowired
  private ChildEntityRepository childEntityRepository;

  @Autowired
  private ConnectionFactory connectionFactory;

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
    RootEntity rootEntity = rootEntityRepository.save(new RootEntity().setName(name)).block();

    childEntityRepository.saveAll(
        List.of(
            new ChildEntity().setRootEntityId(rootEntity.getId()),
            new ChildEntity().setRootEntityId(rootEntity.getId()),
            new ChildEntity().setRootEntityId(rootEntity.getId())
        )
    ).blockLast(); // fine in tests

    StepVerifier
        .create(
            rootEntityRepository
                .findById(rootEntity.getId())
                .zipWith(childEntityRepository.findByRootEntityId(rootEntity.getId()).collectList())
                .map(tuple -> {
                  RootEntity root = tuple.getT1();
                  root.setChildren(tuple.getT2());
                  return root;
                })
        )
        .expectNextMatches(re ->
            Objects.equals(re.getId(), rootEntity.getId()) &&
            Objects.equals(re.getName(), name) &&
            Objects.equals(re.getChildren().size(), 3)
        )
        .verifyComplete();
  }
}
