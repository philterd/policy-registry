package ai.philterd.philter.registry;

import ai.philterd.philter.registry.model.PolicyService;
import ai.philterd.philter.registry.model.services.GitPolicyService;
import ai.philterd.philter.registry.model.services.LocalPolicyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.util.Properties;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class PolicyRegistryApplication {

    private static final Logger LOGGER = LogManager.getLogger(PolicyRegistryApplication.class);

    public static void main(String[] args) {

        LOGGER.info("Starting Policy Registry...");

        SpringApplication.run(PolicyRegistryApplication.class, args);

    }

    @Bean
    public PolicyService policyService() throws Exception {

        final Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream("./application.properties")) {
            properties.load(is);
        }

        if(StringUtils.equalsIgnoreCase(properties.getProperty("policies.store", "local"), "local")) {

            return new LocalPolicyService(properties);

        } else if(StringUtils.equalsIgnoreCase(properties.getProperty("policies.store", "local"), "git")) {

            return new GitPolicyService();

        } else {

            LOGGER.warn("Invalid value for policies.store. Defaulting to local.");
            return new LocalPolicyService(properties);

        }

    }

}
