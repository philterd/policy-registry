package com.mtnfog.philter.registry.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FilterProfileService {

    List<String> get() throws IOException;
    String get(String filterProfileName) throws IOException;
    Map<String, String> getAll() throws IOException;
    void save(String filterProfileJson) throws IOException;
    void delete(String name) throws IOException;

}
