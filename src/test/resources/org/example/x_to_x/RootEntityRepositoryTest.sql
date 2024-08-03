CREATE TABLE IF NOT EXISTS single_select_root_entity(
  id BIGSERIAL PRIMARY KEY,
  type TEXT,
  created_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS single_select_referenced_entity(
  id BIGSERIAL PRIMARY KEY,
  status TEXT,
  root_entity_id BIGINT
);