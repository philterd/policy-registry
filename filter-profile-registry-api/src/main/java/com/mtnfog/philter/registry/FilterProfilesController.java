package com.mtnfog.philter.registry;

import com.mtnfog.philter.model.services.FilterProfileService;
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

    @Autowired
    private FilterProfileService filterProfileService;

    @RequestMapping(value="/api/profiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<String>> get() throws IOException {

        final List<String> filterProfileNames = filterProfileService.get();

        return ResponseEntity.status(HttpStatus.OK)
                .body(filterProfileNames);

    }

    @RequestMapping(value="/api/profiles", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> get(@RequestParam(value="p") String filterProfileName) throws IOException {

        final String filterProfile = filterProfileService.get(filterProfileName);

        return ResponseEntity.status(HttpStatus.OK)
                .body(filterProfile);

    }

    @RequestMapping(value="/api/profiles", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Void> save(@RequestBody String filterProfile) throws IOException {

        filterProfileService.save(filterProfile);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @RequestMapping(value="/api/profiles", method=RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Void> delete(@RequestParam(value="p") String filterProfileName) throws IOException {

        filterProfileService.delete(filterProfileName);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

}
