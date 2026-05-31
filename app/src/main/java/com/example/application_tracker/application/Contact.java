package com.example.application_tracker.application;

public class Contact {
    private String name;
    private String mail;
    private String phone;

    public Contact(String n, String m, String p) {
        this.name = n;
        this.mail = m;
        this.phone = p;
    }

    public String getName() {
        return this.name;
    }

    public String getMail() {
        return this.mail;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {

        if (name == null || name.isBlank()) {
            return "";
        }

        String result = name;

        if (mail != null && !mail.isBlank()) {
            result += " | " + mail;
        }

        if (phone != null && !phone.isBlank()) {
            result += " | " + phone;
        }

        return result;
    }
}
