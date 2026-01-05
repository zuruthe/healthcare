package healthcare.view;

import healthcare.controller.Cliniciancontroller;
import healthcare.model.Clinician;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ClinicianPanel extends JPanel {

    private final Cliniciancontroller controller;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;

    public ClinicianPanel(Cliniciancontroller controller) {
        this.controller = controller;
        setLayout(new BorderLayout(8, 8));

        buildUI();
        loadClinicians();
    }

    private void buildUI() {
        // ---- TOP: title + buttons ----
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Clinicians");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        topPanel.add(title, BorderLayout.WEST);

        JButton addButton = new JButton("Add Clinician");
        JButton deleteButton = new JButton("Delete Selected");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addButton);
        btnPanel.add(deleteButton);
        topPanel.add(btnPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ---- TABLE (CENTER) ----
        String[] columns = {
                "ID", "Title", "First Name", "Last Name",
                "Speciality", "GMC / NMC No",
                "Workplace ID", "Workplace Type",
                "Employment Status", "Phone", "Email", "Start Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // optional column width tweaks
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // Title
        table.getColumnModel().getColumn(2).setPreferredWidth(80);   // First
        table.getColumnModel().getColumn(3).setPreferredWidth(80);   // Last
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Speciality
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // Workplace ID
        table.getColumnModel().getColumn(7).setPreferredWidth(90);   // Type
        table.getColumnModel().getColumn(8).setPreferredWidth(90);   // Status

        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        // ---- DETAILS AREA (BOTTOM) ----
        detailsArea = new JTextArea(6, 60);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createTitledBorder("Clinician details"));

        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        add(detailsScroll, BorderLayout.SOUTH);

        // when row selected, show details
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedDetails();
            }
        });

        // ---- Button actions ----
        addButton.addActionListener(e -> showAddClinicianDialog());
        deleteButton.addActionListener(e -> deleteSelectedClinician());
    }

    private void loadClinicians() {
        tableModel.setRowCount(0);
        List<Clinician> clinicians = controller.getallclinicians();
        for (Clinician c : clinicians) {
            tableModel.addRow(new Object[]{
                    c.getClinicianId(),
                    c.getTitle(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getSpeciality(),
                    c.getGmcNumber(),
                    c.getWorkplaceId(),
                    c.getWorkplaceType(),
                    c.getEmploymentStatus(),
                    c.getPhoneNumber(),
                    c.getEmail(),
                    c.getStartDate()
            });
        }

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
            showSelectedDetails();
        } else {
            detailsArea.setText("");
        }
    }

    private void showSelectedDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            detailsArea.setText("");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Clinician c = controller.getbyid(id);
        if (c == null) {
            detailsArea.setText("Clinician not found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(c.getClinicianId()).append("\n");
        sb.append("Name: ").append(c.getTitle()).append(" ")
                .append(c.getFirstName()).append(" ")
                .append(c.getLastName()).append("\n");
        sb.append("Speciality: ").append(c.getSpeciality()).append("\n\n");

        sb.append("GMC / NMC number: ").append(c.getGmcNumber()).append("\n");
        sb.append("Employment status: ").append(c.getEmploymentStatus()).append("\n");
        sb.append("Start date: ").append(c.getStartDate()).append("\n\n");

        sb.append("Workplace ID: ").append(c.getWorkplaceId()).append("\n");
        sb.append("Workplace type: ").append(c.getWorkplaceType()).append("\n\n");

        sb.append("Phone: ").append(c.getPhoneNumber()).append("\n");
        sb.append("Email: ").append(c.getEmail()).append("\n");

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    // ================== Add Clinician dialog ==================

    private void showAddClinicianDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 4, 4));

        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField firstField = new JTextField();
        JTextField lastField = new JTextField();
        JTextField specialityField = new JTextField();
        JTextField gmcField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField workplaceIdField = new JTextField();
        JTextField workplaceTypeField = new JTextField();
        JTextField employmentStatusField = new JTextField();
        JTextField startDateField = new JTextField("2020-01-01"); // YYYY-MM-DD

        panel.add(new JLabel("Clinician ID:"));              panel.add(idField);
        panel.add(new JLabel("Title (e.g. Dr., Nurse):"));   panel.add(titleField);
        panel.add(new JLabel("First name:"));               panel.add(firstField);
        panel.add(new JLabel("Last name:"));                panel.add(lastField);
        panel.add(new JLabel("Speciality:"));               panel.add(specialityField);
        panel.add(new JLabel("GMC / NMC number:"));         panel.add(gmcField);
        panel.add(new JLabel("Phone:"));                    panel.add(phoneField);
        panel.add(new JLabel("Email:"));                    panel.add(emailField);
        panel.add(new JLabel("Workplace ID:"));             panel.add(workplaceIdField);
        panel.add(new JLabel("Workplace type:"));           panel.add(workplaceTypeField);
        panel.add(new JLabel("Employment status:"));        panel.add(employmentStatusField);
        panel.add(new JLabel("Start date (YYYY-MM-DD):"));  panel.add(startDateField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Clinician",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Clinician ID is required.");
                return;
            }

            Clinician c = new Clinician(
                    id,
                    firstField.getText().trim(),
                    lastField.getText().trim(),
                    titleField.getText().trim(),
                    specialityField.getText().trim(),
                    gmcField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    workplaceIdField.getText().trim(),
                    workplaceTypeField.getText().trim(),
                    employmentStatusField.getText().trim(),
                    LocalDate.parse(startDateField.getText().trim()) // expects YYYY-MM-DD
            );

            controller.addclinician(c);
            loadClinicians();
            JOptionPane.showMessageDialog(this, "Clinician added.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    // ================== Delete Clinician ==================

    private void deleteSelectedClinician() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a clinician first.");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete clinician: " + id + " ?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (controller.deleteClinician(id)) {
            JOptionPane.showMessageDialog(this, "Clinician deleted.");
            loadClinicians();
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete clinician.");
        }
    }
}
