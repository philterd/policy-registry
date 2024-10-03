package com.mtnfog.test.philter.registry.services;

import ai.philterd.phileas.model.policy.Identifiers;
import ai.philterd.phileas.model.policy.Policy;
import ai.philterd.phileas.model.policy.filters.Age;
import ai.philterd.phileas.model.policy.filters.CreditCard;
import ai.philterd.phileas.model.policy.filters.strategies.rules.AgeFilterStrategy;
import ai.philterd.phileas.model.policy.filters.strategies.rules.CreditCardFilterStrategy;
import com.google.gson.Gson;
import com.mtnfog.philter.registry.model.PolicyService;
import com.mtnfog.philter.registry.model.services.LocalPolicyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LocalPolicyServiceTest {

    private static final Logger LOGGER = LogManager.getLogger(LocalPolicyServiceTest.class);

    private final Gson gson = new Gson();

    @Test
    public void list() throws IOException {

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("policies.store.local.directory", temp.toFile().getAbsolutePath());

        final PolicyService policyService = new LocalPolicyService(properties);

        policyService.save(gson.toJson(getFilterProfile("name1")));
        policyService.save(gson.toJson(getFilterProfile("name2")));
        final List<String> names = policyService.get();

        LOGGER.info("Found {} policies", names.size());

        Assert.assertEquals(2, names.size());
        Assert.assertTrue(names.contains("name1"));
        Assert.assertTrue(names.contains("name2"));

    }

    @Test
    public void getAll() throws IOException {

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("policies.store.local.directory", temp.toFile().getAbsolutePath());

        final PolicyService policyService = new LocalPolicyService(properties);

        policyService.save(gson.toJson(getFilterProfile("name1")));
        policyService.save(gson.toJson(getFilterProfile("name2")));

        final Map<String, String> all = policyService.getAll();

        Assert.assertTrue(all.size() == 2);
        Assert.assertTrue(all.keySet().contains("name1"));
        Assert.assertTrue(all.keySet().contains("name2"));

    }

    @Test
    public void save() throws IOException {

        final String name = "default";

        final String profile = gson.toJson(getFilterProfile(name));

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("policies.store.local.directory", temp.toFile().getAbsolutePath());

        final PolicyService policyService = new LocalPolicyService(properties);

        policyService.save(profile);

        final File file = new File(temp.toFile(), name + ".json");
        Assert.assertTrue(file.exists());

    }

    @Test
    public void get() throws IOException {

        final String name = "default";

        final String profile = gson.toJson(getFilterProfile(name));

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("policies.store.local.directory", temp.toFile().getAbsolutePath());

        final PolicyService policyService = new LocalPolicyService(properties);

        policyService.save(profile);
        final String filterProfileJson = policyService.get(name);

        Assert.assertEquals(profile, filterProfileJson);

    }

    @Test
    public void delete() throws IOException {

        final String name = "default";

        final String profile = gson.toJson(getFilterProfile(name));

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("policies.store.local.directory", temp.toFile().getAbsolutePath());

        final PolicyService policyService = new LocalPolicyService(properties);

        policyService.save(profile);
        policyService.delete(name);

        final File file = new File(temp.toFile(), name + ".json");
        Assert.assertFalse(file.exists());

    }

    private Policy getFilterProfile(String name) {

        AgeFilterStrategy ageFilterStrategy = new AgeFilterStrategy();

        Age age = new Age();
        age.setAgeFilterStrategies(List.of(ageFilterStrategy));

        CreditCardFilterStrategy creditCardFilterStrategy = new CreditCardFilterStrategy();

        CreditCard creditCard = new CreditCard();
        creditCard.setCreditCardFilterStrategies(List.of(creditCardFilterStrategy));

        Identifiers identifiers = new Identifiers();

        identifiers.setAge(age);

        Policy policy = new Policy();
        policy.setName(name);
        policy.setIdentifiers(identifiers);

        return policy;

    }

}
