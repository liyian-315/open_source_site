package com.sdu.open.source.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OpenSourceSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenSourceSiteApplication.class, args);
    }

}
