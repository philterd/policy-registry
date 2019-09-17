package com.mtnfog.philter.registry;

import com.mtnfog.philter.model.api.Status;
import com.mtnfog.philter.model.exceptions.api.BadRequestException;
import com.mtnfog.philter.registry.services.FilterProfileService;
import org.apache.commons.lang3.StringUtils;
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

    private static final String HEALTHY = "Healthy";

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

        filterProfileService.delete(filterProfileName);

    }

    @RequestMapping(value="/api/status", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Status> status() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Status(HEALTHY));

    }

}
