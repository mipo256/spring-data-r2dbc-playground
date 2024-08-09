package org.example.x_to_x;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table("single_select_root_entity")
public class SingleSelectRootEntity {

  @Id
  private Long id;

  @CreatedDate
  private OffsetDateTime createdAt;

  private String type;

  @Column(value = "root_entity_id") // It actually does not matter what annotation we'll put in there
  private List<ReferencedEntity> referencedEntities;
}
