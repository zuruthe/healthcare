package healthcare.controller;

import healthcare.model.Staff;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Staffcontroller {

    private final List<Staff> staffList;
    private static final String FILE = "staff.csv";

    public Staffcontroller() {
        staffList = new ArrayList<>();
        loadCsv();
    }

    private void loadCsv() {
        DateTimeFormatter dfSlash = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {

            String line = br.readLine(); // header

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",", -1);
                if (d.length < 12) {
                    System.out.println("Skipping invalid staff row: " + line);
                    continue;
                }

                String dateStr = d[9].trim();
                LocalDate startDate = null;

                if (!dateStr.isEmpty()) {
                    try {
                        startDate = LocalDate.parse(dateStr);
                    } catch (Exception e1) {
                        try {
                            startDate = LocalDate.parse(dateStr, dfSlash);
                        } catch (Exception e2) {
                            System.out.println("Could not parse start date for row: " + line);
                        }
                    }
                }

                Staff s = new Staff(
                        d[0].trim(),
                        d[1].trim(),
                        d[2].trim(),
                        d[3].trim(),
                        d[4].trim(),
                        d[5].trim(),
                        d[6].trim(),
                        d[7].trim(),
                        d[8].trim(),
                        startDate,
                        d[10].trim(),
                        d[11].trim()
                );

                staffList.add(s);
            }

        } catch (IOException e) {
            System.out.println("Error loading staff: " + e.getMessage());
        }
    }

    // ---------- SAVE ----------
    private void save() {
        try (FileWriter writer = new FileWriter(FILE)) {

            writer.write(
                "staff_id,first_name,last_name,role,department,facility_id,phone_number,email,employment_status,start_date,line_manager,access_level\n"
            );

            for (Staff s : staffList) {
                writer.write(
                        s.getStaffId() + "," +
                        s.getFirstName() + "," +
                        s.getLastName() + "," +
                        s.getRole() + "," +
                        s.getDepartment() + "," +
                        s.getFacilityId() + "," +
                        s.getPhoneNumber() + "," +
                        s.getEmail() + "," +
                        s.getEmploymentStatus() + "," +
                        (s.getStartDate() == null ? "" : s.getStartDate()) + "," +
                        s.getLineManager() + "," +
                        s.getAccessLevel() + "\n"
                );
            }

        } catch (Exception e) {
            System.out.println("Error saving staff: " + e.getMessage());
        }
    }

    // ---------- PUBLIC API ----------

    public List<Staff> getAllStaff() {
        return staffList;
    }

    public Staff getById(String staffId) {
        for (Staff s : staffList) {
            if (s.getStaffId().equals(staffId)) {
                return s;
            }
        }
        return null;
    }

    public void addStaff(Staff s) {
        staffList.add(s);
        save();
    }

    public boolean deleteStaff(String staffId) {
        Staff target = getById(staffId);
        if (target == null) return false;

        staffList.remove(target);
        save();
        return true;
    }
}
