package com.mtnfog.philter.registry.services;

import com.google.gson.Gson;
import com.mtnfog.philter.model.profile.FilterProfile;
import com.mtnfog.philter.model.services.FilterProfileService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticFilterProfileService implements FilterProfileService {

    private static final Logger LOGGER = LogManager.getLogger(StaticFilterProfileService.class);

    private FilterProfile filterProfile;
    private Gson gson;

    public StaticFilterProfileService(String filterProfileJson) {

        this.gson = new Gson();
        this.filterProfile = gson.fromJson(filterProfileJson, FilterProfile.class);

    }

    @Override
    public List<String> get() throws IOException {

        return Arrays.asList(filterProfile.getName());

    }

    @Override
    public String get(String name) throws IOException {

        return gson.toJson(filterProfile);

    }

    @Override
    public Map<String, FilterProfile> getAll() throws IOException {

        final Map<String, FilterProfile> filterProfiles = new HashMap<>();

        filterProfiles.put(filterProfile.getName(), filterProfile);

        return filterProfiles;

    }

    @Override
    public void save(String filterProfileJson) throws IOException {

        this.filterProfile = gson.fromJson(filterProfileJson, FilterProfile.class);

    }

    @Override
    public void delete(String name) throws IOException {

        throw new NotImplementedException("This function is not defined.");

    }

}
