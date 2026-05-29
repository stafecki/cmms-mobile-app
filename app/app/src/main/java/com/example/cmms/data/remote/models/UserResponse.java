package com.example.cmms.data.remote.models;

import java.util.List;

public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String role;
    private boolean isActive;
    private String createdAt;
    private List<Certification> certifications;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    public String getCreatedAt() { return createdAt; }
    public List<Certification> getCertifications() { return certifications; }

    public static UserResponse fromLocal(String name, String email, String role) {
        UserResponse u = new UserResponse();
        u.name = name;
        u.email = email;
        u.role = role;
        return u;
    }

    public static class Certification {
        private String id;
        private String type;
        private String expiresAt;
        private boolean isValid;

        public String getId() { return id; }
        public String getType() { return type; }
        public String getExpiresAt() { return expiresAt; }
        public boolean isValid() { return isValid; }
    }
}
