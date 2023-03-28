package gigi.restskeleton.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppBeanConfiguration {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public OpenAPI customOpenAPI(
      @Value("${app.api-version}") String apiVersion,
      @Value("${app.project-url}") String projectUrl) {

    return new OpenAPI()
        .info(
            new Info()
                .title("Gigi Rest Skeleton API")
                .description("Simple Rest Spring Boot 3 skeleton application.")
                .version(apiVersion)
                .license(new License().name("Apache 2.0")))
        .externalDocs(new ExternalDocumentation().description("GitHub repository").url(projectUrl));
  }
}
