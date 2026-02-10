package de.paulm.four_in_a_row;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "de.paulm.four_in_a_row", // Mein Code
        "de.paulm.api" // Die generierten Controller/Interfaces
})
public class FourInARowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FourInARowApplication.class, args);
    }

}
