package org.example.fluent_access_api;

import java.time.Instant;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "orders", schema = "public")
public class Order {

  @Id
  @EqualsAndHashCode.Include
  private Long id;

  private OrderType type;

  private OrderStatus status;

  @CreatedDate
  private Instant createdAt;
}
