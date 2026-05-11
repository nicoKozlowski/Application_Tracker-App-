package com.tracker.service;

import com.tracker.model.Application;
import com.tracker.repository.FileStorage;

import java.util.ArrayList;
import java.util.List;

public class ApplicationService {
    private final List<Application> applications;
    private final FileStorage storage = new FileStorage();

    public ApplicationService() {
        this.applications = storage.load();
    }
    public void addApplication(Application app) {
        applications.add(app);
    }

    public void deleteApplication(Application app) {
        applications.remove(app);
    }

    public List<Application> getApplications() {
        return new ArrayList<>(applications);
    }

    public void changeState(Application app, Application.posStates newState) {
        app.setState(newState);
    }

    public List<Application> filterByState(Application.posStates state) {
        List<Application> filtered = new ArrayList<>();

        for (Application app : applications) {
            if (app.getStatus() == (state)) {
                filtered.add(app);
            }
        }
        return filtered;
    }

    public void saveAll() {
        storage.save(applications);
    }
}
