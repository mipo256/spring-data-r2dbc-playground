package org.example.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration(proxyBeanMethods = true)
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

  @Value("${r2dbc.host}")
  private String host;

  @Value("${r2dbc.port}")
  private Integer port;

  @Value("${r2dbc.password}")
  private String password;

  @Value("${r2dbc.db}")
  private String database;

  @Value("${r2dbc.username}")
  private String username;

  @Bean
  @Override
  public @NotNull ConnectionFactory connectionFactory() {
    return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
        .host(host)
        .port(port)
        .username(username)
        .database(database)
        .password(password)
        .build()
    );
  }

  @Bean
  public ReactiveTransactionManager reactiveTransactionManager() {
    return new R2dbcTransactionManager(connectionFactory());
  }

  @Bean
  public TransactionalOperator transactionalOperator() {
    return TransactionalOperator.create(reactiveTransactionManager());
  }
}
