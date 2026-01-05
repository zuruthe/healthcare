package healthcare.controller;

import healthcare.model.Appointment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Appointmentcontroller {

    private List<Appointment> appointments;
    private static final String FILE = "appointments.csv";

    public Appointmentcontroller() {
        appointments = new ArrayList<>();
        loadAppointments();
    }

    // ============== LOAD CSV ==============
    private void loadAppointments() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] d = parseCsvLine(line);

                if (d.length != 13) {
                    System.out.println("Skipping invalid appointment row (found " + d.length + " columns): " + line);
                    continue;
                }

                Appointment appointment = new Appointment(
                        d[0],                       // appointment_id
                        d[1],                       // patient_id
                        d[2],                       // clinician_id
                        d[3],                       // facility_id
                        parseDate(d[4]),            // appointment_date
                        parseTime(d[5]),            // appointment_time
                        Integer.parseInt(d[6]),     // duration_minutes
                        d[7],                       // appointment_type
                        d[8],                       // status
                        d[9],                       // reason_for_visit
                        d[10],                      // notes
                        parseDate(d[11]),           // created_date
                        parseDate(d[12])            // last_modified
                );

                appointments.add(appointment);
            }

        } catch (IOException e) {
            System.out.println("Error loading appointments: " + e.getMessage());
        }
    }

    // ============== SAVE CSV ==============
    private void saveAppointments() {
        try (FileWriter writer = new FileWriter(FILE)) {

            writer.write(
                "appointment_id,patient_id,clinician_id,facility_id," +
                "appointment_date,appointment_time,duration_minutes," +
                "appointment_type,status,reason_for_visit,notes," +
                "created_date,last_modified\n"
            );

            for (Appointment a : appointments) {
                writer.write(
                        csv(a.getAppointmentId()) + "," +
                        csv(a.getPatientId()) + "," +
                        csv(a.getClinicianId()) + "," +
                        csv(a.getFacilityId()) + "," +
                        csv(a.getAppointmentDate() == null ? "" : a.getAppointmentDate().toString()) + "," +
                        csv(a.getAppointmentTime() == null ? "" : a.getAppointmentTime().toString()) + "," +
                        a.getDurationMinutes() + "," +
                        csv(a.getAppointmentType()) + "," +
                        csv(a.getStatus()) + "," +
                        csv(a.getReasonForVisit()) + "," +
                        csv(a.getNotes()) + "," +
                        csv(a.getCreatedDate() == null ? "" : a.getCreatedDate().toString()) + "," +
                        csv(a.getLastModified() == null ? "" : a.getLastModified().toString()) +
                        "\n"
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving appointments: " + e.getMessage());
        }
    }

    // ============== PUBLIC METHODS ==============
    public List<Appointment> getAll() {
        return appointments;
    }

    public Appointment getById(String appointmentId) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(appointmentId)) {
                return a;
            }
        }
        return null;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        saveAppointments();
    }

    public boolean updateStatus(String appointmentId, String newStatus) {
        Appointment a = getById(appointmentId);
        if (a != null) {
            a.setStatus(newStatus);
            a.setLastModified(LocalDate.now());
            saveAppointments();
            return true;
        }
        return false;
    }

    public boolean changeNotes(String appointmentId, String notes) {
        Appointment a = getById(appointmentId);
        if (a != null) {
            a.setNotes(notes);
            a.setLastModified(LocalDate.now());
            saveAppointments();
            return true;
        }
        return false;
    }

    public boolean deleteAppointment(String appointmentId) {
        Appointment a = getById(appointmentId);
        if (a != null) {
            appointments.remove(a);
            saveAppointments();
            return true;
        }
        return false;
    }

    // 🔹 NEW: reschedule date/time
    public boolean rescheduleAppointment(String appointmentId, LocalDate newDate, LocalTime newTime) {
        Appointment a = getById(appointmentId);
        if (a == null) return false;

        a.setAppointmentDate(newDate);
        a.setAppointmentTime(newTime);
        a.setLastModified(LocalDate.now());
        saveAppointments();
        return true;
    }

public boolean rescheduleAppointment(String appointmentId,
                                     LocalDate newDate,
                                     LocalTime newTime,
                                     int newDurationMinutes) {
    Appointment a = getById(appointmentId);
    if (a != null) {
        a.setAppointmentDate(newDate);
        a.setAppointmentTime(newTime);
        a.setDurationMinutes(newDurationMinutes);
        a.setLastModified(LocalDate.now());
        saveAppointments();
        return true;
    }
    return false;
}
    // ============== HELPERS: CSV PARSING & FORMATTING ==============

    // Handle quotes and commas: "Something, with comma"
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

    private LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            System.out.println("Could not parse date: " + text);
            return null;
        }
    }

    private LocalTime parseTime(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return LocalTime.parse(text); // expects HH:mm or HH:mm:ss
        } catch (DateTimeParseException e) {
            System.out.println("Could not parse time: " + text);
            return null;
        }
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
