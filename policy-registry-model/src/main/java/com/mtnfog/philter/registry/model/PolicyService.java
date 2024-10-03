package com.mtnfog.philter.registry.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PolicyService {

    /**
     * Gets the names of all policies.
     * @return
     * @throws IOException
     */
    List<String> get() throws IOException;

    /**
     * Gets the content of a policy.
     * @param filterProfileName
     * @return
     * @throws IOException
     */
    String get(String filterProfileName) throws IOException;

    /**
     * Get the names and content of all policies.
     * @return
     * @throws IOException
     */
    Map<String, String> getAll() throws IOException;

    /**
     * Saves a policy.
     * @param filterProfileJson
     * @throws IOException
     */
    void save(String filterProfileJson) throws IOException;

    /**
     * Deletes a policy.
     * @param name
     * @throws IOException
     */
    void delete(String name) throws IOException;

}
