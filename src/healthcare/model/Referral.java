package healthcare.model;

import java.time.LocalDate;

public class Referral {

    private String referralId;

    private String patientId;
    private String referringClinicianId;
    private String referredToClinicianId;
    private String referringFacilityId;
    private String referredToFacilityId;
    private String appointmentId;

    private LocalDate referralDate;
    private String urgencyLevel;
    private String referralReason;
    private String clinicalSummary;
    private String requestedInvestigations;
    private String status;
    private String notes;

    private LocalDate createdDate;
    private LocalDate lastUpdated;

    public Referral(
            String referralId,
            String patientId,
            String referringClinicianId,
            String referredToClinicianId,
            String referringFacilityId,
            String referredToFacilityId,
            LocalDate referralDate,
            String urgencyLevel,
            String referralReason,
            String clinicalSummary,
            String requestedInvestigations,
            String status,
            String appointmentId,
            String notes,
            LocalDate createdDate,
            LocalDate lastUpdated
    ) {
        this.referralId = referralId;
        this.patientId = patientId;
        this.referringClinicianId = referringClinicianId;
        this.referredToClinicianId = referredToClinicianId;
        this.referringFacilityId = referringFacilityId;
        this.referredToFacilityId = referredToFacilityId;
        this.referralDate = referralDate;
        this.urgencyLevel = urgencyLevel;
        this.referralReason = referralReason;
        this.clinicalSummary = clinicalSummary;
        this.requestedInvestigations = requestedInvestigations;
        this.status = status;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
    }

    // Getters
    public String getReferralId() { return referralId; }
    public String getPatientId() { return patientId; }
    public String getReferringClinicianId() { return referringClinicianId; }
    public String getReferredToClinicianId() { return referredToClinicianId; }
    public String getReferringFacilityId() { return referringFacilityId; }
    public String getReferredToFacilityId() { return referredToFacilityId; }
    public LocalDate getReferralDate() { return referralDate; }
    public String getUrgencyLevel() { return urgencyLevel; }
    public String getReferralReason() { return referralReason; }
    public String getClinicalSummary() { return clinicalSummary; }
    public String getRequestedInvestigations() { return requestedInvestigations; }
    public String getStatus() { return status; }
    public String getAppointmentId() { return appointmentId; }
    public String getNotes() { return notes; }
    public LocalDate getCreatedDate() { return createdDate; }
    public LocalDate getLastUpdated() { return lastUpdated; }

    // Setters with audit
    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
        touch();
    }

    public void setStatus(String status) {
        this.status = status;
        touch();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        touch();
    }

    public void setRequestedInvestigations(String requestedInvestigations) {
        this.requestedInvestigations = requestedInvestigations;
        touch();
    }

    private void touch() {
        this.lastUpdated = LocalDate.now();
    }

    @Override
    public String toString() {
        return referralId + " | Patient: " + patientId + " | " + urgencyLevel + " | " + status;
    }
}
