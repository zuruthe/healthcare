package healthcare.controller;

import healthcare.model.Prescription;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Prescriptioncontroller {

    private List<Prescription> prescriptions;
    private static final String FILE_NAME = "prescriptions.csv";

    public Prescriptioncontroller() {
        prescriptions = new ArrayList<>();
        loadPrescriptions();
    }

    // ---------- Load from CSV ----------
    private void loadPrescriptions() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                // split, keeping empty fields
                String[] d = line.split(",", -1);

                if (d.length < 15) {
                    System.out.println("Skipping invalid prescription row (columns=" + d.length + "): " + line);
                    continue;
                }

                try {
                    int durationDays = parseIntStrict(d[8]);     // e.g. "28"
                    int quantity     = parseQuantity(d[9]);      // e.g. "28 tablets" -> 28

                    Prescription prescription = new Prescription(
                            d[0],                           // prescriptionId
                            d[1],                           // patientId
                            d[2],                           // clinicianId
                            d[3],                           // appointmentId
                            parseDate(d[4]),                // prescriptionDate
                            d[5],                           // medicationName
                            d[6],                           // dosage
                            d[7],                           // frequency
                            durationDays,                   // durationDays
                            quantity,                       // quantity
                            d[10],                          // instructions
                            d[11],                          // pharmacyName
                            d[12],                          // status
                            parseDate(d[13]),               // issueDate
                            d[14].isEmpty() ? null : parseDate(d[14]) // collectionDate
                    );

                    prescriptions.add(prescription);

                } catch (Exception ex) {
                    System.out.println("Skipping invalid prescription row (parse error): " + line);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading prescriptions: " + e.getMessage());
        }
    }

    // ---------- Helpers ----------

    // Accept both yyyy-MM-dd and dd/MM/yyyy
    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) return null;

        // try ISO yyyy-MM-dd
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ignored) { }

        // try dd/MM/yyyy
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(text, f);
        } catch (DateTimeParseException e) {
            System.out.println("Could not parse date: " + text);
            return null;
        }
    }

    // For fields that should be just a number (like duration)
    private int parseIntStrict(String text) {
        return Integer.parseInt(text.trim());
    }

    // For quantity like "28 tablets", "1 pen"
    private int parseQuantity(String text) {
        if (text == null) return 0;
        // keep only digits
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0;
        return Integer.parseInt(digits);
    }

    // ---------- Get all prescriptions ----------
    public List<Prescription> getAllPrescriptions() {
        return prescriptions;
    }

    // ---------- Find by ID ----------
    public Prescription getPrescriptionById(String prescriptionId) {
        for (Prescription p : prescriptions) {
            if (p.getPrescriptionId().equals(prescriptionId)) {
                return p;
            }
        }
        return null;
    }

    // ---------- Create new prescription ----------
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
        savePrescriptions();
    }

    // ---------- Mark as collected ONLY ----------
    public boolean markAsCollected(String prescriptionId) {
        Prescription p = getPrescriptionById(prescriptionId);
        if (p != null) {
            p.setStatus("Collected");
            p.setCollectionDate(LocalDate.now());
            savePrescriptions();
            return true;
        }
        return false;
    }

    // ---------- Save back to CSV ----------
    private void savePrescriptions() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {

            writer.write(
                    "prescription_id,patient_id,clinician_id,appointment_id,prescription_date," +
                    "medication_name,dosage,frequency,duration_days,quantity,instructions," +
                    "pharmacy_name,status,issue_date,collection_date\n"
            );

            for (Prescription p : prescriptions) {
                writer.write(
                        p.getPrescriptionId() + "," +
                        p.getPatientId() + "," +
                        p.getClinicianId() + "," +
                        p.getAppointmentId() + "," +
                        (p.getPrescriptionDate() == null ? "" : p.getPrescriptionDate()) + "," +
                        p.getMedicationName() + "," +
                        p.getDosage() + "," +
                        p.getFrequency() + "," +
                        p.getDurationDays() + "," +
                        p.getQuantity() + "," +
                        p.getInstructions() + "," +
                        p.getPharmacyName() + "," +
                        p.getStatus() + "," +
                        (p.getIssueDate() == null ? "" : p.getIssueDate()) + "," +
                        (p.getCollectionDate() == null ? "" : p.getCollectionDate()) +
                        "\n"
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving prescriptions: " + e.getMessage());
        }
    }
}
