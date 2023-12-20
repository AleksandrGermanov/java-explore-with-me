package ru.practicum.ewmapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan("ru.practicum.endpointhitclient")
public class EwmApp {
    public static void main(String[] args) {
        SpringApplication.run(EwmApp.class);
    }
}
