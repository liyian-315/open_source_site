package com.sdu.open.source.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OpenSourceSiteApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OpenSourceSiteApplication.class);
        String env = System.getenv("APP_ENV");
        if (env == null || env.trim().isEmpty()) {
            env = "dev";
        }
        app.setAdditionalProfiles(env);
        app.run(args);
    }
}
