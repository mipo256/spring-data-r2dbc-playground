package org.example.loading_relations;

import java.time.LocalDate;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("child_entity")
public class ChildEntity {

  @Id
  private Long id;

  @Column("root_entity_id")
  private Long rootEntityId;

  @CreatedDate
  private LocalDate createdAt;
}
