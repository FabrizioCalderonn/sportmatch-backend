package org.ncapas.canchitas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CanchitasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanchitasApplication.class, args);
    }

}