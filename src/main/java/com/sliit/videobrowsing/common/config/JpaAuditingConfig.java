package com.sliit.videobrowsing.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Enables @CreatedDate / @LastModifiedDate on BaseEntity for every module. */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
