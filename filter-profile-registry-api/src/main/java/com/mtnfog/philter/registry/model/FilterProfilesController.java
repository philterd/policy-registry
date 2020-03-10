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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class FilterProfilesController {

    private static final Logger LOGGER = LogManager.getLogger(FilterProfilesController.class);

    private static final String HEALTHY = "Healthy";
    private static final String UNHEALTHY = "Unhealthy";

    @Autowired
    private FilterProfileService filterProfileService;

    @RequestMapping(value="/api/profiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<String>> getFilterProfileNames() throws IOException {

        final List<String> filterProfileNames = filterProfileService.get();

        return ResponseEntity.status(HttpStatus.OK)
                .body(filterProfileNames);

    }

    @RequestMapping(value="/api/profiles/{filterProfileName}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getFilterProfile(@PathVariable String filterProfileName) throws IOException {

        if(StringUtils.isEmpty(filterProfileName)) {
            throw new BadRequestException("The filter profile name is missing.");
        }

        final String filterProfile = filterProfileService.get(filterProfileName);

        return ResponseEntity.status(HttpStatus.OK)
                .body(filterProfile);

    }

    @RequestMapping(value="/api/profiles", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody String filterProfile) throws IOException {

        filterProfileService.save(filterProfile);

    }

    @RequestMapping(value="/api/profiles/{filterProfileName}", method=RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String filterProfileName) throws IOException {

        if(StringUtils.isEmpty(filterProfileName)) {
            throw new BadRequestException("The filter profile name is missing.");
        }

        filterProfileService.delete(filterProfileName);

    }

    @RequestMapping(value="/api/status", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Status> status() throws IOException {

        try {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Status(HEALTHY));

        } catch (Exception ex) {

            LOGGER.error("Unable to determine count of filter profiles.", ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Status(UNHEALTHY));

        }

    }

}
