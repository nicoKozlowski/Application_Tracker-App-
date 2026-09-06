package com.example.application_tracker.service;

import com.example.application_tracker.application.Application;
import com.example.application_tracker.repository.FileStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import android.content.Context;

public class ApplicationService {
    private final List<Application> applications;
    private final FileStorage storage;

    public ApplicationService(Context context) {
        this.storage = new FileStorage(context);
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
            if (app.getState() == (state)) {
                filtered.add(app);
            }
        }
        return filtered;
    }

    public void updateGhostedApplications() {

        for (Application app : applications) {

            if (app.getState() == Application.posStates.PENDING) {

                if (app.getDate().plusWeeks(8).isBefore(LocalDate.now())) {

                    app.setState(Application.posStates.GHOSTED);
                }
            }
        }
    }
    public void saveAll() {
        storage.save(applications);
    }
}
