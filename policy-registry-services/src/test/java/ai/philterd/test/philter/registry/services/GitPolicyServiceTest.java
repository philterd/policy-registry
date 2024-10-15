package ai.philterd.test.philter.registry.services;

import ai.philterd.phileas.model.policy.Identifiers;
import ai.philterd.phileas.model.policy.Policy;
import ai.philterd.phileas.model.policy.filters.Age;
import ai.philterd.phileas.model.policy.filters.CreditCard;
import ai.philterd.phileas.model.policy.filters.strategies.rules.AgeFilterStrategy;
import ai.philterd.phileas.model.policy.filters.strategies.rules.CreditCardFilterStrategy;
import com.google.gson.Gson;
import ai.philterd.philter.registry.model.PolicyService;
import ai.philterd.philter.registry.model.services.GitPolicyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class GitPolicyServiceTest {

    private static final Logger LOGGER = LogManager.getLogger(GitPolicyServiceTest.class);

    private final Gson gson = new Gson();

    @Test
    public void list() throws Exception {

        final Path temp = Files.createTempDirectory("philter");

        final PolicyService policyService = new GitPolicyService(temp.toFile());

        policyService.save(gson.toJson(getFilterProfile("name1")));
        policyService.save(gson.toJson(getFilterProfile("name2")));
        final List<String> names = policyService.get();

        LOGGER.info("Found {} policies", names.size());

        Assert.assertEquals(2, names.size());
        Assert.assertTrue(names.contains("name1"));
        Assert.assertTrue(names.contains("name2"));

    }

    @Test
    public void getAll() throws Exception {

        final Path temp = Files.createTempDirectory("philter");

        final PolicyService policyService = new GitPolicyService(temp.toFile());

        policyService.save(gson.toJson(getFilterProfile("name1")));
        policyService.save(gson.toJson(getFilterProfile("name2")));

        final Map<String, String> all = policyService.getAll();

        Assert.assertEquals(2, all.size());
        Assert.assertTrue(all.containsKey("name1"));
        Assert.assertTrue(all.containsKey("name2"));

    }

    @Test
    public void save() throws Exception {

        final String name = "default";

        final String profile = gson.toJson(getFilterProfile(name));

        final Path temp = Files.createTempDirectory("philter");

        final PolicyService policyService = new GitPolicyService(temp.toFile());

        policyService.save(profile);

        final File file = new File(temp.toFile(), name + ".json");
        Assert.assertTrue(file.exists());

    }

    @Test
    public void get() throws Exception {

        final String name = "default";

        final String profile = gson.toJson(getFilterProfile(name));

        final Path temp = Files.createTempDirectory("philter");

        final PolicyService policyService = new GitPolicyService(temp.toFile());

        policyService.save(profile);
        final String filterProfileJson = policyService.get(name);

        Assert.assertEquals(profile, filterProfileJson);

    }

    @Test
    public void delete() throws Exception {

        final String name = "default";

        final String profile = gson.toJson(getFilterProfile(name));

        final Path temp = Files.createTempDirectory("philter");

        final PolicyService policyService = new GitPolicyService(temp.toFile());

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
