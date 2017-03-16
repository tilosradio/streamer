package hu.tilos.streamer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;

import javax.servlet.*;
import java.io.IOException;

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

  @Configuration
  static class Config {
    @Autowired
    CounterService counterService;

    @Bean
    Filter counterFilter() {
      return new Filter() {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
          try {
            counterService.increment("active-http-sessions");
            filterChain.doFilter(servletRequest, servletResponse);
          } finally {
            counterService.decrement("active-http-sessions");
          }
        }

        @Override
        public void destroy() {
        }
      };
    }

  }
}
