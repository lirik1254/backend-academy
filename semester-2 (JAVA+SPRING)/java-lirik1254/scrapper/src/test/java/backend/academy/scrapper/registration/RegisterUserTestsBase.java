package backend.academy.scrapper.registration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.TestConfig;
import backend.academy.scrapper.dbInitializeBase;
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

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestConfig.class)
public class RegisterUserTestsBase extends dbInitializeBase {

    @BeforeEach
    public void clearDatabase() {
        try (Connection conn =
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP SCHEMA public CASCADE");
                stmt.execute("CREATE SCHEMA public");
                stmt.execute("GRANT ALL ON SCHEMA public TO public");
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
