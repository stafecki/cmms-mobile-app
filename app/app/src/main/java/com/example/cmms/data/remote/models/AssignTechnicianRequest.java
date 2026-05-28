package com.example.cmms.data.remote.models;

public class AssignTechnicianRequest {
    private final String technicianId;

    public AssignTechnicianRequest(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianId() { return technicianId; }
}
