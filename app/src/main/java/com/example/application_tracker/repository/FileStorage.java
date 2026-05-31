package com.example.application_tracker.repository;

import android.content.Context;

import com.example.application_tracker.application.Application;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private static final String FILE_NAME = "applications.txt";

    private final Context context;

    public FileStorage(Context context) {
        this.context = context;
    }

    public void save(List<Application> applications) {

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
                )
        )) {

            for (Application app : applications) {

                writer.write(
                        app.getCompany() + ";" +
                                app.getAddress() + ";" +
                                app.getPosition() + ";" +
                                app.getDate().toString() + ";" +
                                app.getState()
                );

                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Application> load() {
        List<Application> applications = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.openFileInput(FILE_NAME))
        )) {

            String line;

            while ((line = reader.readLine()) != null) {

                try {
                    String[] parts = line.split(";", -1);

                    if (parts.length < 5) {
                        System.out.println("Skipping bad line: " + line);
                        continue;
                    }

                    LocalDate date;
                    try {
                        date = LocalDate.parse(parts[3]);
                    } catch (Exception e) {
                        date = LocalDate.now();
                    }

                    Application.posStates state;
                    try {
                        state = Application.posStates.valueOf(parts[4].trim());
                    } catch (Exception e) {
                        state = Application.posStates.PENDING;
                    }

                    Application app = new Application(
                            parts[0],
                            parts[1],
                            parts[2],
                            date,
                            state,
                            null,
                            null
                    );

                    applications.add(app);

                } catch (Exception e) {
                    System.out.println("FAILED LINE: " + line);
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            return applications;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return applications;
    }
}
