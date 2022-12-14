package com.argusoft.who.emcare.web;

import com.argusoft.who.emcare.web.language.service.impl.LanguageServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = "com.argusoft.who.emcare.web")
@EntityScan(basePackages = "com.argusoft.who.emcare.web")
public class EmcareWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EmcareWebApplication.class, args);

//        Translate Newly Added Label On Server Up
        context.getBean(LanguageServiceImpl.class).translateNewlyAddedLabels();
    }

}