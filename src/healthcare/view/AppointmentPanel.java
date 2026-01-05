package healthcare.view;

import healthcare.controller.Appointmentcontroller;
import healthcare.controller.Patientcontroller;
import healthcare.controller.Cliniciancontroller;
import healthcare.controller.Facilitycontroller;

import healthcare.model.Appointment;
import healthcare.model.Patient;
import healthcare.model.Clinician;
import healthcare.model.Facility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class AppointmentPanel extends JPanel {

    private final Appointmentcontroller controller;
    private final Patientcontroller patientController;
    private final Cliniciancontroller clinicianController;
    private final Facilitycontroller facilityController;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextArea detailsArea;

    // Main constructor with all controllers
    public AppointmentPanel(Appointmentcontroller controller,
                            Patientcontroller patientController,
                            Cliniciancontroller clinicianController,
                            Facilitycontroller facilityController) {
        this.controller = controller;
        this.patientController = patientController;
        this.clinicianController = clinicianController;
        this.facilityController = facilityController;

        setLayout(new BorderLayout(8, 8));

        // ---------- TABLE ----------
        String[] columns = {
                "ID", "Patient", "Clinician", "Facility",
                "Date", "Time", "Duration (min)",
                "Type", "Status", "Reason", "Notes"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        // ---------- DETAILS ----------
        detailsArea = new JTextArea(6, 60);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createTitledBorder("Appointment details"));
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        add(detailsScroll, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedDetails();
            }
        });

        // ---------- BUTTONS ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton viewAllBtn = new JButton(" View All");
        JButton addBtn = new JButton("Add");
        JButton rescheduleBtn = new JButton("Change Date/Time");
        JButton updateStatusBtn = new JButton("Update Status");
        JButton changeNotesBtn = new JButton("Change Notes");
        JButton deleteBtn = new JButton("Delete");

        buttonPanel.add(viewAllBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(rescheduleBtn);
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(changeNotesBtn);
        buttonPanel.add(deleteBtn);

        add(buttonPanel, BorderLayout.NORTH);

        viewAllBtn.addActionListener(e -> refreshTable());
        addBtn.addActionListener(e -> addAppointment());
        rescheduleBtn.addActionListener(e -> rescheduleAppointment());
        updateStatusBtn.addActionListener(e -> updateStatus());
        changeNotesBtn.addActionListener(e -> changeNotes());
        deleteBtn.addActionListener(e -> deleteAppointment());

        refreshTable();
    }

    // Optional no-arg constructor for quick testing
    public AppointmentPanel() {
        this(new Appointmentcontroller(),
             new Patientcontroller(),
             new Cliniciancontroller(),
             new Facilitycontroller());
    }

    // ================== TABLE / DETAILS ==================

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Appointment a : controller.getAll()) {
            tableModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    a.getPatientId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getDurationMinutes(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReasonForVisit(),
                    a.getNotes()
            });
        }
        detailsArea.setText("");
    }

    private void showSelectedDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            detailsArea.setText("");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Appointment a = controller.getById(id);
        if (a == null) {
            detailsArea.setText("Appointment not found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Appointment ID: ").append(a.getAppointmentId()).append("\n");
        sb.append("Patient ID:     ").append(a.getPatientId()).append("\n");
        sb.append("Clinician ID:   ").append(a.getClinicianId()).append("\n");
        sb.append("Facility ID:    ").append(a.getFacilityId()).append("\n\n");

        sb.append("Date:           ").append(a.getAppointmentDate()).append("\n");
        sb.append("Time:           ").append(a.getAppointmentTime()).append("\n");
        sb.append("Duration:       ").append(a.getDurationMinutes()).append(" minutes\n");
        sb.append("Type:           ").append(a.getAppointmentType()).append("\n");
        sb.append("Status:         ").append(a.getStatus()).append("\n\n");

        sb.append("Reason for visit:\n");
        sb.append(a.getReasonForVisit()).append("\n\n");

        sb.append("Notes:\n");
        sb.append(a.getNotes()).append("\n\n");

        sb.append("Created:        ").append(a.getCreatedDate()).append("\n");
        sb.append("Last modified:  ").append(a.getLastModified()).append("\n");

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    // ================== Helper: form panel with calendar + time dropdown ==================

    /** Builds and shows a form dialog for creating / editing date+time etc.
     *  Returns true if user clicked OK.
     */
    private boolean showAppointmentForm(JTextField idField,
                                        JTextField patientField,
                                        JTextField clinicianField,
                                        JSpinner dateSpinner,
                                        JComboBox<String> timeCombo,
                                        JTextField durationField,
                                        JTextField typeField,
                                        JTextField statusField,
                                        JTextField reasonField,
                                        JTextField notesField,
                                        String title) {

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 4, 2, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0;
        panel.add(new JLabel("Appointment ID:"), gc);
        gc.gridx = 1;
        panel.add(idField, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Patient ID:"), gc);
        gc.gridx = 1;
        panel.add(patientField, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Clinician ID:"), gc);
        gc.gridx = 1;
        panel.add(clinicianField, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Date:"), gc);
        gc.gridx = 1;
        panel.add(dateSpinner, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Time:"), gc);
        gc.gridx = 1;
        panel.add(timeCombo, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Duration (minutes):"), gc);
        gc.gridx = 1;
        panel.add(durationField, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Type:"), gc);
        gc.gridx = 1;
        panel.add(typeField, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Status:"), gc);
        gc.gridx = 1;
        panel.add(statusField, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Reason:"), gc);
        gc.gridx = 1;
        panel.add(reasonField, gc);

        gc.gridx = 0; gc.gridy++;
        panel.add(new JLabel("Notes:"), gc);
        gc.gridx = 1;
        panel.add(notesField, gc);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        return result == JOptionPane.OK_OPTION;
    }

    private JSpinner createDateSpinner(LocalDate initial) {
        Date initDate = Date.from(initial.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel model = new SpinnerDateModel(initDate, null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    private JComboBox<String> createTimeCombo(LocalTime initial) {
        String[] slots = {
                "08:00", "08:30",
                "09:00", "09:30",
                "10:00", "10:30",
                "11:00", "11:30",
                "12:00", "12:30",
                "13:00", "13:30",
                "14:00", "14:30",
                "15:00", "15:30",
                "16:00", "16:30"
        };

        JComboBox<String> combo = new JComboBox<>(slots);
        if (initial != null) {
            String initStr = initial.toString().substring(0, 5); // HH:mm
            for (int i = 0; i < slots.length; i++) {
                if (slots[i].equals(initStr)) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }
        return combo;
    }

    private LocalDate spinnerToLocalDate(JSpinner spinner) {
        Date d = (Date) spinner.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalTime comboToLocalTime(JComboBox<String> combo) {
        String s = (String) combo.getSelectedItem(); // e.g. "09:30"
        return LocalTime.parse(s + ":00");
    }

    // ================== Add Appointment (facility auto from clinician) ==================

    private void addAppointment() {
        try {
            JTextField idField = new JTextField();
            JTextField patientField = new JTextField();
            JTextField clinicianField = new JTextField();

            JSpinner dateSpinner = createDateSpinner(LocalDate.now());
            JComboBox<String> timeCombo = createTimeCombo(LocalTime.of(9, 0));

            JTextField durationField = new JTextField("15");
            JTextField typeField = new JTextField("Consultation");
            JTextField statusField = new JTextField("Scheduled");
            JTextField reasonField = new JTextField();
            JTextField notesField = new JTextField();

            boolean ok = showAppointmentForm(
                    idField, patientField, clinicianField,
                    dateSpinner, timeCombo,
                    durationField, typeField, statusField,
                    reasonField, notesField,
                    "Add Appointment"
            );
            if (!ok) return;

            String id = idField.getText().trim();
            String patientId = patientField.getText().trim();
            String clinicianId = clinicianField.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Appointment ID is required.");
                return;
            }
            if (patientId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient ID is required.");
                return;
            }
            if (clinicianId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Clinician ID is required.");
                return;
            }

            // Validate patient exists
            Patient patient = patientController.getPatientById(patientId);
            if (patient == null) {
                JOptionPane.showMessageDialog(this, "Patient ID " + patientId + " does not exist.");
                return;
            }

            // Validate clinician exists
            Clinician clinician = clinicianController.getbyid(clinicianId);
            if (clinician == null) {
                JOptionPane.showMessageDialog(this, "Clinician ID " + clinicianId + " does not exist.");
                return;
            }

            // 🔥 Facility auto from clinician
            String facilityId = clinician.getWorkplaceId();
            Facility facility = facilityController.getbyid(facilityId);
            String facilityInfo = facility != null
                    ? facility.getFacilityName() + " (" + facilityId + ")"
                    : facilityId;

            // Parse rest of fields
            LocalDate date = spinnerToLocalDate(dateSpinner);
            LocalTime time = comboToLocalTime(timeCombo);

            int duration = Integer.parseInt(durationField.getText().trim());
            String type = typeField.getText().trim();
            String status = statusField.getText().trim();
            String reason = reasonField.getText().trim();
            String notes = notesField.getText().trim();

            Appointment appointment = new Appointment(
                    id, patientId, clinicianId, facilityId,
                    date, time, duration,
                    type, status, reason, notes,
                    LocalDate.now(), LocalDate.now()
            );

            controller.addAppointment(appointment);
            refreshTable();
            JOptionPane.showMessageDialog(
                    this,
                    "Appointment added " 
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    // ================== Reschedule (uses friendlier date/time UI) ==================

    private void rescheduleAppointment() {
        String id = JOptionPane.showInputDialog(this, "Appointment ID to reschedule:");
        if (id == null || id.trim().isEmpty()) return;
        id = id.trim();

        Appointment a = controller.getById(id);
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Appointment not found.");
            return;
        }

        // build a tiny panel with only date + time + duration (optional)
        JSpinner dateSpinner = createDateSpinner(
                a.getAppointmentDate() != null ? a.getAppointmentDate() : LocalDate.now()
        );
        JComboBox<String> timeCombo = createTimeCombo(
                a.getAppointmentTime() != null ? a.getAppointmentTime() : LocalTime.of(9, 0)
        );

        JTextField durationField = new JTextField(String.valueOf(a.getDurationMinutes()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 4, 4));
        panel.add(new JLabel("New Date:"));
        panel.add(dateSpinner);
        panel.add(new JLabel("New Time:"));
        panel.add(timeCombo);
        panel.add(new JLabel("Duration (minutes):"));
        panel.add(durationField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Reschedule Appointment " + id,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            LocalDate newDate = spinnerToLocalDate(dateSpinner);
            LocalTime newTime = comboToLocalTime(timeCombo);
            int newDuration = Integer.parseInt(durationField.getText().trim());

            boolean okUpdate = controller.rescheduleAppointment(id, newDate, newTime, newDuration);
            if (okUpdate) {
                JOptionPane.showMessageDialog(this, "Appointment rescheduled.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Appointment not found.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    // ================== Update Status / Notes / Delete ==================

    private void updateStatus() {
        String id = JOptionPane.showInputDialog(this, "Appointment ID to update status:");
        if (id == null || id.trim().isEmpty()) return;

        String status = JOptionPane.showInputDialog(this, "New Status:");
        if (status == null) return;

        if (controller.updateStatus(id.trim(), status.trim())) {
            refreshTable();
            JOptionPane.showMessageDialog(this, "Status updated.");
        } else {
            JOptionPane.showMessageDialog(this, "Appointment not found.");
        }
    }

    private void changeNotes() {
        String id = JOptionPane.showInputDialog(this, "Appointment ID to edit notes:");
        if (id == null || id.trim().isEmpty()) return;

        String notes = JOptionPane.showInputDialog(this, "New Notes:");
        if (notes == null) return;

        if (controller.changeNotes(id.trim(), notes.trim())) {
            refreshTable();
            JOptionPane.showMessageDialog(this, "Notes updated.");
        } else {
            JOptionPane.showMessageDialog(this, "Appointment not found.");
        }
    }

    private void deleteAppointment() {
        String id = JOptionPane.showInputDialog(this, "Appointment ID to delete:");
        if (id == null || id.trim().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete appointment " + id + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (controller.deleteAppointment(id.trim())) {
            refreshTable();
            JOptionPane.showMessageDialog(this, "Appointment deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "Appointment not found.");
        }
    }
}
