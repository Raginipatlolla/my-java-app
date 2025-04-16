package com.innominds.nokia.ngds.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public interface ProjectService {
    String storeSites(String projectName, List<Map<String, Object>> sitesList);
    Map<String, Map<String, Map<String, Object>>> getAllProjects();
    Map<String, Map<String, Object>> getProjectById(String projectId);
    Map<String, Object> getSiteByProjectAndSiteId(String projectId, String siteId);
}
