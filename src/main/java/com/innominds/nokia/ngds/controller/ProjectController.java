package com.innominds.nokia.ngds.controller;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.innominds.nokia.ngds.service.ProjectService;
import com.innominds.nokia.ngds.validator.Validator;
import io.micrometer.core.instrument.config.validate.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private Validator validator;

    @PostMapping(value="/store", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> storeSites(@RequestBody Map<String,Object> requestBody) throws IOException {

        List<String> errors;
        try {
            errors = Validator.validateJsonAgainstSchema(requestBody);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error loading schema: " + e.getMessage()));
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", errors));
        }
       String projectName = (String) requestBody.get("project");
        List<Map<String, Object>> sitesList = (List<Map<String, Object>>) requestBody.get("sites");

        if (projectName == null ) {
            return ResponseEntity.badRequest().body("Project name is Required");
        }
        String message = projectService.storeSites(projectName, sitesList);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getAll() {
        Map<String, Map<String, Map<String, Object>>> allProjects = projectService.getAllProjects();
        if (allProjects.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No projects or site data found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(allProjects);
    }

    @GetMapping("/project/{project_id}/sites")
    public ResponseEntity<?> getProject(@PathVariable("project_id") String projectId) {
        Map<String, Map<String, Object>> project = projectService.getProjectById(projectId);

        if (project == null || project.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Project ID " + projectId + "not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(project);
    }

    @GetMapping("/project/{project_id}/sites/{site_id}")
    public ResponseEntity<?> getSite(@PathVariable("project_id") String projectId,
                                     @PathVariable("site_id") String siteId) {
        Map<String, Object> site = projectService.getSiteByProjectAndSiteId(projectId, siteId);

        if (site == null || site.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Site ID " + siteId + " could not found in the project " + projectId + ".");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(site);
    }
}
