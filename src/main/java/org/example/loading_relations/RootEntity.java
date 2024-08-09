package org.example.loading_relations;

import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("root_entity")
public class RootEntity {

  @Id
  private Long id;

  private String name;

  @Transient
  private List<ChildEntity> children;
}
