package healthcare.controller;

import healthcare.model.Facility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Facilitycontroller {

    private final List<Facility> facilities;
    private static final String FILE = "facilities.csv";

    public Facilitycontroller() {
        facilities = new ArrayList<>();
        loadcsv();
    }

    // ---------------- LOAD ----------------
    private void loadcsv() {
        try (BufferedReader b = new BufferedReader(new FileReader(FILE))) {

            String line = b.readLine(); // skip header

            while ((line = b.readLine()) != null) {
                if (line.isBlank()) continue;

                // Split on commas that are NOT inside quotes
                String[] d = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (d.length < 11) {
                    System.out.println("Skipping invalid facility row (columns=" + d.length + "): " + line);
                    continue;
                }

                // Remove quotes around any quoted fields
                for (int i = 0; i < d.length; i++) {
                    d[i] = d[i].trim();
                    if (d[i].startsWith("\"") && d[i].endsWith("\"") && d[i].length() >= 2) {
                        d[i] = d[i].substring(1, d[i].length() - 1);
                    }
                }

                int capacity;
                try {
                    capacity = Integer.parseInt(d[9].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid capacity in row, defaulting to 0: " + d[9]);
                    capacity = 0;
                }

                Facility f = new Facility(
                        d[0], // facilityId
                        d[1], // facilityName
                        d[2], // facilityType
                        d[3], // address
                        d[4], // postcode
                        d[5], // phoneNumber
                        d[6], // email
                        d[7], // openingHours
                        d[8], // managerName
                        capacity,
                        d[10] // specialitiesOffered
                );

                facilities.add(f);
            }

        } catch (IOException e) {
            System.out.println("Error loading facilities: " + e.getMessage());
        }
    }

    // ---------------- SAVE ----------------
    private void save() {
        try (FileWriter writer = new FileWriter(FILE)) {

            writer.write(
                "facility_id,facility_name,facility_type,address,postcode,phone_number," +
                "email,opening_hours,manager_name,capacity,specialities_offered\n"
            );

            for (Facility f : facilities) {
                writer.write(
                        safe(f.getFacilityId()) + "," +
                        safe(f.getFacilityName()) + "," +
                        safe(f.getFacilityType()) + "," +
                        quoteIfNeeded(f.getAddress()) + "," +
                        safe(f.getPostcode()) + "," +
                        safe(f.getPhoneNumber()) + "," +
                        safe(f.getEmail()) + "," +
                        quoteIfNeeded(f.getOpeningHours()) + "," +
                        safe(f.getManagerName()) + "," +
                        f.getCapacity() + "," +
                        quoteIfNeeded(f.getSpecialitiesOffered()) +
                        "\n"
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving facilities: " + e.getMessage());
        }
    }

    // helper: basic null-safe text
    private String safe(String s) {
        return s == null ? "" : s;
    }

    // quote fields that might contain commas
    private String quoteIfNeeded(String s) {
        if (s == null) return "";
        if (s.contains(",")) {
            return "\"" + s + "\"";
        }
        return s;
    }

    // ---------------- PUBLIC API ----------------

    public List<Facility> getall() {
        return facilities;
    }

    public Facility getbyid(String id) {
        for (Facility f : facilities) {
            if (f.getFacilityId().equals(id)) return f;
        }
        return null;
    }

    public void addFacility(Facility f) {
        facilities.add(f);
        save();
    }

    public boolean deleteFacility(String id) {
        Facility f = getbyid(id);
        if (f == null) return false;
        facilities.remove(f);
        save();
        return true;
    }
}
