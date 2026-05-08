package com.example.cmms;

import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private List<Certificate> certificates;
    public record Certificate(String name, String date) {};

    public User(String name, String lastName, String email, String role, List<Certificate> certificates) {
        this.firstName = name;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.certificates = certificates;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getRole() {
        return role;
    }
    public List<Certificate> getCertificates() {
        return certificates;
    }

}
