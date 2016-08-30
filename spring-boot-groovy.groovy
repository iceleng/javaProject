package com.ice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class IceWebApp {

    @RequestMapping("/")
    public String greeting() {
        return "Hello World!";
    }

    @RequestMapping("/ice")
    public String ice() {
        return "Hello World , this is ice!";
    }

    @RequestMapping("/test")
    public String test() {
        return "Test";
    }
    /*
    public static void main(String[] args) {
        SpringApplication.run(IceWebApp.class, args);
    }
    */
}
