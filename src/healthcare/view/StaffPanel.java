package healthcare.view;

import healthcare.controller.Staffcontroller;
import healthcare.model.Staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StaffPanel extends JPanel {

    private final Staffcontroller controller;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;

    public StaffPanel(Staffcontroller controller) {
        this.controller = controller;

        setLayout(new BorderLayout(8, 8));

        buildUI();
        loadStaff();
    }

    private void buildUI() {

        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Staff");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        topPanel.add(title, BorderLayout.WEST);

        JButton addButton = new JButton("Add Staff");
        JButton deleteButton = new JButton("Delete Selected");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addButton);
        btnPanel.add(deleteButton);

        topPanel.add(btnPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {
                "ID", "First Name", "Last Name", "Role",
                "Department", "Facility ID", "Phone",
                "Email", "Status", "Start Date", "Line Manager", "Access Level"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        detailsArea = new JTextArea(7, 60);
        detailsArea.setEditable(false);
        detailsArea.setBorder(
                BorderFactory.createTitledBorder("Staff details")
        );

        add(new JScrollPane(detailsArea), BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelectedDetails();
        });

        addButton.addActionListener(e -> addStaffDialog());
        deleteButton.addActionListener(e -> deleteSelectedStaff());
    }

    private void loadStaff() {
        tableModel.setRowCount(0);

        List<Staff> staff = controller.getAllStaff();
        for (Staff s : staff) {
            tableModel.addRow(new Object[]{
                    s.getStaffId(),
                    s.getFirstName(),
                    s.getLastName(),
                    s.getRole(),
                    s.getDepartment(),
                    s.getFacilityId(),
                    s.getPhoneNumber(),
                    s.getEmail(),
                    s.getEmploymentStatus(),
                    s.getStartDate(),
                    s.getLineManager(),
                    s.getAccessLevel()
            });
        }

        if (tableModel.getRowCount() > 0)
            table.setRowSelectionInterval(0, 0);
    }

    private void showSelectedDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            detailsArea.setText("");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Staff s = controller.getById(id);

        if (s == null) {
            detailsArea.setText("Staff not found.");
            return;
        }

        detailsArea.setText(
                "ID: " + s.getStaffId() + "\n" +
                "Name: " + s.getFirstName() + " " + s.getLastName() + "\n" +
                "Role: " + s.getRole() + "\n" +
                "Department: " + s.getDepartment() + "\n" +
                "Facility: " + s.getFacilityId() + "\n\n" +
                "Phone: " + s.getPhoneNumber() + "\n" +
                "Email: " + s.getEmail() + "\n" +
                "Status: " + s.getEmploymentStatus() + "\n" +
                "Start Date: " + s.getStartDate() + "\n\n" +
                "Line Manager: " + s.getLineManager() + "\n" +
                "Access Level: " + s.getAccessLevel()
        );
    }

    private void addStaffDialog() {

        JTextField id = new JTextField();
        JTextField first = new JTextField();
        JTextField last = new JTextField();
        JTextField role = new JTextField();
        JTextField dept = new JTextField();
        JTextField facility = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        JTextField status = new JTextField();
        JTextField start = new JTextField("2024-01-01");
        JTextField manager = new JTextField();
        JTextField access = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));

        panel.add(new JLabel("Staff ID:")); panel.add(id);
        panel.add(new JLabel("First Name:")); panel.add(first);
        panel.add(new JLabel("Last Name:")); panel.add(last);
        panel.add(new JLabel("Role:")); panel.add(role);
        panel.add(new JLabel("Department:")); panel.add(dept);
        panel.add(new JLabel("Facility ID:")); panel.add(facility);
        panel.add(new JLabel("Phone:")); panel.add(phone);
        panel.add(new JLabel("Email:")); panel.add(email);
        panel.add(new JLabel("Employment Status:")); panel.add(status);
        panel.add(new JLabel("Start Date (yyyy-MM-dd):")); panel.add(start);
        panel.add(new JLabel("Line Manager:")); panel.add(manager);
        panel.add(new JLabel("Access Level:")); panel.add(access);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add Staff", JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            LocalDate sd = start.getText().isBlank()
                    ? null
                    : LocalDate.parse(start.getText().trim());

            Staff s = new Staff(
                    id.getText().trim(),
                    first.getText().trim(),
                    last.getText().trim(),
                    role.getText().trim(),
                    dept.getText().trim(),
                    facility.getText().trim(),
                    phone.getText().trim(),
                    email.getText().trim(),
                    status.getText().trim(),
                    sd,
                    manager.getText().trim(),
                    access.getText().trim()
            );

            controller.addStaff(s);
            loadStaff();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid data: " + ex.getMessage());
        }
    }

    private void deleteSelectedStaff() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member first.");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete staff: " + id + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (controller.deleteStaff(id)) {
            loadStaff();
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete.");
        }
    }
}
