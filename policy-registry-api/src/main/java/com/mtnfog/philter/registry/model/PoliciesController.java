package com.mtnfog.philter.registry.model;

import com.mtnfog.philter.registry.model.exceptions.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

@Controller
public class PoliciesController {

    private static final Logger LOGGER = LogManager.getLogger(PoliciesController.class);

    private static final String HEALTHY = "Healthy";
    private static final String UNHEALTHY = "Unhealthy";

    @Autowired
    private PolicyService policyService;

    @RequestMapping(value="/api/policies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<String>> getPolicyNames() throws IOException {

        final List<String> policyNames = policyService.get();

        return ResponseEntity.status(HttpStatus.OK)
                .body(policyNames);

    }

    @RequestMapping(value="/api/policies/{policyName}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getPolicy(@PathVariable String policyName) throws IOException {

        if(StringUtils.isEmpty(policyName)) {
            throw new BadRequestException("The policy name is missing.");
        }

        final String policy = policyService.get(policyName);

        return ResponseEntity.status(HttpStatus.OK)
                .body(policy);

    }

    @RequestMapping(value="/api/policies", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody String policy) throws IOException {

        policyService.save(policy);

    }

    @RequestMapping(value="/api/policies/{policyName}", method=RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String policyName) throws IOException {

        if(StringUtils.isEmpty(policyName)) {
            throw new BadRequestException("The policy name is missing.");
        }

        policyService.delete(policyName);

    }

    @RequestMapping(value="/api/status", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Status> status() throws IOException {

        try {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Status(HEALTHY));

        } catch (Exception ex) {

            LOGGER.error("Unable to determine count of policies.", ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Status(UNHEALTHY));

        }

    }

}
