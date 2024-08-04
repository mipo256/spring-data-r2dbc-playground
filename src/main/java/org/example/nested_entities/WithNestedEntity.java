package org.example.nested_entities;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class WithNestedEntity {

  @Id
  private Long id;

  private String type;

  @CreatedDate
  private Instant createdAt;

  @Embedded(onEmpty = OnEmpty.USE_NULL)
  private NestedEntity nestedEntity;

  @Data
  static class NestedEntity {
    private String anything;
  }
}
