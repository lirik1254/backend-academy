package backend.academy.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@Import(TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class ExternalInitBase {
    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Container
    protected static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:6.2-alpine"))
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @Container
    protected static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.4.0");

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.base-url", () -> "http://localhost:8080");
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("app.github-token", () -> "test");
        registry.add("app.stackoverflow.key", () -> "test");
        registry.add("app.stackoverflow.access-token", () -> "test");
        registry.add("app.ai.api-key", () -> "test");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        registry.add("app.message-transport", () -> "HTTP");
        registry.add("resilience4j.circuitbreaker.instances.baseCircuitBreaker.slidingWindowType", () -> "COUNT_BASED");
        registry.add("resilience4j.circuitbreaker.instances.baseCircuitBreaker.failureRateThreshold", () -> "50");
        registry.add("resilience4j.circuitbreaker.instances.baseCircuitBreaker.waitDurationInOpenState", () -> "5s");
        registry.add(
                "resilience4j.circuitbreaker.instances.baseCircuitBreaker.permittedNumberOfCallsInHalfOpenState",
                () -> "2");

        // Retry
        //        registry.add("resilience4j.retry.instances.defaultRetry.max-attempts",
        //            () -> "3");
        registry.add("resilience4j.retry.instances.defaultRetry.wait-duration", () -> "500ms");
        registry.add("resilience4j.retry.instances.defaultRetry.exponential-backoff-multiplier", () -> "2");
        registry.add("resilience4j.retry.instances.defaultRetry.enable-exponential-backoff", () -> "true");
        registry.add("resilience4j.retry.instances.defaultRetry.retryable-status-pattern", () -> "5\\d{2}|429");

        // RateLimiter
        //        registry.add("resilience4j.ratelimiter.configs.defaultConfig.limit-for-period",
        //            () -> "3");
        registry.add("resilience4j.ratelimiter.configs.defaultConfig.timeout-duration", () -> "0s");
        registry.add("resilience4j.ratelimiter.configs.defaultConfig.limit-refresh-period", () -> "5s");
    }

    protected static void runMigrations(Connection conn) throws Exception {
        try (PostgresDatabase database = new PostgresDatabase()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE SCHEMA IF NOT EXISTS scrapper");
            }
            database.setConnection(new JdbcConnection(conn));
            database.setLiquibaseSchemaName("scrapper");
            database.setDefaultSchemaName("scrapper");
            Path migrationsPath = Paths.get("..", "migrations").toAbsolutePath().normalize();

            Scope.child("resourceAccessor", new DirectoryResourceAccessor(migrationsPath.toFile()), () -> {
                CommandScope updateCommand = new CommandScope("update")
                        .addArgumentValue("changelogFile", "changelog-master.xml")
                        .addArgumentValue("database", database);
                updateCommand.execute();
            });
        }
    }

    @BeforeAll
    protected static void beforeAll() {
        try (Connection conn =
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            runMigrations(conn);
            System.out.println("RUnMigrationCorrect");
        } catch (Exception e) {
            throw new RuntimeException("Initial migration failed", e);
        }
    }
}
