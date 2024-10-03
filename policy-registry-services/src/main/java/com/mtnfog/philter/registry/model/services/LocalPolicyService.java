package com.mtnfog.philter.registry.model.services;

import com.mtnfog.philter.registry.model.PolicyService;
import com.mtnfog.philter.registry.model.exceptions.BadRequestException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Implementation of {@link PolicyService} that is backed by the local file system.
 */
public class LocalPolicyService implements PolicyService {

    private static final Logger LOGGER = LogManager.getLogger(LocalPolicyService.class);

    private final String policiesDirectory;

    public LocalPolicyService(Properties applicationProperties) {

        // Path to the policies.
        policiesDirectory = applicationProperties.getProperty("policies.store.local.directory", System.getProperty("user.dir") + "/policies/");
        LOGGER.info("Looking for policies in {}", policiesDirectory);

    }

    @Override
    public List<String> get() throws IOException {

        final List<String> names = new LinkedList<>();

        // Read the policies from the file system.
        final Collection<File> files = FileUtils.listFiles(new File(policiesDirectory), new String[]{"json"}, false);

        for(final File file : files) {

            final String json = FileUtils.readFileToString(file, Charset.defaultCharset());

            final JSONObject object = new JSONObject(json);
            final String name = object.getString("name");

            names.add(name);

        }

        return names;

    }

    @Override
    public String get(final String policy) throws IOException {

        final File file = new File(policiesDirectory, policy + ".json");

        if(file.exists()) {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } else {
            throw new FileNotFoundException("Policy with name " + policy + " does not exist.");
        }

    }

    @Override
    public Map<String, String> getAll() throws IOException {

        final Map<String, String> policies = new HashMap<>();

        // Read the policies from the file system.
        final Collection<File> files = FileUtils.listFiles(new File(policiesDirectory), new String[]{"json"}, false);
        LOGGER.info("Found {} policies", files.size());

        for(final File file : files) {

            LOGGER.info("Loading policy {}", file.getAbsolutePath());
            final String json = FileUtils.readFileToString(file, Charset.defaultCharset());

            final JSONObject object = new JSONObject(json);
            final String name = object.getString("name");

            policies.put(name, json);
            LOGGER.debug("Added policy named {}", name);

        }

        return policies;

    }

    @Override
    public void save(String policyJson) throws IOException {

        try {

            final JSONObject object = new JSONObject(policyJson);
            final String name = object.getString("name");

            final File file = new File(policiesDirectory, name + ".json");

            FileUtils.writeStringToFile(file, policyJson, Charset.defaultCharset());

        } catch (JSONException ex) {

            LOGGER.error("The provided policy is not valid.", ex);
            throw new BadRequestException("The provided policy is not valid.");

        }

    }

    @Override
    public void delete(String policyName) throws IOException {

        final File file = new File(policiesDirectory, policyName+ ".json");

        if(file.exists()) {
            file.delete();
        } else {
            throw new FileNotFoundException("Policy with name " + policyName + " does not exist.");
        }

    }

}
