package com.mtnfog.philter.registry;

import com.mtnfog.philter.registry.model.FilterProfileService;
import com.mtnfog.philter.registry.model.services.LocalFilterProfileService;
import com.mtnfog.philter.registry.model.services.S3FilterProfileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
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

        if(StringUtils.equalsIgnoreCase(properties.getProperty("filter.profiles.store", "local"), "local")) {

            return new LocalFilterProfileService(properties);

        } else if(StringUtils.equalsIgnoreCase(properties.getProperty("filter.profiles.store", "local"), "s3")) {

            return new S3FilterProfileService(properties, false);

        } else {

            LOGGER.warn("Invalid value for filter.profiles.store. Defaulting to local.");
            return new LocalFilterProfileService(properties);

        }

    }

}
