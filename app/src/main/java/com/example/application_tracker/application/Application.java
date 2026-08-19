package com.example.application_tracker.application;

import java.time.LocalDate;
import java.time.LocalTime;

public class Application {
    private String company;
    private String address;
    private String position;
    private LocalDate date;
    private posStates state;
    private LocalDate interviewDate;
    private LocalTime interviewTime;

    private String interviewAddress;
    private Contact contact;

    public Application (String comp,
                        String add,
                        String pos,
                        LocalDate d,
                        posStates stat,
                        LocalDate inDate,
                        LocalTime inTime,
                        String interviewAddress,
                        Contact cont) {
        this.company = comp;
        this.address = add;
        this.position = pos;
        this.date = d;
        this.state = stat;
        this.interviewDate = inDate;
        this.interviewTime = inTime;
        this.interviewAddress = interviewAddress;
        this.contact = cont;
    }

    public String getCompany() {
        return this.company;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPosition() {
        return this.position;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public posStates getState() {
        return this.state;
    }

    public LocalDate getInterviewDate() {
        return this.interviewDate;
    }

    public LocalTime getInterviewTime() {
        return this.interviewTime;
    }

    public String getInterviewAddress() {return this.interviewAddress; }

    public Contact getContact() {
        return this.contact;
    }

    public enum posStates {
        PENDING,
        INTERVIEW,
        CANCELLED,
        GHOSTED
    }

    public void setCompany(String comp) {
        this.company = comp;
    }

    public void setAddress(String addr) {
        this.address = addr;
    }

    public void setPosition(String pos) {
        this.position = pos;
    }

    public void setDate(LocalDate d) {
        this.date = d;
    }

    public void setState(posStates stat) {
        this.state = stat;
    }

    public void setInterviewDate(LocalDate inDate) {
        this.interviewDate = inDate;
    }

    public void setInterviewTime(LocalTime inTime) {
        this.interviewTime = inTime;
    }

    public void setInterviewAddress(String inAddress) {this.interviewAddress = inAddress; }

    public void setContact(Contact con) {
        this.contact = con;
    }



    @Override
    public String toString() {

        String base = String.format(
                "\n%s: \n| address: %s |\n| position: %s |\n| date: %s |\n",
                company,
                address,
                position,
                date
        );

        base += "| state: " + state + " |";
        if (state == posStates.INTERVIEW) {
            base += " date: " + interviewDate + " |\n time: " + interviewTime + " |";
        }

        if (contact != null && contact.getName() != null && !contact.getName().isBlank()) {
            base += "\n| contact: " + contact + " |";
        }

        if (interviewAddress != null) {
            base += "\n| interviewAddress: " + interviewAddress + " |";
        }

        return base;
    }
}
