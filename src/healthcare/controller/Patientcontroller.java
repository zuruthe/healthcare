package healthcare.controller;

import healthcare.model.Patient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Patientcontroller {

    private List<Patient> patients;
    // Make sure patients.csv is in the project root folder
    private static final String FILE = "patients.csv";

    public Patientcontroller() {
        patients = new ArrayList<>();
        loadPatients();
    }

    // ================= LOAD CSV =================
    private void loadPatients() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;

            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = parseCsvLine(line);

                if (data.length != 14) {
                    System.out.println("Skipping invalid row (found " + data.length + " columns): " + line);
                    continue;
                }

                Patient p = new Patient(
                        data[0],                          // patientId
                        data[1],                          // firstName
                        data[2],                          // lastName
                        parseDate(data[3]),               // dateOfBirth
                        data[4],                          // nhsNumber
                        data[5],                          // gender
                        data[6],                          // phoneNumber
                        data[7],                          // email
                        data[8],                          // address
                        data[9],                          // postcode
                        data[10],                         // emergencyContactName
                        data[11],                         // emergencyContactPhone
                        parseDate(data[12]),              // registrationDate
                        data[13]                          // gpSurgeryId
                );

                patients.add(p);
            }

        } catch (IOException e) {
            System.out.println("Error loading patients.csv: " + e.getMessage());
        }
    }

    // ================= SAVE CSV =================
    private void savePatients() {
        try (FileWriter writer = new FileWriter(FILE)) {

            writer.write(
                    "patient_id,first_name,last_name,date_of_birth,nhs_number,gender," +
                    "phone_number,email,address,postcode,emergency_contact_name," +
                    "emergency_contact_phone,registration_date,gp_surgery_id\n"
            );

            for (Patient p : patients) {
                String line =
                        csv(p.getPatientId()) + "," +
                        csv(p.getFirstName()) + "," +
                        csv(p.getLastName()) + "," +
                        csv(formatDate(p.getDateOfBirth())) + "," +
                        csv(p.getNhsNumber()) + "," +
                        csv(p.getGender()) + "," +
                        csv(p.getPhoneNumber()) + "," +
                        csv(p.getEmail()) + "," +
                        csv(p.getAddress()) + "," +
                        csv(p.getPostcode()) + "," +
                        csv(p.getEmergencyContactName()) + "," +
                        csv(p.getEmergencyContactPhone()) + "," +
                        csv(formatDate(p.getRegistrationDate())) + "," +
                        csv(p.getGpSurgeryId()) + "\n";

                writer.write(line);
            }

        } catch (IOException e) {
            System.out.println("Error saving patients.csv: " + e.getMessage());
        }
    }

    // ================= PUBLIC API =================
    public List<Patient> getAllPatients() {
        return patients;
    }

    public Patient getPatientById(String patientId) {
        for (Patient p : patients) {
            if (p.getPatientId().equals(patientId)) {
                return p;
            }
        }
        return null;
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        savePatients();
    }

    public boolean editPatient(String patientId, String phone, String email, String address) {
        Patient p = getPatientById(patientId);
        if (p != null) {
            if (phone != null && !phone.isBlank()) p.setPhoneNumber(phone);
            if (email != null && !email.isBlank()) p.setEmail(email);
            if (address != null && !address.isBlank()) p.setAddress(address);
            savePatients();
            return true;
        }
        return false;
    }

    public boolean deletePatient(String patientId) {
        Patient p = getPatientById(patientId);
        if (p != null) {
            patients.remove(p);
            savePatients();
            return true;
        }
        return false;
    }

    // ================= HELPER: CSV PARSER =================
    // Handles quotes + commas in address: "123 Oak Street, Birmingham"
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes; // toggle
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString()); // last field

        return fields.toArray(new String[0]);
    }

    // ================= HELPER: DATE PARSING =================
    // Accept either 1985-03-15 or 15/03/1985
    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) return null;

        // try ISO first
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ignore) { }

        // then try dd/MM/yyyy
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(text, f);
        } catch (DateTimeParseException e) {
            System.out.println("Could not parse date: " + text);
            return null;
        }
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        // write as ISO yyyy-MM-dd
        return date.toString();
    }

    // Escape field for CSV if it contains comma or quote
    private String csv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\""); // escape quotes
            return "\"" + s + "\"";
        }
        return s;
    }
}
