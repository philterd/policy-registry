package com.mtnfog.philter.registry.services;

import com.google.gson.Gson;
import com.mtnfog.philter.model.profile.FilterProfile;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class LocalFilterProfileService implements FilterProfileService {

    private static final Logger LOGGER = LogManager.getLogger(LocalFilterProfileService.class);

    private Properties applicationProperties;
    private String filterProfilesDirectory;
    private Gson gson;

    public LocalFilterProfileService(Properties applicationProperties) {

        this.applicationProperties = applicationProperties;
        this.gson = new Gson();

        // Path to the filter profiles.
        filterProfilesDirectory = applicationProperties.getProperty("filter.profiles.directory", System.getProperty("user.dir") + "/profiles/");
        LOGGER.info("Looking for filter profiles in {}", filterProfilesDirectory);

    }

    @Override
    public List<String> get() throws IOException {

        final List<String> names = new LinkedList<>();

        // Read the filter profiles from the file system.
        final Collection<File> files = FileUtils.listFiles(new File(filterProfilesDirectory), new String[]{"json"}, false);

        for(final File file : files) {

            final String json = FileUtils.readFileToString(file, Charset.defaultCharset());
            final FilterProfile filterProfile = gson.fromJson(json, FilterProfile.class);
            names.add(filterProfile.getName());

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
    public Map<String, FilterProfile> getAll() throws IOException {

        final Map<String, FilterProfile> filterProfiles = new HashMap<>();

        // Read the filter profiles from the file system.
        final Collection<File> files = FileUtils.listFiles(new File(filterProfilesDirectory), new String[]{"json"}, false);
        LOGGER.info("Found {} filter profiles", files.size());

        for(final File file : files) {

            LOGGER.info("Loading filter profile {}", file.getAbsolutePath());
            final String json = FileUtils.readFileToString(file, Charset.defaultCharset());
            final FilterProfile filterProfile = gson.fromJson(json, FilterProfile.class);
            filterProfiles.put(filterProfile.getName(), filterProfile);
            LOGGER.info("Added filter profile named {}", filterProfile.getName());

        }

        return filterProfiles;

    }

    @Override
    public void save(String filterProfileJson) throws IOException {

        final FilterProfile filterProfile = gson.fromJson(filterProfileJson, FilterProfile.class);

        final File file = new File(filterProfilesDirectory, filterProfile.getName() + ".json");

        FileUtils.writeStringToFile(file, filterProfileJson, Charset.defaultCharset());

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
