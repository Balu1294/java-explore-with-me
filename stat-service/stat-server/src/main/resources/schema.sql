DROP TABLE IF EXISTS stats;

create TABLE IF NOT EXISTS stats (
  id integer GENERATED ALWAYS AS IDENTITY Primary key UNIQUE,
  app VARCHAR(255) not null,
  uri VARCHAR(512) not null,
  ip VARCHAR(512) not null,
  created TIMESTAMP WITHOUT TIME ZONE
);