package com.mtnfog.test.philter.registry.services;

import com.google.gson.Gson;
import com.mtnfog.philter.model.profile.FilterProfile;
import com.mtnfog.philter.model.profile.Identifiers;
import com.mtnfog.philter.model.profile.filters.Age;
import com.mtnfog.philter.model.profile.filters.CreditCard;
import com.mtnfog.philter.model.profile.filters.strategies.rules.AgeFilterStrategy;
import com.mtnfog.philter.model.profile.filters.strategies.rules.CreditCardFilterStrategy;
import com.mtnfog.philter.registry.services.FilterProfileService;
import com.mtnfog.philter.registry.services.LocalFilterProfileService;
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

public class LocalFilterProfileServiceTest {

    private static final Logger LOGGER = LogManager.getLogger(LocalFilterProfileServiceTest.class);

    private Gson gson = new Gson();

    @Test
    public void list() throws IOException {

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("filter.profiles.directory", temp.toFile().getAbsolutePath());

        final FilterProfileService filterProfileService = new LocalFilterProfileService(properties);

        filterProfileService.save(gson.toJson(getFilterProfile("name1")));
        filterProfileService.save(gson.toJson(getFilterProfile("name2")));
        final List<String> names = filterProfileService.get();

        Assert.assertTrue(names.size() == 2);
        Assert.assertTrue(names.contains("name1"));
        Assert.assertTrue(names.contains("name2"));

    }

    @Test
    public void getAll() throws IOException {

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("filter.profiles.directory", temp.toFile().getAbsolutePath());

        final FilterProfileService filterProfileService = new LocalFilterProfileService(properties);

        filterProfileService.save(gson.toJson(getFilterProfile("name1")));
        filterProfileService.save(gson.toJson(getFilterProfile("name2")));

        final Map<String, FilterProfile> all = filterProfileService.getAll();

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
        properties.setProperty("filter.profiles.directory", temp.toFile().getAbsolutePath());

        final FilterProfileService filterProfileService = new LocalFilterProfileService(properties);

        filterProfileService.save(profile);

        final File file = new File(temp.toFile(), name + ".json");
        Assert.assertTrue(file.exists());

    }

    @Test
    public void get() throws IOException {

        final String name = "default";

        final String profile = gson.toJson(getFilterProfile(name));

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("filter.profiles.directory", temp.toFile().getAbsolutePath());

        final FilterProfileService filterProfileService = new LocalFilterProfileService(properties);

        filterProfileService.save(profile);
        final String filterProfileJson = filterProfileService.get(name);

        Assert.assertEquals(profile, filterProfileJson);

    }

    @Test
    public void delete() throws IOException {

        final String name = "default";

        final String profile = gson.toJson(getFilterProfile(name));

        final Path temp = Files.createTempDirectory("philter");

        final Properties properties = new Properties();
        properties.setProperty("filter.profiles.directory", temp.toFile().getAbsolutePath());

        final FilterProfileService filterProfileService = new LocalFilterProfileService(properties);

        filterProfileService.save(profile);
        filterProfileService.delete(name);

        final File file = new File(temp.toFile(), name + ".json");
        Assert.assertFalse(file.exists());

    }

    private FilterProfile getFilterProfile(String name) {

        AgeFilterStrategy ageFilterStrategy = new AgeFilterStrategy();

        Age age = new Age();
        age.setAgeFilterStrategy(ageFilterStrategy);

        CreditCardFilterStrategy creditCardFilterStrategy = new CreditCardFilterStrategy();

        CreditCard creditCard = new CreditCard();
        creditCard.setCreditCardFilterStrategy(creditCardFilterStrategy);

        Identifiers identifiers = new Identifiers();

        identifiers.setAge(age);

        FilterProfile filterProfile = new FilterProfile();
        filterProfile.setName(name);
        filterProfile.setIdentifiers(identifiers);

        return filterProfile;

    }

}
