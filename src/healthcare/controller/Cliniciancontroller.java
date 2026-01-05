package healthcare.controller;

import healthcare.model.Clinician;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliniciancontroller {
    private List<Clinician> clinicians;
    private static final String FILE = "clinicians.csv";

    public Cliniciancontroller() {
        clinicians = new ArrayList<>();
        loadfile();
    }

    // ---------- LOAD FROM CSV ----------
    private void loadfile() {
        try (BufferedReader b = new BufferedReader(new FileReader(FILE))) {
            b.readLine(); // skip header
            String line;
            while ((line = b.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",", -1);

                // basic safety: need 12 fields
                if (d.length < 12) {
                    System.out.println("Skipping invalid clinician row: " + line);
                    continue;
                }

                clinicians.add(new Clinician(
                        d[0],                // clinicianId
                        d[1],                // firstName
                        d[2],                // lastName
                        d[3],                // title
                        d[4],                // speciality
                        d[5],                // gmcNumber
                        d[6],                // phone
                        d[7],                // email
                        d[8],                // workplaceId
                        d[9],                // workplaceType
                        d[10],               // employmentStatus
                        LocalDate.parse(d[11]) // startDate (YYYY-MM-DD)
                ));
            }
        } catch (IOException e) {
            System.out.println("error loading clinicians: " + e.getMessage());
        }
    }

    // ---------- SAVE TO CSV ----------
    private void save() {
        try (FileWriter f = new FileWriter(FILE)) {
            f.write("clinician_id,first_name,last_name,title,speciality,gmc_number,phone,email,workplace_id,workplace_type,employment_status,start_date\n");
            for (Clinician c : clinicians) {
                f.write(c.toCSV() + "\n");
            }
        } catch (IOException e) {
            System.out.println("error " + e.getMessage());
        }
    }

    // ---------- PUBLIC API ----------

    public List<Clinician> getallclinicians() {
        return clinicians;
    }

    public Clinician getbyid(String id) {
        for (Clinician c : clinicians) {
            if (c.getClinicianId().equals(id)) return c;
        }
        return null;
    }

    public void addclinician(Clinician clinician) {
        clinicians.add(clinician);
        save();
    }

    public boolean deleteClinician(String clinicianId) {
        Clinician c = getbyid(clinicianId);
        if (c == null) return false;

        clinicians.remove(c);
        save();
        return true;
    }
}
