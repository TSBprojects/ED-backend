package ru.sstu.vak.edBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class EDBackendApplication extends SpringBootServletInitializer {

    private static final String APP_INFO =
                    "Available endpoints:\n" +
                    "\t(GET)/api/currentEmotion?json=false\n" +
                    "\t(POST)/api/recognizeImage\n" +
                    "Available params:\n" +
                    "\t--model-path\n" +
                    "\t--video-path\n" +
                    "\t--log-emotion";

    public static void main(String[] args) {
        SpringApplication.run(EDBackendApplication.class, args);
        System.out.println(APP_INFO);
    }
}
