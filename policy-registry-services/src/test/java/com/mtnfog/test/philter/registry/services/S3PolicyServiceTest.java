package com.mtnfog.test.philter.registry.services;

import ai.philterd.phileas.model.policy.Identifiers;
import ai.philterd.phileas.model.policy.Policy;
import ai.philterd.phileas.model.policy.filters.Age;
import ai.philterd.phileas.model.policy.filters.CreditCard;
import ai.philterd.phileas.model.policy.filters.strategies.rules.AgeFilterStrategy;
import ai.philterd.phileas.model.policy.filters.strategies.rules.CreditCardFilterStrategy;
import com.google.gson.Gson;
import com.mtnfog.philter.registry.model.PolicyService;
import com.mtnfog.philter.registry.model.services.S3PolicyService;
import io.findify.s3mock.S3Mock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class S3PolicyServiceTest {

    private static final Logger LOGGER = LogManager.getLogger(S3PolicyServiceTest.class);

    private final Gson gson = new Gson();

    private S3Mock api;

    @Before
    public void before() throws IOException {

        LOGGER.info("Starting S3 emulator.");

        final Path temporaryDirectory = Files.createTempDirectory("s3mock");

        api = new S3Mock.Builder().withPort(8001).withFileBackend(temporaryDirectory.toFile().getAbsolutePath()).build();
        api.start();

    }

    @After
    public void after() {

        api.shutdown();

    }

    @Test
    public void list() throws IOException {

        final Properties properties = new Properties();
        properties.setProperty("policies.store", "s3");
        properties.setProperty("policies.store.s3.bucket", "profiles");
        properties.setProperty("policies.store.s3.prefix", "/");

        final PolicyService policyService = new S3PolicyService(properties, true);

        policyService.save(gson.toJson(getPolicy("name1")));
        policyService.save(gson.toJson(getPolicy("name2")));
        final List<String> names = policyService.get();

        LOGGER.info("Found {} policies", names.size());

        Assert.assertTrue(names.size() == 2);
        Assert.assertTrue(names.contains("name1"));
        Assert.assertTrue(names.contains("name2"));

    }

    @Test
    public void getAll() throws IOException {

        final Properties properties = new Properties();
        properties.setProperty("policies.store", "s3");
        properties.setProperty("policies.store.s3.bucket", "profiles");
        properties.setProperty("policies.store.s3.prefix", "/");

        final PolicyService policyService = new S3PolicyService(properties, true);

        policyService.save(gson.toJson(getPolicy("name1")));
        policyService.save(gson.toJson(getPolicy("name2")));

        final Map<String, String> all = policyService.getAll();

        LOGGER.info("Found {} profiles", all.size());

        Assert.assertTrue(all.size() == 2);
        Assert.assertTrue(all.keySet().contains("name1"));
        Assert.assertTrue(all.keySet().contains("name2"));

    }

    @Test
    public void save() throws IOException {

        final String name = "default";

        final String profile = gson.toJson(getPolicy(name));

        final Properties properties = new Properties();
        properties.setProperty("policies.store", "s3");
        properties.setProperty("policies.store.s3.bucket", "profiles");
        properties.setProperty("policies.store.s3.prefix", "/");

        final PolicyService policyService = new S3PolicyService(properties, true);

        policyService.save(profile);

    }

    @Test
    public void get() throws IOException {

        final String name = "default";

        final String profile = gson.toJson(getPolicy(name));

        final Properties properties = new Properties();
        properties.setProperty("policies.store", "s3");
        properties.setProperty("policies.store.s3.bucket", "profiles");
        properties.setProperty("policies.store.s3.prefix", "/");

        final PolicyService policyService = new S3PolicyService(properties, true);

        policyService.save(profile);
        final String filterProfileJson = policyService.get(name);

        Assert.assertEquals(profile, filterProfileJson);

    }

    @Test
    public void delete() throws IOException {

        final String name = "default";

        final String profile = gson.toJson(getPolicy(name));

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("policies.store", "s3");
        properties.setProperty("policies.store.s3.bucket", "profiles");
        properties.setProperty("policies.store.s3.prefix", "/");

        final PolicyService policyService = new S3PolicyService(properties, true);

        policyService.save(profile);
        policyService.delete(name);

        final File file = new File(temp.toFile(), name + ".json");
        Assert.assertFalse(file.exists());

    }

    private Policy getPolicy(String name) {

        AgeFilterStrategy ageFilterStrategy = new AgeFilterStrategy();

        Age age = new Age();
        age.setAgeFilterStrategies(Arrays.asList(ageFilterStrategy));

        CreditCardFilterStrategy creditCardFilterStrategy = new CreditCardFilterStrategy();

        CreditCard creditCard = new CreditCard();
        creditCard.setCreditCardFilterStrategies(Arrays.asList(creditCardFilterStrategy));

        Identifiers identifiers = new Identifiers();

        identifiers.setAge(age);

        Policy policy = new Policy();
        policy.setName(name);
        policy.setIdentifiers(identifiers);

        return policy;

    }

}
