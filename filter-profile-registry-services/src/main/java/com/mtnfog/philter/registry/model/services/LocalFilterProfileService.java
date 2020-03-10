package com.mtnfog.philter.registry.model.services;

import com.mtnfog.philter.registry.model.FilterProfileService;
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
 * Implementation of {@link FilterProfileService} that is backed by the local file system.
 */
public class LocalFilterProfileService implements FilterProfileService {

    private static final Logger LOGGER = LogManager.getLogger(LocalFilterProfileService.class);

    private Properties applicationProperties;
    private String filterProfilesDirectory;

    public LocalFilterProfileService(Properties applicationProperties) {

        // Path to the filter profiles.
        filterProfilesDirectory = applicationProperties.getProperty("filter.profiles.store.local.directory", System.getProperty("user.dir") + "/profiles/");
        LOGGER.info("Looking for filter profiles in {}", filterProfilesDirectory);

    }

    @Override
    public List<String> get() throws IOException {

        final List<String> names = new LinkedList<>();

        // Read the filter profiles from the file system.
        final Collection<File> files = FileUtils.listFiles(new File(filterProfilesDirectory), new String[]{"json"}, false);

        for(final File file : files) {

            final String json = FileUtils.readFileToString(file, Charset.defaultCharset());

            final JSONObject object = new JSONObject(json);
            final String name = object.getString("name");

            names.add(name);

        }

        return names;

    }

    @Override
    public String get(String filterProfileName) throws IOException {

        final File file = new File(filterProfilesDirectory, filterProfileName + ".json");

        if(file.exists()) {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } else {
            throw new FileNotFoundException("Filter profile with name " + filterProfileName + " does not exist.");
        }

    }

    @Override
    public Map<String, String> getAll() throws IOException {

        final Map<String, String> filterProfiles = new HashMap<>();

        // Read the filter profiles from the file system.
        final Collection<File> files = FileUtils.listFiles(new File(filterProfilesDirectory), new String[]{"json"}, false);
        LOGGER.info("Found {} filter profiles", files.size());

        for(final File file : files) {

            LOGGER.info("Loading filter profile {}", file.getAbsolutePath());
            final String json = FileUtils.readFileToString(file, Charset.defaultCharset());

            final JSONObject object = new JSONObject(json);
            final String name = object.getString("name");

            filterProfiles.put(name, json);
            LOGGER.info("Added filter profile named {}", name);

        }

        return filterProfiles;

    }

    @Override
    public void save(String filterProfileJson) throws IOException {

        try {

            final JSONObject object = new JSONObject(filterProfileJson);
            final String name = object.getString("name");

            final File file = new File(filterProfilesDirectory, name + ".json");

            FileUtils.writeStringToFile(file, filterProfileJson, Charset.defaultCharset());

        } catch (JSONException ex) {

            LOGGER.error("The provided filter profile is not valid.", ex);
            throw new BadRequestException("The provided filter profile is not valid.");

        }

    }

    @Override
    public void delete(String name) throws IOException {

        final File file = new File(filterProfilesDirectory, name+ ".json");

        if(file.exists()) {
            file.delete();
        } else {
            throw new FileNotFoundException("Filter profile with name " + name + " does not exist.");
        }

    }

}
