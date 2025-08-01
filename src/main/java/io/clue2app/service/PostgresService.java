package io.clue2app.service;

import io.clue2app.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "clue2app.postgres.enabled", havingValue = "true")
public class PostgresService {

    private static final Logger logger = LoggerFactory.getLogger(PostgresService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PostgresService() {

    }

    public String checkConnection() {
        String version = jdbcTemplate.queryForObject("SELECT version()", String.class);

        logger.info("Postgres version: {}", version);

        return version;
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public User create(final User user) {
        user.setId(UUID.randomUUID());
        user.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));

        userRepository.save(user);

        return user;
    }

    public User delete(final UUID id) {
        final User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setDeleted(true);
        user.setDeletedOn(Timestamp.valueOf(LocalDateTime.now()));

        userRepository.save(user);

        return user;
    }
}
