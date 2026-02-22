package com.example.library.config;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.library.repository")
@EnableMongoRepositories(basePackages = "com.example.library.mongo")
public class RepositoryConfig {
}
