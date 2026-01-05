package healthcare.view;

import healthcare.controller.Facilitycontroller;
import healthcare.model.Facility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacilityPanel extends JPanel {

    private final Facilitycontroller controller;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;

    public FacilityPanel(Facilitycontroller controller) {
        this.controller = controller;
        setLayout(new BorderLayout(8, 8));

        buildUI();
        loadFacilities();
    }

    private void buildUI() {
        // ---- TOP: title + buttons ----
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Facilities");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        topPanel.add(title, BorderLayout.WEST);

        JButton addButton = new JButton("Add Facility");
        JButton deleteButton = new JButton("Delete Selected");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addButton);
        btnPanel.add(deleteButton);

        topPanel.add(btnPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ---- TABLE (CENTER) ----
        String[] columns = {
                "ID",
                "Name",
                "Type",
                "Postcode",
                "Phone",
                "Manager",
                "Capacity"
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

        // Optional column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(90);  // Type
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Postcode
        table.getColumnModel().getColumn(4).setPreferredWidth(110); // Phone
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Manager
        table.getColumnModel().getColumn(6).setPreferredWidth(70);  // Capacity

        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        // ---- DETAILS AREA (BOTTOM) ----
        detailsArea = new JTextArea(7, 60);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createTitledBorder("Facility details"));

        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        add(detailsScroll, BorderLayout.SOUTH);

        // when row selected → show details
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedDetails();
            }
        });

        // button actions
        addButton.addActionListener(e -> showAddFacilityDialog());
        deleteButton.addActionListener(e -> deleteSelectedFacility());
    }

    private void loadFacilities() {
        tableModel.setRowCount(0);
        List<Facility> facilities = controller.getall();
        for (Facility f : facilities) {
            tableModel.addRow(new Object[] {
                    f.getFacilityId(),
                    f.getFacilityName(),
                    f.getFacilityType(),
                    f.getPostcode(),
                    f.getPhoneNumber(),
                    f.getManagerName(),
                    f.getCapacity()
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
        Facility f = controller.getbyid(id);
        if (f == null) {
            detailsArea.setText("Facility not found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID:       ").append(f.getFacilityId()).append("\n");
        sb.append("Name:     ").append(f.getFacilityName()).append("\n");
        sb.append("Type:     ").append(f.getFacilityType()).append("\n\n");

        sb.append("Address:\n");
        sb.append("  ").append(f.getAddress()).append("\n");
        sb.append("  ").append(f.getPostcode()).append("\n\n");

        sb.append("Phone:    ").append(f.getPhoneNumber()).append("\n");
        sb.append("Email:    ").append(f.getEmail()).append("\n\n");

        sb.append("Opening hours:\n");
        sb.append(f.getOpeningHours()).append("\n\n");

        sb.append("Manager:  ").append(f.getManagerName()).append("\n");
        sb.append("Capacity: ").append(f.getCapacity()).append("\n\n");

        sb.append("Specialities offered:\n");
        sb.append(f.getSpecialitiesOffered()).append("\n");

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    // ================ ADD FACILITY ================

    private void showAddFacilityDialog() {
        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField postcodeField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField hoursField = new JTextField();
        JTextField managerField = new JTextField();
        JTextField capacityField = new JTextField("0");
        JTextField specialitiesField = new JTextField();

        form.add(new JLabel("Facility ID:"));      form.add(idField);
        form.add(new JLabel("Name:"));             form.add(nameField);
        form.add(new JLabel("Type:"));             form.add(typeField);
        form.add(new JLabel("Address:"));          form.add(addressField);
        form.add(new JLabel("Postcode:"));         form.add(postcodeField);
        form.add(new JLabel("Phone:"));            form.add(phoneField);
        form.add(new JLabel("Email:"));            form.add(emailField);
        form.add(new JLabel("Opening hours:"));    form.add(hoursField);
        form.add(new JLabel("Manager:"));          form.add(managerField);
        form.add(new JLabel("Capacity:"));         form.add(capacityField);
        form.add(new JLabel("Specialities:"));     form.add(specialitiesField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add Facility",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            int capacity = 0;
            String capText = capacityField.getText().trim();
            if (!capText.isEmpty()) {
                capacity = Integer.parseInt(capText);
            }

            Facility f = new Facility(
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    typeField.getText().trim(),
                    addressField.getText().trim(),
                    postcodeField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    hoursField.getText().trim(),
                    managerField.getText().trim(),
                    capacity,
                    specialitiesField.getText().trim()
            );

            controller.addFacility(f);
            loadFacilities();
            JOptionPane.showMessageDialog(this, "Facility added.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity must be a number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ================ DELETE FACILITY ================

    private void deleteSelectedFacility() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a facility first.");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete facility " + id + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = controller.deleteFacility(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Facility deleted.");
            loadFacilities();
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete facility.");
        }
    }
}
