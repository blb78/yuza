package com.skillogs.yuza.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Profile("!clever")
@Configuration
public class PropertiesLoader {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setLocations(new ClassPathResource[]{
                new ClassPathResource("application.properties"),
                new ClassPathResource("application-local.properties")
        });
        return c;

    }
}
