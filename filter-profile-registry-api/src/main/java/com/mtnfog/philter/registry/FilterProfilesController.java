package com.mtnfog.philter.registry;

import com.mtnfog.philter.model.api.Status;
import com.mtnfog.philter.registry.services.FilterProfileService;
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

    @RequestMapping(value="/api/profiles/{filterProfileName}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> getFilterProfile(@PathVariable String filterProfileName) throws IOException {

        final String filterProfile = filterProfileService.get(filterProfileName);

        return ResponseEntity.status(HttpStatus.OK)
                .body(filterProfile);

    }

    @RequestMapping(value="/api/profiles", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Void> save(@RequestBody String filterProfile) throws IOException {

        filterProfileService.save(filterProfile);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @RequestMapping(value="/api/profiles/{filterProfileName}", method=RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Void> delete(@PathVariable String filterProfileName) throws IOException {

        filterProfileService.delete(filterProfileName);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @RequestMapping(value="/api/status", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Status> status() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Status(HEALTHY));

    }

}
