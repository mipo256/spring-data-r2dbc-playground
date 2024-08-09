package org.example.loading_relations;

import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ChildEntityRepository extends ReactiveCrudRepository<ChildEntity, Long> {

  Flux<ChildEntity> findByRootEntityId(@Param("rootEntityId") Long rootEntityId);
}
