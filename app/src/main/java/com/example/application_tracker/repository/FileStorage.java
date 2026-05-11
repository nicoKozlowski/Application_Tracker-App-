package com.tracker.repository;

import com.tracker.model.Application;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final String FILE_NAME = "applications.txt";

    public void save(List<Application> applications) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

            for (Application app : applications) {
                writer.write(
                        app.getCompany() + ";" +
                                app.getAddress() + ";" +
                                app.getPosition() + ";" +
                                app.getDate() + ";" +
                                app.getStatus() + ";" +
                                app.getContact() + ";"
                );

                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save:" + e.getMessage());
        }
    }

    public List<Application> load() {

        List<Application> applications = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) return applications;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(";");

                Application app = new Application (
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        Application.posStates.valueOf(parts[4]),
                        parts[5]
                );

                applications.add(app);
            }
        } catch (IOException e) {
            System.out.println("failed to load:" + e.getMessage());
        }
        return applications;
    }
}
