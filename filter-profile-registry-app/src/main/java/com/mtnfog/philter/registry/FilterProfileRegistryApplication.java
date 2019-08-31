package com.mtnfog.philter.registry;

import com.mtnfog.philter.registry.services.FilterProfileService;
import com.mtnfog.philter.registry.services.LocalFilterProfileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class FilterProfileRegistryApplication {

    private static final Logger LOGGER = LogManager.getLogger(FilterProfileRegistryApplication.class);

    public static void main(String[] args) {

        LOGGER.info("Starting Filter Profile Registry...");

        SpringApplication.run(FilterProfileRegistryApplication.class, args);

    }

    @Bean
    public FilterProfileService filterProfileService() throws IOException {

        final Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream("./application.properties")) {
            properties.load(is);
        }

        return new LocalFilterProfileService(properties);

    }

}
