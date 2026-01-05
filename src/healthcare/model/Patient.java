package healthcare.model;

import java.time.LocalDate;

public class Patient {

    private String patientId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String nhsNumber;
    private String gender;
    private String phoneNumber;
    private String email;
    private String address;
    private String postcode;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private LocalDate registrationDate;
    private String gpSurgeryId;

    public Patient(String patientId, String firstName, String lastName,
                   LocalDate dateOfBirth, String nhsNumber, String gender,
                   String phoneNumber, String email, String address, String postcode,
                   String emergencyContactName, String emergencyContactPhone,
                   LocalDate registrationDate, String gpSurgeryId) {

        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nhsNumber = nhsNumber;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.registrationDate = registrationDate;
        this.gpSurgeryId = gpSurgeryId;
    }

    // Getters
    public String getPatientId() { return patientId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getNhsNumber() { return nhsNumber; }
    public String getGender() { return gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPostcode() { return postcode; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public String getGpSurgeryId() { return gpSurgeryId; }

    // Setters for updating
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public void setEmergencyContactPhone(String phone) { this.emergencyContactPhone = phone; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + patientId + ")";
    }
}
