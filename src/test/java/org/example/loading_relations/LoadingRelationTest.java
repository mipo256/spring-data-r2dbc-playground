package org.example.loading_relations;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import java.util.List;
import org.example.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    RootEntity rootEntity = rootEntityRepository.save(new RootEntity().setName("SomeName")).block();

    childEntityRepository.saveAll(
        List.of(
            new ChildEntity().setRootEntityId(rootEntity.getId()),
            new ChildEntity().setRootEntityId(rootEntity.getId()),
            new ChildEntity().setRootEntityId(rootEntity.getId())
        )
    ).blockLast(); // fine in tests
  }
}
