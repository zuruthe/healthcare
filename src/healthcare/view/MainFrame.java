package healthcare.view;

import healthcare.controller.Patientcontroller;
import healthcare.controller.Appointmentcontroller;
import healthcare.controller.Prescriptioncontroller;
import healthcare.controller.Referralcontroller;
import healthcare.controller.Cliniciancontroller;
import healthcare.controller.Facilitycontroller;
import healthcare.controller.Staffcontroller;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Healthcare Management System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ---- create shared controllers (ONE instance each) ----
        Patientcontroller patientController = new Patientcontroller();
        Appointmentcontroller appointmentController = new Appointmentcontroller();
        Prescriptioncontroller prescriptionController = new Prescriptioncontroller();
        Referralcontroller referralController = Referralcontroller.getinstance();
        Cliniciancontroller clinicianController = new Cliniciancontroller();
        Facilitycontroller facilityController = new Facilitycontroller();
        Staffcontroller staffController = new Staffcontroller();


        // ---- tabs ----
        JTabbedPane tabs = new JTabbedPane();

        // Patients is now the main "hub" view
        tabs.add("Patients", new PatientPanel(
                patientController,
                appointmentController,
                prescriptionController,
                referralController,
                clinicianController
                 
        ));
        tabs.add("Appointments", new AppointmentPanel(
                appointmentController,
                patientController,
                clinicianController,
                facilityController
        ));

        // Global views (all appointments / all prescriptions / all referrals)
       // tabs.add("Appointments", new AppointmentPanel(appointmentController));
        tabs.add("Prescriptions", new PrescriptionPanel(prescriptionController));
        tabs.add("Referrals", new ReferralPanel(referralController));
        tabs.add("Clinicians", new ClinicianPanel(clinicianController));
        tabs.add("Facilities", new FacilityPanel(facilityController));
        tabs.add("Staff", new StaffPanel(staffController));

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
