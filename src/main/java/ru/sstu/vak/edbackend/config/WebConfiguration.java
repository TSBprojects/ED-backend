package ru.sstu.vak.edbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${ed-app.security.origin}")
    private String origin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("POST", "GET")
                .allowedHeaders(HttpHeaders.CONTENT_TYPE)
                .allowedOrigins(origin);

    }

}
