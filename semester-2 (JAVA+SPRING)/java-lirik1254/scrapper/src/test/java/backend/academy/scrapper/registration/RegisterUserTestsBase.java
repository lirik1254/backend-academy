package backend.academy.scrapper.registration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.ExternalInitBase;
import backend.academy.scrapper.TestConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        properties = {
            "resilience4j.retry.instances.defaultRetry.max-attempts=1",
            "resilience4j.ratelimiter.configs.defaultConfig.limit-for-period=3000"
        })
@AutoConfigureMockMvc
@Testcontainers
@Import(TestConfig.class)
public class RegisterUserTestsBase extends ExternalInitBase {

    @BeforeEach
    public void clearDatabase() {
        try (Connection conn =
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP SCHEMA scrapper CASCADE");
                stmt.execute("CREATE SCHEMA scrapper");
            }

            runMigrations(conn);
        } catch (Exception e) {
            throw new RuntimeException("Database cleanup failed", e);
        }
    }

    protected void performRegisterUserRequest(Long chatId) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tg-chat/" + chatId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
