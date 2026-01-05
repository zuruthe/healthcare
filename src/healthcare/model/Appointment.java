package healthcare.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private String appointmentId;
    private String patientId;
    private String clinicianId;
    private String facilityId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private int durationMinutes;
    private String appointmentType;
    private String status;
    private String reasonForVisit;
    private String notes;
    private LocalDate createdDate;
    private LocalDate lastModified;

    public Appointment(String appointmentId, String patientId, String clinicianId,
                       String facilityId, LocalDate appointmentDate,
                       LocalTime appointmentTime, int durationMinutes,
                       String appointmentType, String status,
                       String reasonForVisit, String notes,
                       LocalDate createdDate, LocalDate lastModified) {

        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.facilityId = facilityId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.durationMinutes = durationMinutes;
        this.appointmentType = appointmentType;
        this.status = status;
        this.reasonForVisit = reasonForVisit;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastModified = lastModified;
    }

    // Getters
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getClinicianId() { return clinicianId; }
    public String getFacilityId() { return facilityId; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getAppointmentType() { return appointmentType; }
    public String getStatus() { return status; }
    public String getReasonForVisit() { return reasonForVisit; }
    public String getNotes() { return notes; }
    public LocalDate getCreatedDate() { return createdDate; }
    public LocalDate getLastModified() { return lastModified; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setLastModified(LocalDate lastModified) { this.lastModified = lastModified; }
 // in Appointment.java
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    @Override
    public String toString() {
        return appointmentId + " - patient " + patientId +
               " with clinician " + clinicianId +
               " on " + appointmentDate + " at " + appointmentTime;
    }
}
