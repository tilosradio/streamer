package hu.tilos.streamer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;

@SpringBootApplication
@Configuration
public class StreamerApplication {

  public static void main(String[] args) {
    SpringApplication.run(StreamerApplication.class, args);
  }

  @Bean
  public HttpMessageConverters customConverter() {
    return new HttpMessageConverters(new ResourceRegionHttpMessageConverter());
  }
}
