package healthcare.view;

import healthcare.controller.Prescriptioncontroller;
import healthcare.model.Prescription;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PrescriptionPanel extends JPanel {

    private final Prescriptioncontroller controller;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextArea detailsArea;

    public PrescriptionPanel(Prescriptioncontroller controller) {
        this.controller = controller;
        setLayout(new BorderLayout(8, 8));

        String[] columns = {
                "ID", "Patient", "Clinician", "Appt",
                "Date", "Medication", "Dosage",
                "Freq", "Days", "Qty", "Status",
                "Issue", "Collected"
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

        detailsArea = new JTextArea(7, 60);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createTitledBorder("Prescription details"));

        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        add(detailsScroll, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedDetails();
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshBtn = new JButton("View All");
        JButton collectBtn = new JButton("Mark Collected");
       
        JButton viewOneBtn = new JButton("View by ID");

        top.add(refreshBtn);
        top.add(collectBtn);
       
        top.add(viewOneBtn);

        add(top, BorderLayout.NORTH);

        refreshBtn.addActionListener(e -> refreshTable());
        collectBtn.addActionListener(e -> markCollectedDialog());
        
        viewOneBtn.addActionListener(e -> viewByIdDialog());

        refreshTable();
    }

    // optional standalone constructor
    public PrescriptionPanel() {
        this(new Prescriptioncontroller());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Prescription p : controller.getAllPrescriptions()) {
            tableModel.addRow(new Object[] {
                    p.getPrescriptionId(),
                    p.getPatientId(),
                    p.getClinicianId(),
                    p.getAppointmentId(),
                    p.getPrescriptionDate(),
                    p.getMedicationName(),
                    p.getDosage(),
                    p.getFrequency(),
                    p.getDurationDays(),
                    p.getQuantity(),
                    p.getStatus(),
                    p.getIssueDate(),
                    p.getCollectionDate()
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
        Prescription p = controller.getPrescriptionById(id);
        if (p == null) {
            detailsArea.setText("Prescription not found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Prescription ID: ").append(p.getPrescriptionId()).append("\n");
        sb.append("Patient ID:      ").append(p.getPatientId()).append("\n");
        sb.append("Clinician ID:    ").append(p.getClinicianId()).append("\n");
        sb.append("Appointment ID:  ").append(p.getAppointmentId()).append("\n\n");

        sb.append("Prescription Date: ").append(p.getPrescriptionDate()).append("\n");
        sb.append("Issue Date:        ").append(p.getIssueDate()).append("\n");
        sb.append("Collection Date:   ").append(p.getCollectionDate()).append("\n\n");

        sb.append("Medication:  ").append(p.getMedicationName()).append("\n");
        sb.append("Dosage:      ").append(p.getDosage()).append("\n");
        sb.append("Frequency:   ").append(p.getFrequency()).append("\n");
        sb.append("Duration:    ").append(p.getDurationDays()).append(" days\n");
        sb.append("Quantity:    ").append(p.getQuantity()).append("\n");
        sb.append("Pharmacy:    ").append(p.getPharmacyName()).append("\n");
        sb.append("Status:      ").append(p.getStatus()).append("\n\n");

        sb.append("Instructions:\n");
        sb.append(p.getInstructions()).append("\n");

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    private void markCollectedDialog() {
        String id = JOptionPane.showInputDialog(this, "Prescription ID to mark as collected:");
        if (id == null || id.trim().isEmpty()) return;

        boolean ok = controller.markAsCollected(id.trim());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Marked as collected.");
            refreshTable();
            selectRowById(id.trim());
        } else {
            JOptionPane.showMessageDialog(this, "Prescription not found.");
        }
    }



    private void viewByIdDialog() {
        String id = JOptionPane.showInputDialog(this, "Prescription ID to view:");
        if (id == null || id.trim().isEmpty()) return;

        Prescription p = controller.getPrescriptionById(id.trim());
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Prescription not found.");
            return;
        }

        selectRowById(id.trim());
        StringBuilder sb = new StringBuilder();
        sb.append("Prescription ID: ").append(p.getPrescriptionId()).append("\n");
        sb.append("Patient ID:      ").append(p.getPatientId()).append("\n");
        sb.append("Clinician ID:    ").append(p.getClinicianId()).append("\n");
        sb.append("Appointment ID:  ").append(p.getAppointmentId()).append("\n\n");
        sb.append("Prescription Date: ").append(p.getPrescriptionDate()).append("\n");
        sb.append("Issue Date:        ").append(p.getIssueDate()).append("\n");
        sb.append("Collection Date:   ").append(p.getCollectionDate()).append("\n\n");
        sb.append("Medication:  ").append(p.getMedicationName()).append("\n");
        sb.append("Dosage:      ").append(p.getDosage()).append("\n");
        sb.append("Frequency:   ").append(p.getFrequency()).append("\n");
        sb.append("Duration:    ").append(p.getDurationDays()).append(" days\n");
        sb.append("Quantity:    ").append(p.getQuantity()).append("\n");
        sb.append("Pharmacy:    ").append(p.getPharmacyName()).append("\n");
        sb.append("Status:      ").append(p.getStatus()).append("\n\n");
        sb.append("Instructions:\n");
        sb.append(p.getInstructions()).append("\n");

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    private void selectRowById(String id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, 0);
            if (id.equals(value)) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                showSelectedDetails();
                break;
            }
        }
    }
}
