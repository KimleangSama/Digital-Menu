package com.keakimleang.digital_menu;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.data.r2dbc.repository.config.*;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = {
        "com.keakimleang.digital_menu.features.users.repos",
        "com.keakimleang.digital_menu.features.stores.repos",
})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
