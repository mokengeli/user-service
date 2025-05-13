package com.bacos.mokengeli.biloko;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.TimeZone;

@SpringBootApplication
@EnableDiscoveryClient
public class Application {
    public static void main(String[] args) {
        // Pour configurer de manière globale le fait que l'application aura des dates en UTC+1 pour gerer
        // le temps en local
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+01:00"));
        SpringApplication.run(Application.class, args);
    }
}