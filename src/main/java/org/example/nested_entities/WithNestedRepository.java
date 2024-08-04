package org.example.nested_entities;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface WithNestedRepository extends ReactiveCrudRepository<WithNestedEntity, Long> {

  Flux<WithNestedEntity> findAllByType(@Param("type") String type, Pageable pageable);

  @Query(value = "SELECT * FROM pagination_entity WHERE type = :type")
  Flux<WithNestedEntity> findAllByTypeString(@Param("type") String type, Pageable pageable);
}
