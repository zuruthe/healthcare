package healthcare.view;

import healthcare.controller.Patientcontroller;
import healthcare.controller.Appointmentcontroller;
import healthcare.controller.Prescriptioncontroller;
import healthcare.controller.Referralcontroller;
import healthcare.controller.Cliniciancontroller;

import healthcare.model.Patient;
import healthcare.model.Appointment;
import healthcare.model.Prescription;
import healthcare.model.Referral;
import healthcare.model.Clinician;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PatientPanel extends JPanel {

    private final Patientcontroller patientController;
    private final Appointmentcontroller appointmentController;
    private final Prescriptioncontroller prescriptionController;
    private final Referralcontroller referralController;
    private final Cliniciancontroller clinicianController;

    // UI components
    private JTable patientTable;
    private DefaultTableModel patientTableModel;

    private JTextArea patientDetailsArea;

    private JTable apptTable;
    private DefaultTableModel apptTableModel;

    private JTable rxTable;
    private DefaultTableModel rxTableModel;

    private JTable refTable;
    private DefaultTableModel refTableModel;

    public PatientPanel(Patientcontroller patientController,
                        Appointmentcontroller appointmentController,
                        Prescriptioncontroller prescriptionController,
                        Referralcontroller referralController,
                        Cliniciancontroller clinicianController) {

        this.patientController = patientController;
        this.appointmentController = appointmentController;
        this.prescriptionController = prescriptionController;
        this.referralController = referralController;
        this.clinicianController = clinicianController;

        setLayout(new BorderLayout(8, 8));

        buildUI();
        loadPatients();
    }

    private void buildUI() {
        // ---- TOP: info label ----
        JLabel info = new JLabel(
                "Select a patient on the left. Use the buttons below to manage patients, prescriptions and referrals."
        );
        add(info, BorderLayout.NORTH);

        // ---- LEFT: patients table + buttons underneath ----
        String[] patientCols = { "ID", "First Name", "Last Name", "DOB", "Gender", "Phone" };
        patientTableModel = new DefaultTableModel(patientCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        patientTable = new JTable(patientTableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowHeight(22);
        JScrollPane patientScroll = new JScrollPane(patientTable);

        // ====== BUTTON BAR (JUST LAYOUT CHANGED) ======
        JButton addPatientBtn    = new JButton("Add Patient");
        JButton deletePatientBtn = new JButton("Delete Patient");
        JButton issueRxBtn       = new JButton("Issue Prescription");
        JButton createRefBtn     = new JButton("Create Referral");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Patient actions"));
        // 2 rows, 2 columns so all buttons are visible
        buttonPanel.setLayout(new GridLayout(2, 2, 8, 4));
        buttonPanel.add(addPatientBtn);
        buttonPanel.add(deletePatientBtn);
        buttonPanel.add(issueRxBtn);
        buttonPanel.add(createRefBtn);

        JPanel leftPanel = new JPanel(new BorderLayout(4, 4));
        leftPanel.add(patientScroll, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ---- RIGHT: tabbed details ----
        JTabbedPane rightTabs = new JTabbedPane();

        // Details tab
        patientDetailsArea = new JTextArea(8, 40);
        patientDetailsArea.setEditable(false);
        patientDetailsArea.setLineWrap(true);
        patientDetailsArea.setWrapStyleWord(true);
        rightTabs.add("Details", new JScrollPane(patientDetailsArea));

        // Appointments tab
        String[] apptCols = { "ID", "Clinician", "Date", "Time", "Type", "Status" };
        apptTableModel = new DefaultTableModel(apptCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        apptTable = new JTable(apptTableModel);
        apptTable.setRowHeight(22);
        rightTabs.add("Appointments", new JScrollPane(apptTable));

        // Prescriptions tab
        String[] rxCols = { "ID", "Medication", "Dosage", "Freq", "Status", "Issue Date" };
        rxTableModel = new DefaultTableModel(rxCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        rxTable = new JTable(rxTableModel);
        rxTable.setRowHeight(22);
        rightTabs.add("Prescriptions", new JScrollPane(rxTable));

        // Referrals tab
        String[] refCols = { "ID", "Urgency", "Status", "Referral Date", "Appointment ID" };
        refTableModel = new DefaultTableModel(refCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        refTable = new JTable(refTableModel);
        refTable.setRowHeight(22);
        rightTabs.add("Referrals", new JScrollPane(refTable));

        // ---- CENTER: Split left/right ----
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                rightTabs
        );
        split.setDividerLocation(380);
        add(split, BorderLayout.CENTER);

        // ---- selection listener for patients ----
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateRightSideForSelectedPatient();
            }
        });

        // ---- button actions ----
        addPatientBtn.addActionListener(e -> addPatientDialog());
        deletePatientBtn.addActionListener(e -> deleteSelectedPatient());
        issueRxBtn.addActionListener(e -> issuePrescriptionForSelectedPatient());
        createRefBtn.addActionListener(e -> createReferralForSelectedPatient());
    }

    private void loadPatients() {
        patientTableModel.setRowCount(0);
        List<Patient> patients = patientController.getAllPatients();
        for (Patient p : patients) {
            patientTableModel.addRow(new Object[] {
                    p.getPatientId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getDateOfBirth(),
                    p.getGender(),
                    p.getPhoneNumber()
            });
        }

        if (patientTableModel.getRowCount() > 0) {
            patientTable.setRowSelectionInterval(0, 0);
            updateRightSideForSelectedPatient();
        }
    }

    private void updateRightSideForSelectedPatient() {
        int row = patientTable.getSelectedRow();
        if (row < 0) {
            patientDetailsArea.setText("");
            apptTableModel.setRowCount(0);
            rxTableModel.setRowCount(0);
            refTableModel.setRowCount(0);
            return;
        }

        String patientId = (String) patientTableModel.getValueAt(row, 0);
        Patient p = patientController.getPatientById(patientId);
        if (p == null) return;

        // ---- Details text ----
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(p.getPatientId()).append("\n");
        sb.append("Name: ").append(p.getFirstName()).append(" ").append(p.getLastName()).append("\n");
        sb.append("DOB: ").append(p.getDateOfBirth()).append("\n");
        sb.append("Gender: ").append(p.getGender()).append("\n\n");
        sb.append("Phone: ").append(p.getPhoneNumber()).append("\n");
        sb.append("Email: ").append(p.getEmail()).append("\n");
        sb.append("Address: ").append(p.getAddress()).append("\n");
        sb.append("Postcode: ").append(p.getPostcode()).append("\n\n");
        sb.append("Emergency Contact: ").append(p.getEmergencyContactName()).append(" (")
                .append(p.getEmergencyContactPhone()).append(")\n");
        sb.append("Registered at GP: ").append(p.getGpSurgeryId()).append("\n");
        sb.append("Registration Date: ").append(p.getRegistrationDate()).append("\n");

        patientDetailsArea.setText(sb.toString());
        patientDetailsArea.setCaretPosition(0);

        // ---- Appointments filtered by patient ----
        apptTableModel.setRowCount(0);
        for (Appointment a : appointmentController.getAll()) {
            if (a.getPatientId().equals(patientId)) {
                apptTableModel.addRow(new Object[] {
                        a.getAppointmentId(),
                        a.getClinicianId(),
                        a.getAppointmentDate(),
                        a.getAppointmentTime(),
                        a.getAppointmentType(),
                        a.getStatus()
                });
            }
        }

        // ---- Prescriptions filtered by patient ----
        rxTableModel.setRowCount(0);
        for (Prescription rx : prescriptionController.getAllPrescriptions()) {
            if (rx.getPatientId().equals(patientId)) {
                rxTableModel.addRow(new Object[] {
                        rx.getPrescriptionId(),
                        rx.getMedicationName(),
                        rx.getDosage(),
                        rx.getFrequency(),
                        rx.getStatus(),
                        rx.getIssueDate()
                });
            }
        }

        // ---- Referrals filtered by patient ----
        refTableModel.setRowCount(0);
        for (Referral r : referralController.getallreferrals()) {
            if (r.getPatientId().equals(patientId)) {
                refTableModel.addRow(new Object[] {
                        r.getReferralId(),
                        r.getUrgencyLevel(),
                        r.getStatus(),
                        r.getReferralDate(),
                        r.getAppointmentId()
                });
            }
        }
    }

    // ================== Add Patient ==================

    private void addPatientDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 4, 4));

        JTextField idField      = new JTextField();
        JTextField firstField   = new JTextField();
        JTextField lastField    = new JTextField();
        JTextField dobField     = new JTextField("2000-01-01"); // YYYY-MM-DD
        JTextField nhsField     = new JTextField();
        JTextField genderField  = new JTextField();
        JTextField phoneField   = new JTextField();
        JTextField emailField   = new JTextField();
        JTextField addrField    = new JTextField();
        JTextField postField    = new JTextField();
        JTextField emNameField  = new JTextField();
        JTextField emPhoneField = new JTextField();
        JTextField regDateField = new JTextField(LocalDate.now().toString());
        JTextField gpField      = new JTextField();

        panel.add(new JLabel("Patient ID:"));                         panel.add(idField);
        panel.add(new JLabel("First name:"));                         panel.add(firstField);
        panel.add(new JLabel("Last name:"));                          panel.add(lastField);
        panel.add(new JLabel("Date of birth (YYYY-MM-DD):"));         panel.add(dobField);
        panel.add(new JLabel("NHS number:"));                         panel.add(nhsField);
        panel.add(new JLabel("Gender:"));                             panel.add(genderField);
        panel.add(new JLabel("Phone:"));                              panel.add(phoneField);
        panel.add(new JLabel("Email:"));                              panel.add(emailField);
        panel.add(new JLabel("Address:"));                            panel.add(addrField);
        panel.add(new JLabel("Postcode:"));                           panel.add(postField);
        panel.add(new JLabel("Emergency contact name:"));             panel.add(emNameField);
        panel.add(new JLabel("Emergency contact phone:"));            panel.add(emPhoneField);
        panel.add(new JLabel("Registration date (YYYY-MM-DD):"));     panel.add(regDateField);
        panel.add(new JLabel("GP surgery ID:"));                      panel.add(gpField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Patient",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient ID is required.");
                return;
            }

            Patient p = new Patient(
                    id,
                    firstField.getText().trim(),
                    lastField.getText().trim(),
                    LocalDate.parse(dobField.getText().trim()),
                    nhsField.getText().trim(),
                    genderField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    addrField.getText().trim(),
                    postField.getText().trim(),
                    emNameField.getText().trim(),
                    emPhoneField.getText().trim(),
                    LocalDate.parse(regDateField.getText().trim()),
                    gpField.getText().trim()
            );

            patientController.addPatient(p);
            loadPatients();
            JOptionPane.showMessageDialog(this, "Patient added.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    // ================== Delete Patient ==================

    private void deleteSelectedPatient() {
        Patient p = getSelectedPatient();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Please select a patient first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete patient " + p.getPatientId() + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = patientController.deletePatient(p.getPatientId());
        if (ok) {
            loadPatients();
            JOptionPane.showMessageDialog(this, "Patient deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete patient.");
        }
    }

    // ================== Issue Prescription ==================

    private void issuePrescriptionForSelectedPatient() {
        Patient p = getSelectedPatient();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Please select a patient first.");
            return;
        }

        String patientId = p.getPatientId();
        LocalDate today = LocalDate.now();

        try {
            String rxId = JOptionPane.showInputDialog(this, "Prescription ID:");
            if (rxId == null || rxId.trim().isEmpty()) return;

            Clinician clinician = chooseClinician("Select prescribing clinician");
            if (clinician == null) return;
            String clinicianId = clinician.getClinicianId();

            String apptId = JOptionPane.showInputDialog(this, "Appointment ID (optional):");
            if (apptId == null) apptId = "";

            String medName = JOptionPane.showInputDialog(this, "Medication name:");
            if (medName == null) return;

            String dosage = JOptionPane.showInputDialog(this, "Dosage (e.g. 20mg):");
            if (dosage == null) return;

            String freq = JOptionPane.showInputDialog(this, "Frequency (e.g. Once daily):");
            if (freq == null) return;

            String durStr = JOptionPane.showInputDialog(this, "Duration in days:");
            if (durStr == null) return;
            int durationDays = Integer.parseInt(durStr.trim());

            String qtyStr = JOptionPane.showInputDialog(this, "Quantity (number):");
            if (qtyStr == null) return;
            int quantity = Integer.parseInt(qtyStr.trim());

            String instructions = JOptionPane.showInputDialog(this, "Instructions:");
            if (instructions == null) instructions = "";

            String pharmacy = JOptionPane.showInputDialog(this, "Pharmacy name:");
            if (pharmacy == null) pharmacy = "";

            Prescription rx = new Prescription(
                    rxId.trim(),
                    patientId,
                    clinicianId,
                    apptId.trim(),
                    today,
                    medName.trim(),
                    dosage.trim(),
                    freq.trim(),
                    durationDays,
                    quantity,
                    instructions.trim(),
                    pharmacy.trim(),
                    "Issued",
                    today,
                    null
            );

            prescriptionController.addPrescription(rx);
            updateRightSideForSelectedPatient();
            JOptionPane.showMessageDialog(this, "Prescription issued.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error issuing prescription: " + ex.getMessage());
        }
    }

    // ================== Create Referral ==================

    private void createReferralForSelectedPatient() {
        Patient p = getSelectedPatient();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Please select a patient first.");
            return;
        }

        String patientId = p.getPatientId();

        try {
            String refId = JOptionPane.showInputDialog(this, "Referral ID:");
            if (refId == null || refId.trim().isEmpty()) return;

            Clinician refClin = chooseClinician("Select referring clinician");
            if (refClin == null) return;

            Clinician toClin = chooseClinician("Select referred-to clinician");
            if (toClin == null) return;

            String refClinId = refClin.getClinicianId();
            String toClinId  = toClin.getClinicianId();

            String refFacId = refClin.getWorkplaceId();
            String toFacId  = toClin.getWorkplaceId();

            String[] urgencyOptions = { "Routine", "Urgent", "Emergency" };
            String urgency = (String) JOptionPane.showInputDialog(
                    this,
                    "Urgency Level:",
                    "Urgency",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    urgencyOptions,
                    urgencyOptions[0]
            );
            if (urgency == null) return;

            String reason = JOptionPane.showInputDialog(this, "Referral Reason:");
            if (reason == null) return;

            String clinicalSummary = askMultiLine("Clinical Summary:");
            if (clinicalSummary == null) return;

            String requestedInvestigations = askMultiLine("Requested Investigations:");
            if (requestedInvestigations == null) return;

            String apptId = JOptionPane.showInputDialog(this, "Linked Appointment ID (optional):");
            if (apptId == null) apptId = "";

            String notes = askMultiLine("Notes (optional):");
            if (notes == null) notes = "";

            Referral r = referralController.createref(
                    refId.trim(),
                    patientId,
                    refClinId,
                    toClinId,
                    refFacId,
                    toFacId,
                    urgency.trim(),
                    reason.trim(),
                    clinicalSummary.trim(),
                    requestedInvestigations.trim(),
                    apptId.trim(),
                    notes.trim()
            );

            updateRightSideForSelectedPatient();
            JOptionPane.showMessageDialog(this, "Referral created: " + r.getReferralId());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating referral: " + ex.getMessage());
        }
    }

    // ================== Helpers ==================

    private Patient getSelectedPatient() {
        int row = patientTable.getSelectedRow();
        if (row < 0) return null;
        String patientId = (String) patientTableModel.getValueAt(row, 0);
        return patientController.getPatientById(patientId);
    }

    private String askMultiLine(String msg) {
        JTextArea area = new JTextArea(6, 30);
        JScrollPane scroll = new JScrollPane(area);

        int result = JOptionPane.showConfirmDialog(
                this,
                new Object[]{msg, scroll},
                "Input",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            return area.getText();
        }
        return null;
    }

    private Clinician chooseClinician(String title) {
        List<Clinician> clinicians = clinicianController.getallclinicians();
        if (clinicians.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No clinicians available.");
            return null;
        }

        DefaultListModel<Clinician> model = new DefaultListModel<>();
        for (Clinician c : clinicians) {
            model.addElement(c);
        }

        JList<Clinician> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(8);

        JScrollPane scroll = new JScrollPane(list);

        int result = JOptionPane.showConfirmDialog(
                this,
                scroll,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION && !list.isSelectionEmpty()) {
            return list.getSelectedValue();
        }
        return null;
    }
}
