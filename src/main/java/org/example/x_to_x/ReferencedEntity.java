package org.example.x_to_x;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table("single_select_referenced_entity")
public class ReferencedEntity {

  @Id
  private Long id;

  private String status;

}
