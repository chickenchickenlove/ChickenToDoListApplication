package todo.application;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;

@SpringBootTest
public class SpringBootBaseTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeAll
    public static void setUp() {

        GenericContainer mysqlContainer = new MySQLContainer<>()
                .withDatabaseName("test")
                .withUsername("test-user")
                .withPassword("test-pass")
                .withExposedPorts(3306);

        mysqlContainer.start();

        Integer mappedPort = mysqlContainer.getFirstMappedPort();
        System.setProperty("MYSQL_PORT", mappedPort.toString());
    }

    @BeforeEach
    void truncateAllTable() {
        truncateAllTable(jdbcTemplate);
    }

    public void truncateAllTable(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        List<String> tableNames = List.of(
                "article",
                "member",
                "member_article",
                "request_share_article",
                "visitor_view");

        tableNames.forEach(tableName ->
                jdbcTemplate.execute("TRUNCATE TABLE " + tableName));

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
