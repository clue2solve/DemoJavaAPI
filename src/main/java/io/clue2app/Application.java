package io.clue2app;

import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

@EnableScheduling
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class/*, FlywayAutoConfiguration.class*/ })
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		final Map<String, String> vars = System.getenv();
		vars.forEach((key, value) -> logger.info(String.format("%s => %s", key, value)));

		SpringApplication.run(Application.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		return mapper;
	}

	@Bean
	public C2aConfig config() throws JsonProcessingException {
		return new C2aConfig();
	}

	@Bean
	public WebIdentityTokenCredentialsProvider webIdentityTokenCredentialsProvider() {
		return WebIdentityTokenCredentialsProvider.builder()
				.roleArn(System.getenv("AWS_ROLE_ARN"))
				.webIdentityTokenFile(System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE"))
				.build();
	}

}