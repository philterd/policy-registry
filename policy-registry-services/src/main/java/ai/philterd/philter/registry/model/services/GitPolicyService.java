package ai.philterd.philter.registry.model.services;

import ai.philterd.philter.registry.model.PolicyService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GitPolicyService implements PolicyService {

    private static final Logger LOGGER = LogManager.getLogger(GitPolicyService.class);

    private final Git git;
    final File directory;

    public GitPolicyService(final File directory) throws Exception {

        this.directory = directory;
        this.git = Git.init().setDirectory(directory).call();

    }

    public GitPolicyService() throws Exception {

        this.directory = new File(System.getProperty("user.dir") + "/git/");
        this.git = Git.init().setDirectory(directory).call();

    }

    @Override
    public List<String> get() throws Exception {

        final List<String> names = new LinkedList<>();

        // Read the policies from the file system.
        final Collection<File> files = FileUtils.listFiles(directory, new String[]{"json"}, false);

        for(final File file : files) {

            final String json = FileUtils.readFileToString(file, Charset.defaultCharset());

            final JSONObject object = new JSONObject(json);
            final String name = object.getString("name");

            names.add(name);

        }

        return names;

    }

    @Override
    public String get(String policyName) throws Exception {

        final File file = new File(directory, policyName + ".json");

        if(file.exists()) {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } else {
            throw new FileNotFoundException("Policy with name " + policyName + " does not exist.");
        }

    }

    @Override
    public Map<String, String> getAll() throws Exception {

        final Map<String, String> policies = new HashMap<>();

        // Read the policies from the file system.
        final Collection<File> files = FileUtils.listFiles(directory, new String[]{"json"}, false);
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
    public void save(String policyJson) throws Exception {

        final JSONObject object = new JSONObject(policyJson);
        final String name = object.getString("name");

        final File file = new File(directory, name + ".json");

        FileUtils.writeStringToFile(file, policyJson, Charset.defaultCharset());

        final AddCommand addCommand = git.add();
        addCommand.addFilepattern(file.getAbsolutePath()).call();

        final GitCommand<RevCommit> commit = git.commit().setMessage("Saving policy " + name);
        commit.call();

    }

    @Override
    public void delete(String policyName) throws Exception {

        final File file = new File(directory, policyName+ ".json");

        if(file.exists()) {

            if(file.delete()) {

                final AddCommand addCommand = git.add();
                addCommand.addFilepattern(file.getAbsolutePath()).call();

                final GitCommand<RevCommit> commit = git.commit().setMessage("Deleted policy " + policyName);
                commit.call();

            } else {
                throw new IOException("Unable to delete policy with name " + policyName + ".");
            }

        } else {
            throw new FileNotFoundException("Policy with name " + policyName + " does not exist.");
        }

    }

}
