package com.example.application_tracker.repository;

import android.content.Context;

import com.example.application_tracker.application.Application;
import com.example.application_tracker.application.Contact;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
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

                String name = app.getContact() != null ? app.getContact().getName() : "";
                String mail = app.getContact() != null ? app.getContact().getMail() : "";
                String phone = app.getContact() != null ? app.getContact().getPhone() : "";

                String inDate = app.getInterviewDate() != null ? app.getInterviewDate().toString() : "";
                String inTime = app.getInterviewTime() != null ? app.getInterviewTime().toString() : "";
                String inAddress = app.getInterviewAddress() != null ? app.getInterviewAddress() : "";

                writer.write(
                        app.getCompany() + ";" +
                                app.getAddress() + ";" +
                                app.getPosition() + ";" +
                                app.getDate().toString() + ";" +
                                app.getState() + ";" +
                                inDate + ";" +
                                inTime + ";" +
                                inAddress + ";" +
                                name + ";" +
                                mail + ";" +
                                phone + ";" +
                                app.getDocumentPath()
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

                    LocalDate interviewDate = null;
                    if (parts.length > 5 && !parts[5].isEmpty() && !parts[5].equals("null")) {
                        try {
                            interviewDate = LocalDate.parse(parts[5]);
                        } catch (Exception e) {
                            interviewDate = null;
                        }
                    }

                    LocalTime interviewTime = null;
                    if (parts.length > 6 && !parts[6].isEmpty() && !parts[6].equals("null")) {
                        try {
                            interviewTime = LocalTime.parse(parts[6]);
                        } catch (Exception e) {
                            interviewTime = null;
                        }
                    }

                    String interviewAddress = null;
                    if (parts.length > 7 && !parts[7].isEmpty() && !parts[7].equals("null")) {
                        interviewAddress = parts[7];
                    }

                    String name = parts[8];
                    String mail = parts[9];
                    String phone = parts[10];

                    Contact contact = null;

                    if (!name.isEmpty() || !mail.isEmpty() || phone.isEmpty()) {
                        contact = new Contact(name, mail, phone);
                    }

                    String documentPath = parts[11];
                    if (documentPath == null || documentPath.trim().isEmpty() || documentPath.trim().equals("null")) {
                        documentPath = null;
                    }

                    Application app = new Application(
                            parts[0],
                            parts[1],
                            parts[2],
                            date,
                            state,
                            interviewDate,
                            interviewTime,
                            interviewAddress,
                            contact,
                            documentPath
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
