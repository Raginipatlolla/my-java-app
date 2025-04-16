package com.innominds.nokia.ngds.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innominds.nokia.ngds.service.ProjectService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String FILE_NAME = "site-storage.json";
    private final File storageFile = new File("data/" + FILE_NAME);

    private Map<String, Map<String, Map<String, Object>>> storage = new HashMap<>();

    public ProjectServiceImpl() {
        loadStorage();
    }

    @Override
    public String storeSites(String projectName, List<Map<String, Object>> sitesList) {
        Map<String, Map<String, Object>> siteMap = new HashMap<>();

        for (Map<String, Object> siteWrapper : sitesList) {
            Map<String, Object> siteData = (Map<String, Object>) siteWrapper.get("sites");
            if (siteData != null) {
                String siteId = (String) siteData.get("site_id");
                if (siteId != null) {
                    siteData.remove("site_id");
                    siteMap.put(siteId, siteData);
                }
            }
        }
        storage.put(projectName, siteMap);
        saveStorage();
        return "Project '" + projectName + "' stored successfully with " + siteMap.size() + " site(s).";
    }

    @Override
    public Map<String, Map<String, Map<String, Object>>> getAllProjects() {
        return storage;
    }

    @Override
    public Map<String, Map<String, Object>> getProjectById(String projectId) {
        return storage.get(projectId);
    }

    @Override
    public Map<String, Object> getSiteByProjectAndSiteId(String projectId, String siteId) {
        Map<String, Map<String, Object>> project = storage.get(projectId);
        if (project != null) {
            return project.get(siteId);
        }
        return null;
    }

    private void saveStorage() {
        try {
            File dir = new File("data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            objectMapper.writeValue(storageFile, storage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStorage() {
        try {
            if (storageFile.exists()) {
                storage = objectMapper.readValue(storageFile, new TypeReference<>() {});
            } else {
                ClassLoader classLoader = getClass().getClassLoader();
                try (InputStream inputStream = classLoader.getResourceAsStream("data/" + FILE_NAME)) {
                    if (inputStream != null) {
                        storage = objectMapper.readValue(inputStream, new TypeReference<>() {});
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
