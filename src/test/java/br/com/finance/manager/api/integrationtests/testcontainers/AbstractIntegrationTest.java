package br.com.finance.manager.api.integrationtests.testcontainers;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

import br.com.finance.manager.api.payloads.requests.LoginRequest;
import br.com.finance.manager.api.payloads.responses.LoginResponse;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected static final String CONTENT_TYPE_JSON = "application/json";

    protected String getJwtToken(String email, String password) {
        return "Bearer ".concat(
            given()
                .webAppContextSetup(webApplicationContext)
                .contentType(CONTENT_TYPE_JSON)
                .body(new LoginRequest(email, password))
            .when()
                .post("/api/v1/finance/auth/login")
            .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(LoginResponse.class)
                .getToken()
        );
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private static final int REDIS_PORT = 6379;
        private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"));
        private static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:alpine")).withExposedPorts(REDIS_PORT);

        private static void startContainers() {
            Startables.deepStart(Stream.of(postgresContainer, redisContainer)).join();
        }

        private static Map<String, String> createConfigurationMap() {
            return Map.of(
                "spring.datasource.url", postgresContainer.getJdbcUrl(),
                "spring.datasource.username", postgresContainer.getUsername(),
                "spring.datasource.password", postgresContainer.getPassword(),
                "spring.data.redis.host", redisContainer.getHost(),
                "spring.data.redis.port", redisContainer.getMappedPort(REDIS_PORT).toString());
        }

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked", "null" })
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();

            MapPropertySource testcontainers = new MapPropertySource("testcontainers", (Map) createConfigurationMap());

            applicationContext.getEnvironment().getPropertySources().addFirst(testcontainers);
        }
    }
}
