package ai.philterd.philter.registry.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PolicyService {

    /**
     * Gets the names of all policies.
     * @return
     * @throws Exception
     */
    List<String> get() throws Exception;

    /**
     * Gets the content of a policy.
     * @param policyJson
     * @return
     * @throws Exception
     */
    String get(String policyJson) throws Exception;

    /**
     * Get the names and content of all policies.
     * @return
     * @throws Exception
     */
    Map<String, String> getAll() throws Exception;

    /**
     * Saves a policy.
     * @param policyJson
     * @throws Exception
     */
    void save(String policyJson) throws Exception;

    /**
     * Deletes a policy.
     * @param policyName
     * @throws Exception
     */
    void delete(String policyName) throws Exception;

}
