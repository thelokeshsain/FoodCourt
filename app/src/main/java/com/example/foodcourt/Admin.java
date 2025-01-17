package com.example.foodcourt;

public class Admin {
    private String email;
    private String password;

    // Default constructor required for calls to DataSnapshot.getValue(Admin.class)
    public Admin() {
    }

    // Constructor
    public Admin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters (if needed)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
