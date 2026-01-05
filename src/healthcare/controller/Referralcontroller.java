package healthcare.controller;

import healthcare.model.Referral;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Referralcontroller {

    // ---------- Singleton ----------
    private static Referralcontroller instance;
    private final List<Referral> referrals;
    private static final String FILE = "referrals.csv";

    private Referralcontroller() {
        referrals = new ArrayList<>();
        loadReferrals();   // <-- load from CSV on startup
    }

    public static Referralcontroller getinstance() {
        if (instance == null) {
            instance = new Referralcontroller();
        }
        return instance;
    }

    // ---------- PUBLIC API ----------

    // Create referral
    public Referral createref(String referralId,
                              String patientId,
                              String referringClinicianId,
                              String referredToClinicianId,
                              String referringFacilityId,
                              String referredToFacilityId,
                              String urgencyLevel,
                              String referralReason,
                              String clinicalSummary,
                              String requestedInvestigations,
                              String appointmentId,
                              String notes) {

        Referral referral = new Referral(
                referralId,
                patientId,
                referringClinicianId,
                referredToClinicianId,
                referringFacilityId,
                referredToFacilityId,
                LocalDate.now(),           // referralDate
                urgencyLevel,
                referralReason,
                clinicalSummary,
                requestedInvestigations,
                "Pending",                 // initial status
                appointmentId,
                notes,
                LocalDate.now(),           // createdDate
                LocalDate.now()            // lastUpdated
        );

        referrals.add(referral);
        saveReferrals();      // save to CSV
        writeReferralText(referral); // write letter

        return referral;
    }

    // Get all
    public List<Referral> getallreferrals() {
        return referrals;
    }

    // Get by ID
    public Referral getReferralbyid(String referralId) {
        for (Referral r : referrals) {
            if (r.getReferralId().equals(referralId)) {
                return r;
            }
        }
        return null;
    }

    // Update status
    public boolean updatestatus(String referralID, String status) {
        Referral referral = getReferralbyid(referralID);
        if (referral != null) {
            referral.setStatus(status);
            saveReferrals();
            writeReferralText(referral); // optional: update letter
            return true;
        }
        return false;
    }

    // Update notes
    public boolean updatenotes(String referralID, String notes) {
        Referral referral = getReferralbyid(referralID);
        if (referral != null) {
            referral.setNotes(notes);
            saveReferrals();
            writeReferralText(referral); // optional
            return true;
        }
        return false;
    }

    // ---------- CSV PERSISTENCE ----------

    private void loadReferrals() {
        File f = new File(FILE);
        if (!f.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] d = parseCsvLine(line);

                if (d.length != 16) {
                    System.out.println("Skipping invalid referral row (found " + d.length + " columns): " + line);
                    continue;
                }

                Referral referral = new Referral(
                        d[0],
                        d[1],
                        d[2],
                        d[3],
                        d[4],
                        d[5],
                        parseDate(d[6]),
                        d[7],
                        d[8],
                        d[9],
                        d[10],
                        d[11],
                        d[12],
                        d[13],
                        parseDate(d[14]),
                        parseDate(d[15])
                );

                referrals.add(referral);
            }

        } catch (IOException e) {
            System.out.println("Error loading referrals.csv: " + e.getMessage());
        }
    }

    private void saveReferrals() {
        try (FileWriter writer = new FileWriter(FILE)) {

            writer.write(
                "referral_id,patient_id,referring_clinician_id,referred_to_clinician_id," +
                "referring_facility_id,referred_to_facility_id,referral_date," +
                "urgency_level,referral_reason,clinical_summary,requested_investigations," +
                "status,appointment_id,notes,created_date,last_updated\n"
            );

            for (Referral r : referrals) {
                writer.write(
                        csv(r.getReferralId()) + "," +
                        csv(r.getPatientId()) + "," +
                        csv(r.getReferringClinicianId()) + "," +
                        csv(r.getReferredToClinicianId()) + "," +
                        csv(r.getReferringFacilityId()) + "," +
                        csv(r.getReferredToFacilityId()) + "," +
                        csv(formatDate(r.getReferralDate())) + "," +
                        csv(r.getUrgencyLevel()) + "," +
                        csv(r.getReferralReason()) + "," +
                        csv(r.getClinicalSummary()) + "," +
                        csv(r.getRequestedInvestigations()) + "," +
                        csv(r.getStatus()) + "," +
                        csv(r.getAppointmentId()) + "," +
                        csv(r.getNotes()) + "," +
                        csv(formatDate(r.getCreatedDate())) + "," +
                        csv(formatDate(r.getLastUpdated())) +
                        "\n"
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving referrals.csv: " + e.getMessage());
        }
    }

    // ---------- TXT LETTER ----------

    private void writeReferralText(Referral referral) {
        String filename = "referral" + referral.getReferralId() + ".txt";
        File f = new File(filename);

        System.out.println("Writing referral letter to: " + f.getAbsolutePath());

        try (FileWriter writer = new FileWriter(f)) {

            writer.write("REFERRAL LETTER\n");
            writer.write("========================\n");
            writer.write("Referral ID: " + referral.getReferralId() + "\n");
            writer.write("Patient ID: " + referral.getPatientId() + "\n");
            writer.write("Referring Clinician: " + referral.getReferringClinicianId() + "\n");
            writer.write("Referred To Clinician: " + referral.getReferredToClinicianId() + "\n");
            writer.write("From Facility: " + referral.getReferringFacilityId() + "\n");
            writer.write("To Facility: " + referral.getReferredToFacilityId() + "\n");
            writer.write("Referral Date: " + referral.getReferralDate() + "\n");
            writer.write("Urgency: " + referral.getUrgencyLevel() + "\n");
            writer.write("Status: " + referral.getStatus() + "\n\n");

            writer.write("Reason:\n");
            writer.write(referral.getReferralReason() + "\n\n");

            writer.write("Clinical Summary:\n");
            writer.write(referral.getClinicalSummary() + "\n\n");

            writer.write("Requested Investigations:\n");
            writer.write(referral.getRequestedInvestigations() + "\n\n");

            writer.write("Appointment ID: " + referral.getAppointmentId() + "\n\n");

            writer.write("Notes:\n");
            writer.write(referral.getNotes() + "\n\n");

            writer.write("Created: " + referral.getCreatedDate() + "\n");
            writer.write("Last Updated: " + referral.getLastUpdated() + "\n");

        } catch (IOException e) {
            System.out.println("error " + e.getMessage());
        }
    }

    // ---------- Helper methods ----------

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());

        return fields.toArray(new String[0]);
    }

    // 🔹 UPDATED — accepts yyyy-MM-dd AND dd/MM/yyyy
    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) return null;

        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ignore) { }

        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(text, df);
        } catch (DateTimeParseException e) {
            System.out.println("Could not parse date: " + text);
            return null;
        }
    }

    private String formatDate(LocalDate date) {
        return (date == null) ? "" : date.toString();
    }

    private String csv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
}
