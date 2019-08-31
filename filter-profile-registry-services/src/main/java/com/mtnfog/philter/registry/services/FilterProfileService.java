package com.mtnfog.philter.registry.services;

import com.mtnfog.philter.model.profile.FilterProfile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FilterProfileService {

    List<String> get() throws IOException;
    String get(String filterProfileName) throws IOException;
    Map<String, FilterProfile> getAll() throws IOException;
    void save(String filterProfileJson) throws IOException;
    void delete(String name) throws IOException;

}
