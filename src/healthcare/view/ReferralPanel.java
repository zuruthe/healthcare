package healthcare.view;

import healthcare.controller.Referralcontroller;
import healthcare.model.Referral;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReferralPanel extends JPanel {

    private final Referralcontroller controller;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextArea detailsArea;

    public ReferralPanel(Referralcontroller controller) {
        this.controller = controller;

        setLayout(new BorderLayout(8, 8));

        // ---- TOP: title + buttons ----
        JPanel top = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Referrals");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        top.add(title, BorderLayout.WEST);

        JButton listButton = new JButton("List / Refresh");
        JButton updateStatusButton = new JButton("Update Status");
        JButton updateNotesButton = new JButton("Update Notes");
        JButton viewDetailsButton = new JButton("View Details by ID");

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(listButton);
        buttonsPanel.add(updateStatusButton);
        buttonsPanel.add(updateNotesButton);
        buttonsPanel.add(viewDetailsButton);

        top.add(buttonsPanel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // ---- CENTER: table + details split ----

        // Columns: show lots of key info including clinical summary
        String[] cols = {
                "ID",
                "Patient ID",
                "Referring Clinician",
                "Referred-To Clinician",
                "Referring Facility",
                "Referred-To Facility",
                "Urgency",
                "Status",
                "Date",
                "Appointment ID",
                "Clinical Summary"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // optional width tweaks
        table.getColumnModel().getColumn(0).setPreferredWidth(70);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // Patient
        table.getColumnModel().getColumn(2).setPreferredWidth(130);  // From clinician
        table.getColumnModel().getColumn(3).setPreferredWidth(130);  // To clinician
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // From facility
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // To facility
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // Urgency
        table.getColumnModel().getColumn(7).setPreferredWidth(90);   // Status
        table.getColumnModel().getColumn(8).setPreferredWidth(90);   // Date
        table.getColumnModel().getColumn(9).setPreferredWidth(90);   // Appt ID
        table.getColumnModel().getColumn(10).setPreferredWidth(200); // Clinical summary

        JScrollPane tableScroll = new JScrollPane(table);

        detailsArea = new JTextArea(10, 50);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(
                BorderFactory.createTitledBorder("Referral details")
        );
        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                tableScroll,
                detailsScroll
        );
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);

        // ---- event: select row → show details ----
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedReferralDetails();
            }
        });

        // ---- button actions ----
        listButton.addActionListener(e -> listReferrals());
        updateStatusButton.addActionListener(e -> updateStatus());
        updateNotesButton.addActionListener(e -> updateNotes());
        viewDetailsButton.addActionListener(e -> viewDetails());

        // initial load
        listReferrals();
    }

    // optional standalone constructor
    public ReferralPanel() {
        this(Referralcontroller.getinstance());
    }

    // ================= LIST / REFRESH =================

    private void listReferrals() {
        tableModel.setRowCount(0);
        detailsArea.setText("");

        if (controller.getallreferrals().isEmpty()) {
            detailsArea.setText("No referrals created yet.\n");
            return;
        }

        for (Referral r : controller.getallreferrals()) {
            tableModel.addRow(new Object[] {
                    r.getReferralId(),
                    r.getPatientId(),
                    r.getReferringClinicianId(),
                    r.getReferredToClinicianId(),
                    r.getReferringFacilityId(),
                    r.getReferredToFacilityId(),
                    r.getUrgencyLevel(),
                    r.getStatus(),
                    r.getReferralDate(),
                    r.getAppointmentId(),
                    r.getClinicalSummary()
            });
        }

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
            showSelectedReferralDetails();
        }
    }

    private void showSelectedReferralDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            detailsArea.setText("");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Referral r = controller.getReferralbyid(id);
        if (r == null) {
            detailsArea.setText("Referral not found.");
            return;
        }

        detailsArea.setText(buildDetailsText(r));
        detailsArea.setCaretPosition(0);
    }

    private String buildDetailsText(Referral r) {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Referral Overview ===\n");
        sb.append("Referral ID:   ").append(r.getReferralId()).append("\n");
        sb.append("Patient ID:    ").append(r.getPatientId()).append("\n");
        sb.append("Date:          ").append(r.getReferralDate()).append("\n");
        sb.append("Urgency:       ").append(r.getUrgencyLevel()).append("\n");
        sb.append("Status:        ").append(r.getStatus()).append("\n");
        sb.append("Appointment ID:").append(r.getAppointmentId()).append("\n\n");

        sb.append("=== Clinicians & Facilities ===\n");
        sb.append("Referring Clinician:    ").append(r.getReferringClinicianId()).append("\n");
        sb.append("Referred-To Clinician:  ").append(r.getReferredToClinicianId()).append("\n");
        sb.append("Referring Facility:     ").append(r.getReferringFacilityId()).append("\n");
        sb.append("Referred-To Facility:   ").append(r.getReferredToFacilityId()).append("\n\n");

        sb.append("=== Reason ===\n");
        sb.append(r.getReferralReason()).append("\n\n");

        sb.append("=== Clinical Summary ===\n");
        sb.append(r.getClinicalSummary()).append("\n\n");

        sb.append("=== Requested Investigations ===\n");
        sb.append(r.getRequestedInvestigations()).append("\n\n");

        sb.append("=== Notes ===\n");
        sb.append(r.getNotes()).append("\n\n");

        sb.append("Created:       ").append(r.getCreatedDate()).append("\n");
        sb.append("Last Updated:  ").append(r.getLastUpdated()).append("\n");

        return sb.toString();
    }

    // ================= UPDATE STATUS =================

    private void updateStatus() {
        String id = ask("Referral ID to update status:");
        if (id == null || id.trim().isEmpty()) return;

        String status = ask("New status (e.g. Pending, Accepted, Rejected, Completed):");
        if (status == null) return;

        if (controller.updatestatus(id.trim(), status.trim())) {
            JOptionPane.showMessageDialog(this, "Status updated.");
            listReferrals();
            selectRowById(id.trim());
        } else {
            JOptionPane.showMessageDialog(this, "Referral not found.");
        }
    }

    // ================= UPDATE NOTES =================

    private void updateNotes() {
        String id = ask("Referral ID to update notes:");
        if (id == null || id.trim().isEmpty()) return;

        String notes = askMultiLine("New notes:");
        if (notes == null) return;

        if (controller.updatenotes(id.trim(), notes.trim())) {
            JOptionPane.showMessageDialog(this, "Notes updated.");
            listReferrals();
            selectRowById(id.trim());
        } else {
            JOptionPane.showMessageDialog(this, "Referral not found.");
        }
    }

    // ================= VIEW DETAILS BY ID =================

    private void viewDetails() {
        String id = ask("Referral ID to view:");
        if (id == null || id.trim().isEmpty()) return;

        Referral r = controller.getReferralbyid(id.trim());
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Referral not found.");
            return;
        }

        selectRowById(id.trim());
        detailsArea.setText(buildDetailsText(r));
        detailsArea.setCaretPosition(0);
    }

    // ================= HELPERS =================

    private String ask(String msg) {
        return JOptionPane.showInputDialog(this, msg);
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

    private void selectRowById(String id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, 0);
            if (id.equals(value)) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                showSelectedReferralDetails();
                break;
            }
        }
    }
}
