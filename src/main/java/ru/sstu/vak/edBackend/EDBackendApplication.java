package ru.sstu.vak.edBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EDBackendApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(EDBackendApplication.class, args);
    }
}
