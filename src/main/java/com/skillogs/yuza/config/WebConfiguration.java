package com.skillogs.yuza.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skillogs.yuza.net.http.ValveFilter;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

import static java.util.Arrays.asList;

@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter implements ServletContextInitializer {

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        return builder;
    }

    @Bean
    @Profile("local")
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowedMethods(asList("PUT", "POST", "GET", "OPTIONS", "DELETE"));
        config.setAllowedHeaders(asList("x-requested-with", "content-type", "Authorization", "X-CSRF-Token"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext
                .addFilter("valveFilter", new ValveFilter())
                .addMappingForUrlPatterns(
                        EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR),
                        true,
                        "/*");
    }
}
