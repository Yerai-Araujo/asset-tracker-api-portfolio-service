package com.at.asset_tracker.portfolio.infrastructure.scheduling;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OutboxCleanupJob {

    private final JdbcTemplate jdbcTemplate;

    public OutboxCleanupJob(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Ejecutar cada día a las 3:00 AM
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanup() {
        deleteOldPublishedEvents();
    }

    // Ejecutar al arrancar la aplicación
    @PostConstruct
    public void cleanupOnStartup() {
        deleteOldPublishedEvents();
    }

    @Transactional
    private void deleteOldPublishedEvents() {
        int deletedEvents = jdbcTemplate.update("""
                    DELETE FROM outbox_events
                    WHERE createdat < now() - interval '7 days'
                """);

        log.info("Outbox cleanup: {} events deleted", deletedEvents);
    }

}